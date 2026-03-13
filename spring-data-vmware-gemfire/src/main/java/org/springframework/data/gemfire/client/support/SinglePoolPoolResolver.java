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

package org.springframework.data.gemfire.client.support;

import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link PoolResolver} implementation that resolves a single, configured {@link GudPool}.
 *
 * @author John Blum
 * @see GudPool
 * @see PoolResolver
 * @since 2.3.0
 */
public class SinglePoolPoolResolver implements PoolResolver {

	/**
	 * Factory method used to construct a new instance of {@link SinglePoolPoolResolver} from an instance of
	 * {@link GudClientCache} using the {@link GudClientCache#getDefaultPool()}  DEFAULT} {@link GudPool}.
	 *
	 * @param clientCache {@link GudClientCache} instance used to resolve the {@link GudClientCache#getDefaultPool() DEFAULT}
	 * {@link GudPool}.
	 * @return a new {@link SinglePoolPoolResolver} initialized with the {@literal DEFAULT} {@link GudPool}.
	 * @throws IllegalArgumentException if the {@link GudClientCache} or the {@link GudClientCache#getDefaultPool()} DEFAULT}
	 * {@link GudPool} is {@literal null}.
	 * @see GudClientCache
	 * @see GudClientCache#getDefaultPool()
	 */
	public static SinglePoolPoolResolver from(@NonNull GudClientCache clientCache) {

		Assert.notNull(clientCache, "ClientCache must not be null");

		return new SinglePoolPoolResolver(clientCache.getDefaultPool());
	}

	private final GudPool pool;

	/**
	 * Constructs an instance of {@link SinglePoolPoolResolver} initialized with the given {@link GudPool}
	 * returned during resolution.
	 *
	 * @param pool {@link GudPool} object resolved by this {@link PoolResolver}.
	 * @throws IllegalArgumentException if {@link GudPool} is {@literal null}.
	 * @see GudPool
	 */
	public SinglePoolPoolResolver(@NonNull GudPool pool) {

		Assert.notNull(pool, "Pool must not be null");

		this.pool = pool;
	}

	/**
	 * Returns a reference to the configured, "resolvable" {@link GudPool}.
	 *
	 * @return a reference to the configured, "resolvable" {@link GudPool}.
	 * @see GudPool
	 */
	protected @NonNull GudPool getPool() {
		return this.pool;
	}

	/**
	 * Returns the configured {@link GudPool} iff the given {@link String poolName} matches
	 * the configured {@link GudPool} {@link GudPool#getName() name}.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} to resolve.
	 * @return the configured {@link GudPool} if the configured {@link GudPool} {@link GudPool#getName() name}
	 * and the given {@link String poolName} match.
	 * @see GudPool#getName()
	 */
	@Nullable @Override
	public GudPool resolve(@Nullable String poolName) {

		GudPool pool = getPool();

		return pool.getName().equals(poolName) ? pool : null;
	}
}
