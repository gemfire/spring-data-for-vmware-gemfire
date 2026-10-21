/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Native serialization view, valid during callback delivery. No substitute serialization format. */
public interface DriverSerializedCacheValue<V> {
	byte[] getSerializedValue();
	V getDeserializedValue();
}
