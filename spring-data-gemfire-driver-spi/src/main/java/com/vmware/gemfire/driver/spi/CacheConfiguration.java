/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Native-free peer/client cache configuration. Property names and values are passed to the native
 * cache factory unchanged; validation belongs to the driver. Values can contain credentials, so
 * diagnostic output and equality/hash-code implementations never incorporate them.
 */
public final class CacheConfiguration implements DriverConfiguration {

	public enum Mode { PEER, CLIENT }

	private final Mode mode;
	private final Map<String, String> properties;

	public CacheConfiguration(Mode mode, Map<String, String> properties) {
		this.mode = Objects.requireNonNull(mode, "mode");
		this.properties = Map.copyOf(Objects.requireNonNull(properties, "properties"));
	}

	public Mode mode() {
		return mode;
	}

	/** A fresh copy for the driver; callers must not log these values. */
	public Properties properties() {
		Properties copy = new Properties();
		copy.putAll(properties);
		return copy;
	}

	@Override
	public String toString() {
		return "CacheConfiguration[mode=" + mode + ", properties=<redacted>]";
	}
}
