/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import java.util.Set;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

/** Local settings only. Distribution, statistics, copying and disk effects are never simulated. */
final class MockRegionAttributes<K, V> implements DriverRegionAttributes<K, V> {
	private final MockRegion<K, V> region;
	private final DataPolicy dataPolicy;
	private final Scope scope;
	private final Class<K> keyConstraint;
	private final Class<V> valueConstraint;
	private final Integer initialCapacity;
	private final Float loadFactor;
	private final Integer concurrencyLevel;
	private final Boolean concurrencyChecksEnabled;
	private final Boolean statisticsEnabled;
	private final Boolean ignoreJTA;
	private final Boolean lockGrantor;
	private final Boolean multicastEnabled;
	private final String poolName;
	private final Boolean cloningEnabled;
	private final String diskStoreName;
	private final Boolean diskSynchronous;
	private final Boolean indexMaintenanceSynchronous;
	private final Boolean enableSubscriptionConflation;
	private final Boolean earlyAck;
	private final Boolean publisher;
	private final Set<String> gatewaySenderIds;
	private final Set<String> asyncEventQueueIds;
	MockRegionAttributes(MockRegion<K, V> region, RegionConfiguration<K, V> config, boolean client) {
		this.region = region;
		if (config == null) throw invalid("Null region configuration");
		dataPolicy = config.dataPolicy().isSet() ? config.dataPolicy().value() : DataPolicy.NORMAL;
		scope = config.scope().isSet() ? config.scope().value() : Scope.LOCAL;
		keyConstraint = config.keyConstraint().isSet() ? config.keyConstraint().value() : null;
		valueConstraint = config.valueConstraint().isSet() ? config.valueConstraint().value() : null;
		initialCapacity = config.initialCapacity().isSet() ? config.initialCapacity().value() : 16;
		loadFactor = config.loadFactor().isSet() ? config.loadFactor().value() : 0.75f;
		concurrencyLevel = config.concurrencyLevel().isSet() ? config.concurrencyLevel().value() : 16;
		concurrencyChecksEnabled = config.concurrencyChecksEnabled().isSet() ? config.concurrencyChecksEnabled().value() : true;
		statisticsEnabled = config.statisticsEnabled().isSet() ? config.statisticsEnabled().value() : false;
		ignoreJTA = config.ignoreJTA().isSet() ? config.ignoreJTA().value() : false;
		lockGrantor = config.lockGrantor().isSet() ? config.lockGrantor().value() : false;
		multicastEnabled = config.multicastEnabled().isSet() ? config.multicastEnabled().value() : false;
		poolName = config.poolName().isSet() ? config.poolName().value() : null;
		cloningEnabled = config.cloningEnabled().isSet() ? config.cloningEnabled().value() : false;
		diskStoreName = config.diskStoreName().isSet() ? config.diskStoreName().value() : null;
		diskSynchronous = config.diskSynchronous().isSet() ? config.diskSynchronous().value() : true;
		indexMaintenanceSynchronous = config.indexMaintenanceSynchronous().isSet() ? config.indexMaintenanceSynchronous().value() : true;
		enableSubscriptionConflation = config.enableSubscriptionConflation().isSet() ? config.enableSubscriptionConflation().value() : false;
		earlyAck = config.earlyAck().isSet() ? config.earlyAck().value() : false;
		publisher = config.publisher().isSet() ? config.publisher().value() : false;
		gatewaySenderIds = config.gatewaySenderIds().isSet() ? config.gatewaySenderIds().value() : Set.of();
		asyncEventQueueIds = config.asyncEventQueueIds().isSet() ? config.asyncEventQueueIds().value() : Set.of();
		if (config.serverRegionName().isSet()) unsupported("serverRegionName");
		if (client) {
			if (config.dataPolicy().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.dataPolicy");
			if (config.scope().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.scope");
			if (config.ignoreJTA().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.ignoreJTA");
			if (config.lockGrantor().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.lockGrantor");
			if (config.multicastEnabled().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.multicastEnabled");
			if (config.indexMaintenanceSynchronous().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.indexMaintenanceSynchronous");
			if (config.enableSubscriptionConflation().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.enableSubscriptionConflation");
			if (config.earlyAck().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.earlyAck");
			if (config.publisher().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.publisher");
			if (config.gatewaySenderIds().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.gatewaySenderIds");
			if (config.asyncEventQueueIds().isSet()) throw UnsupportedFeatureException.notIntroduced("ClientRegionConfiguration.asyncEventQueueIds");
		}
		if (initialCapacity < 0 || concurrencyLevel <= 0 || !(loadFactor > 0)) throw invalid("Invalid region sizing configuration");
		if (keyConstraint != null && keyConstraint.isPrimitive() || valueConstraint != null && valueConstraint.isPrimitive()) throw invalid("Primitive constraints are invalid");
		if (dataPolicy != DataPolicy.NORMAL) unsupported("dataPolicy");
		if (scope != Scope.LOCAL) unsupported("scope");
		if (statisticsEnabled) unsupported("statisticsEnabled");
		if (cloningEnabled) unsupported("cloningEnabled");
		if (lockGrantor) unsupported("lockGrantor");
		if (multicastEnabled) unsupported("multicastEnabled");
		if (enableSubscriptionConflation) unsupported("enableSubscriptionConflation");
		if (earlyAck) unsupported("earlyAck");
		if (publisher) unsupported("publisher");
		if (poolName != null && !poolName.isEmpty()) unsupported("poolName");
		if (diskStoreName != null) unsupported("diskStoreName");
		if (!gatewaySenderIds.isEmpty()) unsupported("gatewaySenderIds");
		if (!asyncEventQueueIds.isEmpty()) unsupported("asyncEventQueueIds");
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
	@Override public boolean isLockGrantor() { return lockGrantor; }
	@Override public boolean getMulticastEnabled() { return multicastEnabled; }
	@Override public String getPoolName() { return poolName; }
	@Override public boolean getCloningEnabled() { return cloningEnabled; }
	@Override public String getDiskStoreName() { return diskStoreName; }
	@Override public boolean isDiskSynchronous() { return diskSynchronous; }
	@Override public boolean getIndexMaintenanceSynchronous() { return indexMaintenanceSynchronous; }
	@Override public boolean getEnableSubscriptionConflation() { return enableSubscriptionConflation; }
	@Override public boolean getEarlyAck() { return earlyAck; }
	@Override public boolean getPublisher() { return publisher; }
	@Override public Set<String> getGatewaySenderIds() { return gatewaySenderIds; }
	@Override public Set<String> getAsyncEventQueueIds() { return asyncEventQueueIds; }
	@Override public List<DriverCacheListener<K, V>> getCacheListeners() { return region.getCacheListeners(); }
	@Override public DriverCacheLoader<K, V> getCacheLoader() { return region.getCacheLoader(); }
	@Override public DriverCacheWriter<K, V> getCacheWriter() { return region.getCacheWriter(); }
}
