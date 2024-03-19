/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import java.util.Optional;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link PoolResolver} is a strategy interface for resolving references to Apache Geode {@link Pool} instances.
 *
 * This is used throughout SDG's codebase to separate SDG's {@link Pool} resolution logic from being explicitly tied to
 * to Apache Geode's static {@link org.apache.geode.cache.client.PoolManager} class.  This interfaces also serves
 * as an SPI for different strategies when resolving a {@link Pool}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.Pool
 * @since 2.3.0
 */
@FunctionalInterface
public interface PoolResolver {

	String DEFAULT_POOL_NAME = CacheUtils.DEFAULT_POOL_NAME;

	/**
	 * Resolves the {@literal DEFAULT} {@link Pool} from the given {@link ClientCache} instance.
	 *
	 * @param clientCache {@link ClientCache} instance from which to resolve the {@literal DEFAULT} {@link Pool}.
	 * @return the configured {@literal DEFAULT} {@link Pool} from the given {@link ClientCache} instance.
	 * @see org.apache.geode.cache.client.ClientCache#getDefaultPool()
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see org.apache.geode.cache.client.Pool
	 */
	default @Nullable Pool resolve(@Nullable ClientCache clientCache) {
		return clientCache != null ? clientCache.getDefaultPool() : null;
	}

	/**
	 * Resolves the {@link Pool} instance used by the given {@link Region}.
	 *
	 * If the {@link Region} is a {@literal client} {@link Region} but does not explicitly configure
	 * a specific {@link Pool} reference, then the {@literal DEFAULT} {@link Pool} is returned.
	 *
	 * If the {@link Region} is {@literal local} or a {@literal peer} {@link Region}, then {@literal null}
	 * is returned.
	 *
 	 * @param region {@link Region} from which to resolve the associated {@link Pool}.
	 * @return the {@link Pool} instance associated with the given {@link Region},
	 * or the {@literal DEFAULT} {@link Pool} if the {@link Region} is a {@literal client} {@link Region},
	 * or {@literal null} if the {@link Region} is not a {@literal client} {@link Region}.
	 * @see org.apache.geode.cache.Region
	 * @see org.apache.geode.cache.client.Pool
	 */
	default @Nullable Pool resolve(@Nullable Region<?, ?> region) {

		return Optional.ofNullable(region)
			.map(Region::getAttributes)
			.map(RegionAttributes::getPoolName)
			.filter(StringUtils::hasText)
			.map(this::resolve)
			.orElse(null);
	}

	/**
	 * Resolves a {@link Pool} with the given {@link String name}.
	 *
	 * @param poolName {@link String name} of the {@link Pool} to resolve.
	 * @return the {@link Pool} with the given {@link String name} or {@link null} if no {@link Pool} exists with
	 * the {@link String name}.
	 * @see org.apache.geode.cache.client.Pool
	 */
	@Nullable Pool resolve(@Nullable String poolName);

	/**
	 * Requires a {@link Pool} object with the given {@link String name} to exist.
	 *
	 * @param poolName {@link String name} of the required {@link Pool} to resolve.
	 * @return the required {@link Pool} with the given {@link String name} or throw an {@link IllegalStateException}
	 * if a {@link Pool} with {@link String name} does not exist!
	 * @throws IllegalStateException if a {@link Pool} with the given {@link String name} does not exist.
	 * @see org.apache.geode.cache.client.Pool
	 * @see #resolve(String)
	 */
	default @NonNull Pool require(@NonNull String poolName) {

		Pool pool = resolve(poolName);

		Assert.state(pool != null,
			() -> String.format("Pool with name [%s] not found", poolName));

		return pool;
	}
}
