/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
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
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.junit.Test;

/**
 * Unit Tests for {@link GemfireFunctions}
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.data.gemfire.support.GemfireFunctions
 * @since 2.7.0
 */
public class GemfireFunctionsUnitTests {

	@Test
	public void getRegionFromCacheFunctionReturnsRegion() {

		GudClientCache mockCache = mock(GudClientCache.class);

		GudRegion<?, ?> mockRegion = mock(GudRegion.class);

		doReturn(mockRegion).when(mockCache).getRegion(eq("TestRegion"));

		Function<GudClientCache, GudRegion<Object, Object>> function =
			GemfireFunctions.getRegionFromCache("TestRegion");

		assertThat(function).isNotNull();
		assertThat(function.apply(mockCache)).isEqualTo(mockRegion);

		verify(mockCache, times(1)).getRegion(eq("TestRegion"));
		verifyNoMoreInteractions(mockCache);
		verifyNoInteractions(mockRegion);
	}

	@Test
	public void getRegionFromCacheSupplierReturnsRegions() {

		GudClientCache mockCache = mock(GudClientCache.class);

		GudRegion<?, ?> mockRegion = mock(GudRegion.class);

		doReturn(mockRegion).when(mockCache).getRegion(eq("TestRegion"));

		Supplier<GudRegion<Object, Object>> supplier =
			GemfireFunctions.getRegionFromCache(mockCache, "TestRegion");

		assertThat(supplier).isNotNull();
		assertThat(supplier.get()).isEqualTo(mockRegion);

		verify(mockCache, times(1)).getRegion(eq("TestRegion"));
		verifyNoMoreInteractions(mockCache);
		verifyNoInteractions(mockRegion);
	}

	@Test
	public void getSubregionFromRegionFunctionReturnsRegion() {

		GudRegion<?, ?> mockParentRegion = mock(GudRegion.class);
		GudRegion<?, ?> mockSubregion = mock(GudRegion.class);

		doReturn(mockSubregion).when(mockParentRegion).getSubregion(eq("TestSubregion"));

		Function<GudRegion<?, ?>, GudRegion<Object, Object>> function =
			GemfireFunctions.getSubregionFromRegion("TestSubregion");

		assertThat(function).isNotNull();
		assertThat(function.apply(mockParentRegion)).isEqualTo(mockSubregion);

		verify(mockParentRegion, times(1)).getSubregion(eq("TestSubregion"));
		verifyNoMoreInteractions(mockParentRegion);
		verifyNoInteractions(mockSubregion);
	}
}
