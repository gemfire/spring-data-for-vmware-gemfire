/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-01: Replaced Apache Geode types with GUD equivalents (GudAttributesMutator, GudRegion, GudRegionAttributes, GudRegionService, GudClientSubscriptionConfig)
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

import org.springframework.data.gemfire.gud.api.GudAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudClientSubscriptionConfig;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionService;

/**
 * Unit Tests for {@link GemFireMockObjectsSupport}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GudAttributesMutator
 * @see GudRegion
 * @see GudRegionAttributes
 * @see GudRegionService
 * @see GudClientSubscriptionConfig
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

		GudClientSubscriptionConfig mockClientSubscriptionConfig =
			GemFireMockObjectsSupport.mockGudClientSubscriptionConfig();

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

		GudRegion mockRegion = mock(GudRegion.class);

		doReturn("MockRegion").when(mockRegion).getName();
		doReturn("/MockRegion").when(mockRegion).getFullPath();

		GudRegionAttributes mockRegionAttributes = mock(GudRegionAttributes.class);

		GudRegion mockSubRegion = GemFireMockObjectsSupport.mockSubRegion(mockRegion, "MockSubRegion",
			mockRegionAttributes);

		assertThat(mockSubRegion).isNotNull();
		assertThat(mockSubRegion.getName()).isEqualTo("MockSubRegion");
		assertThat(mockSubRegion.getFullPath()).isEqualTo("/MockRegion/MockSubRegion");
		assertThat(mockSubRegion.getParentRegion()).isEqualTo(mockRegion);
	}

	@Test
	public void regionAttributesMutatorGetRegionReturnsRegion() {

		GudRegionService mockRegionService = mock(GudRegionService.class);

		GudRegionAttributes<?, ?> mockRegionAttributes = mock(GudRegionAttributes.class);

		GudRegion<?, ?> mockRegion =
			GemFireMockObjectsSupport.mockRegion(mockRegionService, "MockRegion", mockRegionAttributes);

		assertThat(mockRegion).isNotNull();
		assertThat(mockRegion.getName()).isEqualTo("MockRegion");
		assertThat(mockRegion.getRegionService()).isSameAs(mockRegionService);

		GudAttributesMutator<?, ?> mockAttributesMutator = mockRegion.getAttributesMutator();

		assertThat(mockAttributesMutator).isNotNull();
		assertThat(mockAttributesMutator.getRegion()).isSameAs(mockRegion);
	}

	@Test
	public void regionAttributesMutatorIsInitialized() {

		GudRegionService mockRegionService = mock(GudRegionService.class);

		GudRegionAttributes<?, ?> mockRegionAttributes = mock(GudRegionAttributes.class);

		GudRegion<?, ?> mockRegion =
			GemFireMockObjectsSupport.mockRegion(mockRegionService, "MockRegion", mockRegionAttributes);

		assertThat(mockRegion).isNotNull();
		assertThat(mockRegion.getName()).isEqualTo("MockRegion");
		assertThat(mockRegion.getRegionService()).isSameAs(mockRegionService);

		GudAttributesMutator<?, ?> mockAttributesMutator = mockRegion.getAttributesMutator();

		assertThat(mockAttributesMutator).isNotNull();

		mockAttributesMutator.setCloningEnabled(true);

		assertThat(mockRegion.getAttributes()).isNotNull();
		assertThat(mockRegion.getAttributes()).isNotSameAs(mockRegionAttributes);
		assertThat(mockRegion.getAttributes().getCloningEnabled()).isTrue();

		verify(mockAttributesMutator, times(1)).setCloningEnabled(eq(true));
	}

	@Test
	public void regionCloningEnabledReturnsFalseByDefault() {

		GudRegionService mockRegionService = mock(GudRegionService.class);

		GudRegionAttributes<?, ?> mockRegionAttributes = mock(GudRegionAttributes.class);

		GudRegion<?, ?> mockRegion =
			GemFireMockObjectsSupport.mockRegion(mockRegionService, "MockRegion", mockRegionAttributes);

		assertThat(mockRegion).isNotNull();
		assertThat(mockRegion.getName()).isEqualTo("MockRegion");
		assertThat(mockRegion.getAttributes()).isNotNull();
		assertThat(mockRegion.getAttributes()).isNotSameAs(mockRegionAttributes);
		assertThat(mockRegion.getAttributes().getCloningEnabled()).isFalse();
	}
}
