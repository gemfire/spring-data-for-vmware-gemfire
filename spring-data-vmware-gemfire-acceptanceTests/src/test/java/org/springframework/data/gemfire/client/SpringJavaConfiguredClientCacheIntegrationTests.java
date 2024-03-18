/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import java.util.Properties;

import org.apache.geode.cache.client.ClientCache;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for the proper configuration of a {@link ClientCache} instance
 * using Spring Java-based configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @link https://jira.spring.io/browse/SGF-441
 * @since 1.8.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringJavaConfiguredClientCacheIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class SpringJavaConfiguredClientCacheIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("&clientCache")
	private ClientCacheFactoryBean clientCacheFactoryBean;

	@Autowired
	private Properties gemfireProperties;

	@Test
	public void clientCacheFactoryBeanConfiguration() {

		Assertions.assertThat(clientCacheFactoryBean).isNotNull();
		Assertions.assertThat(clientCacheFactoryBean.getBeanName()).isEqualTo("clientCache");
		Assertions.assertThat(clientCacheFactoryBean.getProperties()).isEqualTo(gemfireProperties);
	}

	@Configuration
	static class TestConfiguration {

		@Bean
		Properties gemfireProperties() {

			Properties gemfireProperties = new Properties();

			gemfireProperties.setProperty("name", SpringJavaConfiguredClientCacheIntegrationTests.class.getSimpleName());

			return gemfireProperties;
		}

		@Bean
		ClientCacheFactoryBean clientCache() {

			ClientCacheFactoryBean clientCacheFactoryBean = new ClientCacheFactoryBean();

			clientCacheFactoryBean.setUseBeanFactoryLocator(false);
			clientCacheFactoryBean.setProperties(gemfireProperties());

			return clientCacheFactoryBean;
		}
	}
}
