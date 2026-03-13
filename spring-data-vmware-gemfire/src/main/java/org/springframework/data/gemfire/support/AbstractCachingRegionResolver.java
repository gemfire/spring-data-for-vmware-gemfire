/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.support;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.gemfire.RegionResolver;
import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheListenerAdapter;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link RegionResolver} implementation capable of caching the results of a Region resolution (lookup) operation.
 *
 * @author John Blum
 * @see GudRegion
 * @see GudCacheListener
 * @see GudCacheListenerAdapter
 * @see RegionResolver
 * @since 2.3.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCachingRegionResolver extends GudCacheListenerAdapter implements RegionResolver {

	private final Map<String, GudRegion> nameToRegionCache = new ConcurrentHashMap<>();

	/**
	 * Internal, utility method to cache a {@link GudRegion} by {@link GudRegion#getName() name},
	 * overriding any existing cache entry for the given {@link GudRegion} if it already exists.
	 *
	 * @param region {@link GudRegion} to cache.
	 */
	synchronized void cache(@Nullable GudRegion region) {
		Optional.ofNullable(region)
			.filter(it -> StringUtils.hasText(region.getName()))
			.ifPresent(it -> this.nameToRegionCache.put(region.getName(), region));
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable @Override @SuppressWarnings("unchecked")
	public synchronized <K, V> GudRegion<K, V> resolve(@Nullable String regionName) {

		return StringUtils.hasText(regionName)
			? this.nameToRegionCache.computeIfAbsent(regionName, this::doResolveAndRegisterResolverAsCacheListener)
			: null;
	}

	/**
	 * Performs the actual {@link GudRegion} resolution operation to resolve a {@link GudRegion} with
	 * the given {@link String name} by calling {@link #doResolve(String)} and then registers
	 * this {@link RegionResolver} as a {@link GudCacheListener} with the resolved {@link GudRegion}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param regionName {@link String name} of the {@link GudRegion} to resolve.
	 * @return the resolved {@link GudRegion} with the given {@link String name}; may be {@literal null}.
	 * @see GudRegion
	 * @see String
	 * @see #doResolve(String)
	 */
	@SuppressWarnings("unchecked")
	<K, V> GudRegion<K, V> doResolveAndRegisterResolverAsCacheListener(String regionName) {

		return Optional.<GudRegion<K, V>>ofNullable(doResolve(regionName))
			//.filter(this::isResolverNotRegisteredAsCacheListener)
			.map(GudRegion::getAttributesMutator)
			.map(attributesMutator -> {
				attributesMutator.addCacheListener(AbstractCachingRegionResolver.this);
				return attributesMutator.getRegion();
			})
			.orElse(null);
	}

	@SuppressWarnings("unused")
	private boolean isResolverNotRegisteredAsCacheListener(GudRegion region) {
		return region != null && !isResolverRegisteredAsCacheListener(region);
	}

	private boolean isResolverRegisteredAsCacheListener(GudRegion region) {

		return Optional.ofNullable(region)
			.map(GudRegion::getAttributes)
			.map(GudRegionAttributes::getCacheListeners)
			.map(Arrays::asList)
			.filter(cacheListeners -> cacheListeners.contains(this))
			.isPresent();
	}

	/**
	 * Performs the actual {@link GudRegion} resolution operation to resolve a {@link GudRegion} with
	 * the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param regionName {@link String name} of the {@link GudRegion} to resolve.
	 * @return the resolved {@link GudRegion} with the given {@link String name}; may be {@literal null}.
	 * @see GudRegion
	 * @see String
	 */
	@Nullable
	protected abstract <K, V> GudRegion<K, V> doResolve(@Nullable String regionName);

	/**
	 * Clears the cache entry for the {@link GudRegion} identified by the {@link GudRegionEvent}.
	 *
	 * @param event {@link GudRegionEvent} object capturing the details of the {@link GudRegion} destroyed event.
	 * @see GudRegionEvent
	 * @see #remove(String)
	 */
	@Override
	public void afterRegionDestroy(@Nullable GudRegionEvent event) {

		Optional.ofNullable(event)
			.map(GudRegionEvent::getRegion)
			.map(GudRegion::getName)
			.filter(StringUtils::hasText)
			.ifPresent(this::remove);
	}

	/**
	 * Removes the cache entry for the cached {@link GudRegion} with the given {@link String name}.
	 *
	 * @param regionName {@link String name} of the {@link GudRegion} to remove from the cache.
	 * @see ConcurrentHashMap#remove(Object)
	 */
	synchronized void remove(@NonNull String regionName) {
		this.nameToRegionCache.remove(regionName);
	}
}
