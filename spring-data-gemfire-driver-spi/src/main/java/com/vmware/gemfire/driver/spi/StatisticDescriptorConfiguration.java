/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Objects;

/** Definition for a local statistic. Deprecated native int statistics use LONG storage. */
public record StatisticDescriptorConfiguration(String name, String description, String unit,
		ValueType type, boolean counter, boolean largerBetter) {

	public enum ValueType { LONG, DOUBLE }

	public StatisticDescriptorConfiguration {
		Objects.requireNonNull(type, "type");
	}

	/** Counters default to larger-is-better; gauges default to smaller-is-better. */
	public StatisticDescriptorConfiguration(String name, String description, String unit,
			ValueType type, boolean counter) {
		this(name, description, unit, type, counter, counter);
	}
}
