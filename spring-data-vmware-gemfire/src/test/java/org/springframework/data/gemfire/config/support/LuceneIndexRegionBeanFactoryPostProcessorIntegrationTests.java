/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * Integration Tests for {@link LuceneIndexRegionBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.support.LuceneIndexRegionBeanFactoryPostProcessor
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class LuceneIndexRegionBeanFactoryPostProcessorIntegrationTests extends IntegrationTestsSupport {

	private static final List<String> beanNames = new CopyOnWriteArrayList<>();

	@Autowired
	private ApplicationContext applicationContext;

	private BeanDefinition getBeanDefinition(String beanName) {

		return Optional.ofNullable(this.applicationContext)
			.filter(it -> it instanceof AbstractApplicationContext)
			.map(it -> (AbstractApplicationContext) it)
			.map(it -> it.getBeanFactory())
			.map(beanFactory -> beanFactory.getBeanDefinition(beanName))
			.orElse(null);
	}

	@Test
	public void regionLuceneIndexAndDiskStoreBeanDependenciesAreCorrect() {

		BeanDefinition mockDiskStore = getBeanDefinition("MockDiskStore");

		assertThat(mockDiskStore).isNotNull();
		assertThat(nullSafeArray(mockDiskStore.getDependsOn(), String.class))
			.doesNotContain("BookTitleLuceneIndex", "ContractDescriptionLuceneIndex");

		BeanDefinition bookTitleLuceneIndex = getBeanDefinition("BookTitleLuceneIndex");

		assertThat(bookTitleLuceneIndex).isNotNull();
		assertThat(nullSafeArray(bookTitleLuceneIndex.getDependsOn(), String.class))
			.doesNotContain("Books", "BookTitleLuceneIndex", "ContractDescriptionLuceneIndex");

		BeanDefinition contractDescriptionLuceneIndex = getBeanDefinition("ContractDescriptionLuceneIndex");

		assertThat(contractDescriptionLuceneIndex).isNotNull();
		assertThat(nullSafeArray(contractDescriptionLuceneIndex.getDependsOn(), String.class))
			.doesNotContain("Contracts", "BookTitleLuceneIndex", "ContractDescriptionLuceneIndex");

		BeanDefinition booksRegion = getBeanDefinition("Books");

		assertThat(booksRegion).isNotNull();
		assertThat(booksRegion.getDependsOn()).contains("BookTitleLuceneIndex");
		assertThat(booksRegion.getDependsOn()).doesNotContain("ContractDescriptionLuceneIndex");

		BeanDefinition contractsRegion = getBeanDefinition("Contracts");

		assertThat(contractsRegion).isNotNull();
		assertThat(contractsRegion.getDependsOn()).contains("ContractDescriptionLuceneIndex");
		assertThat(contractsRegion.getDependsOn()).doesNotContain("BookTitleLuceneIndex");

		BeanDefinition peopleRegion = getBeanDefinition("People");

		assertThat(peopleRegion).isNotNull();
		assertThat(nullSafeArray(peopleRegion.getDependsOn(), String.class))
			.doesNotContain("BookTitleLuceneIndex", "ContractDescriptionLucenenIndex");
	}

	@Test
	public void gemfireBeanProcessingOrderIsCorrect() {

		assertThat(beanNames.indexOf("BookTitleLuceneIndex")).isLessThan(beanNames.indexOf("Books"));
		assertThat(beanNames.indexOf("ContractDescriptionLuceneIndex")).isLessThan(beanNames.indexOf("Contracts"));
	}

	static class BeanProcessingOrderRecordingBeanPostProcessor implements BeanPostProcessor {

		@Nullable @Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

			if (isRegionBean(bean)) {

				Assert.isTrue(!"Books".equals(beanName) || beanNames.contains("BookTitleLuceneIndex"),
					"Expected [BookTitleLuceneIndex] to already exist");

				Assert.isTrue(!"Contracts".equals(beanName) || beanNames.contains("ContractDescriptionLuceneIndex"),
					"Expected [ContractDescriptionLuceneIndex] to already exist");
			}

			if (!beanNames.contains(beanName)) {
				beanNames.add(beanName);
			}

			return bean;
		}

		private boolean isRegionBean(Object bean) {
			return bean instanceof Region || bean instanceof PeerRegionFactoryBean;
		}
	}
}
