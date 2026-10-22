package com.vmware.gemfire.driver.core;

import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import com.vmware.gemfire.driver.spi.AmbiguousDriverException;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.NativeVersionIncompatibleException;
import com.vmware.gemfire.driver.spi.NoDriverFoundException;
import com.vmware.gemfire.driver.spi.SpiVersion;
import com.vmware.gemfire.driver.spi.SpiVersionIncompatibleException;

/**
 * Discovers the single {@link GemFireDriver} on the classpath via {@link ServiceLoader} and validates it: its stamped
 * SPI version against the runtime SPI (spec §6.2), then its stamped GemFire version against the GemFire actually on
 * the classpath, by exact match (§6.3).
 *
 * <p>An ordinary instance — no static state, no singleton (spec §5.7). Resolution and validation happen once,
 * eagerly, in the constructor; the resolved driver is held in a {@code final} field, so it is safely published
 * to every thread that obtains the manager. A failed construction yields no manager, so there is no cached
 * failure state to reset. Consumers receive the manager by injection; the container owns its lifecycle.
 */
public final class GemFireDriverManager {

	private final SpiVersion spiVersion;
	private final GemFireDriver driver;

	public GemFireDriverManager() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public GemFireDriverManager(ClassLoader classLoader) {
		this(validateCore(), classLoader);
	}

	private GemFireDriverManager(SpiVersion runtime, ClassLoader classLoader) {
		this(DriverCandidate.discover(ServiceLoader.load(GemFireDriver.class, classLoader)), runtime);
	}

	private static SpiVersion validateCore() {
		SpiVersion runtime = SpiVersion.runtime();
		CoreStamp.read().validate(runtime);
		return runtime;
	}

	// Package-private: lets tests supply candidates and an SPI version without any static reset hook.
	GemFireDriverManager(List<DriverCandidate> candidates, SpiVersion spiVersion) {
		this.spiVersion = spiVersion;
		this.driver = resolveAndValidate(candidates);
	}

	public GemFireDriver driver() {
		return driver;
	}

	/** The SPI version this manager validated the driver against, for diagnostics. */
	public SpiVersion spiVersion() {
		return spiVersion;
	}

	private GemFireDriver resolveAndValidate(List<DriverCandidate> candidates) {
		if (candidates.isEmpty()) {
			throw new NoDriverFoundException();
		}
		if (candidates.size() > 1) {
			throw new AmbiguousDriverException(candidates.stream().map(DriverCandidate::providerClass).toList());
		}

		DriverCandidate candidate = candidates.get(0);
		validateSpiCompatibility(candidate);                 // §6.2 — from the stamp, before the provider is instantiated
		GemFireDriver instance = instantiate(candidate);     // §6.2 — LinkageError backstop
		validateRuntimeGemFireVersion(instance, candidate);  // §6.3 — sequenced after, and independent of, the SPI check
		return instance;
	}

	private void validateSpiCompatibility(DriverCandidate candidate) {
		SpiVersion builtAgainst = candidate.stamp().builtAgainstSpiVersion();
		if (!spiVersion.accepts(builtAgainst)) {
			throw new SpiVersionIncompatibleException(candidate.providerClass(), builtAgainst, spiVersion);
		}
	}

	private GemFireDriver instantiate(DriverCandidate candidate) {
		try {
			return candidate.instantiate();
		}
		catch (LinkageError | ServiceConfigurationError ex) {
			throw new DriverBindException("Driver Impl " + candidate.providerClass() + " could not be loaded: "
				+ ex, ex);
		}
	}

	// driver-core has no native GemFire dependency, so the runtime version comes from the driver (§5.1).
	private void validateRuntimeGemFireVersion(GemFireDriver instance, DriverCandidate candidate) {
		String runtimeVersion;
		try {
			runtimeVersion = instance.runtimeGemFireVersion();
		}
		catch (LinkageError | ServiceConfigurationError ex) {
			throw new DriverBindException("Driver Impl " + candidate.providerClass()
				+ " could not report its runtime GemFire version.", ex);
		}
		String stampedVersion = candidate.stamp().gemFireVersion();
		if (!stampedVersion.equals(runtimeVersion)) {
			throw new NativeVersionIncompatibleException(candidate.providerClass(), stampedVersion, runtimeVersion);
		}
	}

}
