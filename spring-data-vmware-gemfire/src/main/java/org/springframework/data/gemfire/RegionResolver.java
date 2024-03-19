/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.Region;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link RegionResolver} interface is a {@literal Strategy} interface used to encapsulate different algorithms
 * (Strategies) used to resolve a cache {@link Region}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @since 2.3.0
 */
@FunctionalInterface
public interface RegionResolver {

	/**
	 * Returns a {@link Region} resolved with the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value;
	 * @param regionName {@link String name} of the {@link Region} to resolve; may be {@literal null}.
	 * @return the resolved {@link Region} with the given {@link String name}; may be {@literal null}.
	 * @see org.apache.geode.cache.Region
	 * @see java.lang.String
	 */
	@Nullable <K, V> Region<K, V> resolve(@Nullable String regionName);

	/**
	 * Requires a {@link Region} resolved from the given {@link String name}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value;
	 * @param regionName {@link String name} of the {@link Region} to resolve; must not be {@literal null}.
	 * @return the resolved {@link Region} with the given {@link String name}; never {@literal null}.
	 * @throws IllegalStateException if the resolved {@link Region} is {@literal null}, i.e. does not exist.
	 * @see org.apache.geode.cache.Region
	 * @see java.lang.String
	 * @see #resolve(String)
	 */
	default @NonNull <K, V> Region<K, V> require(@NonNull String regionName) {

		Region<K, V> region = StringUtils.hasText(regionName) ? resolve(regionName) : null;

		Assert.state(region != null,
			() -> String.format("Region with name [%s] not found", regionName));

		return region;
	}
}
