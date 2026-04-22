/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import org.junit.Test;
import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for {@link ClientCacheDefaultPoolResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.client.GudPool
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.client.PoolResolver
 * @see org.springframework.data.gemfire.client.support.ClientCacheDefaultPoolResolver
 * @since 2.3.0
 */
public class ClientCacheDefaultPoolResolverUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void constructClientCacheDefaultPoolResolver() {

		CacheResolver<GudClientCache> mockClientCacheResolver = mock(CacheResolver.class);

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

		GudClientCache mockClientCache = mock(GudClientCache.class);

		GudPool mockPool = mock(GudPool.class, PoolResolver.DEFAULT_POOL_NAME);

		when(mockClientCache.getDefaultPool()).thenReturn(mockPool);

		CacheResolver<GudClientCache> clientCacheResolver = () -> mockClientCache;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(clientCacheResolver);
		assertThat(poolResolver.resolve(PoolResolver.DEFAULT_POOL_NAME)).isEqualTo(mockPool);

		verify(mockClientCache, times(1)).getDefaultPool();
		verifyNoInteractions(mockPool);
	}

	@Test
	public void resolvePoolWhenClientCacheResolvesToNullIsNullSafe() {

		CacheResolver<GudClientCache> clientCacheResolver = () -> null;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(clientCacheResolver);
		assertThat(poolResolver.resolve(PoolResolver.DEFAULT_POOL_NAME)).isNull();
	}

	@Test
	public void resolvePoolWithNonDefaultPool() {

		GudClientCache mockClientCache = mock(GudClientCache.class);

		CacheResolver<GudClientCache> clientCacheResolver = () -> mockClientCache;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheResolver()).isEqualTo(clientCacheResolver);
		assertThat(poolResolver.resolve("CUSTOM")).isNull();

		verifyNoInteractions(mockClientCache);
	}
}
