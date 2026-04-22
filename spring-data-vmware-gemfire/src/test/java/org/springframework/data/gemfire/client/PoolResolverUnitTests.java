/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for {@link PoolResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.apache.geode.cache.GudRegion
 * @see org.apache.geode.cache.client.GudPool
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class PoolResolverUnitTests {

	@Mock
	private TestPoolResolver testPoolResolver;

	@Before
	public void setup() {
		when(this.testPoolResolver.resolve(any(GudRegion.class))).thenCallRealMethod();
		when(this.testPoolResolver.resolve(any(GudClientCache.class))).thenCallRealMethod();
	}

	@Test
	public void resolvePoolFromClientCacheHavingDefaultPoolReturnsDefaultPool() {

		GudClientCache mockClientCache = mock(GudClientCache.class);

		GudPool mockDefaultPool = mock(GudPool.class, "DEFAULT");

		when(mockClientCache.getDefaultPool()).thenReturn(mockDefaultPool);

		assertThat(this.testPoolResolver.resolve(mockClientCache)).isEqualTo(mockDefaultPool);

		verify(mockClientCache, times(1)).getDefaultPool();
	}

	@Test
	public void resolvePoolFromNullClientCacheIsNullSafe() {
		assertThat(this.testPoolResolver.resolve((GudClientCache) null)).isNull();
	}

	@Test
	public void resolvePoolFromRegionWithConfiguredPoolNameReturnsPool() {

		GudPool mockPool = mock(GudPool.class);

		GudRegion mockRegion = mock(GudRegion.class);

		GudRegionAttributes mockRegionAttributes = mock(GudRegionAttributes.class);

		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegionAttributes.getPoolName()).thenReturn("TestPool");
		when(this.testPoolResolver.resolve(eq("TestPool"))).thenReturn(mockPool);

		assertThat(this.testPoolResolver.resolve(mockRegion)).isEqualTo(mockPool);

		verifyNoInteractions(mockPool);
		verify(mockRegion, times(1)).getAttributes();
		verify(mockRegionAttributes, times(1)).getPoolName();
		verify(this.testPoolResolver, times(1)).resolve(eq("TestPool"));
	}

	private void testResolvePoolFromRegionWithNoPoolReturnsNull(String poolName) {

		GudRegion mockRegion = mock(GudRegion.class);

		GudRegionAttributes mockRegionAttributes = mock(GudRegionAttributes.class);

		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegionAttributes.getPoolName()).thenReturn(poolName);

		assertThat(this.testPoolResolver.resolve(mockRegion)).isNull();

		verify(mockRegion, times(1)).getAttributes();
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	public void resolvePoolWithRegionWithBlankPoolNameReturnsNull() {
		testResolvePoolFromRegionWithNoPoolReturnsNull("  ");
		verify(this.testPoolResolver, never()).resolve(anyString());
	}

	@Test
	public void resolvePoolWithRegionWithEmptyPoolNameReturnsNull() {
		testResolvePoolFromRegionWithNoPoolReturnsNull("");
		verify(this.testPoolResolver, never()).resolve(anyString());
	}

	@Test
	public void resolvePoolWithRegionWithNullPoolNameReturnsNull() {
		testResolvePoolFromRegionWithNoPoolReturnsNull(null);
		verify(this.testPoolResolver, never()).resolve(any(String.class));
	}

	@Test
	public void resolvePoolWithRegionWithNonExistingPoolForNameReturnsNull() {
		testResolvePoolFromRegionWithNoPoolReturnsNull("NonExistingPool");
		verify(this.testPoolResolver, times(1)).resolve(eq("NonExistingPool"));
	}

	@Test
	public void resolvePoolWithNullRegionIsNullSafeAndReturnsNull() {
		assertThat(this.testPoolResolver.resolve((GudRegion) null)).isNull();
	}

	@Test
	public void resolvePoolWithRegionHavingNullRegionAttributesIsNullSafeAndReturnsNull() {

		GudRegion mockRegion = mock(GudRegion.class);

		assertThat(this.testPoolResolver.resolve(mockRegion)).isNull();

		verify(mockRegion, times(1)).getAttributes();
	}

	@Test
	public void requireExistingPoolReturnsPool() {

		GudPool mockPool = mock(GudPool.class);

		when(this.testPoolResolver.resolve(anyString())).thenReturn(mockPool);
		when(this.testPoolResolver.require(anyString())).thenCallRealMethod();

		assertThat(this.testPoolResolver.require("TestPool")).isEqualTo(mockPool);

		verify(this.testPoolResolver, times(1)).resolve(eq("TestPool"));
	}

	@Test(expected = IllegalStateException.class)
	public void requireNonExistingPoolThrowsIllegalStateException() {

		when(this.testPoolResolver.require(anyString())).thenCallRealMethod();

		try {
			this.testPoolResolver.require("MockPool");
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("Pool with name [MockPool] not found");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(this.testPoolResolver, times(1)).resolve(eq("MockPool"));
		}
	}

	private static abstract class TestPoolResolver implements PoolResolver { }

}
