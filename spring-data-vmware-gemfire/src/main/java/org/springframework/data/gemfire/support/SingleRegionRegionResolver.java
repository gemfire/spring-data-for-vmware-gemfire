/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.RegionResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RegionResolver} implementation resolving a single, configured {@link Region} object.
 *
 * @author John Blum
 * @see Region
 * @see RegionResolver
 * @since 2.3.0
 */
@SuppressWarnings("rawtypes")
public class SingleRegionRegionResolver implements RegionResolver {

	private final Region region;

	/**
	 * Constructs a new instance of {@link SingleRegionRegionResolver} with the given {@link Region}.
	 *
	 * @param region {@link Region} returned in the resolution process; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link Region} is {@literal null}.
	 * @see Region
	 */
	public SingleRegionRegionResolver(@NonNull Region region) {

		Assert.notNull(region, "Region must not be null");

		this.region = region;
	}

	/**
	 * Returns a reference to the configured {@link Region}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value.
	 * @return a reference to the configured {@link Region}.
	 * @see Region
	 */
	@SuppressWarnings("unchecked")
	protected @NonNull <K, V> Region<K, V> getRegion() {
		return this.region;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public @Nullable <K, V> Region<K, V> resolve(@Nullable String regionName) {

		Region<K, V> region = getRegion();

		return region.getName().equals(regionName) ? region : null;
	}
}
