/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration.ClusterSchemaObjectInitializer;
import static org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration.SchemaObjectContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableClusterConfiguration} and {@link ClusterConfigurationConfiguration} asserting that
 * SDG support custom registered, user-defined {@link GemfireAdminOperations}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see ClientCache
 * @see GemfireAdminOperations
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClusterConfigurationWithCustomGemfireAdminOperationsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private ClusterConfigurationConfiguration configuration;

	@Autowired
	private ClusterSchemaObjectInitializer initializer;

	@Autowired
	private GemfireAdminOperations gemfireAdminOperations;

	@Before
	public void setup() {

		assertThat(this.clientCache).isNotNull();
		assertThat(this.configuration).isNotNull();
		assertThat(this.gemfireAdminOperations).isNotNull();
		assertThat(this.initializer).isNotNull();
		assertThat(this.configuration.resolveGemfireAdminOperations(null, this.clientCache))
			.isSameAs(this.gemfireAdminOperations);
	}

	@Test
	public void customGemfireAdminOperationsRegistered() {

		SchemaObjectContext schemaObjectContext = this.initializer.getSchemaObjectContext();

		assertThat(schemaObjectContext).isNotNull();
		assertThat(schemaObjectContext.<GemfireAdminOperations>getGemfireAdminOperations())
			.isSameAs(this.gemfireAdminOperations);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableClusterConfiguration
	static class TestConfiguration {

		@Bean
		BeanPostProcessor clusterSchemaObjectInitializerBeanPostProcessor() {

			return new BeanPostProcessor() {

				@Nullable @Override
				public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

					if (bean instanceof ClusterSchemaObjectInitializer) {

						ClusterSchemaObjectInitializer initializer = spy((ClusterSchemaObjectInitializer) bean);

						doReturn(false).when(initializer).isAutoStartup();

						bean = initializer;
					}

					return bean;
				}
			};
		}

		@Bean
		GemfireAdminOperations mockGemfireAdminOperations() {
			return mock(GemfireAdminOperations.class);
		}
	}
}
