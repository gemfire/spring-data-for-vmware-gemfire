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

import org.springframework.data.gemfire.RegionResolver;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RegionResolver} implementation resolving a single, configured {@link GudRegion} object.
 *
 * @author John Blum
 * @see GudRegion
 * @see RegionResolver
 * @since 2.3.0
 */
@SuppressWarnings("rawtypes")
public class SingleRegionRegionResolver implements RegionResolver {

	private final GudRegion region;

	/**
	 * Constructs a new instance of {@link SingleRegionRegionResolver} with the given {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} returned in the resolution process; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link GudRegion} is {@literal null}.
	 * @see GudRegion
	 */
	public SingleRegionRegionResolver(@NonNull GudRegion region) {

		Assert.notNull(region, "Region must not be null");

		this.region = region;
	}

	/**
	 * Returns a reference to the configured {@link GudRegion}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @return a reference to the configured {@link GudRegion}.
	 * @see GudRegion
	 */
	@SuppressWarnings("unchecked")
	protected @NonNull <K, V> GudRegion<K, V> getRegion() {
		return this.region;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable <K, V> GudRegion<K, V> resolve(@Nullable String regionName) {

		GudRegion<K, V> region = getRegion();

		return region.getName().equals(regionName) ? region : null;
	}
}
