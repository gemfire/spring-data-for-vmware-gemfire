/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Objects;

import org.apache.geode.cache.RegionEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;

final class GemFireRegionLifecycleListener<K, V> extends CacheListenerAdapter<K, V> {

	private final DriverRegionLifecycleListener delegate;
	private final org.apache.geode.cache.Region<K, V> region;

	GemFireRegionLifecycleListener(DriverRegionLifecycleListener delegate, org.apache.geode.cache.Region<K, V> region) {
		this.region = region;
		this.delegate = Objects.requireNonNull(delegate, "Region lifecycle listener must not be null");
	}

	@Override
	public void afterRegionDestroy(RegionEvent<K, V> event) {
		delegate.afterRegionDestroy(new GemFireRegion<>(event.getRegion()));
	}

	@Override
	public void close() {
		// General listener reinitialization retains this registration. Native init closes even retained
		// callbacks; the resolver's region-owned lifetime ends only with region/cache destruction.
		if (region.isDestroyed() || region.getRegionService().isClosed()) delegate.close();
	}

	// Native registration deduplicates by equality. Preserve callback identity across SPI handles.
	@Override
	public boolean equals(Object other) {
		return other instanceof GemFireRegionLifecycleListener<?, ?> listener && delegate == listener.delegate;
	}

	@Override
	public int hashCode() { return System.identityHashCode(delegate); }
}
