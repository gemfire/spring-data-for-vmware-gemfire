/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

/** Mutates the owning region; callback replacements return the previous application object. */
public interface DriverRegionAttributesMutator<K, V> {
	DriverRegion<K, V> getRegion();
	void setCloningEnabled(boolean enabled);
	boolean getCloningEnabled();
	void addCacheListener(DriverCacheListener<K, V> listener);
	void removeCacheListener(DriverCacheListener<K, V> listener);
	void initCacheListeners(List<DriverCacheListener<K, V>> listeners);
	DriverCacheLoader<K, V> setCacheLoader(DriverCacheLoader<K, V> loader);
	DriverCacheWriter<K, V> setCacheWriter(DriverCacheWriter<K, V> writer);
}
