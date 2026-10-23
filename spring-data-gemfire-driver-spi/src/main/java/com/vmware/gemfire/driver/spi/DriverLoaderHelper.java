/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Context valid during a client loader invocation. */
public interface DriverLoaderHelper<K, V> {

	K getKey();

	DriverRegion<K, V> getRegion();

	Object getArgument();
}
