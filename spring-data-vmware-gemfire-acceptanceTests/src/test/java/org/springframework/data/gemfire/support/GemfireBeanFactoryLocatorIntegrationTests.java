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
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests using XML for {@link GemfireBeanFactoryLocator}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
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

		Assertions.assertThat(beanFactory.containsBean(GemfireBeanFactoryLocator.class.getName())).isTrue();

		GemfireBeanFactoryLocator gemfireBeanFactoryLocator =
			beanFactory.getBean(GemfireBeanFactoryLocator.class.getName(), GemfireBeanFactoryLocator.class);

		Assertions.assertThat(gemfireBeanFactoryLocator).isNotNull();
		Assertions.assertThat(gemfireBeanFactoryLocator.getBeanFactory()).isSameAs(beanFactory);
		Assertions.assertThat(gemfireBeanFactoryLocator.getAssociatedBeanName())
			.startsWith(GemfireBeanFactoryLocator.class.getName());
		Assertions.assertThat(gemfireBeanFactoryLocator.getAssociatedBeanNameWithAliases()).isNotNull();
		Assertions.assertThat(gemfireBeanFactoryLocator.getAssociatedBeanNameWithAliases())
			.containsAll(CollectionUtils.asSet(GemfireBeanFactoryLocator.class.getName()));
		Assertions.assertThat(beanFactory.getAliases(GemfireBeanFactoryLocator.class.getName()))
			.containsAll(CollectionUtils.asSet(GemfireBeanFactoryLocator.class.getName().concat("#0")));
	}

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

		Set<String> beanNames = CollectionUtils.asSet("gemfire-cache", "gemfireCache", "testBeanFactoryLocator", "aliasOne", "aliasTwo",
			GemfireBeanFactoryLocator.class.getName());

		Assertions.assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.keySet()).containsAll(beanNames);

		for (String beanName : beanNames) {
			Assertions.assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES.get(beanName)).isSameAs(beanFactory);
		}
	}
}
