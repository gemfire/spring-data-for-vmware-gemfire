/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.cache.GemfireCacheManager;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * Integration Tests for {@link EnableGemfireCaching} and {@link GemfireCachingConfiguration}.
 *
 * @author John Blum
 * @see Test
 * @see org.apache.geode.cache.Region
 * @see Cacheable
 * @see Bean
 * @see EnableGemfireCaching
 * @see GemfireCachingConfiguration
 * @see EnableCachingDefinedRegions
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class EnableGemfireCachingIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private CalculatorService calculator;

	@Autowired
	@SuppressWarnings("unused")
	private GemfireCacheManager gemfireCacheManager;

	@Test
	public void gemfireCacheManagerIsConfigured() {
		assertThat(this.gemfireCacheManager).isNotNull();
	}

	@Test
	public void enableGemfireCachingIsSuccessful() {

		assertThat(calculator.isCacheMiss()).isFalse();
		assertThat(calculator.factorial(0L)).isEqualTo(1L);
		assertThat(calculator.isCacheMiss()).isTrue();
		assertThat(calculator.factorial(1L)).isEqualTo(1L);
		assertThat(calculator.isCacheMiss()).isTrue();
		assertThat(calculator.factorial(2L)).isEqualTo(2L);
		assertThat(calculator.isCacheMiss()).isTrue();
		assertThat(calculator.factorial(0L)).isEqualTo(1L);
		assertThat(calculator.isCacheMiss()).isFalse();
		assertThat(calculator.factorial(1L)).isEqualTo(1L);
		assertThat(calculator.isCacheMiss()).isFalse();
		assertThat(calculator.factorial(2L)).isEqualTo(2L);
		assertThat(calculator.isCacheMiss()).isFalse();
		assertThat(calculator.factorial(3L)).isEqualTo(6L);
		assertThat(calculator.isCacheMiss()).isTrue();
		assertThat(calculator.factorial(4L)).isEqualTo(24L);
		assertThat(calculator.isCacheMiss()).isTrue();
		assertThat(calculator.factorial(5L)).isEqualTo(120L);
		assertThat(calculator.isCacheMiss()).isTrue();
	}

	@Service
	static class CalculatorService {

		private volatile boolean cacheMiss;

		public boolean isCacheMiss() {
			boolean cacheMiss = this.cacheMiss;
			this.cacheMiss = false;
			return cacheMiss;
		}

		@Cacheable("Factorials")
		public Long factorial(Long number) {

			Assert.notNull(number, "Number to compute the factorial of must not be null");

			Assert.isTrue(number > -1,
				String.format("Number [%d] must be greater than equal to 0", number));

			cacheMiss = true;

			if (number < 3) {
				return (number > 1L ? 2L : 1L);
			}

			long result = number;

			while (number-- > 1) {
				result *= number;
			}

			return result;
		}
	}

	@ClientCacheApplication
	@EnableCachingDefinedRegions
	@EnableGemfireCaching
	@EnableGemFireMockObjects
	@SuppressWarnings("unused")
	static class TestConfiguration {

		@Bean
		CalculatorService calculatorService() {
			return new CalculatorService();
		}
	}
}
