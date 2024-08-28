/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableCachingDefinedRegions} and {@link CachingDefinedRegionsConfiguration} to assert
 * that {@link RegionConfigurer RegionConfigurers} beans are applied to the caching-defined {@link Region Regions}.
 *
 * @author John Blum
 * @see Test
 * @see GemFireCache
 * @see Region
 * @see Cacheable
 * @see CachingDefinedRegionsConfiguration
 * @see EnableCachingDefinedRegions
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class CachingDefinedRegionsAppliesRegionConfigurersIntegrationTests extends IntegrationTestsSupport {

	private static final List<String> configuredRegionNames = Collections.synchronizedList(new ArrayList<>());

	@Autowired
	private GemFireCache clientCache;

	@Test
	public void clientCacheContainsCachingDefinedRegions() {

		assertThat(this.clientCache).isNotNull();

		assertThat(this.clientCache.rootRegions().stream()
			.filter(Objects::nonNull)
			.map(region -> region.getName())
			.collect(Collectors.toList())).containsExactlyInAnyOrder("TestCacheOne", "TestCacheTwo");
	}

	@Test
	public void regionConfigurerWasAppliedToCachingDefinedRegions() {
		assertThat(configuredRegionNames).containsExactlyInAnyOrder("TestCacheOne", "TestCacheTwo");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class TestConfiguration {

		@Bean
		TestCacheableService testCacheableService() {
			return new TestCacheableService();
		}

		@Bean
		RegionConfigurer testRegionConfigurer() {

			return new RegionConfigurer() {

				@Override
				public void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) {
					configuredRegionNames.add(beanName);
				}
			};
		}
	}

	@Service
	static class TestCacheableService {

		@Cacheable("TestCacheOne")
		public Object testCacheableOperationOne(String key) {
			return "TEST";
		}

		@Cacheable("TestCacheTwo")
		public Object testCacheableOperationTwo(String key) {
			return "MOCK";
		}
	}
}
