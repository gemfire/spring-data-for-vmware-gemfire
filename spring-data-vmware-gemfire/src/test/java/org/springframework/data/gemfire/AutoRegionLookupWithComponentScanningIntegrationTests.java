/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
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
 * Integration Tests for SDG's Auto {@link Region} Lookup behavior with Spring component scanning functionality.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Region
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class AutoRegionLookupWithComponentScanningIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void testAutowiredNativeRegions() {

		assertThat(this.applicationContext.containsBean("autoRegionLookupDao"))
			.describedAs("The 'autoRegionLookupDao' Spring bean DAO was not properly configured an initialized")
			.isTrue();

		assertThat(this.applicationContext.getBean("autoRegionLookupDao", AutoRegionLookupDao.class)).isNotNull();
	}
}
