/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.support.RegionServiceRegionResolver.ConnectionResolver;
import java.util.Optional;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.gemfire.CacheResolver;

/**
 * Unit Tests for {@link RegionServiceRegionResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see com.vmware.gemfire.driver.spi.DriverConnection
 * @see com.vmware.gemfire.driver.spi.DriverRegion
 * @see com.vmware.gemfire.driver.spi.DriverConnection
 * @see com.vmware.gemfire.driver.spi.DriverConnection
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.support.RegionServiceRegionResolver
 * @see org.springframework.data.gemfire.support.RegionServiceRegionResolver.ConnectionResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RegionServiceRegionResolverUnitTests {

	@Mock
	private DriverRegion mockRegion;


	@Test
	public void constructRegionServiceRegionResolverWithNonNullConnectionResolver() {

		ConnectionResolver mockConnectionResolver = mock(ConnectionResolver.class);

		RegionServiceRegionResolver regionResolver = new RegionServiceRegionResolver(mockConnectionResolver);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getConnectionResolver()).isEqualTo(mockConnectionResolver);

		verifyNoInteractions(mockConnectionResolver);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructRegionServiceRegionResolverWithNull() {

		try {
			new RegionServiceRegionResolver<>(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("ConnectionResolver must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void fromNonNullCacheResolverResolvingClientCache() {

		DriverConnection mockClientCache = mock(DriverConnection.class);

		when(mockClientCache.getRegion(anyString())).thenReturn(this.mockRegion);

		CacheResolver<DriverConnection> mockClientCacheResolver = mock(CacheResolver.class);

		when(mockClientCacheResolver.resolve()).thenReturn(mockClientCache);

		RegionServiceRegionResolver regionResolver = RegionServiceRegionResolver.from(mockClientCacheResolver);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getConnectionResolver()).isNotNull();
		assertThat(regionResolver.doResolve("TestRegion")).isEqualTo(this.mockRegion);

		verify(mockClientCache, times(1)).getRegion(eq("TestRegion"));
		verify(mockClientCacheResolver, times(1)).resolve();
		verifyNoInteractions(this.mockRegion);
	}

	@Test
	public void fromNonNullCacheResolveResolvingNullCacheResolvesNullRegion() {

		CacheResolver<DriverConnection> mockCacheResolver = mock(CacheResolver.class);

		when(mockCacheResolver.resolve()).thenReturn(null);

		RegionServiceRegionResolver regionResolver = RegionServiceRegionResolver.from(mockCacheResolver);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getConnectionResolver()).isNotNull();
		assertThat(regionResolver.doResolve("TestRegion")).isNull();

		verify(mockCacheResolver, times(1)).resolve();
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromNullCacheResolverThrowsIllegalArgumentException() {

		try {
			RegionServiceRegionResolver.from((CacheResolver<DriverConnection>) null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("CacheResolver must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void fromNonNullRegionService() {

		DriverConnection mockRegionService = mock(DriverConnection.class);

		when(mockRegionService.getRegion(anyString())).thenReturn(this.mockRegion);

		RegionServiceRegionResolver regionResolver = RegionServiceRegionResolver.from(mockRegionService);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getConnectionResolver()).isNotNull();
		assertThat(regionResolver.getConnectionResolver().resolve().orElse(null)).isEqualTo(mockRegionService);
		assertThat(regionResolver.doResolve("TestRegion")).isEqualTo(this.mockRegion);

		verify(mockRegionService, times(1)).getRegion(eq("TestRegion"));
		verifyNoInteractions(this.mockRegion);
	}

	@Test
	public void fromNullRegionServiceIsNullSafe() {

		RegionServiceRegionResolver regionResolver = RegionServiceRegionResolver.from((DriverConnection) null);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getConnectionResolver()).isNotNull();
		assertThat(regionResolver.getConnectionResolver().resolve().orElse(null)).isNull();
		assertThat(regionResolver.doResolve("TestRegion")).isNull();
	}

	@Test
	public void resolveCachesAndReturnsRegion() {

		DriverConnection mockRegionService = mock(DriverConnection.class);

		ConnectionResolver mockConnectionResolver = mock(ConnectionResolver.class);

		when(mockRegionService.getRegion(anyString())).thenReturn(this.mockRegion);
		when(mockConnectionResolver.resolve()).thenReturn(Optional.of(mockRegionService));

		RegionServiceRegionResolver regionResolver = spy(new RegionServiceRegionResolver(mockConnectionResolver));

		assertThat(regionResolver.getConnectionResolver()).isEqualTo(mockConnectionResolver);

		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);
		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);

		verify(mockConnectionResolver, times(1)).resolve();
		verify(mockRegionService, times(1)).getRegion("MockRegion");
		verify(regionResolver, times(1)).doResolve(eq("MockRegion"));
	}

	@Test
	public void resolveReturnsNullWhenRegionServiceReturnsNull() {

		DriverConnection mockRegionService = mock(DriverConnection.class);

		ConnectionResolver mockConnectionResolver = mock(ConnectionResolver.class);

		when(mockRegionService.getRegion(anyString())).thenReturn(null);
		when(mockConnectionResolver.resolve()).thenReturn(Optional.of(mockRegionService));

		RegionServiceRegionResolver regionResolver = spy(new RegionServiceRegionResolver(mockConnectionResolver));

		assertThat(regionResolver.getConnectionResolver()).isEqualTo(mockConnectionResolver);

		assertThat(regionResolver.resolve("ProxyRegion")).isNull();
		assertThat(regionResolver.resolve("ProxyRegion")).isNull();

		verify(mockConnectionResolver, times(2)).resolve();
		verify(mockRegionService, times(2)).getRegion("ProxyRegion");
		verify(regionResolver, times(2)).doResolve(eq("ProxyRegion"));
	}

	@Test
	public void resolveReturnsNullWhenRegionServiceResolvesToNull() {

		ConnectionResolver mockConnectionResolver = mock(ConnectionResolver.class);

		RegionServiceRegionResolver regionResolver = spy(new RegionServiceRegionResolver(mockConnectionResolver));

		assertThat(regionResolver.getConnectionResolver()).isEqualTo(mockConnectionResolver);
		assertThat(regionResolver.resolve("TestRegion")).isNull();
		assertThat(regionResolver.resolve("TestRegion")).isNull();

		verify(mockConnectionResolver, times(2)).resolve();
		verify(regionResolver, times(2)).doResolve(eq("TestRegion"));
	}
}
