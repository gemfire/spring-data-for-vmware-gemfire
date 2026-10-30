/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Objects;
import org.apache.geode.cache.client.ClientRegionFactory;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.TimeToLiveResetPolicy;

/** Applies only explicit overrides, leaving shortcut/version defaults in the native factory. */
final class GemFireRegionConfiguration {
	<K, V> void apply(RegionConfiguration<K, V> config, ClientRegionFactory<K, V> factory) {
		Objects.requireNonNull(config, "configuration");
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
		GemFireExpirationConversions conversions = new GemFireExpirationConversions();
		config.entryTimeToLive().ifSet(value -> factory.setEntryTimeToLive(conversions.toNative(value)));
		config.entryTimeToLiveResetPolicy().ifSet(policy -> {
			if (policy != TimeToLiveResetPolicy.LAST_MODIFIED) throw UnsupportedFeatureException.notIntroduced("ClientRegion.entryTimeToLiveResetPolicy");
		});
		config.entryIdleTimeout().ifSet(value -> factory.setEntryIdleTimeout(conversions.toNative(value)));
		config.regionTimeToLive().ifSet(value -> factory.setRegionTimeToLive(conversions.toNative(value)));
		config.regionIdleTimeout().ifSet(value -> factory.setRegionIdleTimeout(conversions.toNative(value)));
		config.customEntryTimeToLive().ifSet(value -> factory.setCustomEntryTimeToLive(conversions.toNative(value)));
		config.customEntryIdleTimeout().ifSet(value -> factory.setCustomEntryIdleTimeout(conversions.toNative(value)));
		config.evictionAttributes().ifSet(value -> factory.setEvictionAttributes(conversions.toNative(value)));
		config.compressor().ifSet(value -> factory.setCompressor(conversions.toNative(value)));
		config.callbacks().ifSet(callbacks -> {
			@SuppressWarnings("unchecked") org.apache.geode.cache.CacheListener<K, V>[] listeners = callbacks.listeners().stream()
				.map(listener -> new GemFireCacheListenerFactory().adapt(listener)).toArray(org.apache.geode.cache.CacheListener[]::new);
			factory.initCacheListeners(listeners);
		});
	}
}
