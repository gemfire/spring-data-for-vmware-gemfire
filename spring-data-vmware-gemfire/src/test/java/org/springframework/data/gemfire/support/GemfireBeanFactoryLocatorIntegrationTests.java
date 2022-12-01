/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests using XML for {@link GemfireBeanFactoryLocator}.
 *
 * @author John Blum
 * @see Test
 * @see BeanFactory
 * @see GemfireBeanFactoryLocator
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class GemfireBeanFactoryLocatorIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	@Test
	public void beanFactoryContainsGemfireBeanFactoryLocatorBean() {

		assertThat(beanFactory.containsBean(GemfireBeanFactoryLocator.class.getName())).isTrue();

		GemfireBeanFactoryLocator gemfireBeanFactoryLocator =
			beanFactory.getBean(GemfireBeanFactoryLocator.class.getName(), GemfireBeanFactoryLocator.class);

		assertThat(gemfireBeanFactoryLocator).isNotNull();
		assertThat(gemfireBeanFactoryLocator.getBeanFactory()).isSameAs(beanFactory);
		assertThat(gemfireBeanFactoryLocator.getAssociatedBeanName())
			.startsWith(GemfireBeanFactoryLocator.class.getName());
		assertThat(gemfireBeanFactoryLocator.getAssociatedBeanNameWithAliases()).isNotNull();
		assertThat(gemfireBeanFactoryLocator.getAssociatedBeanNameWithAliases())
			.containsAll(asSet(GemfireBeanFactoryLocator.class.getName()));
		assertThat(beanFactory.getAliases(GemfireBeanFactoryLocator.class.getName()))
			.containsAll(asSet(GemfireBeanFactoryLocator.class.getName().concat("#0")));
	}

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

		Set<String> beanNames = asSet("gemfire-cache", "gemfireCache", "testBeanFactoryLocator", "aliasOne", "aliasTwo",
			GemfireBeanFactoryLocator.class.getName());

		assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.keySet()).containsAll(beanNames);

		for (String beanName : beanNames) {
			assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.get(beanName)).isSameAs(beanFactory);
		}
	}
}
