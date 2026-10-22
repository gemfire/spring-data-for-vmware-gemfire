/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
package com.vmware.gemfire.driver.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Compile a provider against a smaller synthetic old SPI, then bind without that old SPI present. */
class CompiledDriverCompatibilityTests {

	@TempDir Path root;

	@Test
	void oldProviderUsesNewTypedUnsupportedDefault() throws Exception {
		try (var loader = fixture("1.1", "1.0", "spi.version=1.0\ngemfire.version=10.4.0\n", "", "return \"10.4.0\";")) {
			Object manager = bind(loader);
			Object driver = manager.getClass().getMethod("driver").invoke(manager);
			Object connection = driver.getClass().getMethod("connect", loader.loadClass("com.vmware.gemfire.driver.spi.DriverConfiguration"))
				.invoke(driver, new Object[] { null });
			assertThatThrownBy(() -> invoke(loader.loadClass("com.vmware.gemfire.driver.spi.DriverConnection")
				.getMethod("getTransactionManager"), connection))
				.satisfies(ex -> assertThat(ex.getClass().getName()).endsWith("UnsupportedFeatureException"))
				.hasMessageContaining("Connection.getTransactionManager");
		}
	}

	@Test
	void coreRequirementIsCheckedIndependentlyBeforeProviderDiscovery() throws Exception {
		for (String required : new String[] { "2.0", "1.2" }) {
			try (var loader = fixture("1.1", required, null, "throw new AssertionError(\"must not instantiate\");", "return \"10.4.0\";")) {
				assertBindFailure(loader, "SpiVersionIncompatibleException", "driver-core");
			}
		}
	}

	@Test
	void providerMajorAndNewerMinorAreRejectedBeforeConstruction() throws Exception {
		for (String required : new String[] { "2.0", "1.2" }) {
			try (var loader = fixture("1.1", "1.0", "spi.version=" + required + "\ngemfire.version=10.4.0\n",
					"throw new AssertionError(\"must not instantiate\");", "return \"10.4.0\";")) {
				assertBindFailure(loader, "SpiVersionIncompatibleException", "fixture.OldDriver");
			}
		}
	}

	@Test
	void missingAndMalformedCoreStampsFailClosed() throws Exception {
		for (String stamp : new String[] { null, "garbage", "\\uZZZZ" }) {
			try (var loader = fixture("1.1", stamp, null, "", "return \"10.4.0\";")) {
				assertBindFailure(loader, "DriverBindException", "driver-core");
			}
		}
	}

	@Test
	void missingAndMalformedProviderStampsFailClosed() throws Exception {
		for (String stamp : new String[] { null, "spi.version=oops\ngemfire.version=10.4.0\n",
				"spi.version=1.0\n", "spi.version=\\uZZZZ\ngemfire.version=10.4.0\n" }) {
			try (var loader = fixture("1.1", "1.0", stamp, "", "return \"10.4.0\";")) {
				assertBindFailure(loader, "DriverBindException", "fixture.OldDriver");
			}
		}
	}

	@Test
	void runtimeNativePatchMismatchIsRejected() throws Exception {
		try (var loader = fixture("1.1", "1.0", "spi.version=1.0\ngemfire.version=10.4.0\n", "", "return \"10.4.1\";")) {
			assertBindFailure(loader, "NativeVersionIncompatibleException", "10.4.1");
		}
	}

	@Test
	void actualMissingMethodDuringConstructionOrVersionReadIsWrapped() throws Exception {
		for (boolean constructor : new boolean[] { true, false }) {
			String call = "com.vmware.gemfire.driver.spi.DriverConnection c = new OldConnection(); c.oldMethod();";
			try (var loader = fixture("1.1", "1.0", "spi.version=1.0\ngemfire.version=10.4.0\n",
					constructor ? call : "", constructor ? "return \"10.4.0\";" : call + "return \"10.4.0\";")) {
				assertThatThrownBy(() -> bind(loader)).satisfies(ex -> {
					assertThat(ex.getClass().getSimpleName()).isEqualTo("DriverBindException");
					Throwable cause = ex;
					while (cause.getCause() != null) cause = cause.getCause();
					assertThat(cause).isInstanceOf(NoSuchMethodError.class);
				});
			}
		}
	}

	private void assertBindFailure(ClassLoader loader, String type, String message) {
		assertThatThrownBy(() -> bind(loader)).satisfies(ex -> assertThat(ex.getClass().getSimpleName()).isEqualTo(type))
			.hasMessageContaining(message);
	}

