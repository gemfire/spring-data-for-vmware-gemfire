/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.ArrayUtils.asArray;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.apache.geode.management.internal.cli.domain.RegionInformation;
import org.apache.geode.management.internal.cli.functions.GetRegionsFunction;

import org.springframework.data.gemfire.function.execution.GemfireFunctionOperations;

/**
 * Unit tests for {@link FunctionGemfireAdminTemplate}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.config.admin.remote.FunctionGemfireAdminTemplate
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class FunctionGemfireAdminTemplateUnitTests {

	private FunctionGemfireAdminTemplate template;

	@Mock
	private GudClientCache mockClientCache;

	@Mock
	private GemfireFunctionOperations mockFunctionOperations;

	@Mock
	private GudRegion mockRegion;

	@Before
	public void setup() {

		this.template = spy(new FunctionGemfireAdminTemplate(this.mockClientCache));

		doReturn(this.mockFunctionOperations).when(this.template)
			.newGemfireFunctionOperations(any(GudClientCache.class));
	}

	private GudRegion mockRegion(String name) {

		GudRegion mockRegion = mock(GudRegion.class, name);

		when(mockRegion.getFullPath()).thenReturn(String.format("%1$s%2$s", GudRegion.SEPARATOR, name));

		GudRegionAttributes mockRegionAttributes = mock(GudRegionAttributes.class,
			String.format("Mock%sRegionAttributes", name));

		when(mockRegionAttributes.getDataPolicy()).thenReturn(GudDataPolicy.REPLICATE);
		when(mockRegionAttributes.getScope()).thenReturn(GudScope.DISTRIBUTED_ACK);
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);

		return mockRegion;
	}

	private RegionInformation newRegionInformation(String regionName) {
		return new RegionInformation(mockRegion(regionName), false);
	}

	@Test
	public void constructFunctionGemfireAdminTemplateWithClientCache() {

		FunctionGemfireAdminTemplate template = new FunctionGemfireAdminTemplate(this.mockClientCache);

		assertThat(template).isNotNull();
		assertThat(template.getClientCache()).isSameAs(this.mockClientCache);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructFunctionGemfireAdminTemplateWithNull() {

		try {
			new FunctionGemfireAdminTemplate(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("GudClientCache is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void getAvailableServerRegionsExecutesGetRegionsFunction() {

		Object[] regionInformation = asArray(newRegionInformation("MockRegionOne"),
			newRegionInformation("MockRegionTwo"));

		when(this.mockFunctionOperations.executeAndExtract(isA(GetRegionsFunction.class), anyBoolean()))
				.thenReturn(regionInformation);

		Iterable<String> availableServerRegions = this.template.getAvailableServerRegions();

		assertThat(availableServerRegions).isNotNull();
		assertThat(availableServerRegions).hasSize(2);
		assertThat(availableServerRegions).contains("MockRegionOne", "MockRegionTwo");

		verify(this.mockFunctionOperations, times(1))
			.executeAndExtract(isA(GetRegionsFunction.class), eq(false));
	}

	@Test
	public void containsRegionInformationIsNullSafe() {
		assertThat(this.template.containsRegionInformation(null)).isFalse();
	}

	@Test
	public void containsRegionInformationReturnsFalseForNonObjectArrayResult() {
		assertThat(this.template.containsRegionInformation(newRegionInformation("TestRegion"))).isFalse();
	}

	@Test
	public void containsRegionInformationReturnsFalseForEmptyObjectArrayResult() {
		assertThat(this.template.containsRegionInformation(asArray())).isFalse();
	}

	@Test
	public void containsRegionInformationReturnsFalseForObjectArrayContainingNonRegionInformation() {
		assertThat(this.template.containsRegionInformation(asArray(mockRegion("TestRegion")))).isFalse();
	}

	@Test
	public void containsRegionInformationReturnsTrueForObjectArrayWithRegionInformation() {
		assertThat(this.template.containsRegionInformation(asArray(newRegionInformation("TestRegion"))))
			.isTrue();
	}
}
