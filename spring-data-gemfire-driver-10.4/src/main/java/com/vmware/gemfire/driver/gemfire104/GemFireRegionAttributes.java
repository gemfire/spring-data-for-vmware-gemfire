/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.List;
import java.util.function.Supplier;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.DataPolicy;
import com.vmware.gemfire.driver.spi.config.Scope;

final class GemFireRegionAttributes<K, V> implements DriverRegionAttributes<K, V> {
	private final GemFireRegion<K, V> region;
	private final org.apache.geode.cache.RegionAttributes<K, V> attributes;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireRegionAttributes(GemFireRegion<K, V> region) { this.region = region; this.attributes = region.region.getAttributes(); }
	@Override public DataPolicy getDataPolicy() { return invoke("getDataPolicy", () -> DataPolicy.valueOf(attributes.getDataPolicy().toString())); }
	@Override public Scope getScope() { return invoke("getScope", () -> Scope.valueOf(attributes.getScope().toString())); }
	@Override public Class<K> getKeyConstraint() { return invoke("getKeyConstraint", () -> attributes.getKeyConstraint()); }
	@Override public Class<V> getValueConstraint() { return invoke("getValueConstraint", () -> attributes.getValueConstraint()); }
	@Override public int getInitialCapacity() { return invoke("getInitialCapacity", () -> attributes.getInitialCapacity()); }
	@Override public float getLoadFactor() { return invoke("getLoadFactor", () -> attributes.getLoadFactor()); }
	@Override public int getConcurrencyLevel() { return invoke("getConcurrencyLevel", () -> attributes.getConcurrencyLevel()); }
	@Override public boolean getConcurrencyChecksEnabled() { return invoke("getConcurrencyChecksEnabled", () -> attributes.getConcurrencyChecksEnabled()); }
	@Override public boolean getStatisticsEnabled() { return invoke("getStatisticsEnabled", () -> attributes.getStatisticsEnabled()); }
	@Override public boolean getIgnoreJTA() { return invoke("getIgnoreJTA", () -> attributes.getIgnoreJTA()); }
	@Override public String getPoolName() { return invoke("getPoolName", () -> attributes.getPoolName()); }
	@Override public boolean getCloningEnabled() { return invoke("getCloningEnabled", () -> attributes.getCloningEnabled()); }
	@Override public String getDiskStoreName() { return invoke("getDiskStoreName", () -> attributes.getDiskStoreName()); }
	@Override public boolean isDiskSynchronous() { return invoke("isDiskSynchronous", () -> attributes.isDiskSynchronous()); }
	@Override public boolean getIndexMaintenanceSynchronous() { return invoke("getIndexMaintenanceSynchronous", () -> attributes.getIndexMaintenanceSynchronous()); }
	@Override public List<DriverCacheListener<K, V>> getCacheListeners() { return region.getCacheListeners(); }
	@Override public DriverCacheLoader<K, V> getCacheLoader() { return region.getCacheLoader(); }
	@Override public DriverCacheWriter<K, V> getCacheWriter() { return region.getCacheWriter(); }
	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException failure) { throw exceptions.translate("RegionAttributes." + operation, failure); }
	}
}
