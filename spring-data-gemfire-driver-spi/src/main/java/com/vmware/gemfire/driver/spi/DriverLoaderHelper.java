/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Context valid during a loader invocation. Network search follows native region semantics. */
public interface DriverLoaderHelper<K, V> {

	K getKey();

	DriverRegion<K, V> getRegion();

	Object getArgument();

	/** Unavailable in synthetic callable contexts and the local-only mock. */
	V netSearch(boolean doNetLoad);
}
