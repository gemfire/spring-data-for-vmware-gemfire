/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
import org.springframework.data.gemfire.DiskStoreFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link DiskStoreConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.DiskStore
 * @see org.apache.geode.cache.DiskStoreFactory
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.DiskStoreFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.DiskStoreConfiguration
 * @see org.springframework.data.gemfire.config.annotation.DiskStoreConfigurer
 * @see org.springframework.data.gemfire.config.annotation.EnableDiskStore
 * @see org.springframework.data.gemfire.config.annotation.EnableDiskStores
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class DiskStoreConfigurerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("configurerOne")
	private TestDiskStoreConfigurer configurerOne;

	@Autowired
	@Qualifier("configurerTwo")
	private TestDiskStoreConfigurer configurerTwo;

	private void assertDiskStoreConfigurerCalled(TestDiskStoreConfigurer configurer, String... beanNames) {

		assertThat(configurer).isNotNull();
		assertThat(configurer).hasSize(beanNames.length);
		assertThat(configurer).contains(beanNames);
	}

	@Test
	public void diskStoreConfigurerOneCalledSuccessfully() {
		assertDiskStoreConfigurerCalled(this.configurerOne, "cd", "floppy", "tape");
	}

	@Test
	public void diskStoreConfigurerTwoCalledSuccessfully() {
		assertDiskStoreConfigurerCalled(this.configurerTwo, "cd", "floppy", "tape");
	}

	@ClientCacheApplication
	@EnableDiskStores(diskStores = {
		@EnableDiskStore(name = "cd"),
		@EnableDiskStore(name = "floppy"),
		@EnableDiskStore(name = "tape"),
	})
	static class TestConfiguration {

		@Bean
		GemFireMockObjectsBeanPostProcessor testBeanPostProcessor() {
			return new GemFireMockObjectsBeanPostProcessor();
		}

		@Bean
		TestDiskStoreConfigurer configurerOne() {
			return new TestDiskStoreConfigurer();
		}

		@Bean
		TestDiskStoreConfigurer configurerTwo() {
			return new TestDiskStoreConfigurer();
		}

		@Bean
		Object nonRelevantBean() {
			return "test";
		}
	}

	static class TestDiskStoreConfigurer implements DiskStoreConfigurer, Iterable<String> {

		private final Set<String> beanNames = new HashSet<>();

		@Override
		public void configure(String beanName, DiskStoreFactoryBean bean) {
			this.beanNames.add(beanName);
		}

		@Override
		public Iterator<String> iterator() {
			return Collections.unmodifiableSet(this.beanNames).iterator();
		}
	}
}
