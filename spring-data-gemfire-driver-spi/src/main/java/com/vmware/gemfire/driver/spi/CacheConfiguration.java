/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Native-free client cache configuration. Property names and values are passed to the native
 * cache factory unchanged; validation belongs to the driver. Values can contain credentials, so
 * diagnostic output and equality/hash-code implementations never incorporate them.
 * See {@link ClientConfigurationProperties} for the retained client property names.
 */
public final class CacheConfiguration implements DriverConfiguration {

	private final Map<String, String> properties;

	public CacheConfiguration(Map<String, String> properties) {
		this.properties = Map.copyOf(Objects.requireNonNull(properties, "properties"));
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
