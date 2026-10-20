/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import com.vmware.gemfire.driver.spi.DriverRegion;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link RegionResolver} interface is a {@literal Strategy} interface used to encapsulate different algorithms
 * (Strategies) used to resolve a cache {@link DriverRegion}.
 *
 * @author John Blum
 * @see DriverRegion
 * @since 2.3.0
 */
@FunctionalInterface
public interface RegionResolver {

	/**
	 * Returns a {@link DriverRegion} resolved with the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link DriverRegion} key.
	 * @param <V> {@link Class type} of the {@link DriverRegion} value;
	 * @param regionName {@link String name} of the {@link DriverRegion} to resolve; may be {@literal null}.
	 * @return the resolved {@link DriverRegion} with the given {@link String name}; may be {@literal null}.
	 * @see DriverRegion
	 * @see String
	 */
	@Nullable <K, V> DriverRegion<K, V> resolve(@Nullable String regionName);

	/**
	 * Requires a {@link DriverRegion} resolved from the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link DriverRegion} key.
	 * @param <V> {@link Class type} of the {@link DriverRegion} value;
	 * @param regionName {@link String name} of the {@link DriverRegion} to resolve; must not be {@literal null}.
	 * @return the resolved {@link DriverRegion} with the given {@link String name}; never {@literal null}.
	 * @throws IllegalStateException if the resolved {@link DriverRegion} is {@literal null}, i.e. does not exist.
	 * @see DriverRegion
	 * @see String
	 * @see #resolve(String)
	 */
	default @NonNull <K, V> DriverRegion<K, V> require(@NonNull String regionName) {

		DriverRegion<K, V> region = StringUtils.hasText(regionName) ? resolve(regionName) : null;

		Assert.state(region != null,
			() -> String.format("Region with name [%s] not found", regionName));

		return region;
	}
}
