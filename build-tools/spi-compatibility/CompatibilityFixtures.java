/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.tools.ToolProvider;

/** Fresh compiled jars exercise the gate; none is represented as a released SPI. */
public final class CompatibilityFixtures {
    private final Path root;
    private int cases;

    private CompatibilityFixtures(Path root) { this.root = root; }

    public static void main(String[] args) throws Exception {
        var fixtures = new CompatibilityFixtures(Files.createTempDirectory(Path.of(args[0]).toAbsolutePath()
                .getParent(), "spi-abi-fixtures-"));
        fixtures.run();
        System.out.println("Passed " + fixtures.cases + " compiled ABI/stamp fixtures");
    }

    private void run() throws Exception {
        Path old = jar("old", "1.0", "public interface Api { String get(); }", "public interface Callback { void event(); }");
        Path additive = jar("additive", "1.1", "public interface Api { String get(); default String added() { return \"ok\"; } }",
                "public interface Callback { void event(); default void added() {} }");
        AbiCheck.check(old, additive, "", "", false); cases++;
        oldCallbackOnNewSpi(old, additive);
        reject("abstract", old, "1.1", "public interface Api { String get(); void added(); }", null, "Incompatible");
        reject("callback", old, "1.1", null, "public interface Callback { void event(); void added(); }", "Incompatible");
        reject("return", old, "1.1", "public interface Api { Object get(); }", null, "Incompatible");
        reject("remove", old, "1.1", "public interface Api {}", null, "Incompatible");
        reject("parameter", old, "1.1", "public interface Api { String get(int value); }", null, "Incompatible");
        reject("throws", old, "1.1", "public interface Api { String get() throws IllegalStateException; }", null, "throws clause");
        reject("major", old, "2.0", null, null, "major transition");
        Path approved = jar("approved", "2.0", "public interface Api {}", "public interface Callback {}");
        AbiCheck.check(old, approved, "", "", true); cases++;
        reject("minor", additive, "1.0", null, null, "minor version regressed");
        reject("no-bump", old, "1.0", "public interface Api { String get(); default void added() {} }", null, "minor bump");
        fails(() -> AbiCheck.check(old, old, "", "", false), "against itself");
        Path enumOld = jar("enum-old", "1.0", "public enum Api { A, B }", "public interface Callback {}");
        reject("enum-remove", enumOld, "1.1", "public enum Api { A }", "public interface Callback {}", "Incompatible");
        Path constant = jar("constant", "1.0", "public interface Api { int VALUE = 1; }", "public interface Callback {}");
        reject("constant-change", constant, "1.1", "public interface Api { int VALUE = 2; }", "public interface Callback {}", "constant changed");
        Path superclass = jar("superclass", "1.0", "public class Api { public String get() { return null; } }", "public interface Callback {}");
        reject("final-class", superclass, "1.1", "public final class Api { public String get() { return null; } }", "public interface Callback {}", "Incompatible");
        reject("protected", superclass, "1.1", "public class Api { protected String get() { return null; } }", "public interface Callback {}", "Incompatible");
        Path protectedApi = jar("protected-api", "1.0", "public class Api { protected void hook() {} }", "public interface Callback {}");
        reject("protected-remove", protectedApi, "1.1", "public class Api {}", "public interface Callback {}", "Incompatible");
        Path inherited = jar("inherited", "1.0", "public interface Api extends Callback {}", "public interface Callback { void event(); }");
        reject("inherited-abstract", inherited, "1.1", "public interface Api extends Callback {}",
                "public interface Callback { void event(); void added(); }", "Incompatible");
        // Baseline manifest failures exercise the actual command entry point.
        Path policy = root.resolve("baseline.properties");
        Files.writeString(policy, "status=released\nspi.version=1.0\nsha256=bad\n");
        fails(() -> AbiCheck.main(new String[] {policy.toString(), additive.toString(), old.toString(), "", ""}), "SHA-256");
        Files.writeString(policy, "status=released\nspi.version=1.0\nsha256=" + AbiCheck.sha256(old) + "\n");
        AbiCheck.main(new String[] {policy.toString(), additive.toString(), old.toString(), "", ""}); cases++;
        Files.writeString(policy, "status=released\nspi.version=1.2\nsha256=" + AbiCheck.sha256(old) + "\n");
        fails(() -> AbiCheck.main(new String[] {policy.toString(), additive.toString(), old.toString(), "", ""}), "baseline SPI version");
        Files.writeString(policy, "status=unreleased\n");
        AbiCheck.main(new String[] {policy.toString(), old.toString()}); cases++;
        fails(() -> AbiCheck.main(new String[] {policy.toString(), additive.toString()}), "First-release");
        Path goodStamp = resourceJar("good-stamp", "spi.version=1.0\ngemfire.version=10.4.0\n");
        StampVerifier.verify(goodStamp, "stamp.properties", "1.0", "10.4.0"); cases++;
        fails(() -> StampVerifier.verify(goodStamp, "absent", "1.0", "10.4.0"), "Missing stamp");
        fails(() -> StampVerifier.verify(goodStamp, "stamp.properties", "1.1", "10.4.0"), "resolved compile SPI");
        fails(() -> StampVerifier.verify(goodStamp, "stamp.properties", "1.0", "10.4.1"), "resolved native");
        fails(() -> StampVerifier.verify(resourceJar("malformed", "spi.version=oops\n"), "stamp.properties", "1.0", ""), "resolved compile SPI");
        fails(() -> StampVerifier.verify(resourceJar("missing-spi", "gemfire.version=10.4.0\n"), "stamp.properties", "1.0", "10.4.0"), "resolved compile SPI");
        StampVerifier.verify(resourceJar("core", "spi.version=1.0\n"), "stamp.properties", "1.0", ""); cases++;
        fails(() -> StampVerifier.verify(goodStamp, "stamp.properties", "1.0", ""), "resolved native");
    }

