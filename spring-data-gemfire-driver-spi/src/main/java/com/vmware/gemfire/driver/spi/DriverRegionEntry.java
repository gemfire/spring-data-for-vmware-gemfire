/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A native entry view; lifetime, equality and mutation follow the provider. */
public interface DriverRegionEntry<K, V> extends java.util.Map.Entry<K, V> {
	DriverRegion<K, V> getRegion();
	boolean isLocal();
	boolean isDestroyed();
	DriverCacheStatistics getStatistics();
	Object getUserAttribute();
	Object setUserAttribute(Object value);
}
