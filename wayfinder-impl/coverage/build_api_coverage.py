#!/usr/bin/env python3
"""Inventory class-file signatures without loading/initializing native classes.

Run after compiling Spring Data main. See README.md for scope and review rules.
Only Python's standard library is required; no native API is executed.
"""

import argparse
import csv
import hashlib
import json
import re
import struct
import zipfile
from collections import Counter, defaultdict, deque
from pathlib import Path

from scope_policy import classify, counts, status_counts

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
VERSION = "10.4.0-build.3056.df98c835"
MAIN = ROOT / "spring-data-vmware-gemfire/src/main/java"
CLASSES = ROOT / "spring-data-vmware-gemfire/build/classes/java/main"
AUDIT = HERE.parent / "audits"
NATIVE = ("org.apache.geode.", "com.gemstone.gemfire.", "com.vmware.gemfire.")
TOKEN = re.compile(r"(?:org\.apache\.geode|com\.gemstone\.gemfire|com\.vmware\.gemfire)(?:\.[\w$]+)*")


def native(name):
    return name.startswith(NATIVE) and not name.startswith("com.vmware.gemfire.driver.")


def types(signature):
    # Includes nested generic arguments, bounds, arrays and throws signatures.
    return set(x.replace("/", ".") for x in re.findall(r"L([\w$/]+)", signature))


def digest(data):
    return hashlib.sha256(data).hexdigest()


def row_id(kind, key):
    return kind + "-" + digest(key.encode())[:16]


class Reader:
    def __init__(self, data):
        self.data, self.pos = data, 0

    def take(self, n):
        result = self.data[self.pos:self.pos + n]
        self.pos += n
        if len(result) != n:
            raise ValueError("Truncated class file")
        return result

    def u1(self):
        return self.take(1)[0]

    def u2(self):
        return struct.unpack(">H", self.take(2))[0]

    def u4(self):
        return struct.unpack(">I", self.take(4))[0]


def parse_class(data):
    r = Reader(data)
    assert r.u4() == 0xCAFEBABE
    r.take(4)
    cp = [None] * r.u2()
    i = 1
    while i < len(cp):
        tag = r.u1()
        if tag == 1:
            cp[i] = (tag, r.take(r.u2()).decode("utf-8", errors="replace"))
        elif tag in (3, 4):
            cp[i] = (tag, r.take(4).hex())
        elif tag in (5, 6):
            cp[i] = (tag, r.take(8).hex())
            i += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[i] = (tag, r.u2())
        elif tag in (9, 10, 11, 12, 17, 18):
            cp[i] = (tag, r.u2(), r.u2())
        elif tag == 15:
            cp[i] = (tag, r.u1(), r.u2())
        else:
            raise ValueError(f"Unknown constant-pool tag {tag}")
        i += 1

    def utf(index):
        return cp[index][1]

    def classname(index):
        return utf(cp[index][1]).replace("/", ".") if index else ""

    def attributes():
        return {utf(r.u2()): r.take(r.u4()) for _ in range(r.u2())}

    def signature(attrs):
        return utf(struct.unpack(">H", attrs["Signature"])[0]) if "Signature" in attrs else ""

    def constant(index):
        value = cp[index]
        if value[0] == 8:
            return utf(value[1])
        if value[0] == 3:
            return struct.unpack(">i", bytes.fromhex(value[1]))[0]
        if value[0] == 5:
            return struct.unpack(">q", bytes.fromhex(value[1]))[0]
        return value[1]

    def annotation_value(reader):
        tag = chr(reader.u1())
        if tag in "BCDFIJSZs":
            return constant(reader.u2())
        if tag == "e":
            return {"enum_type": utf(reader.u2()), "constant": utf(reader.u2())}
        if tag == "c":
            return {"class": utf(reader.u2())}
        if tag == "[":
            return [annotation_value(reader) for _ in range(reader.u2())]
        if tag == "@":
            annotation_type = utf(reader.u2())
            return {"annotation_type": annotation_type,
                    "values": {utf(reader.u2()): annotation_value(reader) for _ in range(reader.u2())}}
        raise ValueError(f"Invalid annotation value tag {tag}")

    def members(kind):
        result = []
        for _ in range(r.u2()):
            access, name, desc = r.u2(), utf(r.u2()), utf(r.u2())
            attrs = attributes()
            ex = Reader(attrs.get("Exceptions", b"\0\0"))
            exceptions = [classname(ex.u2()) for _ in range(ex.u2())]
            result.append(dict(kind=kind, access=access, name=name, descriptor=desc,
                               signature=signature(attrs), exceptions=exceptions,
                               constant_value=constant(struct.unpack(">H", attrs["ConstantValue"])[0])
                               if "ConstantValue" in attrs else None,
                               annotation_default=annotation_value(Reader(attrs["AnnotationDefault"]))
                               if "AnnotationDefault" in attrs else None))
        return result

    access, name, parent = r.u2(), classname(r.u2()), classname(r.u2())
    interfaces = [classname(r.u2()) for _ in range(r.u2())]
    fields, methods = members("field"), members("method")
    attrs = attributes()
    refs = []
    classrefs = set()
    for item in cp[1:]:
        if not item:
            continue
        if item[0] in (9, 10, 11):
            nt = cp[item[2]]
            refs.append((classname(item[1]), utf(nt[1]), utf(nt[2])))
        elif item[0] == 7:
            value = utf(item[1]).replace("/", ".")
            classrefs.update(types(value.replace(".", "/")) if value.startswith("[") else [value])
    source = utf(struct.unpack(">H", attrs["SourceFile"])[0]) if "SourceFile" in attrs else ""
    return dict(name=name, access=access, parent=parent, interfaces=interfaces,
                signature=signature(attrs), members=fields + methods, refs=refs,
                classrefs=sorted(classrefs), source=source)


