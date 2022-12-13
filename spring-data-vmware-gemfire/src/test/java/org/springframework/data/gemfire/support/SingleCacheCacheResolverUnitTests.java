/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Test;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.data.gemfire.CacheResolver;

/**
 * Unit Tests for {@link SingleCacheCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see Cache
 * @see ClientCache
 * @see CacheResolver
 * @see SingleCacheCacheResolver
 * @since 2.3.0
 */
public class SingleCacheCacheResolverUnitTests {

	@Test
	public void fromCacheReturnsCacheResolverResolvingCache() {

		Cache mockCache = mock(Cache.class);

		CacheResolver<Cache> cacheResolver = SingleCacheCacheResolver.from(mockCache);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isSameAs(mockCache);

		verifyNoInteractions(mockCache);
	}

	@Test
	public void fromNullCacheReturnsCacheResolverReturningNull() {

		CacheResolver<Cache> cacheResolver = SingleCacheCacheResolver.from((Cache) null);

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
