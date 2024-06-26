/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.transaction.GemfireTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration Tests for {@link EnableGemfireCacheTransactions} and {@link GemfireCacheTransactionsConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.data.gemfire.transaction.GemfireTransactionManager
 * @see org.springframework.data.gemfire.transaction.config.EnableGemfireCacheTransactions
 * @see org.springframework.data.gemfire.transaction.config.GemfireCacheTransactionsConfiguration
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see org.springframework.transaction.annotation.Transactional
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class EnableGemfireCacheTransactionsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireCache gemfireCache;

	@Autowired
	private GemfireTransactionManager transactionManager;

	@Autowired
	@Qualifier("Example")
	private Region<Object, Object> example;

	@Autowired
	private TestTransactionalService transactionalService;

	@Test
	public void transactionManagerIsConfigured() {

		assertThat(this.gemfireCache).isNotNull();
		assertThat(this.gemfireCache.getName()).isEqualTo("EnableGemfireCacheTransactionsIntegrationTests");
		assertThat(this.gemfireCache.getRegion("Example")).isNotNull();
		assertThat(this.transactionManager).isNotNull();
		assertThat(this.transactionManager.getCache()).isSameAs(this.gemfireCache);
	}

	@Test
	public void doInTransactionCommits() {

		assertThat(this.example).isNotNull();
		assertThat(this.example).isEmpty();
		assertThat(this.transactionalService.doInTransactionCommits(1, "pass")).isTrue();
		assertThat(this.example).hasSize(1);
		assertThat(this.example).containsKey(1);
		assertThat(this.example.get(1)).isEqualTo("pass");
	}

	@Test(expected = RuntimeException.class)
	public void doInTransactionRollsback() {

		try {
			assertThat(this.example).isNotNull();
			assertThat(this.example).doesNotContainKey(2);

			this.transactionalService.doInTransactionRollsBack(2, "fail");
		}
		catch (RuntimeException expected) {
			assertThat(expected).hasMessage("test");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(example).doesNotContainKey(2);
		}
	}

	@SuppressWarnings("unused")
	@EnableGemfireCacheTransactions
	@ClientCacheApplication(name = "EnableGemfireCacheTransactionsIntegrationTests")
	static class TestConfiguration {

		@Bean("Example")
		public ClientRegionFactoryBean<Object, Object> exampleRegion(GemFireCache gemFireCache) {

			ClientRegionFactoryBean<Object, Object> example = new ClientRegionFactoryBean<>();

			example.setCache(gemFireCache);
			example.setPersistent(false);

			return example;
		}

		@Bean
		TestTransactionalService transactionalService() {
			return new TestTransactionalService();
		}
	}

	@Service
	@SuppressWarnings("all")
	static class TestTransactionalService {

		@Autowired
		@Qualifier("Example")
		private Region<Object, Object> example;

		@Transactional
		public boolean doInTransactionCommits(Object key, Object value) {
			example.put(key, value);
			return true;
		}

		@Transactional
		public boolean doInTransactionRollsBack(Object key, Object value) {
			example.put(2, "fail");
			throw new RuntimeException("test");
		}
	}
}
