// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/**
 * Native-free context handed to a {@link DriverPartitionResolver} when routing a key.
 * The operation and new value may be null when the caller routes without them.
 */
public interface DriverEntryOperation<K, V> {
	DriverRegion<K, V> getRegion();
	DriverOperation getOperation();
	K getKey();
	Object getCallbackArgument();
	boolean isCallbackArgumentAvailable();
	V getNewValue();
}
