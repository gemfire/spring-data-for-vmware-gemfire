/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import com.vmware.gemfire.driver.spi.DriverConnection;
import org.junit.Test;
import org.springframework.data.gemfire.CacheResolver;

/**
 * Unit Tests for {@link SingleCacheCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see com.vmware.gemfire.driver.spi.DriverConnection
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.support.SingleCacheCacheResolver
 * @since 2.3.0
 */
public class SingleCacheCacheResolverUnitTests {

	@Test
	public void fromCacheReturnsCacheResolverResolvingCache() {

		DriverConnection mockCache = mock(DriverConnection.class);

		CacheResolver<DriverConnection> cacheResolver = SingleCacheCacheResolver.from(mockCache);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isSameAs(mockCache);

		verifyNoInteractions(mockCache);
	}

	@Test
	public void fromNullCacheReturnsCacheResolverReturningNull() {

		CacheResolver<DriverConnection> cacheResolver = SingleCacheCacheResolver.from(null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isNull();
	}

	@Test
	public void fromClientCacheReturnsCacheResolverResolvingClientCache() {

		DriverConnection mockClientCache = mock(DriverConnection.class);

		CacheResolver<DriverConnection> clientCacheResolver = SingleCacheCacheResolver.from(mockClientCache);

		assertThat(clientCacheResolver).isNotNull();
		assertThat(clientCacheResolver.resolve()).isSameAs(mockClientCache);

		verifyNoInteractions(mockClientCache);
	}

	@Test
	public void fromNullClientCacheReturnsCacheResolverReturningNull() {

		CacheResolver<DriverConnection> cacheResolver = SingleCacheCacheResolver.from((DriverConnection) null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isNull();
	}
}
