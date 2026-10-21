/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Objects;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import com.vmware.gemfire.driver.spi.*;

/** Applies only explicit overrides, leaving shortcut/version defaults in the native factory. */
final class GemFireRegionConfiguration {
	<K, V> void apply(RegionConfiguration<K, V> config, RegionFactory<K, V> factory) {
		Objects.requireNonNull(config, "configuration");
		if (config.serverRegionName().isSet()) throw UnsupportedFeatureException.notIntroduced("RegionConfiguration.serverRegionName");
		config.dataPolicy().ifSet(value -> factory.setDataPolicy(org.apache.geode.cache.DataPolicy.valueOf(value.name())));
		config.scope().ifSet(value -> factory.setScope(org.apache.geode.cache.Scope.fromString(value.name())));
		config.keyConstraint().ifSet(factory::setKeyConstraint);
		config.valueConstraint().ifSet(factory::setValueConstraint);
		config.initialCapacity().ifSet(factory::setInitialCapacity);
		config.loadFactor().ifSet(factory::setLoadFactor);
		config.concurrencyLevel().ifSet(factory::setConcurrencyLevel);
		config.concurrencyChecksEnabled().ifSet(factory::setConcurrencyChecksEnabled);
		config.statisticsEnabled().ifSet(factory::setStatisticsEnabled);
		config.ignoreJTA().ifSet(factory::setIgnoreJTA);
		config.lockGrantor().ifSet(factory::setLockGrantor);
		config.multicastEnabled().ifSet(factory::setMulticastEnabled);
		config.poolName().ifSet(factory::setPoolName);
		config.cloningEnabled().ifSet(factory::setCloningEnabled);
		config.diskStoreName().ifSet(factory::setDiskStoreName);
		config.diskSynchronous().ifSet(factory::setDiskSynchronous);
		config.indexMaintenanceSynchronous().ifSet(factory::setIndexMaintenanceSynchronous);
		config.enableSubscriptionConflation().ifSet(factory::setEnableSubscriptionConflation);
		config.earlyAck().ifSet(factory::setEarlyAck);
		config.publisher().ifSet(factory::setPublisher);
		config.gatewaySenderIds().ifSet(values -> values.forEach(factory::addGatewaySenderId));
		config.asyncEventQueueIds().ifSet(values -> values.forEach(factory::addAsyncEventQueueId));
		config.callbacks().ifSet(callbacks -> {
			@SuppressWarnings("unchecked") org.apache.geode.cache.CacheListener<K, V>[] listeners = callbacks.listeners().stream()
				.map(listener -> new GemFireCacheListenerFactory().adapt(listener)).toArray(org.apache.geode.cache.CacheListener[]::new);
			factory.initCacheListeners(listeners);
			factory.setCacheLoader(callbacks.loader() == null ? null : new GemFireCacheLoader<>(callbacks.loader()));
			factory.setCacheWriter(callbacks.writer() == null ? null : new GemFireCacheWriter<>(callbacks.writer()));
		});
	}
	<K, V> void apply(RegionConfiguration<K, V> config, ClientRegionFactory<K, V> factory) {
		Objects.requireNonNull(config, "configuration");
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
		if (config.callbacks().isSet() && (config.callbacks().value().loader() != null || config.callbacks().value().writer() != null)) {
			throw UnsupportedFeatureException.notIntroduced("ClientRegion.creationLoaderWriter");
		}
		config.serverRegionName().ifSet(factory::setServerRegionName);
		config.keyConstraint().ifSet(factory::setKeyConstraint);
		config.valueConstraint().ifSet(factory::setValueConstraint);
		config.initialCapacity().ifSet(factory::setInitialCapacity);
		config.loadFactor().ifSet(factory::setLoadFactor);
		config.concurrencyLevel().ifSet(factory::setConcurrencyLevel);
		config.concurrencyChecksEnabled().ifSet(factory::setConcurrencyChecksEnabled);
		config.statisticsEnabled().ifSet(factory::setStatisticsEnabled);
		config.poolName().ifSet(factory::setPoolName);
		config.cloningEnabled().ifSet(factory::setCloningEnabled);
		config.diskStoreName().ifSet(factory::setDiskStoreName);
		config.diskSynchronous().ifSet(factory::setDiskSynchronous);
		config.callbacks().ifSet(callbacks -> {
			@SuppressWarnings("unchecked") org.apache.geode.cache.CacheListener<K, V>[] listeners = callbacks.listeners().stream()
				.map(listener -> new GemFireCacheListenerFactory().adapt(listener)).toArray(org.apache.geode.cache.CacheListener[]::new);
			factory.initCacheListeners(listeners);
		});
	}
}
