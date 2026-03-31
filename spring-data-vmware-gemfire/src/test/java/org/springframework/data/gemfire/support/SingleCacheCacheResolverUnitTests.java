/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.junit.Test;
import org.springframework.data.gemfire.CacheResolver;

/**
 * Unit Tests for {@link SingleCacheCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.support.SingleCacheCacheResolver
 * @since 2.3.0
 */
public class SingleCacheCacheResolverUnitTests {

	@Test
	public void fromCacheReturnsCacheResolverResolvingCache() {

		GudClientCache mockCache = mock(GudClientCache.class);

		CacheResolver<GudClientCache> cacheResolver = SingleCacheCacheResolver.from(mockCache);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isSameAs(mockCache);

		verifyNoInteractions(mockCache);
	}

	@Test
	public void fromNullCacheReturnsCacheResolverReturningNull() {

		CacheResolver<GudClientCache> cacheResolver = SingleCacheCacheResolver.from(null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isNull();
	}

	@Test
	public void fromClientCacheReturnsCacheResolverResolvingClientCache() {

		GudClientCache mockClientCache = mock(GudClientCache.class);

		CacheResolver<GudClientCache> clientCacheResolver = SingleCacheCacheResolver.from(mockClientCache);

		assertThat(clientCacheResolver).isNotNull();
		assertThat(clientCacheResolver.resolve()).isSameAs(mockClientCache);

		verifyNoInteractions(mockClientCache);
	}

	@Test
	public void fromNullClientCacheReturnsCacheResolverReturningNull() {

		CacheResolver<GudClientCache> cacheResolver = SingleCacheCacheResolver.from((GudClientCache) null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isNull();
	}
}
