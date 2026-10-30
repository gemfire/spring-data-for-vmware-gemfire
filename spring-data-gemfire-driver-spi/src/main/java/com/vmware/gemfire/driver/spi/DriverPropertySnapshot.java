/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Detached string configuration values with redacted diagnostics. String defaults are flattened;
 * non-string keys and values are excluded, so native objects cannot escape through Properties.
 * Values may contain credentials. Only explicit access through {@link #properties()} reveals them.
 * Equality and hashing use object identity and do not incorporate configuration values.
 */
public final class DriverPropertySnapshot {

	private final Map<String, String> values;

	public DriverPropertySnapshot(Properties properties) {
		Objects.requireNonNull(properties, "properties");
		Map<String, String> copy = new HashMap<>();
		for (String name : properties.stringPropertyNames()) {
			copy.put(name, properties.getProperty(name));
		}
		this.values = Map.copyOf(copy);
	}

	/** A fresh mutable copy for trusted consumers. Do not log this copy or its values. */
	public Properties properties() {
		Properties copy = new Properties();
		copy.putAll(values);
		return copy;
	}

	@Override public String toString() { return "DriverPropertySnapshot[properties=<redacted>]"; }
}
