/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import org.junit.Test;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for {@link RegionUtils}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.util.RegionUtils
 * @since 2.1.0
 */
public class RegionUtilsUnitTests {

	@Test
	public void assertAllDataPoliciesWithNullPersistentPropertyIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, null);
	}

	@Test
	public void assertNonPersistentDataPolicyWithNoPersistenceIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.NORMAL, false);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.EMPTY, false);
	}

	@Test
	public void assertPersistentDataPolicyWithPersistenceIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, true);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertNonPersistentDataPolicyWithPersistentAttribute() {

		try {
			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.NORMAL, true);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Data Policy [NORMAL] is not valid when persistent is true");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertPersistentDataPolicyWithNonPersistentAttribute() {

		try {
			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, false);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Data Policy [PERSISTENT_REPLICATE] is not valid when persistent is false");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void closeRegionHandlesNull() {
		assertThat(RegionUtils.close((GudRegion<?, ?>) null)).isFalse();
	}

	@Test
	public void closeRegionSuccessfully() {

		GudRegion mockRegion = mock(GudRegion.class);

		assertThat(RegionUtils.close(mockRegion)).isTrue();

		verify(mockRegion, times(1)).close();
	}

	@Test
	public void closeRegionUnsuccessfully() {

		GudRegion mockRegion = mock(GudRegion.class);

		doThrow(new RuntimeException("TEST")).when(mockRegion).close();

		assertThat(RegionUtils.close(mockRegion)).isFalse();

		verify(mockRegion, times(1)).close();
	}

	@Test
	public void nullRegionIsNotCloseable() {
		assertThat(RegionUtils.isCloseable(null)).isFalse();
	}

	@Test
	public void regionIsCloseable() {

		GudRegion mockRegion = mock(GudRegion.class);
		GudRegionService mockRegionService = mock(GudRegionService.class);

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.isClosed()).thenReturn(false);

		assertThat(RegionUtils.isCloseable(mockRegion)).isTrue();

		verify(mockRegion, times(1)).getRegionService();
		verify(mockRegionService, times(1)).isClosed();
	}

	@Test
	public void regionIsNotCloseable() {

		GudRegion mockRegion = mock(GudRegion.class);
		GudRegionService mockRegionService = mock(GudRegionService.class);

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.isClosed()).thenReturn(true);

		assertThat(RegionUtils.isCloseable(mockRegion)).isFalse();

		verify(mockRegion, times(1)).getRegionService();
		verify(mockRegionService, times(1)).isClosed();
	}

	@Test
	public void regionWithNoRegionServiceIsNotCloseable() {

		GudRegion mockRegion = mock(GudRegion.class);

		when(mockRegion.getRegionService()).thenReturn(null);

		assertThat(RegionUtils.isCloseable(mockRegion)).isFalse();

		verify(mockRegion, times(1)).getRegionService();
	}

	@Test
	public void nullRegionIsNotLocal() {
		assertThat(RegionUtils.isLocal(null)).isFalse();
	}

	@Test
	public void localRegionIsLocal() {

		GudRegion<?, ?> mockRegion = mock(GudRegion.class);
		when(mockRegion.isLocalRegion()).thenReturn(true);

		assertThat(RegionUtils.isLocal(mockRegion)).isTrue();
	}

	@Test
	public void nonLocalRegionIsNotLocal() {

		GudRegion<?, ?> mockRegion = mock(GudRegion.class);
		when(mockRegion.isLocalRegion()).thenReturn(false);

		assertThat(RegionUtils.isLocal(mockRegion)).isFalse();
	}
}
