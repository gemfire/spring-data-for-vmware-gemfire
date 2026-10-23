/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

/** Local settings only. Distribution, statistics, copying and disk effects are never simulated. */
final class MockRegionAttributes<K, V> implements DriverRegionAttributes<K, V> {
	private final MockRegion<K, V> region;
	private final DataPolicy dataPolicy = DataPolicy.NORMAL;
	private final Scope scope = Scope.LOCAL;
	private final Class<K> keyConstraint;
	private final Class<V> valueConstraint;
	private final Integer initialCapacity;
	private final Float loadFactor;
	private final Integer concurrencyLevel;
	private final Boolean concurrencyChecksEnabled;
	private final Boolean statisticsEnabled;
	private final Boolean ignoreJTA;
	private final String poolName;
	private final Boolean cloningEnabled;
	private final String diskStoreName;
	private final Boolean diskSynchronous;
	private final Boolean indexMaintenanceSynchronous;
	MockRegionAttributes(MockRegion<K, V> region, RegionConfiguration<K, V> config) {
		this.region = region;
		if (config == null) throw invalid("Null region configuration");
		keyConstraint = config.keyConstraint().isSet() ? config.keyConstraint().value() : null;
		valueConstraint = config.valueConstraint().isSet() ? config.valueConstraint().value() : null;
		initialCapacity = config.initialCapacity().isSet() ? config.initialCapacity().value() : 16;
		loadFactor = config.loadFactor().isSet() ? config.loadFactor().value() : 0.75f;
		concurrencyLevel = config.concurrencyLevel().isSet() ? config.concurrencyLevel().value() : 16;
		concurrencyChecksEnabled = config.concurrencyChecksEnabled().isSet() ? config.concurrencyChecksEnabled().value() : true;
		statisticsEnabled = config.statisticsEnabled().isSet() ? config.statisticsEnabled().value() : false;
		ignoreJTA = false;
		poolName = config.poolName().isSet() ? config.poolName().value() : null;
		cloningEnabled = config.cloningEnabled().isSet() ? config.cloningEnabled().value() : false;
		diskStoreName = config.diskStoreName().isSet() ? config.diskStoreName().value() : null;
		diskSynchronous = config.diskSynchronous().isSet() ? config.diskSynchronous().value() : true;
		indexMaintenanceSynchronous = true;
		if (config.serverRegionName().isSet()) unsupported("serverRegionName");

		if (initialCapacity < 0 || concurrencyLevel <= 0 || !(loadFactor > 0)) throw invalid("Invalid region sizing configuration");
		if (keyConstraint != null && keyConstraint.isPrimitive() || valueConstraint != null && valueConstraint.isPrimitive()) throw invalid("Primitive constraints are invalid");
		if (statisticsEnabled) unsupported("statisticsEnabled");
		if (cloningEnabled) unsupported("cloningEnabled");
		if (poolName != null && !poolName.isEmpty()) unsupported("poolName");
		if (diskStoreName != null) unsupported("diskStoreName");
	}
	private void unsupported(String setting) { throw UnsupportedFeatureException.notIntroduced("RegionConfiguration." + setting); }
	private DriverOperationException invalid(String message) { return new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, message, null); }
	@Override public DataPolicy getDataPolicy() { return dataPolicy; }
	@Override public Scope getScope() { return scope; }
	@Override public Class<K> getKeyConstraint() { return keyConstraint; }
	@Override public Class<V> getValueConstraint() { return valueConstraint; }
	@Override public int getInitialCapacity() { return initialCapacity; }
	@Override public float getLoadFactor() { return loadFactor; }
	@Override public int getConcurrencyLevel() { return concurrencyLevel; }
	@Override public boolean getConcurrencyChecksEnabled() { return concurrencyChecksEnabled; }
	@Override public boolean getStatisticsEnabled() { return statisticsEnabled; }
	@Override public boolean getIgnoreJTA() { return ignoreJTA; }
	@Override public String getPoolName() { return poolName; }
	@Override public boolean getCloningEnabled() { return cloningEnabled; }
	@Override public String getDiskStoreName() { return diskStoreName; }
	@Override public boolean isDiskSynchronous() { return diskSynchronous; }
	@Override public boolean getIndexMaintenanceSynchronous() { return indexMaintenanceSynchronous; }
	@Override public List<DriverCacheListener<K, V>> getCacheListeners() { return region.getCacheListeners(); }
	@Override public DriverCacheLoader<K, V> getCacheLoader() { return region.getCacheLoader(); }
	@Override public DriverCacheWriter<K, V> getCacheWriter() { return region.getCacheWriter(); }
}
