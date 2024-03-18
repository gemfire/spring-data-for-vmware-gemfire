/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link DisableBeanDefinitionOverridingApplicationContextInitializer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.data.gemfire.support.DisableBeanDefinitionOverridingApplicationContextInitializer
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = DisableBeanDefinitionOverridingApplicationContextInitializer.class)
@SuppressWarnings("unused")
public class DisableBeanDefinitionOverridingApplicationContextInitializerIntegrationTests
		extends IntegrationTestsSupport {

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Test
	public void beanDefinitionOverridingIsNotAllowed() {

		Assertions.assertThat(this.applicationContext).isNotNull();

		ConfigurableListableBeanFactory beanFactory = this.applicationContext.getBeanFactory();

		Assertions.assertThat(beanFactory).isInstanceOf(DefaultListableBeanFactory.class);

		Assertions.assertThat(((DefaultListableBeanFactory) beanFactory).isAllowBeanDefinitionOverriding()).isFalse();
	}
}
