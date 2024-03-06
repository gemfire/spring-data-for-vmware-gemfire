/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Optional;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring {@link ApplicationContextInitializer} implementation that disables the Spring container's
 * ({@link ConfigurableApplicationContext}) default behavior of bean definition overriding.
 *
 * @author John Blum
 * @see ApplicationContextInitializer
 * @see ConfigurableApplicationContext
 * @since 2.6.0
 */
public final class DisableBeanDefinitionOverridingApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	/**
	 * @inheritDoc
	 */
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {

		Optional.ofNullable(applicationContext)
			.map(ConfigurableApplicationContext::getBeanFactory)
			.filter(DefaultListableBeanFactory.class::isInstance)
			.map(DefaultListableBeanFactory.class::cast)
			.ifPresent(beanFactory -> beanFactory.setAllowBeanDefinitionOverriding(false));
	}
}
