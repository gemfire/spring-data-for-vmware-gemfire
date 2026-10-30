/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.List;
import java.util.function.Supplier;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.ExpirationAttributes;
import com.vmware.gemfire.driver.spi.config.TimeToLiveResetPolicy;

final class GemFireRegionAttributesMutator<K, V> implements DriverRegionAttributesMutator<K, V> {
	private final GemFireRegion<K, V> region;
	private final org.apache.geode.cache.AttributesMutator<K, V> mutator;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	private final GemFireExpirationConversions conversions = new GemFireExpirationConversions();
	GemFireRegionAttributesMutator(GemFireRegion<K, V> region) { this.region = region; this.mutator = region.region.getAttributesMutator(); }
	@Override public DriverRegion<K, V> getRegion() { return invoke("getRegion", () -> new GemFireRegion<>(mutator.getRegion())); }
	@Override public boolean getCloningEnabled() { return invoke("getCloningEnabled", mutator::getCloningEnabled); }
	@Override public void setCloningEnabled(boolean enabled) { invoke("setCloningEnabled", () -> { mutator.setCloningEnabled(enabled); return null; }); }
	@Override public void addCacheListener(DriverCacheListener<K, V> listener) { region.addCacheListener(listener); }
	@Override public void removeCacheListener(DriverCacheListener<K, V> listener) { region.removeCacheListener(listener); }
	@Override public void initCacheListeners(List<DriverCacheListener<K, V>> listeners) { region.initCacheListeners(listeners); }
	@Override public DriverCacheLoader<K, V> setCacheLoader(DriverCacheLoader<K, V> callback) {
		return invoke("setCacheLoader", () -> {
			var current = region.region.getAttributes().getCacheLoader();
			if (current != null && !(current instanceof GemFireCacheLoader<?, ?>)) throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheLoader");
			var replacement = current instanceof GemFireCacheLoader<?, ?> adapter && adapter.delegate() == callback
				? current : callback == null ? null : new GemFireCacheLoader<>(callback);
			var previous = mutator.setCacheLoader(replacement);
			if (previous == null) return null;
			if (previous instanceof GemFireCacheLoader<K, V> adapter) return adapter.delegate();
			throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheLoader");
		});
	}
	@Override public DriverCacheWriter<K, V> setCacheWriter(DriverCacheWriter<K, V> callback) {
		return invoke("setCacheWriter", () -> {
			var current = region.region.getAttributes().getCacheWriter();
			if (current != null && !(current instanceof GemFireCacheWriter<?, ?>)) throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheWriter");
			var replacement = current instanceof GemFireCacheWriter<?, ?> adapter && adapter.delegate == callback
				? current : callback == null ? null : new GemFireCacheWriter<>(callback);
			var previous = mutator.setCacheWriter(replacement);
			if (previous == null) return null;
			if (previous instanceof GemFireCacheWriter<K, V> adapter) return adapter.delegate;
			throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheWriter");
		});
	}
	@Override public ExpirationAttributes setEntryTimeToLive(ExpirationAttributes attributes, TimeToLiveResetPolicy resetPolicy) {
		return invoke("setEntryTimeToLive", () -> conversions.fromNative(mutator.setEntryTimeToLive(conversions.toNative(required(attributes)), conversions.toNative(required(resetPolicy)))));
	}
	@Override public ExpirationAttributes setEntryIdleTimeout(ExpirationAttributes attributes) {
		return invoke("setEntryIdleTimeout", () -> conversions.fromNative(mutator.setEntryIdleTimeout(conversions.toNative(required(attributes)))));
	}
	@Override public ExpirationAttributes setRegionTimeToLive(ExpirationAttributes attributes) {
		return invoke("setRegionTimeToLive", () -> conversions.fromNative(mutator.setRegionTimeToLive(conversions.toNative(required(attributes)))));
	}
	@Override public ExpirationAttributes setRegionIdleTimeout(ExpirationAttributes attributes) {
		return invoke("setRegionIdleTimeout", () -> conversions.fromNative(mutator.setRegionIdleTimeout(conversions.toNative(required(attributes)))));
	}
	@Override public DriverCustomExpiry<K, V> setCustomEntryTimeToLive(DriverCustomExpiry<K, V> custom) {
		return invoke("setCustomEntryTimeToLive", () -> conversions.fromNative(mutator.setCustomEntryTimeToLive(conversions.toNative(custom))));
	}
	@Override public DriverCustomExpiry<K, V> setCustomEntryIdleTimeout(DriverCustomExpiry<K, V> custom) {
		return invoke("setCustomEntryIdleTimeout", () -> conversions.fromNative(mutator.setCustomEntryIdleTimeout(conversions.toNative(custom))));
	}
	@Override public void setEvictionMaximum(int maximum) {
		invoke("setEvictionMaximum", () -> { mutator.getEvictionAttributesMutator().setMaximum(maximum); return null; });
	}
	private static <T> T required(T value) {
		if (value == null) throw new IllegalArgumentException("value must not be null");
		return value;
	}
	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException failure) { throw exceptions.translate("AttributesMutator." + operation, failure); }
	}
}
