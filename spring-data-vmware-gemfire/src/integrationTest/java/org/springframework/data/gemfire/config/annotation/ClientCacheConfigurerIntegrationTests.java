/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.client.ClientCache;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link ClientCacheConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@SuppressWarnings("unused")
public class ClientCacheConfigurerIntegrationTests extends IntegrationTestsSupport {

	private static final AtomicBoolean testClientCacheConfigurerThreeCalled = new AtomicBoolean(false);

	@Autowired
	private ClientCache clientCache;

	@Autowired
	@Qualifier("testClientCacheConfigurerOne")
	private TestClientCacheConfigurer configurerOne;

	@Autowired
	@Qualifier("testClientCacheConfigurerTwo")
	private TestClientCacheConfigurer configurerTwo;

	@Before
	public void setup() {
		Assertions.assertThat(this.clientCache).isNotNull();
	}

	private void assertClientCacheConfigurerInvokedSuccessfully(TestClientCacheConfigurer clientCacheConfigurer,
			String... beanNames) {

		Assertions.assertThat(clientCacheConfigurer).isNotNull();
		Assertions.assertThat(clientCacheConfigurer).hasSize(beanNames.length);
		Assertions.assertThat(clientCacheConfigurer).contains(beanNames);
	}

	@Test
	public void clientCacheConfigurerOneCalledSuccessfully() {
		assertClientCacheConfigurerInvokedSuccessfully(this.configurerOne, "gemfireCache");
	}

	@Test
	public void clientCacheConfigurerTwoCalledSuccessfully() {
		assertClientCacheConfigurerInvokedSuccessfully(this.configurerTwo, "gemfireCache");
	}

	@Test
	public void clientCacheConfigurerThreeCalledSuccessfully() {
		Assertions.assertThat(testClientCacheConfigurerThreeCalled.get()).isTrue();
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestConfiguration {

		@Bean
		TestClientCacheConfigurer testClientCacheConfigurerOne() {
			return new TestClientCacheConfigurer();
		}

		@Bean
		TestClientCacheConfigurer testClientCacheConfigurerTwo() {
			return new TestClientCacheConfigurer();
		}

		@Bean
		ClientCacheConfigurer testClientCacheConfigurerThree() {
			return (beanName, bean) -> testClientCacheConfigurerThreeCalled.set(true);
		}

		@Bean
		String nonRelevantBean() {
			return "test";
		}
	}

	static final class TestClientCacheConfigurer implements ClientCacheConfigurer, Iterable<String> {

		private final Set<String> beanNames = new HashSet<>();

		@Override
		public void configure(String beanName, ClientCacheFactoryBean bean) {
			this.beanNames.add(beanName);
		}

		@Override
		public Iterator<String> iterator() {
			return Collections.unmodifiableSet(this.beanNames).iterator();
		}
	}
}
