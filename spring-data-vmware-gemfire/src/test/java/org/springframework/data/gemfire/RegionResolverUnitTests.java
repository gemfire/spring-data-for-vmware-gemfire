/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Unit Tests for {@link RegionResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.data.gemfire.RegionResolver
 * @since 2.3.0
 */
public class RegionResolverUnitTests {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void requiresRegionForNameReturnsRegion() {

		GudRegion mockRegion = mock(GudRegion.class);

		RegionResolver regionResolver = mock(RegionResolver.class);

		when(regionResolver.require(anyString())).thenCallRealMethod();
		when(regionResolver.resolve(eq("MockRegion"))).thenReturn(mockRegion);

		assertThat(regionResolver.require("MockRegion")).isEqualTo(mockRegion);

		verify(regionResolver, times(1)).resolve(eq("MockRegion"));
		verifyNoInteractions(mockRegion);
	}

	@Test(expected = IllegalStateException.class)
	public void requiredRegionResolvingToNullThrowsIllegalStateException() {

		RegionResolver regionResolver = mock(RegionResolver.class);

		when(regionResolver.require(anyString())).thenCallRealMethod();
		when(regionResolver.resolve(anyString())).thenReturn(null);

		try {
			regionResolver.require("TestRegion");
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("GudRegion with name [TestRegion] not found");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(regionResolver, times(1)).resolve(eq("TestRegion"));
		}
	}
}
