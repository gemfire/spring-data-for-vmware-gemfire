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
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode {@literal collocated} {@link DataPolicy#PARTITION} {@link Region Regions}
 * using SDG XML configuration.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see <a href="https://jira.springsource.org/browse/SGF-195">SGF-195</a>
 * @since 1.3.3
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class CollocatedRegionsXmlConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("collocatedRegion")
	private Region<?, ?> colocatedRegion;

	@Autowired
	@Qualifier("sourceRegion")
	private Region<?, ?> sourceRegion;

	private void assertRegionExists(Region<?, ?> region, String expectedRegionName) {

		Assertions.assertThat(region).isNotNull();

		Assertions.assertThat(region.getName())
			.describedAs("Expected Region with name [%1$s]; but was [%2$s]", expectedRegionName, region.getName())
			.isEqualTo(expectedRegionName);
	}

	@Test
	public void collocatedRegionsAreConfiguredCorrectly() {

		assertRegionExists(this.sourceRegion, "Source");
		assertRegionExists(this.colocatedRegion, "Collocated");
		Assertions.assertThat(this.colocatedRegion.getAttributes()).isNotNull();
		Assertions.assertThat(this.colocatedRegion.getAttributes().getPartitionAttributes()).isNotNull();
		Assertions.assertThat(this.colocatedRegion.getAttributes().getPartitionAttributes().getColocatedWith())
			.isEqualTo(this.sourceRegion.getName());
	}
}
