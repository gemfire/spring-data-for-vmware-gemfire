#!/usr/bin/env python3
"""Validate the saved inventory; optionally cross-check representative APIs with javap."""

import argparse
import csv
from collections import Counter
import hashlib
import json
import os
import re
import subprocess
from pathlib import Path

from scope_policy import SCOPE_COLUMNS, classify, counts, status_counts, retired

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]


def read(name):
    with (HERE / name).open() as stream:
        return list(csv.DictReader(stream, delimiter="\t"))


def check_rows(rows):
    ids = [r["id"] for r in rows]
    assert len(ids) == len(set(ids)), "Duplicate row IDs"
    by_key = {r["native_type"] + "#" + r["member"] + r["descriptor"]: r for r in rows}
    baseline = json.loads((HERE / "native-api-baseline.json").read_text())
    assert set(baseline["required_keys"]) <= by_key.keys(), "Frozen API requirement disappeared"
    for key, row in by_key.items():
        assert row["id"] == "API-" + hashlib.sha256(key.encode()).hexdigest()[:16], key
        assert row["status"] in {"missing", "partial", "implemented", "removed"}, key
        for owner in [row["owner"], row["consumer_owner"]]:
            assert owner.startswith("wayfinder-issues/") and (ROOT / owner).is_file(), owner
        assert all(row[x] for x in ["native_impl", "mock", "tests", "required_behavior", "spring_evidence"]), key
    # Independent sentinels for the original blind spots, including inherited
    # Java-only signatures that cannot be found by searching for native imports.
    required = [
        "org.apache.geode.cache.CacheListener#afterCreate(Lorg/apache/geode/cache/EntryEvent;)V",
        "org.apache.geode.cache.RegionEvent#getDistributedMember()Lorg/apache/geode/distributed/DistributedMember;",
        "org.apache.geode.cache.EntryEvent#getSerializedNewValue()Lorg/apache/geode/cache/SerializedCacheValue;",
        "org.apache.geode.cache.SerializedCacheValue#getSerializedValue()[B",
        "org.apache.geode.cache.CacheListener#initialize(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V",
        "org.apache.geode.cache.RegionMembershipListener#initialMembers(Lorg/apache/geode/cache/Region;[Lorg/apache/geode/distributed/DistributedMember;)V",
        "org.apache.geode.cache.query.CqStatusListener#onCqConnected()V",
        "org.apache.geode.cache.client.SocketFactory#createSocket()Ljava/net/Socket;",
        "org.apache.geode.cache.Region#compute(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;",
        "org.apache.geode.cache.server.ServerLoadProbe#getLoad(Lorg/apache/geode/cache/server/ServerMetrics;)Lorg/apache/geode/cache/server/ServerLoad;",
        "org.apache.geode.cache.asyncqueue.AsyncEventListener#processEvents(Ljava/util/List;)Z",
        "org.apache.geode.cache.query.Struct#getFieldValues()[Ljava/lang/Object;",
    ]
    assert set(required) <= by_key.keys(), "Missing required callback, payload or inherited member"
    return by_key


