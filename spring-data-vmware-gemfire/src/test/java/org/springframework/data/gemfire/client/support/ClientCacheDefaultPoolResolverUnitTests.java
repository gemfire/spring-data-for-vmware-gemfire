/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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

import java.util.function.Supplier;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.client.PoolResolver;

/**
 * Unit Tests for {@link ClientCacheDefaultPoolResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.Pool
 * @see java.util.function.Supplier
 * @see org.springframework.data.gemfire.client.PoolResolver
 * @see org.springframework.data.gemfire.client.support.ClientCacheDefaultPoolResolver
 * @since 2.3.0
 */
public class ClientCacheDefaultPoolResolverUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void constructClientCacheDefaultPoolResolver() {

		Supplier<ClientCache> mockClientCacheResolver = mock(Supplier.class);

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(mockClientCacheResolver);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheSupplier()).isEqualTo(mockClientCacheResolver);

		verifyNoInteractions(mockClientCacheResolver);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructClientCacheDefaultPoolResolverWithNullCacheResolver() {

		try {
			new ClientCacheDefaultPoolResolver(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Supplier for ClientCache must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void resolvePoolFromClientCacheReturnsDefaultPool() {

		ClientCache mockClientCache = mock(ClientCache.class);

		Pool mockPool = mock(Pool.class, PoolResolver.DEFAULT_POOL_NAME);

		when(mockClientCache.getDefaultPool()).thenReturn(mockPool);

		Supplier<ClientCache> clientCacheSupplier = () -> mockClientCache;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheSupplier);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheSupplier()).isEqualTo(clientCacheSupplier);
		assertThat(poolResolver.resolve(PoolResolver.DEFAULT_POOL_NAME)).isEqualTo(mockPool);

		verify(mockClientCache, times(1)).getDefaultPool();
		verifyNoInteractions(mockPool);
	}

	@Test
	public void resolvePoolWhenClientCacheResolvesToNullIsNullSafe() {

		Supplier<ClientCache> clientCacheSupplier = () -> null;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheSupplier);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheSupplier()).isEqualTo(clientCacheSupplier);
		assertThat(poolResolver.resolve(PoolResolver.DEFAULT_POOL_NAME)).isNull();
	}

	@Test
	public void resolvePoolWithNonDefaultPool() {

		ClientCache mockClientCache = mock(ClientCache.class);

		Supplier<ClientCache> clientCacheSupplier = () -> mockClientCache;

		ClientCacheDefaultPoolResolver poolResolver = new ClientCacheDefaultPoolResolver(clientCacheSupplier);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getClientCacheSupplier()).isEqualTo(clientCacheSupplier);
		assertThat(poolResolver.resolve("CUSTOM")).isNull();

		verifyNoInteractions(mockClientCache);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void invokesSupplierOnlyForExactDefaultNameAndPropagatesFailure() {
		Supplier<ClientCache> supplier = mock(Supplier.class);
		ClientCacheDefaultPoolResolver resolver = new ClientCacheDefaultPoolResolver(supplier);
		for (String name : new String[] { null, "", " ", "default", "DEFAULT ", "CUSTOM" }) {
			assertThat(resolver.resolve(name)).isNull();
		}
		verifyNoInteractions(supplier);
		RuntimeException failure = new IllegalStateException("failed");
		when(supplier.get()).thenThrow(failure);
		org.assertj.core.api.Assertions.assertThatThrownBy(() -> resolver.resolve(PoolResolver.DEFAULT_POOL_NAME))
			.isSameAs(failure);
		verify(supplier).get();
	}

	@Test
	public void nullDefaultPoolReturnsNull() {
		ClientCache cache = mock(ClientCache.class);
		assertThat(new ClientCacheDefaultPoolResolver(() -> cache).resolve(PoolResolver.DEFAULT_POOL_NAME)).isNull();
		verify(cache).getDefaultPool();
	}
}
