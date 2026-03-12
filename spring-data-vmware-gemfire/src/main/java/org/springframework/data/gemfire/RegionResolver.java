/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link RegionResolver} interface is a {@literal Strategy} interface used to encapsulate different algorithms
 * (Strategies) used to resolve a cache {@link GudRegion}.
 *
 * @author John Blum
 * @see GudRegion
 * @since 2.3.0
 */
@FunctionalInterface
public interface RegionResolver {

	/**
	 * Returns a {@link GudRegion} resolved with the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value;
	 * @param regionName {@link String name} of the {@link GudRegion} to resolve; may be {@literal null}.
	 * @return the resolved {@link GudRegion} with the given {@link String name}; may be {@literal null}.
	 * @see GudRegion
	 * @see String
	 */
	@Nullable <K, V> GudRegion<K, V> resolve(@Nullable String regionName);

	/**
	 * Requires a {@link GudRegion} resolved from the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value;
	 * @param regionName {@link String name} of the {@link GudRegion} to resolve; must not be {@literal null}.
	 * @return the resolved {@link GudRegion} with the given {@link String name}; never {@literal null}.
	 * @throws IllegalStateException if the resolved {@link GudRegion} is {@literal null}, i.e. does not exist.
	 * @see GudRegion
	 * @see String
	 * @see #resolve(String)
	 */
	default @NonNull <K, V> GudRegion<K, V> require(@NonNull String regionName) {

		GudRegion<K, V> region = StringUtils.hasText(regionName) ? resolve(regionName) : null;

		Assert.state(region != null,
			() -> String.format("Region with name [%s] not found", regionName));

		return region;
	}
}
