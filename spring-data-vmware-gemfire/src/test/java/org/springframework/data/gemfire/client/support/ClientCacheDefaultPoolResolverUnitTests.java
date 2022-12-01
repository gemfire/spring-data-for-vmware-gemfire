/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.client.PoolResolver;

/**
 * Unit Tests for {@link ClientCacheDefaultPoolResolver}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see ClientCache
 * @see Pool
 * @see CacheResolver
 * @see PoolResolver
 * @see ClientCacheDefaultPoolResolver
 * @since 2.3.0
 */
public class ClientCacheDefaultPoolResolverUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void constructClientCacheDefaultPoolResolver() {

		CacheResolver<ClientCache> mockClientCacheResolver = mock(CacheResolver.class);

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(mockClientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(mockClientCacheResolver);

		verifyNoInteractions(mockClientCacheResolver);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructClientCacheDefaultPoolResolverWithNullCacheResolver() {

		try {
			new ClientCacheDefaultPoolResolver(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("CacheResolver for ClientCache must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void resolvePoolFromClientCacheReturnsDefaultPool() {

		ClientCache mockClientCache = mock(ClientCache.class);

		Pool mockPool = mock(Pool.class, PoolResolver.DEFAULT_POOL_NAME);

		when(mockClientCache.getDefaultPool()).thenReturn(mockPool);

		CacheResolver<ClientCache> clientCacheResolver = () -> mockClientCache;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(clientCacheResolver);
		assertThat(poolResolver.resolve(PoolResolver.DEFAULT_POOL_NAME)).isEqualTo(mockPool);

		verify(mockClientCache, times(1)).getDefaultPool();
		verifyNoInteractions(mockPool);
	}

	@Test
	public void resolvePoolWhenClientCacheResolvesToNullIsNullSafe() {

		CacheResolver<ClientCache> clientCacheResolver = () -> null;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(clientCacheResolver);
		assertThat(poolResolver.resolve(PoolResolver.DEFAULT_POOL_NAME)).isNull();
	}

	@Test
	public void resolvePoolWithNonDefaultPool() {

		ClientCache mockClientCache = mock(ClientCache.class);

		CacheResolver<ClientCache> clientCacheResolver = () -> mockClientCache;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(clientCacheResolver);
		assertThat(poolResolver.resolve("CUSTOM")).isNull();

		verifyNoInteractions(mockClientCache);
	}
}