    private void oldCallbackOnNewSpi(Path old, Path additive) throws Exception {
        Path dir = Files.createDirectories(root.resolve("old-callback"));
        Path source = dir.resolve("OldCallback.java");
        Files.writeString(source, "package fixture; public class OldCallback implements Callback { public void event() {} }");
        AbiCheck.require(ToolProvider.getSystemJavaCompiler().run(null, null, null,
                "-classpath", old.toString(), "-d", dir.toString(), source.toString()) == 0,
                "Old callback compilation failed");
        try (var loader = new URLClassLoader(new URL[] { additive.toUri().toURL(), dir.toUri().toURL() },
                ClassLoader.getPlatformClassLoader())) {
            Object callback = loader.loadClass("fixture.OldCallback").getConstructor().newInstance();
            loader.loadClass("fixture.Callback").getMethod("added").invoke(callback);
            cases++;
        }
    }

    private void reject(String name, Path old, String version, String api, String callback, String message) throws Exception {
        Path next = jar(name, version, api == null ? "public interface Api { String get(); }" : api,
                callback == null ? "public interface Callback { void event(); }" : callback);
        fails(() -> AbiCheck.check(old, next, "", "", false), message);
    }

    private Path jar(String name, String version, String api, String callback) throws Exception {
        Path dir = Files.createDirectories(root.resolve(name));
        Path source = Files.createDirectories(dir.resolve("src"));
        Path classes = Files.createDirectories(dir.resolve("classes"));
        Files.writeString(source.resolve("Api.java"), "package fixture; " + api);
        Files.writeString(source.resolve("Callback.java"), "package fixture; " + callback);
        int result = ToolProvider.getSystemJavaCompiler().run(null, null, null, "-d", classes.toString(),
                source.resolve("Api.java").toString(), source.resolve("Callback.java").toString());
        AbiCheck.require(result == 0, "Fixture compilation failed: " + name);
        var entries = new LinkedHashMap<String, byte[]>();
        try (var paths = Files.walk(classes)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                entries.put(classes.relativize(path).toString(), Files.readAllBytes(path));
            }
        }
        entries.put("com/vmware/gemfire/driver/spi/spi-version.properties", ("spi.version=" + version + "\n").getBytes());
        return writeJar(dir.resolve(name + ".jar"), entries);
    }

    private Path resourceJar(String name, String text) throws Exception {
        return writeJar(root.resolve(name + ".jar"), Map.of("stamp.properties", text.getBytes()));
    }

    private Path writeJar(Path path, Map<String, byte[]> entries) throws Exception {
        try (var jar = new JarOutputStream(Files.newOutputStream(path))) {
            for (var entry : entries.entrySet()) {
                jar.putNextEntry(new JarEntry(entry.getKey())); jar.write(entry.getValue()); jar.closeEntry();
            }
        }
        return path;
    }

    private void fails(Action action, String message) throws Exception {
        try { action.run(); }
        catch (IllegalStateException ex) {
            AbiCheck.require(ex.getMessage().contains(message), "Unexpected failure: " + ex);
            cases++; return;
        }
        throw new AssertionError("Expected failure: " + message);
    }

    @FunctionalInterface private interface Action { void run() throws Exception; }
}
