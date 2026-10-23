#!/usr/bin/env python3
"""Adversarial fixture tests; no edits to project sources or baseline."""
import argparse
from collections import Counter
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

import check_boundary as gate

parser = argparse.ArgumentParser()
parser.add_argument("--java", type=Path, required=True)
parser.add_argument("--classes", type=Path, required=True)
args, remaining = parser.parse_known_args()


class SourceRules(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temp = tempfile.TemporaryDirectory(prefix="gemfire-boundary-fixtures-")
        cls.root = Path(cls.temp.name)
        fixtures = {
            "Import": "import org.apache.geode.cache.Cache; class Import {}",
            "Qualified": "class Qualified { Object type = org.apache.geode.cache.Cache.class; }",
            "Reflection": 'class Reflection { String type = "org.apache.geode.cache.Cache"; }',
            "Concatenated": 'class Concatenated { String type = "org.apache." + "geode.cache.Cache"; }',
            "Escaped": r'class Escaped { String type = "org\u002eapache.geode.cache.Cache"; }',
            "Wildcard": "import org.apache.geode.cache.*; class Wildcard { Cache cache; }",
            "StaticImport": "import static org.apache.geode.cache.CacheFactory.getAnyInstance; class StaticImport { Object x = getAnyInstance(); }",
            "Comment": "/** org.apache.geode.cache.Cache */ class Comment {}",
            "Logger": 'class Logger { void log() { logger.warn("org.apache.geode.cache.Cache"); } }',
            "Mutable": "class Mutable { private\n static\n int state = 0; }",
            "Array": "class Array { static final String[] VALUES = {}; }",
            "Atomic": "import java.util.concurrent.atomic.AtomicReference; class Atomic { static final AtomicReference<String> VALUE = new AtomicReference<>(); }",
            "Collection": "import java.util.List; class Collection { static final List<String> VALUES = new ArrayList<>(); }",
            "Implicit": "interface Implicit { Object STATE = new Object(); }",
            "Holder": "class Holder { static class Lazy { static final Holder INSTANCE = new Holder(); } }",
            "Singleton": "class Singleton { static Singleton getInstance() { return new Singleton(); } }",
            "StatelessDriver": "final class StatelessDriver implements GemFireDriver { static StatelessDriver driver() { return new StatelessDriver(); } }",
            "EnumService": "enum EnumService { INSTANCE; void execute() {} }",
            "EnumAlias": "enum EnumAlias { NONE, KEYS; static final EnumAlias DEFAULT = KEYS; }",
            "MutableEnumAlias": "enum MutableEnumAlias { NONE, KEYS; int count; static final MutableEnumAlias DEFAULT = KEYS; }",
            "MutableEnumObject": "enum MutableEnumObject { NONE, KEYS; final Object state = new Object(); static final MutableEnumObject DEFAULT = KEYS; }",
            "NonFinalEnumAlias": "enum NonFinalEnumAlias { NONE, KEYS; static NonFinalEnumAlias DEFAULT = KEYS; }",
            "StaticLogger": "class StaticLogger { static final Logger log = LoggerFactory.getLogger(StaticLogger.class); }",
            "Constants": 'class Constants { static final int COUNT = 2; static final String LABEL = "ok"; }',
            "Immutable": "record Immutable(int value) { static Immutable of(int value) { return new Immutable(value); } }",
            "MutableRecord": "record MutableRecord(java.util.List<String> values) { static final MutableRecord SHARED = new MutableRecord(new ArrayList<>()); }",
            "Injection": "class Injection { @Autowired GemFireDriverManager driver; }",
            "MigratedInjection": "import com.vmware.gemfire.driver.core.GemFireDriverManager; class MigratedInjection { @Autowired GemFireDriverManager driver; }",
            "PlainSetter": "class PlainSetter { void setDriver(GemFireDriver driver) {} }",
            "Setter": "class Setter { @Inject void setDriver(GemFireDriverManager driver) {} }",
            "Constructor": "class Constructor { final GemFireDriverManager driver; Constructor(GemFireDriverManager driver) { this.driver = driver; } }",
            "Construction": "class Construction { Object driver = new GemFireDriverManager(); }",
            "Discovery": "class Discovery { Object driver = ServiceLoader.load(GemFireDriver.class); }",
            "Locator": "class Locator { static DriverConnection driver() { return null; } }",
        }
        for name, text in fixtures.items():
            path = cls.root / gate.SPRING / "src/main/java" / (name + ".java")
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(text)
        for module, text in {
            gate.PREFIX + "10.4": "import org.apache.geode.cache.CacheFactory; class Native { Object cache() { return CacheFactory.getAnyInstance(); } }",
            gate.PREFIX + "spi": "import com.vmware.gemfire.driver.core.GemFireDriverManager; class WrongDirection {}",
        }.items():
            path = cls.root / module / "src/main/java/Extra.java"
            path.parent.mkdir(parents=True)
            path.write_text(text)
        model = [dict(module=m, sourceSets=[]) for m in [gate.SPRING, gate.PREFIX + "10.4", gate.PREFIX + "spi"]]
        cls.rows, _ = gate.source_findings(cls.root, model, args.java, args.classes)

    @classmethod
    def tearDownClass(cls):
        cls.temp.cleanup()

    def rules(self, name):
        return {r["rule"] for r in self.rows if r["path"].endswith("/" + name + ".java")}

    def test_imports_qualified_names_and_static_imports(self):
        for name in ["Import", "Qualified", "StaticImport", "Wildcard"]:
            with self.subTest(name=name):
                self.assertIn("native-source", self.rules(name))

    def test_reflection_strings_and_unicode_escape(self):
        for name in ["Reflection", "Escaped", "Concatenated"]:
            with self.subTest(name=name):
                self.assertIn("native-string", self.rules(name))

    def test_mutable_static_shapes(self):
        for name in ["Mutable", "Array", "Atomic", "Collection", "Implicit", "Holder", "StaticLogger", "MutableRecord",
                     "MutableEnumAlias", "MutableEnumObject", "NonFinalEnumAlias"]:
            with self.subTest(name=name):
                self.assertIn("static-state", self.rules(name))

    def test_immutable_enum_constant_alias(self):
        self.assertFalse(self.rules("EnumAlias"))

    def test_singletons(self):
        self.assertIn("static-service", self.rules("Singleton"))
        self.assertIn("static-service", self.rules("StatelessDriver"))
        self.assertIn("singleton", self.rules("EnumService"))

    def test_constructor_injection(self):
        for name in ["Injection", "Setter"]:
            self.assertIn("constructor-injection", self.rules(name))
        self.assertFalse(self.rules("Constructor"))
        self.assertIn("driver-construction", self.rules("Construction"))
        self.assertIn("driver-discovery", self.rules("Discovery"))
        self.assertIn("driver-static-access", self.rules("Locator"))
        self.assertIn("constructor-injection", self.rules("PlainSetter"))
        self.assertIn("migrated-constructor-injection", self.rules("MigratedInjection"))

    def test_documentation_and_logging_are_separate(self):
        self.assertEqual({"documentation"}, self.rules("Comment"))
        self.assertEqual({"logger-text"}, self.rules("Logger"))

    def test_immutable_constants_and_value_factory(self):
        self.assertFalse(self.rules("Constants"))
        self.assertFalse(self.rules("Immutable"))

    def test_native_driver_internals_allowed_spi_core_edge_rejected(self):
        self.assertFalse([r for r in self.rows if r["path"].startswith(gate.PREFIX + "10.4")])
        self.assertTrue([r for r in self.rows if r["rule"] == "dependency-direction"])

    def test_source_findings_fail_empty_baseline(self):
        new, _, _ = gate.compare(self.rows, dict(violations=[]))
        for rule in ["native-source", "native-string", "static-state", "static-service", "singleton",
                     "dependency-direction", "constructor-injection", "migrated-constructor-injection"]:
            with self.subTest(rule=rule):
                self.assertTrue(any(key[0] == rule for key in new))


class ResourceRules(unittest.TestCase):
    def test_service_resource_filename(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            path = root / "META-INF/services/org.apache.geode.cache.CacheListener"
            path.parent.mkdir(parents=True)
            path.write_text("example.MyListener\n")
            self.assertEqual("native-resource", gate.resource_findings(path, root)[0]["rule"])

    def test_schema_contracts_versus_documentation_and_logger(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            path = root / "schema.xsd"
            path.write_text('''<schema xmlns:xsd="urn:xsd" xmlns:tool="urn:tool">
              <xsd:documentation source="org.apache.geode.cache.Cache">org.apache.geode.cache.Cache</xsd:documentation>
              <tool:exports type="org.apache.geode.cache.Cache" />
              <tool:expected-type type="org.apache.geode.cache.CacheListener" />
              <bean class="org.apache.geode.cache.CacheFactory" />
              <logger name="org.apache.geode" />
            </schema>''')
            counts = Counter(r["rule"] for r in gate.resource_findings(path, root))
            self.assertEqual(3, counts["native-resource"])
            self.assertEqual(2, counts["documentation"])
            self.assertEqual(1, counts["logger-text"])


class ClasspathRules(unittest.TestCase):
    def scan(self, module, source, config, components, files=()):
        return gate.classpath_findings([dict(module=module, sourceSets=[dict(name=source,
            classpaths=[dict(name=config, components=components, files=files)])])])

    def test_transitive_native_artifact_even_without_classes(self):
        rows = self.scan(gate.SPRING, "test", "testRuntimeClasspath", [dict(group="com.vmware.gemfire", name="gemfire-cq", version="1")])
        self.assertEqual("native-artifact", rows[0]["rule"])

    def test_hidden_jar_and_directory_native_classes(self):
        with tempfile.TemporaryDirectory() as tmp:
            jar = Path(tmp) / "unrelated-helper.jar"
            with zipfile.ZipFile(jar, "w") as archive:
                archive.writestr("org/apache/geode/Hidden.class", b"fixture")
            directory = Path(tmp) / "classes"
            native = directory / "com/gemstone/gemfire/Hidden.class"
            native.parent.mkdir(parents=True)
            native.write_bytes(b"fixture")
            rows = self.scan(gate.PREFIX + "mock", "test", "testCompileClasspath", [], [str(jar), str(directory)])
            self.assertEqual(2, len(rows))
            self.assertTrue(all(r["rule"] == "native-classpath-content" for r in rows))

    def test_integration_runtime_allowed_compile_forbidden(self):
        component = dict(group="org.apache.geode", name="geode-core", version="1")
        self.assertEqual("native-runtime-allowed", self.scan(gate.SPRING, "integrationTest", "integrationTestRuntimeClasspath", [component])[0]["rule"])
        self.assertEqual("native-artifact", self.scan(gate.SPRING, "integrationTest", "integrationTestCompileClasspath", [component])[0]["rule"])
        self.assertEqual("native-runtime-allowed", self.scan(gate.PREFIX + "10.4", "main", "compileClasspath", [component])[0]["rule"])

    def test_module_dependency_directions_and_test_kit_exception(self):
        for module, target in [(gate.PREFIX + "spi", gate.PREFIX + "core"), (gate.PREFIX + "core", gate.PREFIX + "mock"),
                               (gate.PREFIX + "10.4", gate.PREFIX + "core"), (gate.PREFIX + "10.4", gate.SPRING),
                               (gate.PREFIX + "mock", gate.PREFIX + "10.4"), (gate.SPRING, gate.PREFIX + "10.4")]:
            with self.subTest(module=module, target=target):
                self.assertEqual("dependency-direction", self.scan(module, "main", "compileClasspath", [dict(project=target)])[0]["rule"])
        self.assertFalse(self.scan(gate.PREFIX + "10.4", "integrationTest", "integrationTestCompileClasspath", [dict(project=gate.PREFIX + "contract-tests")]))
        self.assertFalse(self.scan(gate.SPRING, "test", "testRuntimeClasspath", [dict(project=gate.PREFIX + "mock")]))


class BaselineRules(unittest.TestCase):
    def test_new_reintroduced_and_duplicate_violations_fail(self):
        row = gate.finding("native-source", gate.SPRING + "/Example.java", 10, "org.apache.geode.cache.Cache")
        self.assertTrue(gate.compare([row], dict(violations=[]))[0])
        baseline = dict(violations=[dict(row, count=1)])
        self.assertFalse(gate.compare([dict(row, line=100)], baseline)[0])
        self.assertTrue(gate.compare([row, row], baseline)[0])
        self.assertTrue(gate.compare([], baseline)[1])  # removal requires pruning; cannot silently reintroduce later

    def test_baseline_pass_is_not_strict_pass(self):
        row = gate.finding("native-source", gate.SPRING + "/Example.java", 10, "org.apache.geode.cache.Cache")
        baseline = dict(violations=[dict(row, count=1)])
        self.assertEqual((Counter(), Counter(), Counter()), gate.compare([row], baseline))
        self.assertTrue(gate.compare([row], baseline, strict=True)[2])

    def test_only_existing_spring_statics_are_grandfathered(self):
        row = gate.finding("static-state", gate.SPRING + "/Example.java", 10, "static Object legacy")
        baseline = dict(violations=[dict(row, count=1)])
        self.assertFalse(gate.compare([row], baseline, strict=True)[2])
        self.assertTrue(gate.compare([dict(row, detail="static Object newState")], baseline, strict=True)[0])


if __name__ == "__main__":
    unittest.main(argv=[__file__] + remaining)
