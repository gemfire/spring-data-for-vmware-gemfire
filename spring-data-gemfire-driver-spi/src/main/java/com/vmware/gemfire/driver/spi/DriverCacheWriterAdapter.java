/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Explicit opt-in to permitting before-events without application processing. */
public abstract class DriverCacheWriterAdapter<K, V> implements DriverCacheWriter<K, V> {
	@Override public void beforeCreate(DriverEntryEvent<K, V> event) { }
	@Override public void beforeUpdate(DriverEntryEvent<K, V> event) { }
	@Override public void beforeDestroy(DriverEntryEvent<K, V> event) { }
	@Override public void beforeRegionClear(DriverRegionEvent<K, V> event) { }
	@Override public void beforeRegionDestroy(DriverRegionEvent<K, V> event) { }
	@Override public void close() { }
}
