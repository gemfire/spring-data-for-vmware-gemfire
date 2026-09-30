package com.vmware.gemfire.driver.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.AmbiguousDriverException;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverMetadata;
import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.NativeVersionIncompatibleException;
import com.vmware.gemfire.driver.spi.NoDriverFoundException;
import com.vmware.gemfire.driver.spi.SpiVersion;
import com.vmware.gemfire.driver.spi.SpiVersionIncompatibleException;

class GemFireDriverManagerTests {

	private static final SpiVersion RUNTIME_SPI = SpiVersion.of(1, 4);

	private static GemFireDriver driver(String runtimeGemFireVersion) {
		return new GemFireDriver() {
			public DriverMetadata metadata() { return () -> "test-driver"; }
			public String runtimeGemFireVersion() { return runtimeGemFireVersion; }
			public DriverConnection connect(DriverConfiguration configuration) throws DriverBindException {
				throw new UnsupportedOperationException();
			}
		};
	}

	private static DriverCandidate candidate(String providerClass, SpiVersion builtAgainst, String gemFireVersion,
			Supplier<GemFireDriver> instantiator) {
		return new DriverCandidate(providerClass, new DriverStamp(builtAgainst, gemFireVersion), instantiator);
	}

	private static DriverCandidate candidate(SpiVersion builtAgainst, String stampedGemFire, GemFireDriver driver) {
		return candidate("test.Driver", builtAgainst, stampedGemFire, () -> driver);
	}

	private static GemFireDriverManager manager(DriverCandidate... candidates) {
		return new GemFireDriverManager(List.of(candidates), RUNTIME_SPI);
	}

	@Test
	void bindsSingleCompatibleDriver() {
		GemFireDriver driver = driver("10.3.2");
		assertThat(manager(candidate(SpiVersion.of(1, 4), "10.3.2", driver)).driver()).isSameAs(driver);
	}

	@Test
	void bindsDriverBuiltAgainstOlderSpiMinor() {
		GemFireDriver driver = driver("10.1.1");
		assertThat(manager(candidate(SpiVersion.of(1, 0), "10.1.1", driver)).driver()).isSameAs(driver);
	}

	@Test
	void failsWhenNoDriverPresent() {
		assertThatThrownBy(() -> manager()).isInstanceOf(NoDriverFoundException.class);
	}

	@Test
	void failsWhenMultipleDriversPresent() {
		assertThatThrownBy(() -> manager(
			candidate("a.DriverA", SpiVersion.of(1, 0), "10.1.1", () -> driver("10.1.1")),
			candidate("b.DriverB", SpiVersion.of(1, 0), "10.3.2", () -> driver("10.3.2"))))
			.isInstanceOfSatisfying(AmbiguousDriverException.class,
				ex -> assertThat(ex.providerClasses()).containsExactly("a.DriverA", "b.DriverB"));
	}

	@Test
	void failsWhenSpiMajorDiffers() {
		assertThatThrownBy(() -> manager(candidate(SpiVersion.of(2, 0), "10.3.2", driver("10.3.2"))))
			.isInstanceOfSatisfying(SpiVersionIncompatibleException.class, ex -> {
				assertThat(ex.builtAgainst()).isEqualTo(SpiVersion.of(2, 0));
				assertThat(ex.runtime()).isEqualTo(RUNTIME_SPI);
			});
	}

	@Test
	void failsWhenDriverWasBuiltAgainstNewerSpiMinor() {
		assertThatThrownBy(() -> manager(candidate(SpiVersion.of(1, 5), "10.3.2", driver("10.3.2"))))
			.isInstanceOf(SpiVersionIncompatibleException.class);
	}

	@Test
	void failsWhenRuntimeGemFireVersionDiffersEvenByPatch() {
		assertThatThrownBy(() -> manager(candidate(SpiVersion.of(1, 4), "10.3.2", driver("10.3.1"))))
			.isInstanceOfSatisfying(NativeVersionIncompatibleException.class, ex -> {
				assertThat(ex.stampedVersion()).isEqualTo("10.3.2");
				assertThat(ex.runtimeVersion()).isEqualTo("10.3.1");
			});
	}

	@Test
	void failsWhenDriverReportsNoRuntimeGemFireVersion() {
		assertThatThrownBy(() -> manager(candidate(SpiVersion.of(1, 4), "10.3.2", driver(null))))
			.isInstanceOf(NativeVersionIncompatibleException.class);
	}

	@Test
	void spiCheckPrecedesNativeCheck() {
		assertThatThrownBy(() -> manager(candidate(SpiVersion.of(2, 0), "10.3.2", driver("9.9.9"))))
			.isInstanceOf(SpiVersionIncompatibleException.class);
	}

	@Test
	void spiCheckRunsBeforeTheProviderIsInstantiated() {
		AtomicBoolean instantiated = new AtomicBoolean();
		assertThatThrownBy(() -> manager(candidate("test.Driver", SpiVersion.of(2, 0), "10.3.2", () -> {
			instantiated.set(true);
			return driver("10.3.2");
		}))).isInstanceOf(SpiVersionIncompatibleException.class);
		assertThat(instantiated).isFalse();
	}

	@Test
	void linkageErrorDuringInstantiationBecomesTypedBindException() {
		NoSuchMethodError cause = new NoSuchMethodError("Newer.spiMethod()");
		assertThatThrownBy(() -> manager(candidate("test.Driver", SpiVersion.of(1, 4), "10.3.2", () -> {
			throw cause;
		}))).isInstanceOfSatisfying(DriverBindException.class, ex -> {
			assertThat(ex).hasCause(cause);
			assertThat(ex.getMessage()).contains("test.Driver");
		});
	}

	@Test
	void serviceConfigurationErrorDuringInstantiationBecomesTypedBindException() {
		ServiceConfigurationError cause = new ServiceConfigurationError("cannot instantiate");
		assertThatThrownBy(() -> manager(candidate("test.Driver", SpiVersion.of(1, 4), "10.3.2", () -> {
			throw cause;
		}))).isInstanceOfSatisfying(DriverBindException.class, ex -> assertThat(ex).hasCause(cause));
	}

	@Test
	void eachManagerResolvesIndependently() {
		GemFireDriver first = driver("10.1.1");
		GemFireDriver second = driver("10.3.2");
		assertThat(manager(candidate(SpiVersion.of(1, 0), "10.1.1", first)).driver()).isSameAs(first);
		assertThat(manager(candidate(SpiVersion.of(1, 0), "10.3.2", second)).driver()).isSameAs(second);
	}

	@Test
	void exposesTheSpiVersionItValidatedAgainst() {
		assertThat(manager(candidate(SpiVersion.of(1, 0), "10.1.1", driver("10.1.1"))).spiVersion())
			.isEqualTo(RUNTIME_SPI);
	}

}
