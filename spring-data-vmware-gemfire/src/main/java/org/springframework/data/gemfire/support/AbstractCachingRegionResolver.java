/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;

import org.springframework.data.gemfire.RegionResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Caches borrowed region handles, serializing initial resolution and listener registration.
 * Destruction invalidates only the region's name: aliases and path keys retain their historical
 * behavior and are not invalidated. Resolution never creates or closes a connection.
 */
public abstract class AbstractCachingRegionResolver implements RegionResolver, DriverRegionLifecycleListener {

	private final Map<String, DriverRegion<?, ?>> nameToRegionCache = new ConcurrentHashMap<>();

	synchronized void cache(@Nullable DriverRegion<?, ?> region) {
		if (region != null && StringUtils.hasText(region.name())) {
			this.nameToRegionCache.put(region.name(), region);
		}
	}

	@Nullable @Override @SuppressWarnings("unchecked")
	public synchronized <K, V> DriverRegion<K, V> resolve(@Nullable String regionName) {
		return StringUtils.hasText(regionName)
			? (DriverRegion<K, V>) this.nameToRegionCache.computeIfAbsent(regionName, this::resolveAndRegisterListener)
			: null;
	}

	private DriverRegion<?, ?> resolveAndRegisterListener(String regionName) {
		DriverRegion<?, ?> region = doResolve(regionName);
		if (region != null) {
			region.addLifecycleListener(this);
		}
		return region;
	}

	/** Resolves a borrowed handle; null misses and failures are not cached. */
	@Nullable
	protected abstract <K, V> DriverRegion<K, V> doResolve(@Nullable String regionName);

	@Override
	public void afterRegionDestroy(@Nullable DriverRegion<?, ?> region) {
		if (region != null && StringUtils.hasText(region.name())) {
			remove(region.name());
		}
	}

	/** Listener cleanup does not close borrowed handles or independently invalidate cached names. */
	@Override
	public void close() { }

	synchronized void remove(@NonNull String regionName) {
		this.nameToRegionCache.remove(regionName);
	}
}
