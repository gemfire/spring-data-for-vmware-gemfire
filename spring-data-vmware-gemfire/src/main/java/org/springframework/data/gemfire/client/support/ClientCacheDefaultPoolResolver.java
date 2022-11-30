// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.client.support;

import java.util.Optional;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link PoolResolver} implementation used to resolve the {@literal DEFAULT} {@link Pool} from a {@link ClientCache}
 * instance by lazily resolving the {@link ClientCache} instance and calling {@link ClientCache#getDefaultPool()}
 * on {@literal DEFAULT} {@link Pool} resolution.
 *
 * @author John Blum
 * @see ClientCache
 * @see Pool
 * @see CacheResolver
 * @see PoolResolver
 * @since 2.3.0
 */
public class ClientCacheDefaultPoolResolver implements PoolResolver {

	private final CacheResolver<ClientCache> clientCacheResolver;

	/**
	 * Constructs a new instance of {@link ClientCacheDefaultPoolResolver} initialized with a {@link CacheResolver}
	 * used to lazily resolve the {@link ClientCache} instance on {@link Pool} resolution.
	 *
	 * @param clientCacheResolver {@link CacheResolver} used to lazily resolve the {@link ClientCache} instance;
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link CacheResolver} is {@literal null}.
	 * @see ClientCache
	 * @see CacheResolver
	 */
	public ClientCacheDefaultPoolResolver(@NonNull CacheResolver<ClientCache> clientCacheResolver) {

		Assert.notNull(clientCacheResolver, "CacheResolver for ClientCache must not be null");

		this.clientCacheResolver = clientCacheResolver;
	}

	/**
	 * Returns a reference to the configured {@link CacheResolver} used to (lazily) resolve
	 * the {@link ClientCache} instance.
	 *
	 * @return the configured {@link CacheResolver} used to resolve the {@link ClientCache} instance.
	 * @see ClientCache
	 * @see CacheResolver
	 */
	protected @NonNull CacheResolver<ClientCache> getClientCacheResolver() {
		return this.clientCacheResolver;
	}

	/**
	 * @inheritDoc
	 */
	@Nullable @Override
	public Pool resolve(@Nullable String poolName) {

		return Optional.of(getClientCacheResolver())
			.filter(it -> DEFAULT_POOL_NAME.equals(poolName))
			.map(CacheResolver::resolve)
			.map(ClientCache::getDefaultPool)
			.orElse(null);
	}
}
