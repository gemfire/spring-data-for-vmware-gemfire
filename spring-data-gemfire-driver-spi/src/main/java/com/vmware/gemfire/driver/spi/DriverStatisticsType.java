/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

/** Named local statistics schema. Native arrays are replaced with immutable lists of live handles. */
public interface DriverStatisticsType {
	String getName();
	String getDescription();
	/** Declaration order is preserved even though LONG IDs precede DOUBLE IDs. */
	List<DriverStatisticDescriptor> getStatistics();
	/** Unknown names fail with INVALID_ARGUMENT. */
	DriverStatisticDescriptor nameToDescriptor(String name);
	int nameToId(String name);
}
