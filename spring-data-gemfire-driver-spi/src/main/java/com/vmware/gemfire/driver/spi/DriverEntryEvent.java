/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Lazy entry payload. Unavailable and null values are distinct; serialized forms may be null. */
public interface DriverEntryEvent<K, V> extends DriverCacheEvent<K, V> {
	K getKey();
	V getOldValue();
	V getNewValue();
	DriverSerializedCacheValue<V> getSerializedOldValue();
	DriverSerializedCacheValue<V> getSerializedNewValue();
	DriverTransactionId getTransactionId();
	boolean hasClientOrigin();
	boolean isOldValueAvailable();
}
