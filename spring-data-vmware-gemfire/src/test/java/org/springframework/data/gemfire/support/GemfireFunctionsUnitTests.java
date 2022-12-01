/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

/**
 * Unit Tests for {@link GemfireFunctions}
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GemFireCache
 * @see Region
 * @see GemfireFunctions
 * @since 2.7.0
 */
public class GemfireFunctionsUnitTests {

	@Test
	public void getRegionFromCacheFunctionReturnsRegion() {

		GemFireCache mockCache = mock(GemFireCache.class);

		Region<?, ?> mockRegion = mock(Region.class);

		doReturn(mockRegion).when(mockCache).getRegion(eq("TestRegion"));

		Function<GemFireCache, Region<Object, Object>> function =
			GemfireFunctions.getRegionFromCache("TestRegion");

		assertThat(function).isNotNull();
		assertThat(function.apply(mockCache)).isEqualTo(mockRegion);

		verify(mockCache, times(1)).getRegion(eq("TestRegion"));
		verifyNoMoreInteractions(mockCache);
		verifyNoInteractions(mockRegion);
	}

	@Test
	public void getRegionFromCacheSupplierReturnsRegions() {

		GemFireCache mockCache = mock(GemFireCache.class);

		Region<?, ?> mockRegion = mock(Region.class);

		doReturn(mockRegion).when(mockCache).getRegion(eq("TestRegion"));

		Supplier<Region<Object, Object>> supplier =
			GemfireFunctions.getRegionFromCache(mockCache, "TestRegion");

		assertThat(supplier).isNotNull();
		assertThat(supplier.get()).isEqualTo(mockRegion);

		verify(mockCache, times(1)).getRegion(eq("TestRegion"));
		verifyNoMoreInteractions(mockCache);
		verifyNoInteractions(mockRegion);
	}

	@Test
	public void getSubregionFromRegionFunctionReturnsRegion() {

		Region<?, ?> mockParentRegion = mock(Region.class);
		Region<?, ?> mockSubregion = mock(Region.class);

		doReturn(mockSubregion).when(mockParentRegion).getSubregion(eq("TestSubregion"));

		Function<Region<?, ?>, Region<Object, Object>> function =
			GemfireFunctions.getSubregionFromRegion("TestSubregion");

		assertThat(function).isNotNull();
		assertThat(function.apply(mockParentRegion)).isEqualTo(mockSubregion);

		verify(mockParentRegion, times(1)).getSubregion(eq("TestSubregion"));
		verifyNoMoreInteractions(mockParentRegion);
		verifyNoInteractions(mockSubregion);
	}
}