def read_tsv(path):
    with path.open() as stream:
        return list(csv.DictReader(stream, delimiter="\t"))


def encoded_tsv(columns, rows):
    import io
    stream = io.StringIO(newline="")
    writer = csv.DictWriter(stream, columns, delimiter="\t", lineterminator="\n")
    writer.writeheader()
    writer.writerows(rows)
    return stream.getvalue().encode()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Compare regenerated inventory with checked-in files")
    parser.add_argument("--native-cache", type=Path,
                        default=Path.home() / ".gradle/caches/modules-2/files-2.1/com.vmware.gemfire")
    parser.add_argument("--java-home", type=Path, help="JDK containing jmods/java.base.jmod")
    args = parser.parse_args()
    if args.java_home is None:
        import subprocess
        settings = subprocess.run(["java", "-XshowSettings:properties", "-version"],
                                  capture_output=True, text=True, check=True).stderr
        args.java_home = Path(re.search(r"java.home = (.+)", settings)[1].strip())
    jars = sorted(args.native_cache.glob(f"*/{VERSION}/*/*.jar"))
    assert jars, f"No native {VERSION} jars in {args.native_cache}"
    archive, origins, jar_manifest = {}, {}, []
    for path in jars:
        jar_manifest.append(dict(artifact=path.name, sha256=digest(path.read_bytes())))
        with zipfile.ZipFile(path) as z:
            for entry in z.namelist():
                if entry.endswith(".class") and not entry.startswith("META-INF/"):
                    name = entry[:-6].replace("/", ".")
                    if native(name):
                        data = z.read(entry)
                        assert name not in archive or archive[name] == data, f"Conflicting class {name}"
                        archive[name], origins[name] = data, path.name
    # ResourcePermission inherits a Shiro contract. This is the exact version in
    # the audit's resolved compileClasspath, not an arbitrary cached candidate.
    auxiliary = sorted(args.native_cache.parent.glob("org.apache.shiro/shiro-core/2.2.1/*/shiro-core-2.2.1.jar"))
    assert len(auxiliary) == 1, "Expected resolved shiro-core 2.2.1 artifact"
    for path in auxiliary:
        jar_manifest.append(dict(artifact=path.name, sha256=digest(path.read_bytes())))
        with zipfile.ZipFile(path) as z:
            for entry in z.namelist():
                if entry.endswith(".class"):
                    name = entry[:-6].replace("/", ".")
                    archive[name], origins[name] = z.read(entry), path.name
    # Follow JDK supertypes (Map/ConcurrentMap/Collection/etc.) but never fan out
    # through every unrelated JDK return type.
    jdk = {}
    for path in sorted((args.java_home / "jmods").glob("*.jmod")):
        with zipfile.ZipFile(path) as z:
            for entry in z.namelist():
                if entry.startswith("classes/") and entry.endswith(".class"):
                    jdk[entry[8:-6].replace("/", ".")] = (path, entry)
    cache = {}

    def get(name):
        if name not in cache:
            if name in jdk:
                path, entry = jdk[name]
                with zipfile.ZipFile(path) as z:
                    archive[name], origins[name] = z.read(entry), path.stem
            cache[name] = parse_class(archive[name]) if name in archive else None
        return cache[name]

    def resolve(symbol):
        parts = symbol.split(".")
        for end in range(len(parts), 2, -1):
            for split in range(end, 2, -1):
                candidate = ".".join(parts[:split]) + ("$" + "$".join(parts[split:end]) if split < end else "")
                if candidate in archive:
                    return candidate
        return None

    sources = {}
    for path in sorted(MAIN.rglob("*.java")):
        relative = str(path.relative_to(ROOT))
        sources[relative] = digest(path.read_bytes())
        compiled = CLASSES / path.relative_to(MAIN).with_suffix(".class")
        if path.name != "package-info.java":
            assert compiled.exists(), f"Compile main first: {relative}"
            assert compiled.stat().st_mtime_ns >= path.stat().st_mtime_ns, f"Recompile changed source: {relative}"

    evidence = defaultdict(set)
    direct = defaultdict(set)
    exposure = []
    annotation_defaults = []
    for path in sorted(CLASSES.rglob("*.class")):
        cls = parse_class(path.read_bytes())
        src = MAIN / Path(cls["name"].replace(".", "/")).parent / cls["source"]
        if not src.exists():
            raise AssertionError(f"Stale class without source: {path}")
        relative = str(src.relative_to(ROOT))
        for member in cls["members"]:
            if cls["access"] & 0x2000 and member["annotation_default"] is not None:
                annotation_defaults.append(dict(spring_type=cls["name"], member=member["name"],
                    descriptor=member["descriptor"], value=json.dumps(member["annotation_default"], sort_keys=True),
                    source=relative))
        for name in cls["classrefs"]:
            if native(name):
                evidence[name].add(relative)
        for owner, member, desc in cls["refs"]:
            if native(owner):
                direct[owner].add((member, desc))
                evidence[owner].add(relative + "#" + member + desc)
            for name in types(desc):
                if native(name):
                    evidence[name].add(relative + "#bytecode-descriptor")
        signatures = [("<type>", cls["signature"], [cls["parent"]] + cls["interfaces"], None)]
        signatures += [(m["name"] + m["descriptor"], m["signature"] or m["descriptor"], m["exceptions"], m["annotation_default"])
                       for m in cls["members"] if m["access"] & 5]
        for member, sig, extra, default in signatures:
            for name in sorted(types(sig) | set(extra)):
                if native(name):
                    evidence[name].add(relative + "#" + member)
                    exposure.append(dict(spring_type=cls["name"], spring_member=member,
                                         signature=sig, native_type=name, source=relative,
                                         annotation_default=json.dumps(default, sort_keys=True) if default is not None else ""))

    lexical = []
    for r in read_tsv(AUDIT / "native-references.tsv"):
        if r["scope"] != "main":
            continue
        for symbol in r["symbols"].split(";"):
            name = resolve(symbol)
            ref = r["path"] + ":" + r["line"]
            if name:
                evidence[name].add(ref)
            lexical.append(dict(source=ref, symbol=symbol, native_type=name or "",
                                kind=r["kind"], text=r["source"]))
    for r in read_tsv(AUDIT / "native-resource-references.tsv"):
        for symbol in sorted(set(TOKEN.findall(r["source"]))):
            name = resolve(symbol)
            ref = r["path"] + ":" + r["line"]
            if name and "/main/" in r["path"]:
                evidence[name].add(ref)
            lexical.append(dict(source=ref, symbol=symbol, native_type=name or "",
                                kind="resource", text=r["source"]))

    policy = json.loads((HERE / "coverage-policy.json").read_text())
    scope_path = HERE / policy["scope_policy"]
    scope_policy = json.loads(scope_path.read_text())
    assert scope_policy["native_version"] == VERSION, "Review scope when changing native version"
    exposed_internals = {r["native_type"] for r in exposure if ".internal." in r["native_type"]}
    assert exposed_internals <= set(policy["public_internal_payloads"]), "Classify newly exposed internal payloads explicitly"
    baseline = json.loads((HERE / "native-api-baseline.json").read_text())
    assert baseline["native_version"] == VERSION, "Review baseline when changing native version"
    for row in baseline["direct_members"]:
        direct[row["native_type"]].add((row["member"], row["descriptor"]))
    issues = {int(p.name.split("-")[0]): str(p.relative_to(ROOT))
              for p in (ROOT / "wayfinder-issues").glob("[0-9]*.md")}

    def owner(name):
        for rule in policy["ownership"]:
            if re.search(rule["pattern"], name):
                return rule["ticket"]
        raise AssertionError(f"Unowned native type: {name}")

    def ticket(number):
        assert number in issues, f"Owner ticket missing: {number}"
        return issues[number]

    def member_key(name, m):
        return name + "#" + m["name"] + m["descriptor"]

    # Public callback/result specializations may be supplied by applications or
    # returned dynamically, without appearing in a Spring bytecode invocation.
    subtypes = defaultdict(set)
    for name in sorted(archive):
        if native(name) and ".internal." not in name:
            cls = get(name)
            if cls["access"] & 1:
                for parent in [cls["parent"]] + cls["interfaces"]:
                    subtypes[parent].add(name)
    queue = deque((name, "spring-root", name) for name in sorted(evidence))
    queue.extend((name, "frozen-baseline-root", name) for name in baseline["roots"])
    queue.extend((r["type"], r["reason"], r["origin"]) for r in policy["semantic_roots"])
    visited, edges, rows = set(), set(), {}
    while queue:
        name, reason, origin = queue.popleft()
        edges.add((origin, name, reason))
        if name in visited:
            continue
        visited.add(name)
        cls = get(name)
        if not cls:
            raise AssertionError(f"Unresolved signature type {name}; supply exact matching artifact")
        if not native(name):
            continue
        for child in sorted(subtypes[name]):
            if any(re.search(pattern, name) for pattern in policy["subtype_families"]):
                queue.append((child, "public-callback-or-result-specialization", name + "#<type>"))
        own = owner(name)
        internal = ".internal." in name
        boundary_only = internal and name not in policy["public_internal_payloads"]
        if boundary_only:
            assert name in policy["internal_boundaries"], f"Explain internal boundary: {name}"
        type_key = name + "#<type>"
        rows[type_key] = dict(id=row_id("API", type_key), native_type=name, member="<type>",
                             descriptor="", generic_signature=cls["signature"], exceptions="",
                             constant_value="",
                             reachable_types=";".join(sorted(types(cls["signature"]) | set(cls["interfaces"]) | {cls["parent"]} - {""})),
                             artifact=origins[name], owner=ticket(own), status="missing",
                             spi="", native_impl="pending", mock="pending-explicit-local-or-unsupported",
                             tests="pending-shared-and-native", remaining="Required contract not implemented",
                             requirement="internal-boundary" if boundary_only else "native-contract")
        # Internal implementation classes are not an instruction to copy private
        # engine APIs. Retain every member directly referenced by Spring; public
        # native contracts reached from those members continue the traversal.
        selected = [m for m in cls["members"] if
                    ((m["name"], m["descriptor"]) in direct[name] if boundary_only else m["access"] & 5)
                    and m["name"] != "<clinit>" and not m["access"] & 0x1000]
        # A call's JVM receiver may inherit its implementation, including a
        # static factory/constant or a method on an internal superclass.
        found = {(m["name"], m["descriptor"]) for m in selected}
        pending = direct[name] - found
        parents, seen = deque([cls["parent"]] + cls["interfaces"]), set()
        while pending and parents:
            parent = parents.popleft()
            if not parent or parent in seen:
                continue
            seen.add(parent)
            pc = get(parent)
            assert pc, f"Cannot resolve inherited call on {name} through {parent}"
            parents.extend([pc["parent"]] + pc["interfaces"])
            for m in pc["members"]:
                pair = (m["name"], m["descriptor"])
                if pair in pending:
                    selected.append(m)
                    pending.remove(pair)
        assert not pending, f"Unresolved direct native members: {name}: {pending}"
        for m in selected:
            key = member_key(name, m)
            reached = types(m["descriptor"]) | types(m["signature"]) | set(m["exceptions"])
            rows[key] = dict(rows[type_key], id=row_id("API", key), member=m["name"],
                             descriptor=m["descriptor"], generic_signature=m["signature"],
                             constant_value=json.dumps(m["constant_value"]) if m["constant_value"] is not None else "",
                             exceptions=";".join(m["exceptions"]), reachable_types=";".join(sorted(reached)))
            for target in sorted(reached):
                if native(target):
                    queue.append((target, "member-signature", key))
        if not boundary_only:
            for target in sorted(types(cls["signature"]) | set(cls["interfaces"]) | {cls["parent"]} - {""}):
                if native(target):
                    queue.append((target, "type-hierarchy-or-bound", type_key))
            # Capture inherited JDK instance contracts under the native receiver,
            # rather than treating Map's only-Object descriptors as native-free.
            parents = deque([cls["parent"]] + cls["interfaces"])
            seen = set()
            declared = {(m["name"], m["descriptor"]) for m in selected}
            while parents:
                parent = parents.popleft()
                if not parent or parent in seen or parent in ("java.lang.Object", "java.lang.Enum", "java.lang.Throwable"):
                    continue
                seen.add(parent)
                pc = get(parent)
                if pc is None:
                    raise AssertionError(f"Missing superclass/interface {parent} of {name}")
                parents.extend([pc["parent"]] + pc["interfaces"])
                for m in pc["members"]:
                    if not (m["access"] & 5) or m["access"] & (8 | 0x1000) or m["name"].startswith("<"):
                        continue
                    pair = (m["name"], m["descriptor"])
                    if pair in declared:
                        continue
                    declared.add(pair)
                    key = member_key(name, m)
                    reached = types(m["descriptor"]) | types(m["signature"]) | set(m["exceptions"])
                    rows[key] = dict(rows[type_key], id=row_id("API", key), member=m["name"],
                                     descriptor=m["descriptor"], generic_signature=m["signature"],
                                     exceptions=";".join(m["exceptions"]), reachable_types=";".join(sorted(reached)),
                                     requirement="inherited-from:" + parent)
                    for target in sorted(reached):
                        if native(target):
                            queue.append((target, "inherited-member-signature", key))
    for key, override in policy["members"].items():
        assert key in rows, f"Stale mapping: {key}"
        rows[key].update(override)
    assert set(baseline["required_keys"]) <= rows.keys(), "Required API rows disappeared; do not shrink migration coverage"
    for key, row in rows.items():
        row["spring_evidence"] = ("spring-evidence.tsv#" + row["native_type"] if row["native_type"] in evidence
                                  else ("native-api-baseline.json#" if row["native_type"] in baseline["roots"]
                                        else "api-edges.tsv#") + row["native_type"])
        row["required_behavior"] = policy["internal_boundaries"].get(
            row["native_type"], policy["behavior"][str(owner(row["native_type"]))])
        row.setdefault("consumer_owner", ticket(policy["consumer_owner"][str(owner(row["native_type"]))]))
        assert row["status"] in {"missing", "partial", "implemented", "removed"}
        if row["status"] == "implemented":
            assert row["spi"] and row["native_impl"] != "pending" and not row["tests"].startswith("pending")
    scope_fields = classify(list(rows.values()), scope_policy, ROOT)
    for row in rows.values():
        row.update(scope_fields[row["id"]])
    # Never silently lose lexical references, including an old reflective name
    # absent from the selected runtime or documentation/tooling declarations.
    source_rows = []
    for item in lexical:
        key = item["source"] + "#" + item["symbol"]
        name = item["native_type"]
        is_test = "/test/" in item["source"] or "/integrationTest/" in item["source"]
        if name and name + "#<type>" in rows:
            api = rows[name + "#<type>"]["id"]
            disposition = "test-resource-contract" if is_test else "required-native-reference"
            own = 37 if is_test else owner(name)
        elif is_test and name:
            api, disposition, own = "", "test-fixture-native-type", 37
        else:
            matched = [r for r in policy["reference_explanations"] if re.fullmatch(r["symbol"], item["symbol"])
                       and re.search(r.get("source", ".*"), item["source"])]
            assert len(matched) == 1, f"Unexplained lexical reference: {item}"
            explanation = matched[0]
            api, disposition, own = "", explanation["explanation"], explanation["ticket"]
        scope = scope_fields[api] if api else dict(scope="unresolved", scope_owner=scope_policy["unresolved"]["owner"])
        source_owner = ticket(own)
        if api and not is_test:
            source_owner = rows[name + "#<type>"]["owner"]
            disposition = {"required-client": "required-client-reference",
                           "excluded-server": "excluded-server-reference",
                           "unresolved": "unresolved-client-scope-reference"}[scope["scope"]]
        source_rows.append(dict(id=row_id("REF", key), **item, api_row=api, disposition=disposition,
                                owner=source_owner, scope=scope["scope"], scope_owner=scope["scope_owner"]))
    # Scope and evidence fingerprints make source/native drift reviewable.
    products = {
        "api-coverage.tsv": encoded_tsv(list(next(iter(rows.values()))), [rows[k] for k in sorted(rows)]),
        "api-edges.tsv": encoded_tsv(["origin", "native_type", "reason"],
            [dict(origin=a, native_type=b, reason=c) for a, b, c in sorted(edges)]),
        "spring-exposures.tsv": encoded_tsv(["spring_type", "spring_member", "signature", "native_type", "source", "annotation_default"],
            sorted(exposure, key=lambda r: tuple(r.values()))),
        "spring-annotation-defaults.tsv": encoded_tsv(["spring_type", "member", "descriptor", "value", "source"],
            sorted(annotation_defaults, key=lambda r: tuple(r.values()))),
        "source-coverage.tsv": encoded_tsv(
            ["id", "source", "symbol", "native_type", "kind", "text", "api_row", "disposition", "owner", "scope", "scope_owner"], source_rows),
        "spring-evidence.tsv": encoded_tsv(["native_type", "source_or_signature"],
            [dict(native_type=n, source_or_signature=e) for n in sorted(evidence) for e in sorted(evidence[n])]),
        "direct-members.tsv": encoded_tsv(["native_type", "member", "descriptor", "api_row"],
            [dict(native_type=n, member=m, descriptor=d, api_row=rows[n + "#" + m + d]["id"])
             for n in sorted(direct) for m, d in sorted(direct[n])]),
        "existing-spi-coverage.tsv": encoded_tsv(
            ["spi", "kind", "native_rows", "native_impl", "tests", "status", "remaining", "owner", "scopes"],
            [dict(spi=r["spi"], kind=r["kind"], native_rows=";".join(rows[k]["id"] for k in r["native_keys"]),
                  native_impl=r["native_impl"], tests=r["tests"], status=r["status"], remaining=r["remaining"],
                  owner=ticket(r["owner"]), scopes=";".join(sorted({rows[k]["scope"] for k in r["native_keys"]})))
             for r in policy["existing_spi"]]),
    }
    evidence_hashes = {str(p.relative_to(ROOT)): digest(p.read_bytes())
                       for module in ("spring-data-gemfire-driver-spi", "spring-data-gemfire-driver-10.4",
                                      "spring-data-gemfire-driver-mock", "spring-data-gemfire-driver-contract-tests")
                       for p in sorted((ROOT / module / "src").rglob("*.java"))}
    evidence_hashes.update({str(p.relative_to(ROOT)): digest(p.read_bytes()) for p in
                           [HERE / "coverage-policy.json", HERE / "build_api_coverage.py",
                            scope_path, HERE / "scope_policy.py", HERE / "validate_coverage.py",
                            HERE.parent / "client-only-scope.md",
                            AUDIT / "native-references.tsv", AUDIT / "native-resource-references.tsv"]})
    for decision in [scope_policy["unresolved"]] + scope_policy["decisions"]:
        path = ROOT / decision["evidence"].split("#", 1)[0]
        evidence_hashes[str(path.relative_to(ROOT))] = digest(path.read_bytes())
    evidence_hashes[str((HERE / "native-api-baseline.json").relative_to(ROOT))] = digest((HERE / "native-api-baseline.json").read_bytes())
    manifest = dict(native_version=VERSION, jars=jar_manifest, source_sha256=sources,
                    evidence_sha256=evidence_hashes,
                    native_types=len(visited), api_rows=len(rows), edges=len(edges),
                    spring_exposures=len(exposure), source_references=len(source_rows),
                    spring_annotation_defaults=len(annotation_defaults),
                    statuses=dict(sorted(Counter(r["status"] for r in rows.values()).items())),
                    scopes=counts(list(rows.values())), scope_statuses=status_counts(list(rows.values())),
                    source_scopes=counts(source_rows),
                    owner_counts=dict(sorted(Counter(r["owner"] for r in rows.values()).items())),
                    output_sha256={k: digest(v) for k, v in products.items()})
    products["coverage-manifest.json"] = (json.dumps(manifest, indent=2, sort_keys=True) + "\n").encode()
    for name, data in products.items():
        path = HERE / name
        if args.check:
            assert path.exists() and path.read_bytes() == data, f"Coverage drift: regenerate/review {path}"
        else:
            path.write_bytes(data)
    for name, count in [("Native types", len(visited)), ("API rows", len(rows)), ("Spring exposures", len(exposure))]:
        print(f"{name}: {count}")
    print("API scope: " + ", ".join(f"{scope}={count}" for scope, count in manifest["scopes"].items()))
    print("Coverage check passed" if args.check else "Coverage generated")


if __name__ == "__main__":
    main()
