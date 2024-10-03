/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.context;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;

/**
 * Unit Tests for {@link GemFireMockObjectsApplicationContextInitializer}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see ConfigurableListableBeanFactory
 * @see ConfigurableApplicationContext
 * @see GemFireMockObjectsBeanPostProcessor
 * @see GemFireMockObjectsApplicationContextInitializer
 * @since 0.0.16
 */
public class GemFireMockObjectsApplicationContextInitializerUnitTests {

	@Test
	public void initializesBeanFactoryWithGemFireMockObjectBeanPostProcessor() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		ConfigurableListableBeanFactory mockBeanFactory = mock(ConfigurableListableBeanFactory.class);

		doReturn(mockBeanFactory).when(mockApplicationContext).getBeanFactory();

		GemFireMockObjectsApplicationContextInitializer initializer =
			new GemFireMockObjectsApplicationContextInitializer();

		initializer.initialize(mockApplicationContext);

		verify(mockApplicationContext, times(1)).getBeanFactory();
		verify(mockBeanFactory, times(1))
			.addBeanPostProcessor(isA(GemFireMockObjectsBeanPostProcessor.class));
	}
}
