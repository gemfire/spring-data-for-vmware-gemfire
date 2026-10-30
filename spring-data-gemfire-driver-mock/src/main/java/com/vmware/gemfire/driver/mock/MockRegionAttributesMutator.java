/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.ExpirationAttributes;
import com.vmware.gemfire.driver.spi.config.TimeToLiveResetPolicy;

final class MockRegionAttributesMutator<K, V> implements DriverRegionAttributesMutator<K, V> {
	private final MockRegion<K, V> region;
	MockRegionAttributesMutator(MockRegion<K, V> region) { this.region = region; }
	@Override public DriverRegion<K, V> getRegion() { return region; }
	@Override public boolean getCloningEnabled() { return region.getAttributes().getCloningEnabled(); }
	@Override public void setCloningEnabled(boolean enabled) {
		region.checkOpen(); if (enabled) throw UnsupportedFeatureException.notIntroduced("RegionConfiguration.cloningEnabled");
	}
	@Override public void addCacheListener(DriverCacheListener<K, V> listener) { region.addCacheListener(listener); }
	@Override public void removeCacheListener(DriverCacheListener<K, V> listener) { region.removeCacheListener(listener); }
	@Override public void initCacheListeners(List<DriverCacheListener<K, V>> listeners) { region.initCacheListeners(listeners); }
	@Override public DriverCacheLoader<K, V> setCacheLoader(DriverCacheLoader<K, V> callback) {
		var previous = region.getCacheLoader(); region.setCacheLoader(callback); return previous;
	}
	@Override public DriverCacheWriter<K, V> setCacheWriter(DriverCacheWriter<K, V> callback) {
		var previous = region.getCacheWriter(); region.setCacheWriter(callback); return previous;
	}
	@Override public ExpirationAttributes setEntryTimeToLive(ExpirationAttributes attributes, TimeToLiveResetPolicy resetPolicy) {
		java.util.Objects.requireNonNull(resetPolicy, "resetPolicy");
		if (resetPolicy != TimeToLiveResetPolicy.LAST_MODIFIED) { region.checkOpen(); throw UnsupportedFeatureException.notIntroduced("RegionConfiguration.entryTimeToLiveResetPolicy"); }
		return disabledExpiration(attributes);
	}
	@Override public ExpirationAttributes setEntryIdleTimeout(ExpirationAttributes attributes) { return disabledExpiration(attributes); }
	@Override public ExpirationAttributes setRegionTimeToLive(ExpirationAttributes attributes) { return disabledExpiration(attributes); }
	@Override public ExpirationAttributes setRegionIdleTimeout(ExpirationAttributes attributes) { return disabledExpiration(attributes); }
	@Override public DriverCustomExpiry<K, V> setCustomEntryTimeToLive(DriverCustomExpiry<K, V> custom) { return disabledCustomExpiry(custom, "customEntryTimeToLive"); }
	@Override public DriverCustomExpiry<K, V> setCustomEntryIdleTimeout(DriverCustomExpiry<K, V> custom) { return disabledCustomExpiry(custom, "customEntryIdleTimeout"); }
	@Override public void setEvictionMaximum(int maximum) {
		region.checkOpen(); throw UnsupportedFeatureException.notIntroduced("RegionConfiguration.evictionAttributes");
	}
	/** Expiration is never simulated, so only the disabled value may be set; the previous value is always the disabled default. */
	private ExpirationAttributes disabledExpiration(ExpirationAttributes attributes) {
		region.checkOpen(); java.util.Objects.requireNonNull(attributes, "attributes");
		if (attributes.getTimeout() != 0) throw UnsupportedFeatureException.notIntroduced("RegionConfiguration.expiration");
		return ExpirationAttributes.DEFAULT;
	}
	private DriverCustomExpiry<K, V> disabledCustomExpiry(DriverCustomExpiry<K, V> custom, String setting) {
		region.checkOpen(); if (custom != null) throw UnsupportedFeatureException.notIntroduced("RegionConfiguration." + setting);
		return null;
	}
}
