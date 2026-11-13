/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A prepared query. Parameters are positional; null parameters are rejected. */
public interface DriverQuery {
	String queryString();
	/** A {@link DriverSelectResults}, a scalar, a {@link DriverStruct}, or {@link DriverUndefined#INSTANCE}. */
	Object execute();
	Object execute(Object... parameters);
	DriverQueryStatistics statistics();
}
