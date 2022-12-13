/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionService;
import org.apache.geode.internal.cache.LocalRegion;

/**
 * Unit Tests for {@link RegionUtils}.
 *
 * @author John Blum
 * @see RegionUtils
 * @since 2.1.0
 */
public class RegionUtilsUnitTests {

	@Test
	public void assertAllDataPoliciesWithNullPersistentPropertyIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PARTITION, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PERSISTENT_PARTITION, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PERSISTENT_REPLICATE, null);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.REPLICATE, null);
	}

	@Test
	public void assertNonPersistentDataPolicyWithNoPersistenceIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PARTITION, false);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.REPLICATE, false);
	}

	@Test
	public void assertPersistentDataPolicyWithPersistenceIsCompatible() {

		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PERSISTENT_PARTITION, true);
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PERSISTENT_REPLICATE, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertNonPersistentDataPolicyWithPersistentAttribute() {

		try {
			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.REPLICATE, true);
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
			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy.PERSISTENT_PARTITION, false);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Data Policy [PERSISTENT_PARTITION] is not valid when persistent is false");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void closeRegionHandlesNull() {
		assertThat(RegionUtils.close((Region<?, ?>) null)).isFalse();
	}

	@Test
	public void closeRegionSuccessfully() {

		Region mockRegion = mock(Region.class);

		assertThat(RegionUtils.close(mockRegion)).isTrue();

		verify(mockRegion, times(1)).close();
	}

	@Test
	public void closeRegionUnsuccessfully() {

		Region mockRegion = mock(Region.class);

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

		Region mockRegion = mock(Region.class);
		RegionService mockRegionService = mock(RegionService.class);

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.isClosed()).thenReturn(false);

		assertThat(RegionUtils.isCloseable(mockRegion)).isTrue();

		verify(mockRegion, times(1)).getRegionService();
		verify(mockRegionService, times(1)).isClosed();
	}

	@Test
	public void regionIsNotCloseable() {

		Region mockRegion = mock(Region.class);
		RegionService mockRegionService = mock(RegionService.class);

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.isClosed()).thenReturn(true);

		assertThat(RegionUtils.isCloseable(mockRegion)).isFalse();

		verify(mockRegion, times(1)).getRegionService();
		verify(mockRegionService, times(1)).isClosed();
	}

	@Test
	public void regionWithNoRegionServiceIsNotCloseable() {

		Region mockRegion = mock(Region.class);

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
		assertThat(RegionUtils.isLocal(mock(LocalRegion.class))).isTrue();
	}

	@Test
	public void nonLocalRegionIsNotLocal() {
		assertThat(RegionUtils.isLocal(mock(Region.class))).isFalse();
	}
}
