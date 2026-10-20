/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Application callback for a region cache miss. Returning null leaves the value absent.
 * Once installed on a region, callback lifecycle follows the native engine: replacement,
 * removal or region/cache shutdown closes the installed loader. Callers implement both
 * operations explicitly; the SPI adds no callback synchronization or exception suppression.
 */
public interface DriverCacheLoader<K, V> extends AutoCloseable {

	V load(DriverLoaderHelper<K, V> helper);

	@Override
	void close();
}
