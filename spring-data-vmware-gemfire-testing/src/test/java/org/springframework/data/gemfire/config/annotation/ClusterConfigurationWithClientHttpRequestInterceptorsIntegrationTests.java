/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration.ClusterSchemaObjectInitializer;
import static org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration.SchemaObjectContext;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate;
import org.springframework.data.gemfire.config.support.RestTemplateConfigurer;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.tests.util.ReflectionUtils;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Integration Tests for {@link EnableClusterConfiguration} and {@link ClusterConfigurationConfiguration} asserting that
 * all user-defined {@link ClientHttpRequestInterceptor} beans get applied.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.http.client.ClientHttpRequestInterceptor
 * @see org.springframework.http.client.InterceptingClientHttpRequestFactory
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClusterConfigurationWithClientHttpRequestInterceptorsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache clientCache;

	@Autowired
	@Qualifier("mockClientHttpRequestInterceptorOne")
	private ClientHttpRequestInterceptor mockClientHttpRequestInterceptorOne;

	@Autowired
	@Qualifier("mockClientHttpRequestInterceptorTwo")
	private ClientHttpRequestInterceptor mockClientHttpRequestInterceptorTwo;

	@Autowired
	private ClusterConfigurationConfiguration configuration;

	@Autowired
	private ClusterSchemaObjectInitializer initializer;

	@Autowired
	private List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors;

	@Autowired
	private List<RestTemplateConfigurer> restTemplateConfigurers;

	private RestTemplate theRestTemplate;

	@Autowired
	@Qualifier("testRestTemplateConfigurerOne")
	private RestTemplateConfigurer restTemplateConfigurerOne;

	@Autowired
	@Qualifier("testRestTemplateConfigurerTwo")
	private RestTemplateConfigurer restTemplateConfigurerTwo;

	@Before
	public void setupIsCorrect() {

		assertThat(this.clientCache).isNotNull();
		assertThat(this.configuration).isNotNull();
		assertThat(this.initializer).isNotNull();
		assertThat(this.mockClientHttpRequestInterceptorOne).isNotNull();
		assertThat(this.mockClientHttpRequestInterceptorTwo).isNotNull();
		assertThat(this.clientHttpRequestInterceptors).isNotNull();
		assertThat(this.clientHttpRequestInterceptors).hasSize(2);
		assertThat(this.clientHttpRequestInterceptors)
			.containsExactly(this.mockClientHttpRequestInterceptorTwo, this.mockClientHttpRequestInterceptorOne);
		assertThat(this.restTemplateConfigurerOne).isInstanceOf(TestRestTemplateConfigurer.class);
		assertThat(this.restTemplateConfigurerTwo).isInstanceOf(TestRestTemplateConfigurer.class);
		assertThat(this.restTemplateConfigurers).isNotNull();
		assertThat(this.restTemplateConfigurers).hasSize(2);
		assertThat(this.restTemplateConfigurers)
			.containsExactlyInAnyOrder(this.restTemplateConfigurerOne, restTemplateConfigurerTwo);
	}

	@Before
	public void restTemplateWasConfiguredCorrectly() throws Exception {

		assertThat(this.initializer).isNotNull();

		SchemaObjectContext schemaObjectContext = this.initializer.getSchemaObjectContext();

		assertThat(schemaObjectContext).isNotNull();
		assertThat(schemaObjectContext.<ClientCache>getGemfireCache()).isSameAs(this.clientCache);
		assertThat(schemaObjectContext.<GemfireAdminOperations>getGemfireAdminOperations())
			.isInstanceOf(RestHttpGemfireAdminTemplate.class);

		RestHttpGemfireAdminTemplate template = schemaObjectContext.getGemfireAdminOperations();

		this.theRestTemplate = ReflectionUtils.getFieldValue(template, "restTemplate");

		assertThat(this.theRestTemplate).isNotNull();
	}

	@Test
	public void assertRestTemplateConfigurersVisitedAndConfiguredTheClusterConfigurationRestTemplate() {

		assertThat(((TestRestTemplateConfigurer) restTemplateConfigurerOne).getRestTemplate())
			.isEqualTo(this.theRestTemplate);

		assertThat(((TestRestTemplateConfigurer) restTemplateConfigurerTwo).getRestTemplate())
			.isEqualTo(this.theRestTemplate);
	}

	@Test
	public void assertUserDefinedCustomClientHttpRequestInterceptorsAreNotRegisteredByDefault() {

		assertThat(this.theRestTemplate.getInterceptors())
			.doesNotContain(this.mockClientHttpRequestInterceptorTwo, this.mockClientHttpRequestInterceptorOne);

		assertThat(this.theRestTemplate.getRequestFactory())
			.isNotInstanceOf(InterceptingClientHttpRequestFactory.class);
	}

	@Test
	public void configurationWasAutowiredWithUserDefinedClientHttpRequestInterceptors() {

		assertThat(this.configuration.resolveClientHttpRequestInterceptors(true))
			.isEqualTo(this.clientHttpRequestInterceptors);
	}

	@Test
	public void configurationWasAutowiredWithUserDefinedRestTemplateConfigurers() {

		assertThat(this.configuration.resolveRestTemplateConfigurers())
			.isEqualTo(this.restTemplateConfigurers);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableClusterConfiguration(useHttp = true)
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
		@Order(2)
		ClientHttpRequestInterceptor mockClientHttpRequestInterceptorOne() {
			return mock(ClientHttpRequestInterceptor.class);
		}

		@Bean
		@Order(1)
		ClientHttpRequestInterceptor mockClientHttpRequestInterceptorTwo() {
			return mock(ClientHttpRequestInterceptor.class);
		}

		@Bean
		TestRestTemplateConfigurer testRestTemplateConfigurerOne() {
			return new TestRestTemplateConfigurer();
		}

		@Bean
		TestRestTemplateConfigurer testRestTemplateConfigurerTwo() {
			return new TestRestTemplateConfigurer();
		}
	}

	private static final class TestRestTemplateConfigurer implements RestTemplateConfigurer {

		private volatile RestTemplate restTemplate;

		RestTemplate getRestTemplate() {
			return this.restTemplate;
		}

		@Override
		public void configure(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}
	}
}
