#!/usr/bin/env bash

set -euo pipefail

readonly ROOT="$(git -C "$(dirname "${BASH_SOURCE[0]}")" rev-parse --show-toplevel)"
readonly ORACLE_DDL="${ORACLE_DDL:-ddl/oracle/oracle_ddl.sql}"
readonly POSTGRES_DDL="${POSTGRES_DDL:-ddl/postgres/postgres_ddl.sql}"
readonly MIGRATION_DIR="${MIGRATION_DIR:-src/main/resources/db/migration}"
readonly PYTHON_IMAGE="python:3.12-slim"
readonly ATLAS_IMAGE="arigaio/atlas:latest"
readonly POSTGRES_IMAGE="postgres:16"
# Containers write into the bind-mounted repo. Without this they run as root and
# leave root-owned files behind, which the host user cannot then edit.
readonly RUN_AS="$(id -u):$(id -g)"

readonly COMMAND="${1:-}"
if [[ -z "$COMMAND" ]]; then
    echo "usage: $(basename "$0") <migration_name>|--baseline|--verify|--reverse" >&2
    exit 64
fi

cd "$ROOT"

if [[ ! -f "$ORACLE_DDL" ]]; then
    echo "error: $ORACLE_DDL not found" >&2
    exit 66
fi

# Verification works on copies so it never mutates tracked files. Must live
# inside the repo for the bind-mounted containers to reach it.
readonly VERIFY_DIR=".ddl-verify-$$"

readonly NETWORK="atlas-dev-$$"
readonly DEV_DB="atlas-dev-db-$$"

cleanup() {
    docker rm -f "$DEV_DB" >/dev/null 2>&1 || true
    docker network rm "$NETWORK" >/dev/null 2>&1 || true
    rm -rf "$ROOT/$VERIFY_DIR"
}
trap cleanup EXIT

transpile() {
    local target="${1:-$POSTGRES_DDL}"
    echo "==> Converting $ORACLE_DDL to Postgres"
    mkdir -p "$(dirname "$target")"
    docker run --rm \
        --volume "$ROOT:/work" \
        --workdir /work \
        --user "$RUN_AS" \
        --env HOME=/tmp \
        "$PYTHON_IMAGE" \
        sh -c "pip install --quiet --disable-pip-version-check sqlglot \
            && python scripts/oracle_to_postgres.py '$ORACLE_DDL' '$target'"
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
        --user "$RUN_AS" \
        "$ATLAS_IMAGE" migrate hash \
        --dir "file://$MIGRATION_DIR?format=flyway"
}

generate_migration() {
    echo "==> Generating migration '$MIGRATION_NAME'"
    local before after
    before="$(ls "$MIGRATION_DIR" 2>/dev/null | sort)"

    docker run --rm \
        --network "$NETWORK" \
        --volume "$ROOT:/work" \
        --workdir /work \
        --user "$RUN_AS" \
        "$ATLAS_IMAGE" migrate diff "$MIGRATION_NAME" \
        --dir "file://$MIGRATION_DIR?format=flyway" \
        --to "file://$POSTGRES_DDL" \
        --dev-url "postgres://postgres:atlas@$DEV_DB:5432/dev?sslmode=disable&search_path=public"

    after="$(ls "$MIGRATION_DIR" 2>/dev/null | sort)"
    GENERATED_FILES="$(comm -13 <(echo "$before") <(echo "$after"))"
}

# Highest existing V<n>__ version, plus one. Ignores Atlas's timestamp versions.
next_version() {
    local max=0 name version
    for path in "$MIGRATION_DIR"/V*__*.sql; do
        [[ -e "$path" ]] || continue
        name="$(basename "$path")"
        version="${name#V}"
        version="${version%%__*}"
        [[ "$version" =~ ^[0-9]{1,6}$ ]] || continue
        ((version > max)) && max="$version"
    done
    echo "$((max + 1))"
}

# Atlas always names versions by timestamp and always emits an undo script for
# the flyway format; neither is configurable. Renumber to match the sequential
# V<n>__ convention and drop the undo file, then re-hash so atlas.sum agrees.
normalise_output() {
    [[ -n "${GENERATED_FILES:-}" ]] || return 0

    local removed=0 renamed=0
    while IFS= read -r name; do
        [[ -n "$name" ]] || continue
        if [[ "$name" == U*__*.sql ]]; then
            rm -f "$MIGRATION_DIR/$name"
            removed=1
        fi
    done <<< "$GENERATED_FILES"

    while IFS= read -r name; do
        [[ -n "$name" ]] || continue
        if [[ "$name" == V*__*.sql ]]; then
            local target="V$(next_version)__${name#*__}"
            mv "$MIGRATION_DIR/$name" "$MIGRATION_DIR/$target"
            echo "==> Renamed $name to $target"
            GENERATED_FILES="$target"
            renamed=1
        fi
    done <<< "$GENERATED_FILES"

    [[ "$removed" == 1 ]] && echo "==> Removed Atlas undo script"

    if [[ "$renamed" == 1 || "$removed" == 1 ]]; then
        docker run --rm \
            --volume "$ROOT:/work" \
            --workdir /work \
            --user "$RUN_AS" \
        "$ATLAS_IMAGE" migrate hash \
            --dir "file://$MIGRATION_DIR?format=flyway"
    fi
}

