/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionService;
import org.springframework.data.gemfire.gud.api.GudRegion;

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

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.REPLICATE, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.REPLICATE, null);
	}

	@Test
	public void assertNonPersistentDataPolicyWithNoPersistenceIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.REPLICATE, false);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.REPLICATE, false);
	}

	@Test
	public void assertPersistentDataPolicyWithPersistenceIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, true);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.PERSISTENT_REPLICATE, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertNonPersistentDataPolicyWithPersistentAttribute() {

		try {
			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy.REPLICATE, true);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Data Policy [REPLICATE] is not valid when persistent is true");
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
		assertThat(RegionUtils.isLocal(mock(GudRegion.class))).isTrue();
	}

	@Test
	public void nonLocalRegionIsNotLocal() {
		assertThat(RegionUtils.isLocal(mock(GudRegion.class))).isFalse();
	}
}
