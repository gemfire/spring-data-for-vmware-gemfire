/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.server.ClientSubscriptionConfig;

/**
 * Unit Tests for {@link GemFireMockObjectsSupport}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see AttributesMutator
 * @see Region
 * @see RegionAttributes
 * @see RegionService
 * @see org.apache.geode.cache.asyncqueue.AsyncEventListener
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueueFactory
 * @see ClientSubscriptionConfig
 * @see GemFireMockObjectsSupport
 * @since 1.0.0
 */
public class GemFireMockObjectsSupportUnitTests {

	@After
	public void tearDown() {
		GemFireMockObjectsSupport.destroy();
	}

	@Test
	public void mockClientSubscriptionConfigIsCorrect() {

		ClientSubscriptionConfig mockClientSubscriptionConfig =
			GemFireMockObjectsSupport.mockClientSubscriptionConfig();

		assertThat(mockClientSubscriptionConfig).isNotNull();

		mockClientSubscriptionConfig.setCapacity(1024);
		mockClientSubscriptionConfig.setDiskStoreName("TestDiskStore");
		mockClientSubscriptionConfig.setEvictionPolicy("ENTRY");

		assertThat(mockClientSubscriptionConfig.getCapacity()).isEqualTo(1024);
		assertThat(mockClientSubscriptionConfig.getDiskStoreName()).isEqualTo("TestDiskStore");
		assertThat(mockClientSubscriptionConfig.getEvictionPolicy()).isEqualTo("entry");
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void mockSubRegionIsCorrect() {

		Region mockRegion = mock(Region.class);

		doReturn("MockRegion").when(mockRegion).getName();
		doReturn("/MockRegion").when(mockRegion).getFullPath();

		RegionAttributes mockRegionAttributes = mock(RegionAttributes.class);

		Region mockSubRegion = GemFireMockObjectsSupport.mockSubRegion(mockRegion, "MockSubRegion",
			mockRegionAttributes);

		assertThat(mockSubRegion).isNotNull();
		assertThat(mockSubRegion.getName()).isEqualTo("MockSubRegion");
		assertThat(mockSubRegion.getFullPath()).isEqualTo("/MockRegion/MockSubRegion");
		assertThat(mockSubRegion.getParentRegion()).isEqualTo(mockRegion);
	}

	@Test
	public void regionAttributesMutatorGetRegionReturnsRegion() {

		RegionService mockRegionService = mock(RegionService.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		Region<?, ?> mockRegion =
			GemFireMockObjectsSupport.mockRegion(mockRegionService, "MockRegion", mockRegionAttributes);

		assertThat(mockRegion).isNotNull();
		assertThat(mockRegion.getName()).isEqualTo("MockRegion");
		assertThat(mockRegion.getRegionService()).isSameAs(mockRegionService);

		AttributesMutator<?, ?> mockAttributesMutator = mockRegion.getAttributesMutator();

		assertThat(mockAttributesMutator).isNotNull();
		assertThat(mockAttributesMutator.getRegion()).isSameAs(mockRegion);
	}

	@Test
	public void regionAttributesMutatorIsInitialized() {

		RegionService mockRegionService = mock(RegionService.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		Region<?, ?> mockRegion =
			GemFireMockObjectsSupport.mockRegion(mockRegionService, "MockRegion", mockRegionAttributes);

		assertThat(mockRegion).isNotNull();
		assertThat(mockRegion.getName()).isEqualTo("MockRegion");
		assertThat(mockRegion.getRegionService()).isSameAs(mockRegionService);

		AttributesMutator<?, ?> mockAttributesMutator = mockRegion.getAttributesMutator();

		assertThat(mockAttributesMutator).isNotNull();

		mockAttributesMutator.setCloningEnabled(true);

		assertThat(mockRegion.getAttributes()).isNotNull();
		assertThat(mockRegion.getAttributes()).isNotSameAs(mockRegionAttributes);
		assertThat(mockRegion.getAttributes().getCloningEnabled()).isTrue();

		verify(mockAttributesMutator, times(1)).setCloningEnabled(eq(true));
	}

	@Test
	public void regionCloningEnabledReturnsFalseByDefault() {

		RegionService mockRegionService = mock(RegionService.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		Region<?, ?> mockRegion =
			GemFireMockObjectsSupport.mockRegion(mockRegionService, "MockRegion", mockRegionAttributes);

		assertThat(mockRegion).isNotNull();
		assertThat(mockRegion.getName()).isEqualTo("MockRegion");
		assertThat(mockRegion.getAttributes()).isNotNull();
		assertThat(mockRegion.getAttributes()).isNotSameAs(mockRegionAttributes);
		assertThat(mockRegion.getAttributes().getCloningEnabled()).isFalse();
	}
}
