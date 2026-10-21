/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** General cache listener. Implement every callback, or deliberately opt into the no-op adapter.
 * Listener failures are handled by native listener dispatch and do not veto completed operations.
 */
public interface DriverCacheListener<K, V> extends DriverCacheCallback {
	void afterCreate(DriverEntryEvent<K, V> event);
	void afterUpdate(DriverEntryEvent<K, V> event);
	void afterInvalidate(DriverEntryEvent<K, V> event);
	void afterDestroy(DriverEntryEvent<K, V> event);
	void afterRegionInvalidate(DriverRegionEvent<K, V> event);
	void afterRegionDestroy(DriverRegionEvent<K, V> event);
	void afterRegionClear(DriverRegionEvent<K, V> event);
	void afterRegionCreate(DriverRegionEvent<K, V> event);
	void afterRegionLive(DriverRegionEvent<K, V> event);
}
