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

import java.util.Optional;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link PoolResolver} implementation used to resolve the {@literal DEFAULT} {@link GudPool} from a {@link GudClientCache}
 * instance by lazily resolving the {@link GudClientCache} instance and calling {@link GudClientCache#getDefaultPool()}
 * on {@literal DEFAULT} {@link GudPool} resolution.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudPool
 * @see CacheResolver
 * @see PoolResolver
 * @since 2.3.0
 */
public class ClientCacheDefaultPoolResolver implements PoolResolver {

	private final CacheResolver<GudClientCache> clientCacheResolver;

	/**
	 * Constructs a new instance of {@link ClientCacheDefaultPoolResolver} initialized with a {@link CacheResolver}
	 * used to lazily resolve the {@link GudClientCache} instance on {@link GudPool} resolution.
	 *
	 * @param clientCacheResolver {@link CacheResolver} used to lazily resolve the {@link GudClientCache} instance;
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link CacheResolver} is {@literal null}.
	 * @see GudClientCache
	 * @see CacheResolver
	 */
	public ClientCacheDefaultPoolResolver(@NonNull CacheResolver<GudClientCache> clientCacheResolver) {

		Assert.notNull(clientCacheResolver, "CacheResolver for ClientCache must not be null");

		this.clientCacheResolver = clientCacheResolver;
	}

	/**
	 * Returns a reference to the configured {@link CacheResolver} used to (lazily) resolve
	 * the {@link GudClientCache} instance.
	 *
	 * @return the configured {@link CacheResolver} used to resolve the {@link GudClientCache} instance.
	 * @see GudClientCache
	 * @see CacheResolver
	 */
	protected @NonNull CacheResolver<GudClientCache> getClientCacheResolver() {
		return this.clientCacheResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable @Override
	public GudPool resolve(@Nullable String poolName) {

		return Optional.of(getClientCacheResolver())
			.filter(it -> DEFAULT_POOL_NAME.equals(poolName))
			.map(CacheResolver::resolve)
			.map(GudClientCache::getDefaultPool)
			.orElse(null);
	}
}
