/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for JIRA issue SGF-195,concerning collocated cache {@link Region Regions}.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 * @link https://jira.springsource.org/browse/SGF-195
 * @since 1.3.3
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class CollocatedRegionIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("colocatedRegion")
	private Region<?, ?> colocatedRegion;

	@Autowired
	@Qualifier("sourceRegion")
	private Region<?, ?> sourceRegion;

	protected static void assertRegionExists(String expectedRegionName, Region<?, ?> region) {

		assertThat(region).isNotNull();

		assertThat(region.getName())
			.describedAs("Expected Region with name [%1$s]; but was [%2$s]", expectedRegionName, region.getName())
			.isEqualTo(expectedRegionName);
	}

	@Test
	public void testRegionsColocated() {

		assertRegionExists("Source", sourceRegion);
		assertRegionExists("Colocated", colocatedRegion);
		assertThat(colocatedRegion.getAttributes()).isNotNull();
		assertThat(colocatedRegion.getAttributes().getPartitionAttributes()).isNotNull();
		assertThat(colocatedRegion.getAttributes().getPartitionAttributes().getColocatedWith()).isEqualTo(sourceRegion.getName());
	}
}
