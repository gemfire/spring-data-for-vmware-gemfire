/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.ZipFile;

import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.*;

/** japicmp plus the SPI's stricter extension-provider and immutable-signature rules. */
public final class AbiCheck {
    public static void main(String[] args) throws Exception {
        var policy = new Properties();
        try (var input = Files.newInputStream(Path.of(args[0]))) { policy.load(input); }
        Path current = Path.of(args[1]);
        String currentVersion = version(current);
        String status = policy.getProperty("status");
        if ("unreleased".equals(status)) {
            for (String key : List.of("coordinates", "spi.version", "sha256")) {
                require(policy.getProperty(key, "").isBlank(), "Unreleased policy contains a baseline: " + key);
            }
            require("1.0".equals(currentVersion), "First-release mode is only valid for SPI 1.0");
            System.out.println("PRE-FIRST-RELEASE: no released SPI baseline; ABI comparison unavailable. Fixture gate runs separately.");
            return;
        }
        require("released".equals(status), "Unknown baseline status: " + status);
        Path baseline = Path.of(args[2]);
        require(policy.getProperty("sha256", "").equals(sha256(baseline)), "Released baseline SHA-256 mismatch");
        require(policy.getProperty("spi.version", "").equals(version(baseline)), "Released baseline SPI version mismatch");
        check(baseline, current, args[3], args[4], Boolean.parseBoolean(policy.getProperty("allowMajorChange")));
    }

    static String sha256(Path path) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)));
    }

    static String version(Path jar) throws Exception {
        try (var zip = new ZipFile(jar.toFile())) {
            var entry = zip.getEntry("com/vmware/gemfire/driver/spi/spi-version.properties");
            require(entry != null, "Missing SPI version resource: " + jar);
            var p = new Properties();
            try (var input = zip.getInputStream(entry)) { p.load(input); }
            String version = p.getProperty("spi.version", "");
            require(version.matches("[0-9]+\\.[0-9]+"), "Malformed SPI version: " + version);
            return version;
        }
    }

    static void check(Path baseline, Path current, String oldClasspath, String newClasspath,
            boolean allowMajorChange) throws Exception {
        require(!Files.isSameFile(baseline, current) && !sha256(baseline).equals(sha256(current)),
                "Cannot compare an SPI artifact against itself");
        String oldVersion = version(baseline), newVersion = version(current);
        int[] oldParts = Arrays.stream(oldVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] newParts = Arrays.stream(newVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        require(newParts[0] >= oldParts[0], "SPI major version regressed");
        if (newParts[0] != oldParts[0]) {
            require(allowMajorChange, "SPI major transition requires reviewed baseline policy approval");
            System.out.println("Approved SPI major transition " + oldVersion + " -> " + newVersion
                    + "; all drivers must be rebuilt. No within-major compatibility claimed.");
            return;
        }
        require(newParts[1] >= oldParts[1], "SPI minor version regressed");
        var options = new JarArchiveComparatorOptions();
        options.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
        options.getOldClassPath().addAll(classpath(oldClasspath));
        options.getNewClassPath().addAll(classpath(newClasspath));
        // Missing dependencies fail closed; never use ignoreMissingClasses.
        var classes = new JarArchiveComparator(options).compare(
                new JApiCmpArchive(baseline.toFile(), oldVersion), new JApiCmpArchive(current.toFile(), newVersion));
        var failures = new ArrayList<String>();
        boolean changed = false;
        for (var type : classes) {
            changed |= type.getChangeStatus() != JApiChangeStatus.UNCHANGED;
            if (type.getChangeStatus() == JApiChangeStatus.NEW) continue;
            inspect(type.getFullyQualifiedName(), type, failures);
            for (var method : type.getMethods()) {
                inspect(type.getFullyQualifiedName() + "#" + method.getName(), method, failures);
                inspectThrows(type.getFullyQualifiedName(), method, failures);
                // Explicitly reject abstract additions even if JVM binary compatibility labels allow them.
                if (method.getChangeStatus() == JApiChangeStatus.NEW &&
                        method.getAbstractModifier().getNewModifier().orElse(null) == AbstractModifier.ABSTRACT) {
                    failures.add(type.getFullyQualifiedName() + "#" + method.getName() + ": new abstract method");
                }
            }
            for (var constructor : type.getConstructors()) {
                inspect(type.getFullyQualifiedName() + "#<init>", constructor, failures);
                inspectThrows(type.getFullyQualifiedName(), constructor, failures);
            }
            for (var field : type.getFields()) {
                inspect(type.getFullyQualifiedName() + "#" + field.getName(), field, failures);
                if (field.getOldFieldOptional().isPresent() && field.getNewFieldOptional().isPresent() &&
                        !Objects.equals(field.getOldFieldOptional().get().getConstantValue(),
                                field.getNewFieldOptional().get().getConstantValue())) {
                    failures.add(type.getFullyQualifiedName() + "#" + field.getName() + ": inlined constant changed");
                }
            }
        }
        require(failures.isEmpty(), "Incompatible SPI changes:\n" + String.join("\n", failures));
        require(!changed || newParts[1] > oldParts[1], "Additive SPI API changes require a minor bump");
        System.out.println("SPI ABI compatible: " + oldVersion + " -> " + newVersion);
    }

    private static List<String> classpath(String value) {
        return value.isEmpty() ? List.of() : Arrays.asList(value.split(java.util.regex.Pattern.quote(File.pathSeparator)));
    }

    private static void inspect(String name, JApiCompatibility element, List<String> failures) {
        if (!element.isBinaryCompatible() || !element.isSourceCompatible()) {
            failures.add(name + ": " + element.getCompatibilityChanges());
        }
    }

    private static void inspectThrows(String name, JApiBehavior method, List<String> failures) {
        if (method.getChangeStatus() != JApiChangeStatus.NEW && method.getExceptions().stream()
                .anyMatch(ex -> ex.getChangeStatus() != JApiChangeStatus.UNCHANGED)) {
            failures.add(name + "#" + method.getName() + ": throws clause changed");
        }
    }

    static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
