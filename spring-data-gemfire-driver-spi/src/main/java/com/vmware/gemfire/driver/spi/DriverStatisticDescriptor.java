/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/**
 * Driver-owned descriptor. Metadata is fixed; the ID is -1 until type creation assigns it.
 * Reusing a descriptor in another type can reassign its ID, including in earlier handles.
 * Create fresh descriptors for independent types. Ordering compares names lexically.
 */
public interface DriverStatisticDescriptor extends Comparable<DriverStatisticDescriptor> {
	String getName();
	String getDescription();
	String getUnit();
	ValueType getType();
	boolean isCounter();
	boolean isLargerBetter();
	int getId();
}
