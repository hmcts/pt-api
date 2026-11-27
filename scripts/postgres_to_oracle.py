import pathlib
import sys
from collections import Counter

import sqlglot
from sqlglot import expressions as exp

# Postgres type -> Oracle type.
TYPE_MAP: dict[exp.DataType.Type, str] = {
    exp.DataType.Type.BIGINT: "NUMBER(19)",
    exp.DataType.Type.INT: "NUMBER(10)",
    exp.DataType.Type.SMALLINT: "NUMBER(5)",
    exp.DataType.Type.BOOLEAN: "NUMBER(1)",
    exp.DataType.Type.UUID: "RAW(16)",
    exp.DataType.Type.TEXT: "CLOB",
    exp.DataType.Type.VARBINARY: "BLOB",
    exp.DataType.Type.BINARY: "BLOB",
    exp.DataType.Type.TIMESTAMPTZ: "TIMESTAMP WITH TIME ZONE",
}

applied: Counter[str] = Counter()


def map_types(node: exp.Expression) -> exp.Expression:
    if isinstance(node, exp.DataType) and node.this in TYPE_MAP:
        oracle = TYPE_MAP[node.this]
        applied[f"{node.this.name.lower()} -> {oracle}"] += 1
        return exp.DataType.build(oracle, dialect="oracle", udt=True)
    return node


def main(source: str, target: str) -> None:
    parsed = sqlglot.parse(pathlib.Path(source).read_text(), read="postgres")

    rendered = []
    enum_types = 0
    for statement in parsed:
        if statement is None:
            continue
        if isinstance(statement, exp.Create) and statement.args.get("kind") == "TYPE":
            enum_types += 1
        rendered.append(statement.transform(map_types).sql(dialect="oracle", pretty=True))

    sql = ";\n\n".join(rendered) + ";\n"
    sql = sql.replace("CURRENT_TIMESTAMP", "SYSTIMESTAMP")

    out = pathlib.Path(target)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(sql)

    for change, count in sorted(applied.items()):
        print(f"  type: {change} ({count}x)")
    print(f"wrote {len(rendered)} statement(s) to {target}")

    if enum_types:
        print(f"\n{enum_types} enum type(s) left as-is: Oracle has no ENUM.")
        print("Resolve these by hand before treating the file as valid Oracle.")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit("usage: postgres_to_oracle.py <source.sql> <target.sql>")
    main(sys.argv[1], sys.argv[2])
