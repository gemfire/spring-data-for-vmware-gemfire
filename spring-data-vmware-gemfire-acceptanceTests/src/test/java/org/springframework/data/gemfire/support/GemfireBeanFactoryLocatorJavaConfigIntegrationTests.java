/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests using Java-based configuration for {@link GemfireBeanFactoryLocator}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.CacheFactoryBean
 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class GemfireBeanFactoryLocatorJavaConfigIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	@Test
	public void beanFactoryContainsTestBeanFactoryLocatorBean() {

		Assertions.assertThat(beanFactory.containsBean("testBeanFactoryLocator")).isTrue();

		GemfireBeanFactoryLocator testBeanFactoryLocator = beanFactory.getBean("testBeanFactoryLocator",
			GemfireBeanFactoryLocator.class);

		Assertions.assertThat(testBeanFactoryLocator).isNotNull();
		Assertions.assertThat(testBeanFactoryLocator.getBeanFactory()).isSameAs(beanFactory);
		Assertions.assertThat(testBeanFactoryLocator.getAssociatedBeanName()).isEqualTo("testBeanFactoryLocator");
		Assertions.assertThat(testBeanFactoryLocator.getAssociatedBeanNameWithAliases()).isNotNull();
		Assertions.assertThat(testBeanFactoryLocator.getAssociatedBeanNameWithAliases())
			.containsAll(CollectionUtils.asSet("testBeanFactoryLocator", "aliasOne", "aliasTwo"));
		Assertions.assertThat(beanFactory.getAliases("testBeanFactoryLocator")).containsAll(CollectionUtils.asSet("aliasOne", "aliasTwo"));
	}

	@Test
	public void registeredBeanFactoriesIsCorrect() {

		Set<String> beanNames = CollectionUtils.asSet("gemfireCache", "testBeanFactoryLocator", "aliasOne", "aliasTwo");

		Assertions.assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES).hasSameSizeAs(beanNames);
		Assertions.assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.keySet()).containsAll(beanNames);

		for (String beanName : beanNames) {
			Assertions.assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.get(beanName)).isSameAs(beanFactory);
		}
	}

	@Configuration
	@SuppressWarnings("unused")
	static class TestConfiguration {

		@Bean
		GemFireMockObjectsBeanPostProcessor gemfireTestBeanPostProcessor() {
			return new GemFireMockObjectsBeanPostProcessor();
		}

		@Bean
		CacheFactoryBean gemfireCache() {
			CacheFactoryBean gemfireCache = new CacheFactoryBean();

			gemfireCache.setClose(true);
			gemfireCache.setUseBeanFactoryLocator(true);

			return gemfireCache;
		}

		@Bean(name = { "testBeanFactoryLocator", "aliasOne", "aliasTwo" })
		GemfireBeanFactoryLocator testBeanFactoryLocator() {
			return new GemfireBeanFactoryLocator();
		}
	}
}
