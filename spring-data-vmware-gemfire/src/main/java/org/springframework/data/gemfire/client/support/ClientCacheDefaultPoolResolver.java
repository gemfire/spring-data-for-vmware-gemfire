/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import java.util.Optional;

import java.util.function.Supplier;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

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
 * @see Supplier
 * @see PoolResolver
 * @since 2.3.0
 */
public class ClientCacheDefaultPoolResolver implements PoolResolver {

	private final Supplier<ClientCache> clientCacheSupplier;

	/**
	 * Constructs a new instance of {@link ClientCacheDefaultPoolResolver} initialized with a {@link Supplier}
	 * used to lazily resolve the {@link ClientCache} instance on {@link Pool} resolution.
	 *
	 * @param clientCacheSupplier {@link Supplier} used to lazily resolve the {@link ClientCache} instance;
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link Supplier} is {@literal null}.
	 * @see ClientCache
	 * @see Supplier
	 */
	public ClientCacheDefaultPoolResolver(@NonNull Supplier<ClientCache> clientCacheSupplier) {

		Assert.notNull(clientCacheSupplier, "Supplier for ClientCache must not be null");

		this.clientCacheSupplier = clientCacheSupplier;
	}

	/**
	 * Returns a reference to the configured {@link Supplier} used to (lazily) resolve
	 * the {@link ClientCache} instance.
	 *
	 * @return the configured {@link Supplier} used to resolve the {@link ClientCache} instance.
	 * @see ClientCache
	 * @see Supplier
	 */
	protected @NonNull Supplier<ClientCache> getClientCacheSupplier() {
		return this.clientCacheSupplier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable @Override
	public Pool resolve(@Nullable String poolName) {

		return Optional.of(getClientCacheSupplier())
			.filter(it -> DEFAULT_POOL_NAME.equals(poolName))
			.map(Supplier::get)
			.map(ClientCache::getDefaultPool)
			.orElse(null);
	}
}
