/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Before-event callbacks. Throw a DriverOperationException with WRITER_VETO to reject an operation.
 * Any runtime failure aborts the operation; typed driver failures retain their identity.
 */
public interface DriverCacheWriter<K, V> extends DriverCacheCallback {
	void beforeCreate(DriverEntryEvent<K, V> event);
	void beforeUpdate(DriverEntryEvent<K, V> event);
	void beforeDestroy(DriverEntryEvent<K, V> event);
	void beforeRegionClear(DriverRegionEvent<K, V> event);
	void beforeRegionDestroy(DriverRegionEvent<K, V> event);
}
