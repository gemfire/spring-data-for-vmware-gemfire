/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import com.vmware.gemfire.driver.spi.*;

final class MockRegionAttributesMutator<K, V> implements DriverRegionAttributesMutator<K, V> {
	private final MockRegion<K, V> region;
	MockRegionAttributesMutator(MockRegion<K, V> region) { this.region = region; }
	@Override public DriverRegion<K, V> getRegion() { return region; }
	@Override public boolean getCloningEnabled() { return region.getAttributes().getCloningEnabled(); }
	@Override public void setCloningEnabled(boolean enabled) {
		region.checkOpen(); if (enabled) throw UnsupportedFeatureException.notIntroduced("RegionConfiguration.cloningEnabled");
	}
	@Override public void addGatewaySenderId(String id) {
		region.checkOpen();
		if (id == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null identifier", null);
		throw UnsupportedFeatureException.notIntroduced("AttributesMutator.addGatewaySenderId");
	}
	@Override public void removeGatewaySenderId(String id) {
		region.checkOpen();
		if (id == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null identifier", null);
	}
	@Override public void addAsyncEventQueueId(String id) {
		region.checkOpen();
		if (id == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null identifier", null);
		throw UnsupportedFeatureException.notIntroduced("AttributesMutator.addAsyncEventQueueId");
	}
	@Override public void removeAsyncEventQueueId(String id) {
		region.checkOpen();
		if (id == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null identifier", null);
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
}
