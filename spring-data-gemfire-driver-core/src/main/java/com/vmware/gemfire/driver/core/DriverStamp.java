package com.vmware.gemfire.driver.core;

import java.util.Properties;

import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.SpiVersion;

/**
 * The build-generated stamp a Driver Impl jar carries (spec §6.2): the SPI {@code major.minor} the driver was built
 * against and the GemFire version it wraps. Immutable; parsed from a properties resource that is read before the
 * provider class is instantiated.
 */
record DriverStamp(SpiVersion builtAgainstSpiVersion, String gemFireVersion) {

	static final String SPI_VERSION_KEY = "spi.version";
	static final String GEMFIRE_VERSION_KEY = "gemfire.version";

	static DriverStamp parse(Properties properties, String providerClass) {
		String spi = require(properties, SPI_VERSION_KEY, providerClass);
		String gemFire = require(properties, GEMFIRE_VERSION_KEY, providerClass);
		try {
			return new DriverStamp(SpiVersion.parse(spi), gemFire.trim());
		}
		catch (IllegalArgumentException ex) {
			throw new DriverBindException("The stamp of Driver Impl " + providerClass + " is malformed: " + ex.getMessage(),
				ex);
		}
	}

	private static String require(Properties properties, String key, String providerClass) {
		String value = properties.getProperty(key);
		if (value == null || value.isBlank()) {
			throw new DriverBindException("The stamp of Driver Impl " + providerClass + " has no '" + key + "' entry.");
		}
		return value;
	}

}