# Renders the schema the existing migrations produce, as Postgres DDL.
baseline() {
    local redirect="${1:-}"
    echo "==> Rendering current schema from $MIGRATION_DIR" >&2
    docker run --rm \
        --network "$NETWORK" \
        --volume "$ROOT:/work" \
        --workdir /work \
        "$ATLAS_IMAGE" schema inspect \
        --url "file://$MIGRATION_DIR?format=flyway" \
        --dev-url "postgres://postgres:atlas@$DEV_DB:5432/dev?sslmode=disable&search_path=public" \
        --format '{{ sql . "  " }}' \
        | grep -vE '^To upgrade|^$'
}

# Derive both DDLs from what the migrations actually produce. Use this
# to initialise and establish alignment.
reverse() {
    echo "==> Rebuilding $POSTGRES_DDL from $MIGRATION_DIR"
    mkdir -p "$(dirname "$POSTGRES_DDL")"
    baseline > "$POSTGRES_DDL"
    echo "    wrote $(grep -c 'CREATE TABLE' "$POSTGRES_DDL") table(s) to $POSTGRES_DDL"

    echo "==> Converting $POSTGRES_DDL to Oracle"
    mkdir -p "$(dirname "$ORACLE_DDL")"
    docker run --rm \
        --volume "$ROOT:/work" \
        --workdir /work \
        --user "$RUN_AS" \
        --env HOME=/tmp \
        "$PYTHON_IMAGE" \
        sh -c "pip install --quiet --disable-pip-version-check sqlglot \
            && python scripts/postgres_to_oracle.py '$POSTGRES_DDL' '$ORACLE_DDL'"

    # Atlas and sqlglot format SQL differently, so the Postgres DDL has to be
    # re-derived through the forward transpiler. Otherwise --verify compares
    # Atlas's output against sqlglot's and always reports a difference.
    transpile
}

verify() {
    local failed=0
    local expected_ddl="$VERIFY_DIR/postgres_ddl.sql"
    local migrations_copy="$VERIFY_DIR/migrations"

    mkdir -p "$VERIFY_DIR"
    transpile "$expected_ddl"

    echo "==> Comparing against committed $POSTGRES_DDL"
    if ! diff -u "$POSTGRES_DDL" "$expected_ddl"; then
        echo >&2
        echo "FAIL: $POSTGRES_DDL does not match $ORACLE_DDL." >&2
        echo "Run ./scripts/generate-migration.sh <name> and commit the result." >&2
        failed=1
    else
        echo "    $POSTGRES_DDL is up to date"
    fi

    cp -r "$MIGRATION_DIR" "$migrations_copy"
    local before after generated
    before="$(ls "$migrations_copy" | sort)"

    echo "==> Checking the committed migrations produce that schema"
    docker run --rm \
        --network "$NETWORK" \
        --volume "$ROOT:/work" \
        --workdir /work \
        --user "$RUN_AS" \
        "$ATLAS_IMAGE" migrate diff verify_check \
        --dir "file://$migrations_copy?format=flyway" \
        --to "file://$expected_ddl" \
        --dev-url "postgres://postgres:atlas@$DEV_DB:5432/dev?sslmode=disable&search_path=public"

    after="$(ls "$migrations_copy" | sort)"
    generated="$(comm -13 <(echo "$before") <(echo "$after"))"

    if [[ -n "$generated" ]]; then
        echo >&2
        echo "FAIL: $MIGRATION_DIR is missing a migration. Atlas would generate:" >&2
        echo >&2
        sed 's/^/    /' "$migrations_copy"/V*verify_check.sql >&2
        failed=1
    else
        echo "    $MIGRATION_DIR is up to date"
    fi

    if [[ "$failed" == 1 ]]; then
        exit 1
    fi
    echo
    echo "==> Verification passed"
}

warn_on_destructive_changes() {
    [[ -n "${GENERATED_FILES:-}" ]] || return 0
    local newest="$MIGRATION_DIR/$GENERATED_FILES"
    [[ -f "$newest" ]] || return 0

    if grep -qiE '^\s*(DROP|TRUNCATE)\b' "$newest"; then
        echo
        echo "WARNING: $newest contains DROP or TRUNCATE statements." >&2
        echo "This usually means $POSTGRES_DDL does not describe the whole schema." >&2
        echo "Review it before committing." >&2
    fi
}

if [[ "$COMMAND" == "--reverse" ]]; then
    ensure_checksum
    start_dev_database
    reverse
    exit 0
fi

if [[ "$COMMAND" == "--verify" ]]; then
    ensure_checksum
    start_dev_database
    verify
    exit 0
fi

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
normalise_output
warn_on_destructive_changes

echo
echo "==> Done. Review the generated migration in $MIGRATION_DIR"
