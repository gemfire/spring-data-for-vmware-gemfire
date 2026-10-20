#!/usr/bin/env python3
"""Whole-source and resolved-classpath migration gate. Standard library + JDK only."""
import argparse
from collections import Counter
import json
from pathlib import Path
import re
import subprocess
import tempfile
import xml.parsers.expat
import zipfile

SPRING = "spring-data-vmware-gemfire"
PREFIX = "spring-data-gemfire-driver-"
NATIVE = re.compile(r"(?:org[./]apache[./]geode|com[./]gemstone[./]gemfire|com[./]vmware[./]gemfire[./](?!driver\b)[\w$]+)(?:[./][\w$*]+)*")
STATE_RULES = {"static-state", "singleton", "static-service", "static-reset"}
INFO_RULES = {"documentation", "logger-text", "native-runtime-allowed"}


def finding(rule, path, line, detail):
    detail = re.sub(r"\s+", " ", detail).strip()
    return dict(rule=rule, path=path, line=line, detail=detail)


def identity(row):
    return row["rule"], row["path"], row["detail"]


def native_module(module):
    return bool(re.fullmatch(PREFIX + r"\d.*", module))


def native_artifact(component):
    return component.get("group", "").startswith("org.apache.geode") or (
        component.get("group") == "com.vmware.gemfire" and component.get("name", "").startswith("gemfire-"))


def direction_allowed(module, target, source, runtime):
    """Only runtime implementations can enter Spring; test kits can enter provider test configurations."""
    if target == module:
        return True
    if module == SPRING:
        return target in {PREFIX + "spi", PREFIX + "core"} or (
            runtime and source != "main" and (native_module(target) if "integration" in source.lower() else target == PREFIX + "mock"))
    allowed = {PREFIX + "spi"}
    if module == PREFIX + "spi":
        allowed = set()
    if module in {PREFIX + "core", PREFIX + "contract-tests"}:
        allowed.add(PREFIX + "core")
    if source != "main":
        allowed.update({PREFIX + "core", PREFIX + "contract-tests"})
    return target in allowed


def classpath_findings(model):
    rows = []
    scanned = {}
    for module in model:
        name = module["module"]
        for source in module["sourceSets"]:
            for cp in source["classpaths"]:
                location = name + ":" + cp["name"]
                runtime = cp["name"].lower().endswith("runtimeclasspath")
                allow_native = native_module(name) or name == SPRING and runtime and "integration" in source["name"].lower()
                native_rule = "native-runtime-allowed" if allow_native else "native-artifact"
                for component in cp["components"]:
                    if native_artifact(component):
                        rows.append(finding(native_rule, location, 0, component["group"] + ":" + component["name"]))
                    target = component.get("project") or component.get("name", "")
                    if (target.startswith(PREFIX) or target in {SPRING, "spring-test-vmware-gemfire"}) and not direction_allowed(name, target, source["name"], runtime):
                        rows.append(finding("dependency-direction", location, 0, target))
                for file in cp["files"]:
                    path = Path(file)
                    if not path.exists():
                        raise ValueError(f"Resolved classpath entry is missing: {location}: {path}")
                    if file not in scanned:
                        if path.is_dir():
                            names = [str(p.relative_to(path)) for p in path.rglob("*.class")]
                        elif zipfile.is_zipfile(path):
                            with zipfile.ZipFile(path) as jar:
                                names = jar.namelist()
                        else:
                            raise ValueError(f"Cannot inspect classpath entry: {path}")
                        scanned[file] = any(n.endswith(".class") and NATIVE.search(n) for n in names)
                    if scanned[file]:
                        # Keep artifact versions in the resolved report, not in the shrinking identity baseline.
                        components = [c for c in cp["components"] if c.get("version") and path.name.startswith(c.get("name", "") + "-" + c["version"])]
                        label = components[0]["group"] + ":" + components[0]["name"] if len(components) == 1 else path.name
                        rows.append(finding("native-runtime-allowed" if allow_native else "native-classpath-content", location, 0, label))
    return rows


