#!/usr/bin/env bash
#
# Converts the Oracle DDL to Postgres with sqlglot, then generates a Flyway
# migration for the difference between the existing migrations and that DDL.
#
#   ./scripts/generate-migration.sh <migration_name>   generate a migration
#   ./scripts/generate-migration.sh --baseline          dump the current schema
#
# Both tools run in official containers, so nothing needs installing locally.
# Atlas needs a scratch database to compute the diff against; this starts one
# rather than letting Atlas spawn its own, which would need the Docker socket.
#
# IMPORTANT: ddl/postgres is treated as the *complete* desired schema. Anything
# the migrations create that is absent from it will be diffed as a DROP. See
# README notes before using this on a schema the DDL does not fully describe.

set -euo pipefail

readonly ROOT="$(git -C "$(dirname "${BASH_SOURCE[0]}")" rev-parse --show-toplevel)"
readonly ORACLE_DDL="${ORACLE_DDL:-ddl/oracle/oracle_ddl.sql}"
readonly POSTGRES_DDL="${POSTGRES_DDL:-ddl/postgres/postgres_ddl.sql}"
readonly MIGRATION_DIR="src/main/resources/db/migration"
readonly PYTHON_IMAGE="python:3.12-slim"
readonly ATLAS_IMAGE="arigaio/atlas:latest"
readonly POSTGRES_IMAGE="postgres:16"

readonly COMMAND="${1:-}"
if [[ -z "$COMMAND" ]]; then
    echo "usage: $(basename "$0") <migration_name>|--baseline" >&2
    exit 64
fi

cd "$ROOT"

if [[ ! -f "$ORACLE_DDL" ]]; then
    echo "error: $ORACLE_DDL not found" >&2
    exit 66
fi

readonly NETWORK="atlas-dev-$$"
readonly DEV_DB="atlas-dev-db-$$"

cleanup() {
    docker rm -f "$DEV_DB" >/dev/null 2>&1 || true
    docker network rm "$NETWORK" >/dev/null 2>&1 || true
}
trap cleanup EXIT

transpile() {
    echo "==> Converting $ORACLE_DDL to Postgres"
    mkdir -p "$(dirname "$POSTGRES_DDL")"
    docker run --rm \
        --volume "$ROOT:/work" \
        --workdir /work \
        "$PYTHON_IMAGE" \
        sh -c "pip install --quiet --disable-pip-version-check sqlglot \
            && python scripts/oracle_to_postgres.py '$ORACLE_DDL' '$POSTGRES_DDL'"
}

start_dev_database() {
    echo "==> Starting scratch Postgres for Atlas"
    docker network create "$NETWORK" >/dev/null
    docker run --detach --name "$DEV_DB" --network "$NETWORK" \
        --env POSTGRES_PASSWORD=atlas \
        --env POSTGRES_DB=dev \
        "$POSTGRES_IMAGE" >/dev/null

    for _ in $(seq 1 30); do
        if docker exec "$DEV_DB" pg_isready --quiet --username postgres 2>/dev/null; then
            return 0
        fi
        sleep 1
    done

    echo "error: scratch database did not become ready" >&2
    exit 69
}

# Atlas tracks directory integrity in atlas.sum. Flyway does not create one, so
# the first run has to generate it from the existing migrations. Flyway ignores
# non-.sql files, so this is inert as far as the application is concerned.
ensure_checksum() {
    if [[ -f "$MIGRATION_DIR/atlas.sum" ]]; then
        return 0
    fi
    echo "==> No atlas.sum present; hashing existing migrations"
    docker run --rm \
        --volume "$ROOT:/work" \
        --workdir /work \
        "$ATLAS_IMAGE" migrate hash \
        --dir "file://$MIGRATION_DIR?format=flyway"
}

generate_migration() {
    echo "==> Generating migration '$MIGRATION_NAME'"
    docker run --rm \
        --network "$NETWORK" \
        --volume "$ROOT:/work" \
        --workdir /work \
        "$ATLAS_IMAGE" migrate diff "$MIGRATION_NAME" \
        --dir "file://$MIGRATION_DIR?format=flyway" \
        --to "file://$POSTGRES_DDL" \
        --dev-url "postgres://postgres:atlas@$DEV_DB:5432/dev?sslmode=disable&search_path=public"
}

# Renders the schema the existing migrations produce, as Postgres DDL. Use this
# to seed ddl/postgres so a diff describes a delta rather than dropping
# everything the DDL does not mention. Only covers tables owned by these
# migrations: ccd.* comes from the SDK's own migrations and is not included.
baseline() {
    echo "==> Rendering current schema from $MIGRATION_DIR"
    docker run --rm \
        --network "$NETWORK" \
        --volume "$ROOT:/work" \
        --workdir /work \
        "$ATLAS_IMAGE" schema inspect \
        --url "file://$MIGRATION_DIR?format=flyway" \
        --dev-url "postgres://postgres:atlas@$DEV_DB:5432/dev?sslmode=disable&search_path=public" \
        --format '{{ sql . "  " }}'
}

warn_on_destructive_changes() {
    local newest
    newest="$(ls -t "$MIGRATION_DIR"/*.sql 2>/dev/null | head -1)"
    [[ -n "$newest" ]] || return 0

    if grep -qiE '^\s*(DROP|TRUNCATE)\b' "$newest"; then
        echo
        echo "WARNING: $newest contains DROP or TRUNCATE statements." >&2
        echo "This usually means $POSTGRES_DDL does not describe the whole schema." >&2
        echo "Review it before committing." >&2
    fi
}

if [[ "$COMMAND" == "--baseline" ]]; then
    ensure_checksum
    start_dev_database
    baseline
    exit 0
fi

readonly MIGRATION_NAME="$COMMAND"
transpile
ensure_checksum
start_dev_database
generate_migration
warn_on_destructive_changes

echo
echo "==> Done. Review the generated migration in $MIGRATION_DIR"
