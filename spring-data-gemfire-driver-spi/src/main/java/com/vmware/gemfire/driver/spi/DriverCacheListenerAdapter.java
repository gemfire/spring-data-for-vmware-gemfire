/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Explicit opt-in to ignoring callbacks an application does not need. */
public abstract class DriverCacheListenerAdapter<K, V> implements DriverCacheListener<K, V> {
	@Override public void afterCreate(DriverEntryEvent<K, V> event) { }
	@Override public void afterUpdate(DriverEntryEvent<K, V> event) { }
	@Override public void afterInvalidate(DriverEntryEvent<K, V> event) { }
	@Override public void afterDestroy(DriverEntryEvent<K, V> event) { }
	@Override public void afterRegionInvalidate(DriverRegionEvent<K, V> event) { }
	@Override public void afterRegionDestroy(DriverRegionEvent<K, V> event) { }
	@Override public void afterRegionClear(DriverRegionEvent<K, V> event) { }
	@Override public void afterRegionCreate(DriverRegionEvent<K, V> event) { }
	@Override public void afterRegionLive(DriverRegionEvent<K, V> event) { }
	@Override public void close() { }
}
