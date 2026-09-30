package com.vmware.gemfire.driver.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverMetadata;
import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.SpiVersion;

/**
 * Exercises {@link DriverCandidate#discover} against a real {@link ServiceLoader}. The services file lives in a
 * temporary directory loaded by a throwaway class loader, so no provider is registered on the shared test classpath.
 * The stamp of {@link StampedDriver} is a test resource; {@link UnstampedDriver} deliberately has none.
 */
class DriverCandidateTests {

	public static class StampedDriver implements GemFireDriver {
		public DriverMetadata metadata() { return () -> "stamped"; }
		public String runtimeGemFireVersion() { return "10.3.2"; }
		public DriverConnection connect(DriverConfiguration configuration) { throw new UnsupportedOperationException(); }
	}

	public static class UnstampedDriver extends StampedDriver {
	}

	private static ServiceLoader<GemFireDriver> loaderFor(Path directory, Class<?>... providers) throws Exception {
		Path services = Files.createDirectories(directory.resolve("META-INF/services"));
		Files.writeString(services.resolve(GemFireDriver.class.getName()),
			String.join("\n", java.util.Arrays.stream(providers).map(Class::getName).toList()));
		URLClassLoader classLoader = new URLClassLoader(new URL[] { directory.toUri().toURL() },
			DriverCandidateTests.class.getClassLoader());
		return ServiceLoader.load(GemFireDriver.class, classLoader);
	}

	@Test
	void discoversProviderAndReadsItsStampWithoutInstantiatingIt(@TempDir Path directory) throws Exception {
		List<DriverCandidate> candidates = DriverCandidate.discover(loaderFor(directory, StampedDriver.class));

		assertThat(candidates).singleElement().satisfies(candidate -> {
			assertThat(candidate.providerClass()).isEqualTo(StampedDriver.class.getName());
			assertThat(candidate.stamp().builtAgainstSpiVersion()).isEqualTo(SpiVersion.of(1, 0));
			assertThat(candidate.stamp().gemFireVersion()).isEqualTo("10.3.2");
			assertThat(candidate.instantiate()).isInstanceOf(StampedDriver.class);
		});
	}

	@Test
	void missingStampIsABindFailure(@TempDir Path directory) throws Exception {
		ServiceLoader<GemFireDriver> loader = loaderFor(directory, UnstampedDriver.class);
		assertThatThrownBy(() -> DriverCandidate.discover(loader))
			.isInstanceOf(DriverBindException.class)
			.hasMessageContaining(UnstampedDriver.class.getName())
			.hasMessageContaining("no stamp");
	}

	@Test
	void unloadableProviderIsATypedBindFailure(@TempDir Path directory) throws Exception {
		Path services = Files.createDirectories(directory.resolve("META-INF/services"));
		Files.writeString(services.resolve(GemFireDriver.class.getName()), "does.not.Exist");
		ServiceLoader<GemFireDriver> loader = ServiceLoader.load(GemFireDriver.class,
			new URLClassLoader(new URL[] { directory.toUri().toURL() }, getClass().getClassLoader()));
		assertThatThrownBy(() -> DriverCandidate.discover(loader)).isInstanceOf(DriverBindException.class);
	}

	@Test
	void noProvidersYieldsNoCandidates(@TempDir Path directory) throws Exception {
		Files.createDirectories(directory);
		ServiceLoader<GemFireDriver> loader = ServiceLoader.load(GemFireDriver.class,
			new URLClassLoader(new URL[] { directory.toUri().toURL() }, getClass().getClassLoader()));
		assertThat(DriverCandidate.discover(loader)).isEmpty();
	}

}
