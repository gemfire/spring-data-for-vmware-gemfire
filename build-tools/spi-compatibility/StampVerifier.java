/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
import java.nio.file.Path;
import java.util.Properties;
import java.util.zip.ZipFile;

/** Independently verifies the packaged resource, not the generator's output directory. */
public final class StampVerifier {
    public static void main(String[] args) throws Exception {
        verify(Path.of(args[0]), args[1], args[2], args[3]);
        System.out.println("Verified packaged stamp: " + args[0]);
    }

    static void verify(Path jar, String resource, String spi, String nativeVersion) throws Exception {
        try (var zip = new ZipFile(jar.toFile())) {
            var entry = zip.getEntry(resource);
            if (entry == null) throw new IllegalStateException("Missing stamp: " + resource);
            var properties = new Properties();
            try (var input = zip.getInputStream(entry)) { properties.load(input); }
            if (!spi.matches("[0-9]+\\.[0-9]+") || !spi.equals(properties.getProperty("spi.version"))) {
                throw new IllegalStateException("Stamp differs from resolved compile SPI " + spi);
            }
            if (nativeVersion.isEmpty() ? properties.containsKey("gemfire.version")
                    : !nativeVersion.equals(properties.getProperty("gemfire.version"))) {
                throw new IllegalStateException("Stamp differs from resolved native version " + nativeVersion);
            }
        }
    }
}
