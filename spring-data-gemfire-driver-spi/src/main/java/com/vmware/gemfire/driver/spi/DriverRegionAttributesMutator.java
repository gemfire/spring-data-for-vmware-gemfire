/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import com.vmware.gemfire.driver.spi.config.ExpirationAttributes;
import com.vmware.gemfire.driver.spi.config.TimeToLiveResetPolicy;
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
	/** Each setter returns the previous value; null attributes are rejected. */
	ExpirationAttributes setEntryTimeToLive(ExpirationAttributes attributes, TimeToLiveResetPolicy resetPolicy);
	ExpirationAttributes setEntryIdleTimeout(ExpirationAttributes attributes);
	ExpirationAttributes setRegionTimeToLive(ExpirationAttributes attributes);
	ExpirationAttributes setRegionIdleTimeout(ExpirationAttributes attributes);
	/** A null callback clears it. The previous callback is returned and not closed. */
	DriverCustomExpiry<K, V> setCustomEntryTimeToLive(DriverCustomExpiry<K, V> custom);
	DriverCustomExpiry<K, V> setCustomEntryIdleTimeout(DriverCustomExpiry<K, V> custom);
	/** Updates the eviction maximum of a region whose algorithm supports one. */
	void setEvictionMaximum(int maximum);
}
