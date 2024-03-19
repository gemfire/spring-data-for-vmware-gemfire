/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration.ClusterSchemaObjectInitializer;
import static org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration.SchemaObjectContext;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.tests.util.ReflectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Integration Tests for {@link EnableClusterConfiguration} and {@link EnableSecurity}.
 *
 * @author John Blum
 * @see Authenticator
 * @see PasswordAuthentication
 * @see URI
 * @see Properties
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see ApplicationContextInitializer
 * @see ConfigurableApplicationContext
 * @see ConfigurableEnvironment
 * @see PropertiesPropertySource
 * @see GemfireAdminOperations
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see org.springframework.http.client.ClientHttpRequestInterceptor
 * @see InterceptingClientHttpRequestFactory
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see RestTemplate
 * @since 2.2.0
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers =
	EnableClusterConfigurationWithSecurityIntegrationTests.SecurityConfigurationApplicationContextInitializer.class)
public class EnableClusterConfigurationWithSecurityIntegrationTests extends IntegrationTestsSupport {

	private static final String SECURITY_USERNAME = "security-username";
	private static final String SECURITY_PASSWORD = "security-password";

	@Autowired
	@Qualifier("GemFireSecurityAuthenticator")
	private Authenticator authenticator;

	@Autowired
	private AutoConfiguredAuthenticationConfiguration configuration;

	@Autowired
	private ClusterSchemaObjectInitializer initializer;

	@Before
	public void setup() {
		assertThat(this.authenticator).isNotNull();
		assertThat(this.initializer).isNotNull();
	}

	@Test
	public void authenticatorReturnsPasswordAuthenticationWithGemFireSecurityProperties() {

		PasswordAuthentication passwordAuthentication =
			Authenticator.requestPasswordAuthentication("localhost", null, 80,
				"https", null, "https");

		assertThat(passwordAuthentication).isNotNull();
		assertThat(passwordAuthentication.getUserName()).isEqualTo("skeletor");
		assertThat(String.valueOf(passwordAuthentication.getPassword())).isEqualTo("s3cr3t");
	}

	@Test
	public void initializerRestTemplateIncludesClientHttpRequestInterceptors() throws Exception {

		SchemaObjectContext schemaObjectContext = this.initializer.getSchemaObjectContext();

		assertThat(schemaObjectContext).isNotNull();
		assertThat(schemaObjectContext.<GemfireAdminOperations>getGemfireAdminOperations())
			.isInstanceOf(RestHttpGemfireAdminTemplate.class);

		RestHttpGemfireAdminTemplate template = schemaObjectContext.getGemfireAdminOperations();

		RestTemplate restTemplate = ReflectionUtils.getFieldValue(template, "restTemplate");

		assertThat(restTemplate).isNotNull();
		assertThat(restTemplate.getInterceptors()).hasSize(2);
		assertThat(restTemplate.getRequestFactory()).isInstanceOf(InterceptingClientHttpRequestFactory.class);
	}

	@Test
	public void securityAwareClientHttpRequestInterceptorAppliesGemFireSecurityPropertiesToHttpHeaders()
			throws Exception {

		byte[] body = new byte[0];

		URI uri = URI.create("https://localhost:8080/gemfire/v1");

		ClientHttpRequestExecution mockClientHttpRequestExecution = mock(ClientHttpRequestExecution.class);

		HttpHeaders httpHeaders = new HttpHeaders();

		HttpRequest mockHttpRequest = mock(HttpRequest.class);

		when(mockHttpRequest.getHeaders()).thenReturn(httpHeaders);
		when(mockHttpRequest.getURI()).thenReturn(uri);

		this.configuration.securityAwareClientHttpRequestInterceptor().intercept(mockHttpRequest, body, mockClientHttpRequestExecution);

		assertThat(httpHeaders).containsKeys(SECURITY_USERNAME, SECURITY_PASSWORD);
		assertThat(httpHeaders.getFirst(SECURITY_USERNAME)).isEqualTo("skeletor");
		assertThat(httpHeaders.getFirst(SECURITY_PASSWORD)).isEqualTo("s3cr3t");

		verify(mockClientHttpRequestExecution, times(1)).execute(eq(mockHttpRequest), eq(body));
		verify(mockHttpRequest, times(1)).getHeaders();
		verify(mockHttpRequest, times(1)).getURI();
	}

	static class SecurityConfigurationApplicationContextInitializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {

			ConfigurableEnvironment environment = applicationContext.getEnvironment();

			Properties gemfireSecurityProperties = new Properties();

			gemfireSecurityProperties.setProperty("spring.data.gemfire.security.username", "skeletor");
			gemfireSecurityProperties.setProperty("spring.data.gemfire.security.password", "s3cr3t");

			environment.getPropertySources().
				addFirst(new PropertiesPropertySource("EnableClusterConfigurationWithSecurityIntegrationTests",
					gemfireSecurityProperties));
		}
	}

	@ClientCacheApplication
	@EnableSecurity
	@EnableGemFireMockObjects
	@EnableClusterConfiguration(useHttp = true)
	static class TestConfiguration {

		@Bean
		BeanPostProcessor clusterSchemaObjectInitializerPostProcessor() {

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
	}
}
