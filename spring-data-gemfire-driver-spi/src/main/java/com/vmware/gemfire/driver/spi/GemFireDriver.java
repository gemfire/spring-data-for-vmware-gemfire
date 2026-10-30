/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface GemFireDriver {

	DriverMetadata metadata();

	/**
	 * The GemFire product version actually present on the classpath this Driver Impl is co-located
	 * with, read locally from the native jars' build metadata (§6.3). Exposed so driver-core, which
	 * has no native GemFire dependency, can compare it with the GemFire version stamped into the
	 * driver at build time (exact match) and report it in a failure.
	 */
	String runtimeGemFireVersion();

	/**
	 * Discovers the regular property file without connecting or reading its contents. Native
	 * drivers consult their current process configuration and native file/classpath search rules
	 * on each call. This is discovery, not provenance of an existing connection's configuration;
	 * concurrent process-property changes are not an atomic snapshot. Diagnostics are redacted,
	 * but explicitly accessed filenames/URLs may contain sensitive information.
	 * The mock returns {@code gemfire.properties} with no location and performs no ambient lookup.
	 */
	default DriverPropertyFile resolvePropertiesFile() {
		throw UnsupportedFeatureException.notIntroduced("Driver.resolvePropertiesFile");
	}

	/**
	 * Discovers the security property file under the same rules as {@link #resolvePropertiesFile()}.
	 * The mock returns {@code gfsecurity.properties} with no location. Discovery itself does not
	 * load or apply settings; native connection creation retains its own property-loading rules.
	 */
	default DriverPropertyFile resolveSecurityPropertiesFile() {
		throw UnsupportedFeatureException.notIntroduced("Driver.resolveSecurityPropertiesFile");
	}

	/**
	 * Establishes the runtime connection this driver mediates. Thread-safety is whatever the
	 * underlying GemFire client/cache naturally provides — the SPI adds no additional
	 * guarantee (§5.6).
	 */
	DriverConnection connect(DriverConfiguration configuration) throws DriverBindException;

}
