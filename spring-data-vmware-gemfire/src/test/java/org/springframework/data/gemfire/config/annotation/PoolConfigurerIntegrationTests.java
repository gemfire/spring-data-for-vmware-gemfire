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
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link PoolConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.client.PoolFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.AddPoolConfiguration
 * @see org.springframework.data.gemfire.config.annotation.AddPoolsConfiguration
 * @see org.springframework.data.gemfire.config.annotation.PoolConfigurer
 * @see org.springframework.data.gemfire.config.annotation.EnablePool
 * @see org.springframework.data.gemfire.config.annotation.EnablePools
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class PoolConfigurerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("configurerOne")
	private TestPoolConfigurer configurerOne;

	@Autowired
	@Qualifier("configurerTwo")
	private TestPoolConfigurer configurerTwo;

	protected void assertPoolConfigurerCalled(TestPoolConfigurer configurer, String... beanNames) {

		assertThat(configurer).isNotNull();
		assertThat(configurer).hasSize(beanNames.length);
		assertThat(configurer).contains(beanNames);
	}

	@Test
	public void poolConfigurerOneCalledSuccessfully() {
		assertPoolConfigurerCalled(this.configurerOne, "poolOne", "poolTwo", "poolThree");
	}

	@Test
	public void poolConfigurerTwoCalledSuccessfully() {
		assertPoolConfigurerCalled(this.configurerTwo, "poolOne", "poolTwo", "poolThree");
	}

	@Configuration
	@EnablePools(pools = {
		@EnablePool(name = "poolOne"),
		@EnablePool(name = "poolTwo"),
		@EnablePool(name = "poolThree"),
	})
	static class TestConfiguration {

		@Bean
		TestPoolConfigurer configurerOne() {
			return new TestPoolConfigurer();
		}

		@Bean
		TestPoolConfigurer configurerTwo() {
			return new TestPoolConfigurer();
		}

		@Bean
		Object nonRelevantBean() {
			return "test";
		}
	}

	static class TestPoolConfigurer implements Iterable<String>, PoolConfigurer {

		private final Set<String> beanNames = new HashSet<>();

		@Override
		public void configure(String beanName, PoolFactoryBean bean) {
			this.beanNames.add(beanName);
		}

		@Override
		public Iterator<String> iterator() {
			return Collections.unmodifiableSet(this.beanNames).iterator();
		}
	}
}
