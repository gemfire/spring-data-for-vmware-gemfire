/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
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

		Assertions.assertThat(region)
			.describedAs(String.format("Region (%1$s) was not properly configured and initialized", expectedName))
			.isNotNull();

		Assertions.assertThat(region.getName()).isEqualTo(expectedName);
		Assertions.assertThat(region.getFullPath()).isEqualTo(expectedFullPath);

		Assertions.assertThat(region.getAttributes())
			.describedAs(String.format("Region (%1$s) must have RegionAttributes defined", expectedName))
			.isNotNull();

		Assertions.assertThat(region.getAttributes().getDataPolicy()).isEqualTo(expectedDataPolicy);
		Assertions.assertThat(region.getAttributes().getDataPolicy().withPersistence()).isFalse();
	}

	@Autowired
	private TestComponent testComponent;

	@Test
	public void testAutowiredNativeRegions() {

		assertRegionMetaData(testComponent.nativePartitionedRegion, "NativePartitionedRegion", DataPolicy.PARTITION);
		assertRegionMetaData(testComponent.nativeReplicateParent, "NativeReplicateParent", DataPolicy.REPLICATE);
		assertRegionMetaData(testComponent.nativeReplicateChild, "NativeReplicateChild",
			"/NativeReplicateParent/NativeReplicateChild", DataPolicy.REPLICATE);
		assertRegionMetaData(testComponent.nativeReplicateGrandchild, "NativeReplicateGrandchild",
			"/NativeReplicateParent/NativeReplicateChild/NativeReplicateGrandchild", DataPolicy.REPLICATE);
	}

	@Component
	public static final class TestComponent {

		@Autowired
		@Qualifier("NativePartitionedRegion")
		Region<?, ?> nativePartitionedRegion;

		@Autowired
		@Qualifier("NativeReplicateParent")
		Region<?, ?> nativeReplicateParent;

		@Autowired
		@Qualifier("/NativeReplicateParent/NativeReplicateChild")
		Region<?, ?> nativeReplicateChild;

		@Autowired
		@Qualifier("/NativeReplicateParent/NativeReplicateChild/NativeReplicateGrandchild")
		Region<?, ?> nativeReplicateGrandchild;

	}
}
