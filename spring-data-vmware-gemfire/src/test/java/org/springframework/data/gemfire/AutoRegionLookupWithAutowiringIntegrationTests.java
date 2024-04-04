/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for SDG's Auto {@link Region} Lookup functionality when combined with Spring's component
 * auto-wiring capabilities.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class AutoRegionLookupWithAutowiringIntegrationTests extends IntegrationTestsSupport {

	private static void assertRegionMetaData(Region<?, ?> region, String expectedName, DataPolicy expectedDataPolicy) {
		assertRegionMetaData(region, expectedName, Region.SEPARATOR + expectedName, expectedDataPolicy);
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedName, String expectedFullPath,
			DataPolicy expectedDataPolicy) {

		assertThat(region)
			.describedAs(String.format("Region (%1$s) was not properly configured and initialized", expectedName))
			.isNotNull();

		assertThat(region.getName()).isEqualTo(expectedName);
		assertThat(region.getFullPath()).isEqualTo(expectedFullPath);

		assertThat(region.getAttributes())
			.describedAs(String.format("Region (%1$s) must have RegionAttributes defined", expectedName))
			.isNotNull();

		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(expectedDataPolicy);
		assertThat(region.getAttributes().getDataPolicy().withPersistence()).isFalse();
	}

	@Autowired
	private TestComponent testComponent;

	@Test
	public void testAutowiredNativeRegions() {

		assertRegionMetaData(testComponent.nativeClientRegion, "NativeClientRegion", DataPolicy.NORMAL);
		assertRegionMetaData(testComponent.nativeClientParent, "NativeClientParent", DataPolicy.NORMAL);
		assertRegionMetaData(testComponent.nativeClientChild, "NativeClientChild",
			"/NativeClientParent/NativeClientChild", DataPolicy.NORMAL);
		assertRegionMetaData(testComponent.nativeClientGrandchild, "NativeClientGrandchild",
			"/NativeClientParent/NativeClientChild/NativeClientGrandchild", DataPolicy.NORMAL);
	}

	@Component
	public static final class TestComponent {

		@Autowired
		@Qualifier("NativeClientRegion")
		Region<?, ?> nativeClientRegion;

		@Autowired
		@Qualifier("NativeClientParent")
		Region<?, ?> nativeClientParent;

		@Autowired
		@Qualifier("/NativeClientParent/NativeClientChild")
		Region<?, ?> nativeClientChild;

		@Autowired
		@Qualifier("/NativeClientParent/NativeClientChild/NativeClientGrandchild")
		Region<?, ?> nativeClientGrandchild;

	}
}
