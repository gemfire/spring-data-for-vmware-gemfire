package com.vmware.gemfire.driver.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.GemFireDriver;

/**
 * A discovered Driver Impl that has not been instantiated yet: its provider class name, its parsed
 * {@link DriverStamp}, and a supplier that instantiates it (spec §5.2). Immutable. Nothing is instantiated until the
 * stamp has been checked.
 */
record DriverCandidate(String providerClass, DriverStamp stamp, Supplier<GemFireDriver> instantiator) {

	private static final String STAMP_LOCATION = "META-INF/gemfire-driver/";
	private static final String STAMP_SUFFIX = ".properties";

	GemFireDriver instantiate() {
		return instantiator.get();
	}

	/**
	 * Lists the providers without instantiating them and reads each one's stamp from the class loader that loaded the
	 * provider. A missing or malformed stamp is a bind failure, not a pass. Stateless: caches nothing (§5.7).
	 */
	static List<DriverCandidate> discover(ServiceLoader<GemFireDriver> loader) {
		try {
			return loader.stream().map(provider -> {
				Class<? extends GemFireDriver> type = provider.type();
				return new DriverCandidate(type.getName(), readStamp(type), provider::get);
			}).toList();
		}
		catch (ServiceConfigurationError | LinkageError ex) {
			throw new DriverBindException("Unable to load a GemFireDriver provider: " + ex.getMessage(), ex);
		}
	}

	private static DriverStamp readStamp(Class<? extends GemFireDriver> type) {
		String providerClass = type.getName();
		String resource = STAMP_LOCATION + providerClass + STAMP_SUFFIX;
		ClassLoader classLoader = type.getClassLoader();
		try (InputStream in = classLoader != null ? classLoader.getResourceAsStream(resource)
			: ClassLoader.getSystemResourceAsStream(resource)) {
			if (in == null) {
				throw new DriverBindException("Driver Impl " + providerClass + " has no stamp at " + resource
					+ "; it must be built with the driver stamp step (spec §6.2).");
			}
			Properties properties = new Properties();
			properties.load(in);
			return DriverStamp.parse(properties, providerClass);
		}
		catch (IOException | IllegalArgumentException ex) {
			throw new DriverBindException("Unable to read the stamp of Driver Impl " + providerClass + ".", ex);
		}
	}

}