def resource_findings(path, root):
    relative = str(path.relative_to(root))
    rows = []
    if NATIVE.search(relative):
        rows.append(finding("native-resource", relative, 1, "class-bearing resource path: " + path.name))
    if zipfile.is_zipfile(path):
        with zipfile.ZipFile(path) as archive:
            for name in archive.namelist():
                if NATIVE.search(name):
                    rows.append(finding("native-resource", relative, 1, "archive entry: " + name))
        return rows
    try:
        content = path.read_text()
    except UnicodeDecodeError:
        return rows
    if path.suffix not in {".xml", ".xsd"} and not NATIVE.search(content):
        return rows
    if path.suffix in {".xml", ".xsd"}:
        parser = xml.parsers.expat.ParserCreate()
        stack = []
        def start(name, attributes):
            stack.append(name.split(":")[-1])
            for key, value in attributes.items():
                if NATIVE.search(value):
                    rule = "documentation" if "documentation" in stack else "logger-text" if stack[-1] == "logger" else "native-resource"
                    rows.append(finding(rule, relative, parser.CurrentLineNumber, name + "@" + key + "=" + value))
        def data(value):
            if NATIVE.search(value):
                rows.append(finding("documentation" if "documentation" in stack else "native-resource",
                                    relative, parser.CurrentLineNumber, "/".join(stack) + "=" + value))
        parser.StartElementHandler = start
        parser.EndElementHandler = lambda name: stack.pop()
        parser.CharacterDataHandler = data
        parser.CommentHandler = lambda value: rows.append(finding("documentation", relative, parser.CurrentLineNumber, value)) if NATIVE.search(value) else None
        parser.ExternalEntityRefHandler = lambda *args: 1  # never fetch DTDs or network resources
        parser.Parse(content, True)
    else:
        for number, line in enumerate(content.splitlines(), 1):
            if NATIVE.search(line):
                info = line.lstrip().startswith(("#", "!")) or path.suffix in {".md", ".html"}
                logger = bool(re.match(r"\s*(?:log4j\.)?logger[.=]", line))
                rows.append(finding("documentation" if info else "logger-text" if logger else "native-resource", relative, number, line))
    return rows


def source_findings(root, model, java, classes):
    java_files, resources = set(), set()
    for module in model:
        # Include the entire physical tree even if Gradle excludes files from compilation.
        src = root / module["module"] / "src"
        java_files.update(src.rglob("*.java"))
        resources.update(p for p in src.rglob("*") if p.is_file() and "resources" in p.relative_to(src).parts)
        for source in module["sourceSets"]:
            for directory in source["java"]:
                java_files.update((root / directory).rglob("*.java"))
            for directory in source["resources"]:
                resources.update(p for p in (root / directory).rglob("*") if p.is_file())
    with tempfile.TemporaryDirectory(prefix="gemfire-boundary-") as tmp:
        manifest = Path(tmp) / "sources.txt"
        manifest.write_text("\n".join(sorted(str(p.relative_to(root)) for p in java_files)) + "\n")
        result = subprocess.run([str(java), "-cp", str(classes), "SourceFacts", str(root), str(manifest)],
                                check=True, capture_output=True, text=True)
    rows = []
    for line in result.stdout.splitlines():
        rule, path, number, detail = line.split("\t", 3)
        rows.append(finding(rule, path, int(number), detail))
    for path in sorted(resources):
        if not native_module(path.relative_to(root).parts[0]):
            rows.extend(resource_findings(path, root))
    # Java comments are informational, never violations. javac already excludes them from code facts.
    token = re.compile(r'""".*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*.*?\*/', re.S)
    for path in sorted(java_files):
        content = path.read_text()
        for match in token.finditer(content):
            if match.group().startswith(("//", "/*")) and NATIVE.search(match.group()):
                rows.append(finding("documentation", str(path.relative_to(root)), content.count("\n", 0, match.start()) + 1, match.group()))
    source_counts = Counter("/".join(p.relative_to(root).parts[:3]) for p in java_files)
    return rows, dict(java_files=len(java_files), resource_files=len(resources), sources_by_module_and_scope=dict(sorted(source_counts.items())))


