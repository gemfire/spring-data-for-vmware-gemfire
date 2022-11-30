// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * The {@link BeanDefinitionRegistryPostProcessorSupport} is an abstract class supporting the implementation
 * of the Spring {@link BeanDefinitionRegistryPostProcessor} interface.
 *
 * @author John Blum
 * @see ConfigurableListableBeanFactory
 * @see BeanDefinitionRegistry
 * @see BeanDefinitionRegistryPostProcessor
 * @since 2.0.0
 */
@SuppressWarnings("all")
public abstract class BeanDefinitionRegistryPostProcessorSupport implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}
}
