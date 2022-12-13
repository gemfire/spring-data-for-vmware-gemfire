/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.util.Properties;
import java.util.function.Function;

import org.junit.Test;

import org.apache.geode.distributed.Locator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.LocatorFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.lang.Nullable;
import org.springframework.mock.env.MockPropertySource;

/**
 * Integration Tests for {@link LocatorApplication} and {@link LocatorApplicationConfiguration} asserting that
 * the {@link Locator} is configured with {@link Properties}.
 *
 * @author John Blum
 * @see Properties
 * @see org.junit.Test
 * @see Locator
 * @see ConfigurableApplicationContext
 * @see PropertySource
 * @see LocatorFactoryBean
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see org.springframework.mock.env.MockPropertySource
 * @since 2.2.0
 */
@SuppressWarnings("unused")
public class LocatorApplicationPropertiesIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
		Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer =
			applicationContext -> {

				MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

				propertySources.addFirst(testPropertySource);

				return applicationContext;
			};

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	public void locatorIsConfiguredWithProperties() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.locator.bind-address", "10.120.240.32")
			.withProperty("spring.data.gemfire.locator.hostname-for-clients", "skullbox")
			.withProperty("spring.data.gemfire.locator.log-level", "error")
			.withProperty("spring.data.gemfire.locator.name", "MockLocator")
			.withProperty("spring.data.gemfire.locator.port", 54321)
			.withProperty("spring.data.gemfire.locators", "host1[1234],host2[6789]");

		newApplicationContext(testPropertySource, TestConfiguration.class);

		LocatorFactoryBean locatorFactoryBean = getBean(LocatorFactoryBean.class);

		assertThat(locatorFactoryBean).isNotNull();
		assertThat(locatorFactoryBean.getBindAddress().orElse(null)).isEqualTo("10.120.240.32");
		assertThat(locatorFactoryBean.getHostnameForClients().orElse(null)).isEqualTo("skullbox");
		assertThat(locatorFactoryBean.getLocators().orElse(null)).isEqualTo("host1[1234],host2[6789]");
		assertThat(locatorFactoryBean.getLogLevel()).isEqualTo("error");
		assertThat(locatorFactoryBean.getName().orElse(null)).isEqualTo("MockLocator");
		assertThat(locatorFactoryBean.getPort()).isEqualTo(54321);
	}

	@EnableGemFireMockObjects
	@LocatorApplication(
		bindAddress = "10.105.210.16",
		hostnameForClients = "mailbox",
		logLevel = "info",
		name = "TestLocator",
		port = 12345
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
