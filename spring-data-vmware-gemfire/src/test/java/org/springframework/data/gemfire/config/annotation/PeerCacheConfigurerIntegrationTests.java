/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link PeerCacheConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.springframework.data.gemfire.CacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheApplication
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SuppressWarnings("unused")
public class PeerCacheConfigurerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private Cache peerCache;

	@Autowired
	@Qualifier("testPeerCacheConfigurerOne")
	private TestPeerCacheConfigurer configurerOne;

	@Autowired
	@Qualifier("testPeerCacheConfigurerTwo")
	private TestPeerCacheConfigurer configurerTwo;

	@Before
	public void setup() {
		assertThat(this.peerCache).isNotNull();
	}

	private void assertTestPeerCacheConfigurerCalledSuccessully(TestPeerCacheConfigurer peerCacheConfigurer,
			String... beanNames) {

		assertThat(peerCacheConfigurer).isNotNull();
		assertThat(peerCacheConfigurer).hasSize(beanNames.length);
		assertThat(peerCacheConfigurer).contains(beanNames);
	}

	@Test
	public void peerCacheConfigurerOneCalledSuccessfully() {
		assertTestPeerCacheConfigurerCalledSuccessully(this.configurerOne, "gemfireCache");
	}

	@Test
	public void peerCacheConfigurerTwoCalledSuccessfully() {
		assertTestPeerCacheConfigurerCalledSuccessully(this.configurerTwo, "gemfireCache");
	}

	@PeerCacheApplication
	static class TestConfiguration {

		@Bean
		GemFireMockObjectsBeanPostProcessor testBeanPostProcessor() {
			return new GemFireMockObjectsBeanPostProcessor();
		}

		@Bean
		TestPeerCacheConfigurer testPeerCacheConfigurerOne() {
			return new TestPeerCacheConfigurer();
		}

		@Bean
		TestPeerCacheConfigurer testPeerCacheConfigurerTwo() {
			return new TestPeerCacheConfigurer();
		}

		@Bean
		String nonRelevantBean() {
			return "test";
		}
	}

	static class TestPeerCacheConfigurer implements Iterable<String>, PeerCacheConfigurer {

		private final Set<String> beanNames = new HashSet<>();

		@Override
		public void configure(String beanName, CacheFactoryBean bean) {
			this.beanNames.add(beanName);
		}

		@Override
		public Iterator<String> iterator() {
			return Collections.unmodifiableSet(this.beanNames).iterator();
		}
	}
}
