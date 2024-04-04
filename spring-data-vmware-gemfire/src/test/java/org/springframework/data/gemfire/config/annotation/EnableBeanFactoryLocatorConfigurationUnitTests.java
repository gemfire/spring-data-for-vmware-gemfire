/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;

/**
 * Unit Tests for {@link EnableBeanFactoryLocator} and {@link BeanFactoryLocatorConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.BeanFactoryLocatorConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableBeanFactoryLocator
 * @since 2.2.1
 */
public class EnableBeanFactoryLocatorConfigurationUnitTests {

	private final BeanFactoryLocatorConfiguration configuration = new BeanFactoryLocatorConfiguration();

	private void testUseBeanFactoryLocatorBeanPostProcessorProcessesBean(Object cacheBean) {

		BeanPostProcessor beanPostProcessor = this.configuration.useBeanFactoryLocatorBeanPostProcessor();

		assertThat(beanPostProcessor).isNotNull();
		assertThat(beanPostProcessor.postProcessBeforeInitialization(cacheBean, "TestCache"))
			.isEqualTo(cacheBean);
	}

	@Test
	public void useBeanFactoryLocatorBeanPostProcessorProcessesCacheFactoryBean() {

		ClientCacheFactoryBean cacheFactoryBean = new ClientCacheFactoryBean();

		assertThat(cacheFactoryBean.isUseBeanFactoryLocator()).isFalse();

		testUseBeanFactoryLocatorBeanPostProcessorProcessesBean(cacheFactoryBean);

		assertThat(cacheFactoryBean.isUseBeanFactoryLocator()).isTrue();
	}

	@Test
	public void useBeanFactoryLocatorBeanPostProcessorProcessesClientCacheFactoryBean() {

		ClientCacheFactoryBean clientCacheFactoryBean = new ClientCacheFactoryBean();

		assertThat(clientCacheFactoryBean.isUseBeanFactoryLocator()).isFalse();

		testUseBeanFactoryLocatorBeanPostProcessorProcessesBean(clientCacheFactoryBean);

		assertThat(clientCacheFactoryBean.isUseBeanFactoryLocator()).isTrue();
	}

	@Test
	public void useBeanFactoryLocatorBeanPostProcessorWillNotProcessObject() {

		Object mockObject = mock(Object.class);

		testUseBeanFactoryLocatorBeanPostProcessorProcessesBean(mockObject);

		verifyNoInteractions(mockObject);
	}

	@Test
	public void useBeanFactoryLocatorClientCacheConfigurerIsCorrect() throws Exception {

		ClientCacheFactoryBean factoryBean = new ClientCacheFactoryBean();

		assertThat(factoryBean.isUseBeanFactoryLocator()).isFalse();

		factoryBean.setClientCacheConfigurers(this.configuration.useBeanFactoryLocatorClientCacheConfigurer());
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.isUseBeanFactoryLocator()).isTrue();
	}
}
