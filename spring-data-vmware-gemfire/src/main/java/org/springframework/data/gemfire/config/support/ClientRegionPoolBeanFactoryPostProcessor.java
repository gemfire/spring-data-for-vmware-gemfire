/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.gemfire.util.SpringExtensions;

/**
 * {@link ClientRegionPoolBeanFactoryPostProcessor} is a Spring {@link BeanFactoryPostProcessor} implementation
 * ensuring a proper dependency is declared between a client {@link Region} and a client {@link Pool} it references
 * and uses, providing the client {@link Pool} has been defined and configured with Spring Data for Apache Geode
 * configuration metadata (e.g. XML).
 *
 * @author John Blum
 * @see Region
 * @see Pool
 * @see BeanDefinition
 * @see BeanFactoryPostProcessor
 * @see ConfigurableListableBeanFactory
 * @since 1.8.2
 */
public class ClientRegionPoolBeanFactoryPostProcessor extends AbstractDependencyStructuringBeanFactoryPostProcessor {

	protected static final String POOL_NAME_PROPERTY = "poolName";

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		Set<String> clientRegionBeanNames = new HashSet<>();
		Set<String> poolBeanNames = new HashSet<>();

		Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(beanName -> {

			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

			if (isClientRegionBean(beanDefinition)) {
				clientRegionBeanNames.add(beanName);
			}
			else if (isPoolBean(beanDefinition)) {
				poolBeanNames.add(beanName);
			}
		});

		clientRegionBeanNames.forEach(clientRegionBeanName -> {

			BeanDefinition clientRegionBean = beanFactory.getBeanDefinition(clientRegionBeanName);

			String poolName = getPoolName(clientRegionBean);

			if (poolBeanNames.contains(poolName)) {
				SpringExtensions.addDependsOn(clientRegionBean, poolName);
			}
		});
	}

	String getPoolName(BeanDefinition clientRegionBean) {

		return Optional.ofNullable(clientRegionBean.getPropertyValues().getPropertyValue(POOL_NAME_PROPERTY))
			.map(PropertyValue::getValue)
			.map(String::valueOf)
			.orElse(null);
	}
}
