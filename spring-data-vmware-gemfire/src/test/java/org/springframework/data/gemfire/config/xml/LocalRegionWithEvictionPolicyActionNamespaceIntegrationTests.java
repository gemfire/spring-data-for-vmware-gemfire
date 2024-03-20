/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.Scope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the Eviction Policy Actions on Local {@link Region Regions} defined in
 * SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.5.0.M1
 * @link https://jira.spring.io/browse/SGF-295
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class LocalRegionWithEvictionPolicyActionNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("LocalDestroy")
	private Region<?, ?> localDestroyRegion;

	@Autowired
	@Qualifier("None")
	private Region<?, ?> noneRegion;

	@Autowired
	@Qualifier("Overflow")
	private Region<?, ?> overflowRegion;

	@Test
	public void testLocalRegionConfigurationWithEvictionPolicyActionSetToLocalDestroy() {

		assertThat(localDestroyRegion).as("The 'LocalDestroy' Region was not properly configured and initialized")
			.isNotNull();
		assertThat(localDestroyRegion.getName()).isEqualTo("LocalDestroy");
		assertThat(localDestroyRegion.getFullPath()).isEqualTo("/LocalDestroy");
		assertThat(localDestroyRegion.getAttributes()).isNotNull();
		assertThat(localDestroyRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(localDestroyRegion.getAttributes().getScope()).isEqualTo(Scope.LOCAL);
		assertThat(localDestroyRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(localDestroyRegion.getAttributes().getEvictionAttributes().getAction())
			.isEqualTo(EvictionAction.LOCAL_DESTROY);
	}

	@Test
	public void testLocalRegionConfigurationWithEvictionPolicyActionSetToNone() {

		assertThat(noneRegion).as("The 'None' Region was not properly configured and initialized").isNotNull();
		assertThat(noneRegion.getName()).isEqualTo("None");
		assertThat(noneRegion.getFullPath()).isEqualTo("/None");
		assertThat(noneRegion.getAttributes()).isNotNull();
		assertThat(noneRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(noneRegion.getAttributes().getScope()).isEqualTo(Scope.LOCAL);
		assertThat(noneRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(noneRegion.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.NONE);
	}

	@Test
	public void testLocalRegionConfigurationWithEvictionPolicyActionSetToOverflowToDisk() {

		assertThat(overflowRegion).as("The 'Overflow' Region was not properly configured and initialized").isNotNull();
		assertThat(overflowRegion.getName()).isEqualTo("Overflow");
		assertThat(overflowRegion.getFullPath()).isEqualTo("/Overflow");
		assertThat(overflowRegion.getAttributes()).isNotNull();
		assertThat(overflowRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(overflowRegion.getAttributes().getScope()).isEqualTo(Scope.LOCAL);
		assertThat(overflowRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(overflowRegion.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
	}
}
