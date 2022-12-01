/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link GemfireCacheManager}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Test
 * @see GemfireCacheManager
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class ClientCacheManagerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemfireCacheManager cacheManager;

	@Test
	public void cacheManagerUsesConfiguredGemFireRegionAsCache() {

		assertThat(this.cacheManager).isNotNull();
		assertThat(this.cacheManager.getCache("Example")).isNotNull();
	}
}
