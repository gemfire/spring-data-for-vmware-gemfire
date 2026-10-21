/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface DriverRegionEvent<K, V> extends DriverCacheEvent<K, V> {
	boolean isReinitializing();
}
