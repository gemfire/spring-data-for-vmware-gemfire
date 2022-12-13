/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.support;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.util.SpringExtensions;

/**
 * The ClientCachePoolBeanFactoryPostProcessor class...
 *
 * @author John Blum
 * @since 1.0.0
 */
public class ClientCachePoolBeanFactoryPostProcessor extends AbstractDependencyStructuringBeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(beanName -> {

			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

			if (isPoolBean(beanDefinition)) {
				SpringExtensions.addDependsOn(beanDefinition, GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME);
			}
		});
	}
}
