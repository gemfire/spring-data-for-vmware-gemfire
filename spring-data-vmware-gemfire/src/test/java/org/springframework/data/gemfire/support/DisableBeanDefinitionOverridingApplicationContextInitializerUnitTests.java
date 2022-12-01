/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Unit Tests for {@link DisableBeanDefinitionOverridingApplicationContextInitializer}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see org.springframework.context.ApplicationContextInitializer
 * @see ConfigurableApplicationContext
 * @see DisableBeanDefinitionOverridingApplicationContextInitializer
 * @since 2.6.0
 */
public class DisableBeanDefinitionOverridingApplicationContextInitializerUnitTests {

	private final DisableBeanDefinitionOverridingApplicationContextInitializer applicationContextInitializer =
		new DisableBeanDefinitionOverridingApplicationContextInitializer();

	@Test
	public void initializeDisablesBeanDefinitionOverriding() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		DefaultListableBeanFactory beanFactory = spy(DefaultListableBeanFactory.class);

		doReturn(beanFactory).when(mockApplicationContext).getBeanFactory();

		assertThat(beanFactory.isAllowBeanDefinitionOverriding()).isTrue();

		applicationContextInitializer.initialize(mockApplicationContext);

		assertThat(beanFactory.isAllowBeanDefinitionOverriding()).isFalse();

		verify(mockApplicationContext, times(1)).getBeanFactory();
		verify(beanFactory, times(2)).isAllowBeanDefinitionOverriding();
		verify(beanFactory, times(1)).setAllowBeanDefinitionOverriding(eq(false));
		verifyNoMoreInteractions(mockApplicationContext, beanFactory);
	}
}
