/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverRegion;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

/**
 * Spring cache manager backed by an injected Driver SPI connection. Explicit regions take precedence
 * over explicit names; either selection disables dynamic lookup. With neither, all root regions are
 * discovered at initialization and regions created later can be looked up dynamically.
 *
 * <p>This manager borrows its connection; the owner controls connection shutdown.
 */
public class GemfireCacheManager extends AbstractCacheManager {

	private final DriverConnection gemfireCache;
	private final AtomicBoolean dynamic = new AtomicBoolean(true);
	private Set<DriverRegion<?, ?>> regions;
	private Set<String> cacheNames;

	public GemfireCacheManager(DriverConnection gemfireCache) {
		Assert.notNull(gemfireCache, "A GemFire driver connection is required");
		this.gemfireCache = gemfireCache;
	}

	@Override
	protected Collection<Cache> loadCaches() {
		Collection<Cache> caches = new HashSet<>();
		for (DriverRegion<?, ?> region : resolveRegions()) {
			caches.add(newGemfireCache(assertRegionAvailable(region, region.name())));
		}
		return caches;
	}

	private Set<DriverRegion<?, ?>> resolveRegions() {
		assertCacheAvailable();
		if (regions != null && !regions.isEmpty()) {
			dynamic.set(false);
			return regions;
		}
		if (cacheNames != null && !cacheNames.isEmpty()) {
			dynamic.set(false);
			Set<DriverRegion<?, ?>> selected = new HashSet<>();
			for (String name : cacheNames) selected.add(regionFor(name));
			return selected;
		}
		dynamic.set(true);
		return gemfireCache.rootRegions();
	}

	protected GemfireCache newGemfireCache(DriverRegion<?, ?> region) {
		return new GemfireCache(region);
	}

	private DriverRegion<?, ?> regionFor(String name) {
		assertCacheAvailable();
		return assertRegionAvailable(gemfireCache.getRegion(name), name);
	}

	private void assertCacheAvailable() {
		Assert.state(!gemfireCache.isClosed(), "GemFire driver connection has been closed");
	}

	private DriverRegion<?, ?> assertRegionAvailable(DriverRegion<?, ?> region, String name) {
		Assert.state(region != null, () -> "No Region for cache name [" + name + "] was found");
		Assert.state(!region.isDestroyed(), () -> "Region [" + name + "] has been destroyed");
		return region;
	}

	@Override
	protected Cache getMissingCache(String name) {
		return isDynamic() ? newGemfireCache(regionFor(name)) : null;
	}

	protected boolean isDynamic() { return dynamic.get(); }

	protected DriverConnection getCache() { return gemfireCache; }

	/** Configure before initialization. Nonempty names disable dynamic lookup. */
	public void setCacheNames(Set<String> cacheNames) {
		this.cacheNames = cacheNames == null ? null : Set.copyOf(cacheNames);
	}

	/** Configure before initialization. Nonempty regions override names and disable dynamic lookup. */
	public void setRegions(Set<DriverRegion<?, ?>> regions) {
		this.regions = regions == null ? null : Set.copyOf(regions);
	}

	protected Set<DriverRegion<?, ?>> getRegions() { return regions; }
}
