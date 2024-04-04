/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;
import org.springframework.data.gemfire.CacheResolver;

/**
 * Unit Tests for {@link SingleCacheCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.support.SingleCacheCacheResolver
 * @since 2.3.0
 */
public class SingleCacheCacheResolverUnitTests {

	@Test
	public void fromCacheReturnsCacheResolverResolvingCache() {

		ClientCache mockCache = mock(ClientCache.class);

		CacheResolver<ClientCache> cacheResolver = SingleCacheCacheResolver.from(mockCache);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isSameAs(mockCache);

		verifyNoInteractions(mockCache);
	}

	@Test
	public void fromNullCacheReturnsCacheResolverReturningNull() {

		CacheResolver<ClientCache> cacheResolver = SingleCacheCacheResolver.from(null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isNull();
	}

	@Test
	public void fromClientCacheReturnsCacheResolverResolvingClientCache() {

		ClientCache mockClientCache = mock(ClientCache.class);

		CacheResolver<ClientCache> clientCacheResolver = SingleCacheCacheResolver.from(mockClientCache);

		assertThat(clientCacheResolver).isNotNull();
		assertThat(clientCacheResolver.resolve()).isSameAs(mockClientCache);

		verifyNoInteractions(mockClientCache);
	}

	@Test
	public void fromNullClientCacheReturnsCacheResolverReturningNull() {

		CacheResolver<ClientCache> cacheResolver = SingleCacheCacheResolver.from((ClientCache) null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isNull();
	}
}
