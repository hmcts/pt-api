"""Transpile an Oracle DDL file to Postgres using sqlglot.

sqlglot handles most of the dialect translation but leaves a few Oracle-isms
that are not valid Postgres, so they are rewritten explicitly below. Keep this
list small and visible: each entry is a translation sqlglot does not make, not
a preference.
"""

import pathlib
import re
import sys

import sqlglot

FIXUPS: list[tuple[str, str, str]] = [
    (r"\bSYSTIMESTAMP\(\)", "CURRENT_TIMESTAMP", "SYSTIMESTAMP has no Postgres equivalent"),
    (r"\bDECIMAL\(19\)", "BIGINT", "NUMBER(19) is an integer id, not a decimal"),
]


def apply_fixups(sql: str) -> str:
    for pattern, replacement, reason in FIXUPS:
        sql, count = re.subn(pattern, replacement, sql)
        if count:
            print(f"  fixup: {pattern} -> {replacement} ({count}x) — {reason}")
    return sql


def main(source: str, target: str) -> None:
    statements = sqlglot.transpile(
        pathlib.Path(source).read_text(),
        read="oracle",
        write="postgres",
        pretty=True,
    )
    sql = apply_fixups(";\n\n".join(statements) + ";\n")

    out = pathlib.Path(target)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(sql)
    print(f"wrote {len(statements)} statement(s) to {target}")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit("usage: oracle_to_postgres.py <source.sql> <target.sql>")
    main(sys.argv[1], sys.argv[2])
