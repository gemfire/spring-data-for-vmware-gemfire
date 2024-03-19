/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.client.PoolResolver;

/**
 * Unit Tests for {@link SinglePoolPoolResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see Pool
 * @see SinglePoolPoolResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SinglePoolPoolResolverUnitTests {

	@Mock
	private Pool mockPool;

	@Test
	public void constructSinglePoolPoolResolver() {

		SinglePoolPoolResolver poolResolver = new SinglePoolPoolResolver(this.mockPool);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getPool()).isEqualTo(this.mockPool);
	}

	@SuppressWarnings("all")
	@Test(expected = IllegalArgumentException.class)
	public void constructSinglePoolPoolResolverWithNullPoolThrowsIllegalArgumentException() {

		try {
			new SinglePoolPoolResolver(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Pool must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void fromClientCacheWithDefaultPool() {

		ClientCache mockClientCache = mock(ClientCache.class);

		when(mockClientCache.getDefaultPool()).thenReturn(this.mockPool);

		SinglePoolPoolResolver poolResolver = SinglePoolPoolResolver.from(mockClientCache);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getPool()).isEqualTo(this.mockPool);

		verify(mockClientCache, times(1)).getDefaultPool();
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromClientCacheWithNoDefaultPoolThrowsIllegalArgumentException() {

		ClientCache mockClientCache = mock(ClientCache.class);

		try {
			SinglePoolPoolResolver.from(mockClientCache);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Pool must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(mockClientCache, times(1)).getDefaultPool();
		}
	}

	@SuppressWarnings("all")
	@Test(expected = IllegalArgumentException.class)
	public void fromNullClientCacheThrowsIllegalArgumentException() {

		try {
			SinglePoolPoolResolver.from(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("ClientCache must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void resolvesReturnsPoolWhenNamesMatch() {

		PoolResolver poolResolver = new SinglePoolPoolResolver(this.mockPool);

		when(this.mockPool.getName()).thenReturn("TestPool");

		assertThat(poolResolver.resolve("TestPool")).isEqualTo(this.mockPool);

		verify(this.mockPool, times(1)).getName();
	}

	@Test
	public void resolveReturnsNullWhenNamesDoNotMatch() {

		PoolResolver poolResolver = new SinglePoolPoolResolver(this.mockPool);

		when(this.mockPool.getName()).thenReturn("TestPool");

		assertThat(poolResolver.resolve("MockPool")).isNull();
		assertThat(poolResolver.resolve("  ")).isNull();
		assertThat(poolResolver.resolve("")).isNull();

		verify(this.mockPool, times(3)).getName();
	}

	@Test
	public void resolveIsNullSafe() {

		PoolResolver poolResolver = new SinglePoolPoolResolver(this.mockPool);

		when(this.mockPool.getName()).thenReturn("TestPool");

		assertThat(poolResolver.resolve((String) null)).isNull();

		verify(this.mockPool, times(1)).getName();
	}
}
