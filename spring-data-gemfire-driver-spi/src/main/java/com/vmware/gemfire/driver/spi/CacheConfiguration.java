/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Native-free client cache configuration. Property names and values are passed to the native
 * cache factory unchanged; validation belongs to the driver. Values can contain credentials, so
 * diagnostic output and equality/hash-code implementations never incorporate them.
 * See {@link ClientConfigurationProperties} for the retained client property names.
 *
 * <p>An optional default-pool configuration is applied to the native cache factory before the cache
 * is created. Absence means no explicit default pool was requested, so the native cache selects or
 * creates its own default pool lazily. Presence, even with no endpoints, is an explicit request:
 * the native cache fills a missing endpoint with the local host name as a server on the default
 * server port (40404), not a locator. The
 * literal names {@code DEFAULT} and {@code gemfirePool} carry no meaning here; the selected pool
 * is only identified by {@link DriverConnection#getDefaultPool()}.
 */
public final class CacheConfiguration implements DriverConfiguration {

	private final Map<String, String> properties;
	private final PoolConfiguration defaultPool;

	public CacheConfiguration(Map<String, String> properties) {
		this(properties, null);
	}

	/** {@code defaultPool} is nullable; null means no explicit default pool is requested. */
	public CacheConfiguration(Map<String, String> properties, PoolConfiguration defaultPool) {
		this.properties = Map.copyOf(Objects.requireNonNull(properties, "properties"));
		this.defaultPool = defaultPool;
	}

	/** The explicit default-pool settings, or empty when none were requested. */
	public Optional<PoolConfiguration> defaultPool() {
		return Optional.ofNullable(defaultPool);
	}

	/** A fresh copy for the driver; callers must not log these values. */
	public Properties properties() {
		Properties copy = new Properties();
		copy.putAll(properties);
		return copy;
	}

	@Override
	public String toString() {
		return "CacheConfiguration[properties=<redacted>]";
	}
}
