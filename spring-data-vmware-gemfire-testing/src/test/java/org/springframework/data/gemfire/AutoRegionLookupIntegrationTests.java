/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for SDG Auto {@link Region} Lookup functionality.
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
public class AutoRegionLookupIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void testAutoRegionLookup() {

		assertThat(this.applicationContext.containsBean("SpringPartitionedRegion")).isTrue();
		assertThat(this.applicationContext.containsBean("SpringReplicateParent")).isTrue();
		assertThat(this.applicationContext.containsBean("/SpringReplicateParent/SpringReplicateChild")).isTrue();
		assertThat(this.applicationContext.containsBean("NativePartitionedRegion")).isTrue();
		assertThat(this.applicationContext.containsBean("NativeReplicateParent")).isTrue();
		assertThat(this.applicationContext.containsBean("/NativeReplicateParent/NativeReplicateChild")).isTrue();
		assertThat(this.applicationContext.containsBean("/NativeReplicateParent/NativeReplicateChild/NativeReplicateGrandchild")).isTrue();
	}
}
