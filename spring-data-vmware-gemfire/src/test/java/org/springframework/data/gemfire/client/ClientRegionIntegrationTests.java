/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for client {@link Region Regions} created with SDG's {@link ClientRegionFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see GemFireCache
 * @see Region
 * @see ClientRegionFactoryBean
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class ClientRegionIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("Example")
	private Region<Object, Object> example;

	@Test
	public void clientRegionUsesDefaultPoolWhenUnspecified() {

		assertThat(this.example).isNotNull();
		assertThat(this.example.getName()).isEqualTo(GemfireUtils.toRegionName("Example"));
		assertThat(this.example.getFullPath()).isEqualTo(GemfireUtils.toRegionPath("Example"));
		assertThat(this.example.getAttributes()).isNotNull();
		assertThat(this.example.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(this.example.getAttributes().getPoolName()).isNull();

		// NOTE: A null Pool name implies the use of the Apache Geode DEFAULT Pool.

	}

	@ClientCacheApplication
	static class ClientRegionConfiguration {

		@Bean("Example")
		public ClientRegionFactoryBean<Object, Object> exampleRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> exampleRegion = new ClientRegionFactoryBean<>();

			exampleRegion.setCache(gemfireCache);
			exampleRegion.setShortcut(ClientRegionShortcut.LOCAL);

			return exampleRegion;
		}
	}
}
