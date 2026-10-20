#!/usr/bin/env python3
"""Reproduce the lexical native-dependency inventory; not a Java semantic analyzer."""

import csv
import json
import re
from collections import Counter, defaultdict
from pathlib import Path


def main():
    output = Path(__file__).resolve().parent
    repo = output.parents[1]
    source = repo / "spring-data-vmware-gemfire/src"
    # Preserve strings and line numbers while removing Java comments. No source has
    # to compile, and referenced native types in imports remain visible.
    tokens = re.compile(
        r'""".*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*.*?\*/',
        re.S,
    )
    native = re.compile(
        r"\b(?:org\.apache\.geode|com\.gemstone\.gemfire|"
        r"com\.vmware\.gemfire\.(?!driver\b)[A-Za-z_$][\w$]*)(?:\.[\w$*]+)*"
    )

    def strip_comments(text):
        return tokens.sub(
            lambda m: re.sub(r"[^\n]", " ", m.group())
            if m.group().startswith(("//", "/*")) else m.group(), text
        )

    totals, native_files, native_lines = Counter(), Counter(), Counter()
    packages = Counter()
    imports = defaultdict(set)
    rows = []
    raw_files = Counter()
    for path in sorted(source.rglob("*.java")):
        scope = path.relative_to(source).parts[0]
        totals[scope] += 1
        raw = path.read_text()
        original = raw.splitlines()
        if native.search(raw):
            raw_files[scope] += 1
        matched = False
        for number, line in enumerate(strip_comments(raw).splitlines(), 1):
            symbols = sorted(set(native.findall(line)))
            if not symbols:
                continue
            matched = True
            native_lines[scope] += 1
            kind = "import" if line.lstrip().startswith("import ") else "code-or-string"
            relative = str(path.relative_to(repo))
            rows.append([scope, relative, number, kind, ";".join(symbols), original[number - 1].strip()])
            if kind == "import":
                for symbol in symbols:
                    imports[(scope, symbol)].add(relative)
        if matched:
            native_files[scope] += 1
            if scope == "main":
                parts = path.relative_to(source / "main/java/org/springframework/data/gemfire").parts
                packages[parts[0] if len(parts) > 1 else "(root)"] += 1

    def write_tsv(name, header, data):
        with (output / name).open("w", newline="") as stream:
            writer = csv.writer(stream, delimiter="\t", lineterminator="\n")
            writer.writerow(header)
            writer.writerows(data)

    write_tsv("native-references.tsv", ["scope", "path", "line", "kind", "symbols", "source"], rows)
    write_tsv("native-imports.tsv", ["scope", "native_symbol", "file_count", "files"],
              [[scope, symbol, len(paths), ";".join(sorted(paths))]
               for (scope, symbol), paths in sorted(imports.items())])

    resource_rows = []
    for path in sorted(source.rglob("*")):
        if not path.is_file() or "resources" not in path.relative_to(source).parts:
            continue
        try:
            content = path.read_text()
        except UnicodeDecodeError:
            continue
        for number, line in enumerate(content.splitlines(), 1):
            if native.search(line):
                resource_rows.append([str(path.relative_to(repo)), number, line.strip()])
    write_tsv("native-resource-references.tsv", ["path", "line", "source"], resource_rows)

    build = (repo / "spring-data-vmware-gemfire/build.gradle.kts").read_text()
    included = {}
    for name, roots in {
        "spiCacheSourceFiles": [source / "main/java"],
        "spiCacheTestFiles": [source / "test/java", source / "integrationTest/java"],
    }.items():
        body = re.search(r"val " + name + r" = listOf\((.*?)\n\)", build, re.S).group(1)
        patterns = re.findall(r'"([^"]+\.java)"', body)
        selected = {p for root in roots for pattern in patterns for p in root.glob(pattern)}
        included[name] = len(selected)

    summary = {
        "java_files": dict(sorted(totals.items())),
        "java_files_with_noncomment_native_references": dict(sorted(native_files.items())),
        "noncomment_native_reference_lines": dict(sorted(native_lines.items())),
        "java_files_with_raw_native_text_including_comments": dict(sorted(raw_files.items())),
        "unique_imported_native_symbols": {
            scope: sum(1 for s, _ in imports if s == scope) for scope in sorted(totals)
        },
        "main_native_files_by_package": dict(sorted(packages.items())),
        "selected_native_free_source_files": included,
        "raw_resource_reference_lines": len(resource_rows),
        "raw_resource_files": len({r[0] for r in resource_rows}),
    }
    (output / "native-reference-summary.json").write_text(json.dumps(summary, indent=2) + "\n")
    print(json.dumps(summary, indent=2))


if __name__ == "__main__":
    main()
