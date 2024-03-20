/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.LocatorFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link LocatorApplication} and {@link LocatorApplicationConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.LocatorFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.LocatorApplication
 * @see org.springframework.data.gemfire.config.annotation.LocatorApplicationConfiguration
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class LocatorApplicationConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private LocatorFactoryBean locatorFactoryBean;

	@Test
	public void locatorFactoryBeanWasConfiguredFromAnnotationAttributes() {

		assertThat(this.locatorFactoryBean).isNotNull();
		assertThat(this.locatorFactoryBean.getBindAddress().orElse(null)).isEqualTo("10.101.202.8");
		assertThat(this.locatorFactoryBean.getHostnameForClients().orElse(null)).isEqualTo("cardboardBox");
		assertThat(this.locatorFactoryBean.getLocators().orElse(null)).isEqualTo("host1[1234],host2[6789]");
		assertThat(this.locatorFactoryBean.getLogLevel()).isEqualTo("WARN");
		assertThat(this.locatorFactoryBean.getName().orElse(null)).isEqualTo("MockLocator");
		assertThat(this.locatorFactoryBean.getPort()).isEqualTo(9876);
		assertThat(this.locatorFactoryBean.isUseBeanFactoryLocator()).isTrue();
		assertThat(this.locatorFactoryBean.isUseClusterConfigurationService()).isTrue();
	}

	@EnableGemFireMockObjects
	@LocatorApplication(
		bindAddress = "10.101.202.8",
		hostnameForClients = "cardboardBox",
		locators = "host1[1234],host2[6789]",
		logLevel = "WARN",
		name = "MockLocator",
		port = 9876,
		useBeanFactoryLocator = true,
		useClusterConfiguration = true
	)
	static class TestConfiguration {

		@Bean
		BeanPostProcessor locatorFactoryBeanPostProcessor() {

			return new BeanPostProcessor() {

				@Nullable @Override
				public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

					if (bean instanceof LocatorFactoryBean) {

						LocatorFactoryBean locatorFactoryBean = spy((LocatorFactoryBean) bean);

						doNothing().when(locatorFactoryBean).init();

						bean = locatorFactoryBean;

					}

					return bean;
				}
			};
		}
	}
}
