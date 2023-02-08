/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.InterestResultPolicy;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.Interest;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests asserting that caching-defined {@link Region Regions} receive a {@link Lifecycle#start()} callback
 * to register interests.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see Cacheable
 * @see ClientRegionFactoryBean
 * @see EnableCachingDefinedRegions
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 * @see <a href="https://jira.spring.io/browse/DATAGEODE-219">DATAGEODE-219</a>
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class CachingDefinedRegionsRegistersInterestsIntegrationTests extends IntegrationTestsSupport {

	private static final Interest testInterest =
		new Interest<>("TestKey", InterestResultPolicy.KEYS_VALUES, false, true);

	@Autowired
	@Qualifier("CacheOne")
	private Region cacheOne;

	@Autowired
	@Qualifier("CacheTwo")
	private Region cacheTwo;

	@Test
	public void cachesRegisterInterestInTestKey() {

		assertThat(this.cacheOne).isNotNull();
		assertThat(this.cacheOne.getName()).isEqualTo("CacheOne");
		assertThat(this.cacheTwo).isNotNull();
		assertThat(this.cacheTwo.getName()).isEqualTo("CacheTwo");

		Arrays.asList(this.cacheOne, this.cacheTwo).forEach(cache ->
			verify(cache, times(1))
				.registerInterest(eq(testInterest.getKey()), eq(testInterest.getPolicy()), eq(testInterest.isDurable()),
					eq(testInterest.isReceiveValues())));
	}

	@ClientCacheApplication
	@EnableCachingDefinedRegions
	@EnableGemFireMockObjects
	static class TestConfiguration {

		@Bean
		RegionConfigurer interestsRegisteringRegionConfigurer() {

			return new RegionConfigurer() {

				@Override
				public void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) {
					bean.setInterests(ArrayUtils.asArray(testInterest));
				}
			};
		}

		@Bean
		CacheableServiceObject cacheableServiceObject() {
			return new CacheableServiceObject();
		}
	}

	@Service
	static class CacheableServiceObject {

		@Cacheable("CacheOne")
		public Object cacheableOperationOne(String input) {
			return "FROM-CACHE-ONE";
		}

		@Cacheable("CacheTwo")
		public Object cacheableOperationTwo(String input) {
			return "FROM-CACHE-TWO";
		}
	}
}
