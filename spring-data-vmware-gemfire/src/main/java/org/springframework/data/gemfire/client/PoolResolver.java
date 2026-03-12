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

package org.springframework.data.gemfire.client;

import java.util.Optional;

import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;

import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link PoolResolver} is a strategy interface for resolving references to Apache Geode {@link GudPool} instances.
 *
 * This is used throughout SDG's codebase to separate SDG's {@link GudPool} resolution logic from being explicitly tied to
 * to Apache Geode's static PoolManager class. This interface also serves as an SPI for different strategies
 * when resolving a {@link GudPool}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see GudRegion
 * @see GudPool
 * @since 2.3.0
 */
@FunctionalInterface
public interface PoolResolver {

	String DEFAULT_POOL_NAME = CacheUtils.DEFAULT_POOL_NAME;

	/**
	 * Resolves the {@literal DEFAULT} {@link GudPool} from the given {@link GudClientCache} instance.
	 *
	 * @param clientCache {@link GudClientCache} instance from which to resolve the {@literal DEFAULT} {@link GudPool}.
	 * @return the configured {@literal DEFAULT} {@link GudPool} from the given {@link GudClientCache} instance.
	 * @see GudClientCache#getDefaultPool()
	 * @see GudClientCache
	 * @see GudPool
	 */
	default @Nullable GudPool resolve(@Nullable GudClientCache clientCache) {
		return clientCache != null ? clientCache.getDefaultPool() : null;
	}

	/**
	 * Resolves the {@link GudPool} instance used by the given {@link GudRegion}.
	 *
	 * If the {@link GudRegion} is a {@literal client} {@link GudRegion} but does not explicitly configure
	 * a specific {@link GudPool} reference, then the {@literal DEFAULT} {@link GudPool} is returned.
	 *
	 * If the {@link GudRegion} is {@literal local} or a {@link GudRegion}, then {@literal null}
	 * is returned.
	 *
 	 * @param region {@link GudRegion} from which to resolve the associated {@link GudPool}.
	 * @return the {@link GudPool} instance associated with the given {@link GudRegion},
	 * or the {@literal DEFAULT} {@link GudPool} if the {@link GudRegion} is a {@literal client} {@link GudRegion},
	 * or {@literal null} if the {@link GudRegion} is not a {@literal client} {@link GudRegion}.
	 * @see GudRegion
	 * @see GudPool
	 */
	default @Nullable GudPool resolve(@Nullable GudRegion<?, ?> region) {

		return Optional.ofNullable(region)
			.map(GudRegion::getAttributes)
			.map(GudRegionAttributes::getPoolName)
			.filter(StringUtils::hasText)
			.map(this::resolve)
			.orElse(null);
	}

	/**
	 * Resolves a {@link GudPool} with the given {@link String name}.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} to resolve.
	 * @return the {@link GudPool} with the given {@link String name} or {@literal null} if no {@link GudPool} exists with
	 * the {@link String name}.
	 * @see GudPool
	 */
	@Nullable GudPool resolve(@Nullable String poolName);

	/**
	 * Requires a {@link GudPool} object with the given {@link String name} to exist.
	 *
	 * @param poolName {@link String name} of the required {@link GudPool} to resolve.
	 * @return the required {@link GudPool} with the given {@link String name} or throw an {@link IllegalStateException}
	 * if a {@link GudPool} with {@link String name} does not exist!
	 * @throws IllegalStateException if a {@link GudPool} with the given {@link String name} does not exist.
	 * @see GudPool
	 * @see #resolve(String)
	 */
	default @NonNull GudPool require(@NonNull String poolName) {

		GudPool pool = resolve(poolName);

		Assert.state(pool != null,
			() -> String.format("Pool with name [%s] not found", poolName));

		return pool;
	}
}