def compare(rows, baseline, strict=False):
    actual = Counter(identity(r) for r in rows if r["rule"] not in INFO_RULES)
    expected = Counter({identity(r): r["count"] for r in baseline["violations"]})
    new = actual - expected
    stale = expected - actual
    # Legacy Spring statics are permitted by §5.7, but new or changed ones are rejected.
    strict_rows = Counter({key: count for key, count in actual.items()
                           if not (key[0] in STATE_RULES and key[1].startswith(SPRING + "/") and key in expected)})
    return new, stale, strict_rows if strict else Counter()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, required=True)
    parser.add_argument("--java", type=Path, required=True)
    parser.add_argument("--classes", type=Path, required=True)
    parser.add_argument("--classpaths", type=Path, required=True)
    parser.add_argument("--mode", choices=["regression", "strict", "report", "prune"], default="regression")
    args = parser.parse_args()
    root = args.root.resolve()
    model = json.loads(args.classpaths.read_text())
    expected_modules = {p.name for p in root.iterdir() if p.is_dir() and (p.name == SPRING or p.name.startswith(PREFIX))}
    if {m["module"] for m in model} != expected_modules:
        raise ValueError("Classpath report does not include every Spring Data/driver module")
    for module in model:
        if not {"main", "test"}.issubset({s["name"] for s in module["sourceSets"]}):
            raise ValueError("Missing main/test classpaths: " + module["module"])
        for source in module["sourceSets"]:
            if len(source["classpaths"]) != 2:
                raise ValueError("Expected both compile and runtime classpaths: " + module["module"] + ":" + source["name"])
    rows, counts = source_findings(root, model, args.java, args.classes)
    rows += classpath_findings(model)
    rows.sort(key=lambda r: (r["path"], r["line"], r["rule"], r["detail"]))
    output = root / "build/reports/migration-boundary"
    output.mkdir(parents=True, exist_ok=True)
    (output / "findings.json").write_text(json.dumps(rows, indent=2) + "\n")
    summary = dict(counts, violations=dict(sorted(Counter(r["rule"] for r in rows if r["rule"] not in INFO_RULES).items())),
                   informational=dict(sorted(Counter(r["rule"] for r in rows if r["rule"] in INFO_RULES).items())))
    summary["spring_native_java_files_by_scope"] = dict(sorted(Counter(
        path.split("/")[2] for path in {r["path"] for r in rows if r["path"].startswith(SPRING + "/src/")
        and r["rule"] in {"native-source", "native-string", "native-wildcard-use"}}).items()))
    (output / "summary.json").write_text(json.dumps(summary, indent=2) + "\n")
    print(json.dumps(summary, indent=2))
    if args.mode == "report":
        print("Report only; no compliance or regression verdict.")
        return
    baseline_path = root / "build-tools/migration-boundary/violations-baseline.json"
    baseline = json.loads(baseline_path.read_text())
    if len({identity(r) for r in baseline["violations"]}) != len(baseline["violations"]):
        raise ValueError("Duplicate baseline identities")
    for row in baseline["violations"]:
        if not row["path"].startswith(SPRING) or row["count"] < 1 or not row.get("owner") or row["rule"].startswith("migrated-"):
            raise ValueError("Only owned legacy Spring violations may be baselined: " + str(row))
        if not (root / row["owner"]).is_file():
            raise ValueError("Baseline owner does not exist: " + row["owner"])
    new, stale, strict = compare(rows, baseline, args.mode == "strict")
    if args.mode == "prune":
        if new:
            raise ValueError("Refusing to prune while new violations exist; fix regressions first")
        actual = Counter(identity(r) for r in rows if r["rule"] not in INFO_RULES)
        baseline["violations"] = [dict(r, count=actual[identity(r)]) for r in baseline["violations"] if actual[identity(r)]]
        baseline_path.write_text(json.dumps(baseline, indent=2) + "\n")
        print("Pruned resolved entries only. Review the diff with the owning migration ticket.")
        return
    lookup = {identity(r): r for r in rows}
    for label, entries in [("NEW", new), ("STALE baseline — prune resolved entries", stale), ("STRICT violation", strict)]:
        for key, count in list(entries.items())[:25]:
            row = lookup.get(key, dict(line=0))
            print(f"{label}: {key[1]}:{row['line']} [{key[0]}] x{count}: {key[2][:220]}")
        if len(entries) > 25:
            print(f"  ... {len(entries) - 25} more identities; see {output / 'findings.json'}")
    if new or stale or strict:
        raise SystemExit("Boundary check FAILED. Fix new violations; prune resolved baseline entries. Full locations: " + str(output / "findings.json"))
    print("Strict boundary PASS: zero prohibited violations." if args.mode == "strict" else
          "Migration regression PASS against transitional baseline. NOT native-free compliance.")


if __name__ == "__main__":
    main()