def check_scopes(rows):
    policy = json.loads((HERE / "coverage-policy.json").read_text())
    scope_policy = json.loads((HERE / policy["scope_policy"]).read_text())
    baseline = json.loads((HERE / "native-api-baseline.json").read_text())
    assert scope_policy["native_version"] == baseline["native_version"], "Scope native-version mismatch"
    expected = classify(rows, scope_policy, ROOT)
    for row in rows:
        assert {key: row.get(key) for key in SCOPE_COLUMNS} == expected[row["id"]], (
            f"Scope differs from reviewed policy: {row['id']}")
    return scope_policy


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--javap", action="store_true", help="Independently verify representative native declarations")
    args = parser.parse_args()
    manifest = json.loads((HERE / "coverage-manifest.json").read_text())
    for category in ["source_sha256", "evidence_sha256"]:
        for path, expected in manifest[category].items():
            assert hashlib.sha256((ROOT / path).read_bytes()).hexdigest() == expected, f"Stale evidence: {path}"
    for path, expected in manifest["output_sha256"].items():
        assert hashlib.sha256((HERE / path).read_bytes()).hexdigest() == expected, f"Changed inventory: {path}"
    rows = read("api-coverage.tsv")
    by_key = check_rows(rows)
    scope_policy = check_scopes(rows)
    assert manifest["scopes"] == counts(rows), "API scope counts differ"
    assert manifest["scope_statuses"] == status_counts(rows), "Scope/status counts differ"
    ids = {r["id"] for r in rows}
    by_id = {r["id"]: r for r in rows}
    for row in read("direct-members.tsv"):
        key = row["native_type"] + "#" + row["member"] + row["descriptor"]
        assert row["api_row"] == by_key[key]["id"], key
    source_rows = read("source-coverage.tsv")
    assert manifest["source_scopes"] == counts(source_rows), "Source scope counts differ"
    assert len({r["id"] for r in source_rows}) == len(source_rows)
    for row in source_rows:
        path, line = row["source"].rsplit(":", 1)
        assert (ROOT / path).read_text().splitlines()[int(line) - 1].strip() == row["text"], row["source"]
        assert (ROOT / row["owner"]).is_file(), row["owner"]
        assert row["api_row"] in ids or row["disposition"], row
        if row["api_row"]:
            api = by_id[row["api_row"]]
            assert row["scope"] == api["scope"] and row["scope_owner"] == api["scope_owner"], row
        else:
            assert row["scope"] == "unresolved", "Unmapped source cannot be excluded"
            assert row["scope_owner"] == scope_policy["unresolved"]["owner"], row
        assert (ROOT / row["scope_owner"]).is_file() and not retired(row["scope_owner"]), row
        if row["scope"] != "excluded-server":
            assert not retired(row["owner"]), "Retained source needs an active owner"
        key = row["source"] + "#" + row["symbol"]
        assert row["id"] == "REF-" + hashlib.sha256(key.encode()).hexdigest()[:16], key
    with (HERE.parent / "audits/native-imports.tsv").open() as stream:
        imports = {r["native_symbol"] for r in csv.DictReader(stream, delimiter="\t") if r["scope"] == "main"}
    assert imports == {r["symbol"] for r in source_rows if r["kind"] == "import"}
    for row in read("spring-evidence.tsv"):
        path = row["source_or_signature"].split("#", 1)[0].split(":", 1)[0]
        assert (ROOT / path).is_file(), path
    for edge in read("api-edges.tsv"):
        assert edge["native_type"] + "#<type>" in by_key, edge
        assert edge["origin"].split("#")[0] + "#<type>" in by_key, edge
    existing = read("existing-spi-coverage.tsv")
    declared_defaults = Counter()
    for path in (ROOT / "spring-data-gemfire-driver-spi/src/main/java").rglob("*.java"):
        for method in re.findall(r"\bdefault\s+(?:<[^>]+>\s+)?[\w<>, ?\[\].]+\s+(\w+)\s*\(", path.read_text()):
            declared_defaults[path.stem + "#" + method] += 1
    mapped_defaults = Counter(r["spi"].split("(", 1)[0] for r in existing if r["kind"] == "default")
    assert mapped_defaults == declared_defaults, "Every current SPI default needs a native coverage mapping"
    for row in existing:
        assert set(row["native_rows"].split(";")) <= ids
        assert row["scopes"] == ";".join(sorted({by_id[i]["scope"] for i in row["native_rows"].split(";")})), row
        assert (ROOT / row["native_impl"]).is_file() and (ROOT / row["tests"]).is_file()
    # Negative controls: the validator must fail on the original missing method
    # even when all current SPI source/default contracts remain untouched.
    for mutated in [rows + [rows[0]],
                    [r for r in rows if not (r["native_type"] == "org.apache.geode.cache.CacheListener"
                                            and r["member"] == "afterCreate")]]:
        try:
            check_rows(mutated)
        except AssertionError:
            pass
        else:
            raise AssertionError("Negative control unexpectedly passed")
    if args.javap:
        cache = Path.home() / ".gradle/caches/modules-2/files-2.1"
        jars = sorted((cache / "com.vmware.gemfire").glob(f"*/{manifest['native_version']}/*/*.jar"))
        names = [
            "org.apache.geode.cache.CacheListener", "org.apache.geode.cache.Declarable",
            "org.apache.geode.cache.CacheEvent", "org.apache.geode.cache.EntryEvent",
            "org.apache.geode.cache.RegionEvent", "org.apache.geode.cache.SerializedCacheValue",
            "org.apache.geode.cache.RegionMembershipListener", "org.apache.geode.cache.query.CqStatusListener",
            "org.apache.geode.cache.client.SocketFactory", "org.apache.geode.cache.server.ServerLoadProbe",
            "org.apache.geode.cache.asyncqueue.AsyncEventListener", "org.apache.geode.cache.Region",
            "org.apache.geode.cache.query.Struct",
            "org.apache.geode.cache.CacheFactory", "org.apache.geode.cache.Cache",
            "org.apache.geode.cache.RegionFactory", "org.apache.geode.cache.RegionShortcut",
            "org.apache.geode.cache.client.ClientRegionFactory", "org.apache.geode.cache.client.ClientRegionShortcut",
            "org.apache.geode.cache.client.ClientCacheFactory", "org.apache.geode.cache.InterestResultPolicy",
        ]
        output = subprocess.run(["javap", "-public", "-s", "-classpath", os.pathsep.join(map(str, jars)), *names],
                                check=True, capture_output=True, text=True).stdout
        receiver, member, checked = None, None, 0
        for line in output.splitlines():
            header = re.search(r"\b(?:class|interface) (org\.[\w.$]+)", line)
            if header:
                receiver = header[1]
            elif line.strip().startswith("public ") and line.strip().endswith(";"):
                member = line.split("(", 1)[0].split()[-1].rstrip(";")
                if member == receiver:
                    member = "<init>"
            elif line.strip().startswith("descriptor:"):
                descriptor = line.split(":", 1)[1].strip()
                assert receiver + "#" + member + descriptor in by_key, line
                checked += 1
        assert checked >= 100, "javap cross-check did not inspect enough declarations"
        print(f"javap independently matched {checked} public declarations across {len(names)} types")
    print(f"Validated {len(rows)} API rows, {len(imports)} production imports, {len(source_rows)} source references")
    print("API scope: " + ", ".join(f"{scope}={count}" for scope, count in counts(rows).items()))
    print("All ownership/source/ID/scope checks and both inventory negative controls passed")


if __name__ == "__main__":
    main()
