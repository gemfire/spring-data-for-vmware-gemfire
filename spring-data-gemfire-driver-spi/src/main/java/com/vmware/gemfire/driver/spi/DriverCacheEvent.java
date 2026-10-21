/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Callback-scoped event view. Read values during delivery; the driver does not retain native events. */
public interface DriverCacheEvent<K, V> {
	DriverRegion<K, V> getRegion();
	DriverOperation getOperation();
	Object getCallbackArgument();
	boolean isCallbackArgumentAvailable();
	boolean isOriginRemote();
	DriverMember getDistributedMember();
}
