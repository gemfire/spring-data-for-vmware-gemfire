/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import com.vmware.gemfire.driver.spi.config.DataPolicy;
import com.vmware.gemfire.driver.spi.config.Scope;
import com.vmware.gemfire.driver.spi.config.SubscriptionAttributes;
import java.util.List;

/** Provider-backed region settings. Callback identities are the application objects. */
public interface DriverRegionAttributes<K, V> {
	DataPolicy getDataPolicy();
	Scope getScope();
	Class<K> getKeyConstraint();
	Class<V> getValueConstraint();
	int getInitialCapacity();
	float getLoadFactor();
	int getConcurrencyLevel();
	boolean getConcurrencyChecksEnabled();
	boolean getStatisticsEnabled();
	boolean getIgnoreJTA();
	String getPoolName();
	boolean getCloningEnabled();
	String getDiskStoreName();
	boolean isDiskSynchronous();
	boolean getIndexMaintenanceSynchronous();
	/** Effective subscription policy. Client regions report ALL; there is no setter, as the native client factory has none. */
	SubscriptionAttributes getSubscriptionAttributes();
	List<DriverCacheListener<K, V>> getCacheListeners();
	DriverCacheLoader<K, V> getCacheLoader();
	DriverCacheWriter<K, V> getCacheWriter();
}
