/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction.config;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
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
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheApplication
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

		Assertions.assertThat(this.gemfireCache).isNotNull();
		Assertions.assertThat(this.gemfireCache.getName()).isEqualTo("EnableGemfireCacheTransactionsIntegrationTests");
		Assertions.assertThat(this.gemfireCache.getRegion("Example")).isNotNull();
		Assertions.assertThat(this.transactionManager).isNotNull();
		Assertions.assertThat(this.transactionManager.getCache()).isSameAs(this.gemfireCache);
	}

	@Test
	public void doInTransactionCommits() {

		Assertions.assertThat(this.example).isNotNull();
		Assertions.assertThat(this.example).isEmpty();
		Assertions.assertThat(this.transactionalService.doInTransactionCommits(1, "pass")).isTrue();
		Assertions.assertThat(this.example).hasSize(1);
		Assertions.assertThat(this.example).containsKey(1);
		Assertions.assertThat(this.example.get(1)).isEqualTo("pass");
	}

	@Test(expected = RuntimeException.class)
	public void doInTransactionRollsback() {

		try {
			Assertions.assertThat(this.example).isNotNull();
			Assertions.assertThat(this.example).doesNotContainKey(2);

			this.transactionalService.doInTransactionRollsBack(2, "fail");
		}
		catch (RuntimeException expected) {
			Assertions.assertThat(expected).hasMessage("test");
			Assertions.assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			Assertions.assertThat(example).doesNotContainKey(2);
		}
	}

	@SuppressWarnings("unused")
	@EnableGemfireCacheTransactions
	@PeerCacheApplication(name = "EnableGemfireCacheTransactionsIntegrationTests")
	static class TestConfiguration {

		@Bean("Example")
		public LocalRegionFactoryBean<Object, Object> exampleRegion(GemFireCache gemFireCache) {

			LocalRegionFactoryBean<Object, Object> example = new LocalRegionFactoryBean<>();

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
