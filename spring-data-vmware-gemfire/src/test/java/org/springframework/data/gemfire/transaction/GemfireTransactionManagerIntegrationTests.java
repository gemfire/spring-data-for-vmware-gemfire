/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.transaction.config.EnableGemfireCacheTransactions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Integration tests for the {@link GemfireTransactionManager}.
 *
 * @author John Blum
 * @see Test
 * @see GemFireCache
 * @see Region
 * @see IntegrationTestsSupport
 * @see GemfireTransactionManager
 * @see ContextConfiguration
 * @see SpringRunner
 * @see org.springframework.transaction.annotation.EnableTransactionManagement
 * @see Propagation
 * @since 1.9.1
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class GemfireTransactionManagerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("Example")
	private Region<Object, Object> example;

	@Autowired
	private SuspendAndResumeCacheTransactionsService service;

	@Test(expected = IllegalArgumentException.class)
	public void suspendAndResumeIsSuccessful() {

		try {

			assertThat(this.example).isEmpty();

			this.service.doCacheTransactions();
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("BOOM");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(this.example).hasSize(1);
			assertThat(this.example.containsKey("tx-1-op-1")).isFalse();
			assertThat(this.example.containsKey("tx-2-op-1")).isTrue();
		}
	}

	@PeerCacheApplication
	@EnableGemfireCacheTransactions
	static class TestConfiguration {

		@Bean(name = "Example")
		LocalRegionFactoryBean<Object, Object> exampleRegion(GemFireCache gemfireCache) {

			LocalRegionFactoryBean<Object, Object> example = new LocalRegionFactoryBean<>();

			example.setCache(gemfireCache);
			example.setClose(false);
			example.setPersistent(false);

			return example;
		}

		@Bean
		SuspendAndResumeCacheTransactionsRepository suspendAndResumeCacheTransactionsRepository(
				GemFireCache gemFireCache) {

			return new SuspendAndResumeCacheTransactionsRepository(gemFireCache.getRegion("Example"));
		}

		@Bean
		SuspendAndResumeCacheTransactionsService suspendAndResumeCacheTransactionsService(
				SuspendAndResumeCacheTransactionsRepository repository) {

			return new SuspendAndResumeCacheTransactionsService(repository);
		}
	}

	@Service
	static class SuspendAndResumeCacheTransactionsService {

		SuspendAndResumeCacheTransactionsRepository repository;

		SuspendAndResumeCacheTransactionsService(SuspendAndResumeCacheTransactionsRepository repository) {
			Assert.notNull(repository, "Repository must not be null");
			this.repository = repository;
		}

		@Transactional
		public void doCacheTransactions() {
			this.repository.doOperationOneInTransactionOne();
			this.repository.doOperationOneInTransactionTwo();
			this.repository.doOperationTwoInTransactionOne();
		}
	}

	@Repository
	static class SuspendAndResumeCacheTransactionsRepository {

		@SuppressWarnings("all")
		Region<Object, Object> example;

		SuspendAndResumeCacheTransactionsRepository(Region<Object, Object> example) {
			Assert.notNull(example, "Region must not be null");
			this.example = example;
		}

		@Transactional(propagation = Propagation.REQUIRED)
		public void doOperationOneInTransactionOne() {
			this.example.put("tx-1-op-1", "test");
		}

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		public void doOperationOneInTransactionTwo() {
			this.example.put("tx-2-op-1", "test");
		}

		@Transactional(propagation = Propagation.REQUIRED)
		public void doOperationTwoInTransactionOne() {
			throw new IllegalArgumentException("BOOM");
		}
	}
}