	private Object bind(ClassLoader loader) throws Exception {
		try {
			return loader.loadClass(GemFireDriverManager.class.getName()).getConstructor(ClassLoader.class).newInstance(loader);
		}
		catch (InvocationTargetException ex) { throw (Exception) ex.getCause(); }
	}

	private Object invoke(java.lang.reflect.Method method, Object target) throws Throwable {
		try { return method.invoke(target); }
		catch (InvocationTargetException ex) { throw ex.getCause(); }
	}

	private URLClassLoader fixture(String runtimeSpi, String coreSpi, String stamp, String constructor, String runtime) throws Exception {
		Path dir = Files.createTempDirectory(root, "case-");
		Path oldSource = dir.resolve("DriverConnection.java");
		Files.writeString(oldSource, "package com.vmware.gemfire.driver.spi; public interface DriverConnection { String oldMethod(); }");
		Path oldClasses = Files.createDirectories(dir.resolve("old-api"));
		compile(oldClasses, System.getProperty("fixtureRuntimeClasspath"), oldSource);
		Path source = dir.resolve("OldDriver.java");
		Files.writeString(source, """
			package fixture;
			import com.vmware.gemfire.driver.spi.*;
			public class OldDriver implements GemFireDriver {
			  public OldDriver() { %s }
			  public DriverMetadata metadata() { return () -> "old-fixture"; }
			  public String runtimeGemFireVersion() { %s }
			  public DriverConnection connect(DriverConfiguration c) { return new OldConnection(); }
			  public static class OldConnection implements DriverConnection {
			    public String oldMethod() { return "old"; }
			  }
			}
			""".formatted(constructor, runtime));
		Path driverClasses = Files.createDirectories(dir.resolve("driver"));
		compile(driverClasses, oldClasses + File.pathSeparator + System.getProperty("fixtureRuntimeClasspath"), source);
		var spi = classEntries(System.getProperty("fixtureSpiClasses"));
		spi.put("com/vmware/gemfire/driver/spi/spi-version.properties", ("spi.version=" + runtimeSpi).getBytes());
		var core = classEntries(System.getProperty("fixtureCoreClasses"));
		if (coreSpi != null) core.put("META-INF/gemfire-driver-core.properties", ("spi.version=" + coreSpi).getBytes());
		var driver = classEntries(driverClasses.toString());
		driver.put("META-INF/services/com.vmware.gemfire.driver.spi.GemFireDriver", "fixture.OldDriver\n".getBytes());
		if (stamp != null) driver.put("META-INF/gemfire-driver/fixture.OldDriver.properties", stamp.getBytes());
		var urls = new ArrayList<URL>();
		urls.add(jar(dir.resolve("spi.jar"), spi));
		urls.add(jar(dir.resolve("core.jar"), core));
		urls.add(jar(dir.resolve("driver.jar"), driver));
		for (String file : System.getProperty("fixtureRuntimeClasspath").split(File.pathSeparator)) {
			if (file.endsWith(".jar") && !Path.of(file).getFileName().toString().startsWith("spring-data-gemfire-driver-")) {
				urls.add(Path.of(file).toUri().toURL());
			}
		}
		return new URLClassLoader(urls.toArray(URL[]::new), ClassLoader.getPlatformClassLoader());
	}

	private void compile(Path classes, String classpath, Path source) {
		assertThat(ToolProvider.getSystemJavaCompiler().run(null, null, null, "-classpath", classpath,
			"-d", classes.toString(), source.toString())).isZero();
	}

	private Map<String, byte[]> classEntries(String directories) throws Exception {
		var entries = new LinkedHashMap<String, byte[]>();
		for (String directory : directories.split(File.pathSeparator)) {
			Path base = Path.of(directory);
			try (var paths = Files.walk(base)) {
				for (Path path : paths.filter(Files::isRegularFile).toList()) entries.put(base.relativize(path).toString(), Files.readAllBytes(path));
			}
		}
		return entries;
	}

	private URL jar(Path path, Map<String, byte[]> entries) throws Exception {
		try (var output = new JarOutputStream(Files.newOutputStream(path))) {
			for (var entry : entries.entrySet()) {
				output.putNextEntry(new JarEntry(entry.getKey())); output.write(entry.getValue()); output.closeEntry();
			}
		}
		return path.toUri().toURL();
	}
}
