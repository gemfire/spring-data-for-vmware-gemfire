// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests using Java-based configuration for {@link GemfireBeanFactoryLocator}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheApplication
 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class GemfireBeanFactoryLocatorAnnotationConfigIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	@Test
	public void beanFactoryContainsTestBeanFactoryLocatorBean() {

		assertThat(beanFactory.containsBean("testBeanFactoryLocator")).isTrue();

		GemfireBeanFactoryLocator testBeanFactoryLocator = beanFactory.getBean("testBeanFactoryLocator",
			GemfireBeanFactoryLocator.class);

		assertThat(testBeanFactoryLocator).isNotNull();
		assertThat(testBeanFactoryLocator.getBeanFactory()).isSameAs(beanFactory);
		assertThat(testBeanFactoryLocator.getAssociatedBeanName()).isEqualTo("testBeanFactoryLocator");
		assertThat(testBeanFactoryLocator.getAssociatedBeanNameWithAliases()).isNotNull();
		assertThat(testBeanFactoryLocator.getAssociatedBeanNameWithAliases())
			.containsAll(asSet("testBeanFactoryLocator", "aliasOne", "aliasTwo"));
		assertThat(beanFactory.getAliases("testBeanFactoryLocator")).containsAll(asSet("aliasOne", "aliasTwo"));
	}

	@Test
	public void registeredBeanFactoriesIsCorrect() {

		Set<String> beanNames = asSet("gemfireCache", "testBeanFactoryLocator", "aliasOne", "aliasTwo");

		assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES).hasSameSizeAs(beanNames);
		assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.keySet()).containsAll(beanNames);

		for (String beanName : beanNames) {
			assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.get(beanName)).isSameAs(beanFactory);
		}
	}

	@PeerCacheApplication(useBeanFactoryLocator = true)
	@SuppressWarnings("unused")
	static class TestConfiguration {

		@Bean
		GemFireMockObjectsBeanPostProcessor gemfireTestBeanPostProcessor() {
			return new GemFireMockObjectsBeanPostProcessor();
		}

		@Bean(name = { "testBeanFactoryLocator", "aliasOne", "aliasTwo" })
		GemfireBeanFactoryLocator testBeanFactoryLocator() {
			return new GemfireBeanFactoryLocator();
		}
	}
}
