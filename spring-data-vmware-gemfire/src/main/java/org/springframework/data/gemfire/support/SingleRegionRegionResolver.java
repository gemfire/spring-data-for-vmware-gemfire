/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import com.vmware.gemfire.driver.spi.DriverRegion;

import org.springframework.data.gemfire.RegionResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RegionResolver} implementation resolving a single, configured {@link DriverRegion} object.
 *
 * @author John Blum
 * @see DriverRegion
 * @see RegionResolver
 * @since 2.3.0
 */
@SuppressWarnings("rawtypes")
public class SingleRegionRegionResolver implements RegionResolver {

	private final DriverRegion region;

	/**
	 * Constructs a new instance of {@link SingleRegionRegionResolver} with the given {@link DriverRegion}.
	 *
	 * @param region {@link DriverRegion} returned in the resolution process; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link DriverRegion} is {@literal null}.
	 * @see DriverRegion
	 */
	public SingleRegionRegionResolver(@NonNull DriverRegion region) {

		Assert.notNull(region, "Region must not be null");

		this.region = region;
	}

	/**
	 * Returns a reference to the configured {@link DriverRegion}.
	 *
	 * @param <K> {@link Class type} of the {@link DriverRegion} key.
	 * @param <V> {@link Class type} of the {@link DriverRegion} value.
	 * @return a reference to the configured {@link DriverRegion}.
	 * @see DriverRegion
	 */
	@SuppressWarnings("unchecked")
	protected @NonNull <K, V> DriverRegion<K, V> getRegion() {
		return this.region;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable <K, V> DriverRegion<K, V> resolve(@Nullable String regionName) {

		DriverRegion<K, V> region = getRegion();

		return region.name().equals(regionName) ? region : null;
	}
}
