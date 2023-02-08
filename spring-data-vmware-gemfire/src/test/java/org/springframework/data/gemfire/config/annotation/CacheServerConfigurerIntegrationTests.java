/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link CacheServerConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.server.CacheServer
 * @see Configuration
 * @see AddCacheServerConfiguration
 * @see AddCacheServersConfiguration
 * @see CacheServerConfigurer
 * @see EnableCacheServer
 * @see EnableCacheServers
 * @see CacheServerFactoryBean
 * @see IntegrationTestsSupport
 * @see GemFireMockObjectsBeanPostProcessor
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class CacheServerConfigurerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("configurerOne")
	private TestCacheServerConfigurer configurerOne;

	@Autowired
	@Qualifier("configurerTwo")
	private TestCacheServerConfigurer configurerTwo;

	private void assertCacheServerConfigurerCalled(TestCacheServerConfigurer configurer,
			String... cacheServerBeanNames) {

		assertThat(configurer).isNotNull();
		assertThat(configurer).hasSize(cacheServerBeanNames.length);
		assertThat(configurer).contains(cacheServerBeanNames);
	}

	@Test
	public void cacheServerConfigurerOneCalledSuccessfully() {
		assertCacheServerConfigurerCalled(this.configurerOne,
			"gemfireCacheServer", "marsServer", "saturnServer", "venusServer");
	}

	@Test
	public void cacheServerConfigurerTwoCalledSuccessfully() {
		assertCacheServerConfigurerCalled(this.configurerTwo,
			"gemfireCacheServer", "marsServer", "saturnServer", "venusServer");
	}

	@Configuration
	@CacheServerApplication
	@EnableCacheServers(servers = {
		@EnableCacheServer(name = "marsServer"),
		@EnableCacheServer(name = "saturnServer"),
		@EnableCacheServer(name = "venusServer"),
	})
	static class TestConfiguration {

		@Bean
		GemFireMockObjectsBeanPostProcessor testBeanPostProcessor() {
			return new GemFireMockObjectsBeanPostProcessor();
		}

		@Bean
		TestCacheServerConfigurer configurerOne() {
			return new TestCacheServerConfigurer();
		}

		@Bean
		TestCacheServerConfigurer configurerTwo() {
			return new TestCacheServerConfigurer();
		}

		@Bean
		Object nonRelevantBean() {
			return "test";
		}
	}

	static class TestCacheServerConfigurer implements CacheServerConfigurer, Iterable<String> {

		private final Set<String> beanNames = new HashSet<>();

		@Override
		public void configure(String beanName, CacheServerFactoryBean bean) {
			this.beanNames.add(beanName);
		}

		@Override
		public Iterator<String> iterator() {
			return Collections.unmodifiableSet(this.beanNames).iterator();
		}
	}
}
