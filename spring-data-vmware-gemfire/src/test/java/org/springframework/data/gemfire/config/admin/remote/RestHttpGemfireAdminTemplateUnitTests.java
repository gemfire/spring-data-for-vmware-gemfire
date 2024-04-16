/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.admin.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate.FollowRedirectsSimpleClientHttpRequestFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.data.gemfire.config.support.RestTemplateConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Unit tests for {@link RestHttpGemfireAdminTemplate}.
 *
 * @author John Blum
 * @see java.net.URI
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate
 * @see org.springframework.http.HttpHeaders
 * @see org.springframework.http.client.ClientHttpRequestFactory
 * @see org.springframework.http.client.ClientHttpRequestInterceptor
 * @see org.springframework.http.client.InterceptingClientHttpRequestFactory
 * @see org.springframework.web.client.RestOperations
 * @see org.springframework.web.client.RestTemplate
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RestHttpGemfireAdminTemplateUnitTests {

	@Mock
	private ClientCache mockClientCache;

	@Mock
	private Region mockRegion;

	private RestHttpGemfireAdminTemplate template;

	@Mock
	private RestOperations mockRestOperations;

	@Before
	public void setup() {

		this.template = new RestHttpGemfireAdminTemplate(this.mockClientCache) {

			@Override
			@SuppressWarnings("unchecked")
			protected <T extends RestOperations> T newRestOperations(ClientHttpRequestFactory clientHttpRequestFactory,
					List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors,
					List<RestTemplateConfigurer> restTemplateConfigurers) {

				return (T) mockRestOperations;
			}
		};
	}

	@SuppressWarnings("unchecked")
	private <T> T getFieldValue(Object target, String fieldName) throws NoSuchFieldException {

		Field field = ReflectionUtils.findField(target.getClass(), fieldName, ClientHttpRequestFactory.class);

		return Optional.ofNullable(field)
			.map(it -> {
				ReflectionUtils.makeAccessible(it);
				return field;
			})
			.map(it -> (T) ReflectionUtils.getField(it, target))
			.orElseThrow(() ->
				new NoSuchFieldException(String.format("Field [%s] was not found on Object of type [%s]",
					fieldName, target.getClass().getName())));
	}

	@Test
	public void constructDefaultRestHttpGemfireAdminTemplate() {

		RestHttpGemfireAdminTemplate template = new RestHttpGemfireAdminTemplate(this.mockClientCache);

		assertThat(template).isNotNull();
		assertThat(template.getClientCache()).isSameAs(this.mockClientCache);
		assertThat(template.getManagementRestApiUrl())
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_NO_PORT_URL_TEMPLATE,
				RestHttpGemfireAdminTemplate.DEFAULT_SCHEME, RestHttpGemfireAdminTemplate.DEFAULT_HOST));
		assertThat(template.<RestOperations>getRestOperations()).isInstanceOf(RestTemplate.class);

		RestTemplate restTemplate = template.getRestOperations();

		ClientHttpRequestFactory clientHttpRequestFactory = restTemplate.getRequestFactory();

		assertThat(clientHttpRequestFactory).isInstanceOf(FollowRedirectsSimpleClientHttpRequestFactory.class);
		assertThat(((FollowRedirectsSimpleClientHttpRequestFactory) clientHttpRequestFactory).isFollowRedirects())
			.isTrue();

		List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = restTemplate.getInterceptors();

		assertThat(clientHttpRequestInterceptors).isNotNull();
		assertThat(clientHttpRequestInterceptors).isEmpty();
	}

	@Test
	@SuppressWarnings("all")
	public void constructCustomRestHttpGemfireAdminTemplate() throws Exception {

		ClientHttpRequestInterceptor mockInterceptor = mock(ClientHttpRequestInterceptor.class);

		RestHttpGemfireAdminTemplate template =
			new RestHttpGemfireAdminTemplate(this.mockClientCache, "sftp", "skullbox", 8080,
				false, Collections.singletonList(mockInterceptor));

		assertThat(template).isNotNull();
		assertThat(template.getClientCache()).isSameAs(this.mockClientCache);
		assertThat(template.getManagementRestApiUrl())
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_URL_TEMPLATE,
				"sftp", "skullbox", 8080));
		assertThat(template.<RestOperations>getRestOperations()).isInstanceOf(RestTemplate.class);

		RestTemplate restTemplate = (RestTemplate) template.getRestOperations();

		ClientHttpRequestFactory clientHttpRequestFactory = restTemplate.getRequestFactory();

		assertThat(clientHttpRequestFactory).isInstanceOf(InterceptingClientHttpRequestFactory.class);

		clientHttpRequestFactory = this.getFieldValue(clientHttpRequestFactory, "requestFactory");

		assertThat(clientHttpRequestFactory).isInstanceOf(FollowRedirectsSimpleClientHttpRequestFactory.class);
		assertThat(((FollowRedirectsSimpleClientHttpRequestFactory) clientHttpRequestFactory).isFollowRedirects())
			.isFalse();

		List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = restTemplate.getInterceptors();

		assertThat(clientHttpRequestInterceptors).isNotNull();
		assertThat(clientHttpRequestInterceptors).contains(mockInterceptor);
	}

	@Test
	public void newClientHttpRequestFactoryFollowsRedirectsIsTrue() {

		ClientHttpRequestFactory clientHttpRequestFactory =
			this.template.newClientHttpRequestFactory(true);

		assertThat(clientHttpRequestFactory).isInstanceOf(FollowRedirectsSimpleClientHttpRequestFactory.class);
		assertThat(((FollowRedirectsSimpleClientHttpRequestFactory) clientHttpRequestFactory).isFollowRedirects())
			.isTrue();
	}

	@Test
	public void newClientHttpRequestFactoryFollowsRedirectsIsFalse() {

		ClientHttpRequestFactory clientHttpRequestFactory =
			this.template.newClientHttpRequestFactory(false);

		assertThat(clientHttpRequestFactory).isInstanceOf(FollowRedirectsSimpleClientHttpRequestFactory.class);
		assertThat(((FollowRedirectsSimpleClientHttpRequestFactory) clientHttpRequestFactory).isFollowRedirects())
			.isFalse();
	}

	@Test
	public void newRestOperationsWithInterceptors() {

		ClientHttpRequestFactory mockClientHttpRequestFactory = mock(ClientHttpRequestFactory.class);

		ClientHttpRequestInterceptor mockInterceptorOne = mock(ClientHttpRequestInterceptor.class);
		ClientHttpRequestInterceptor mockInterceptorTwo = mock(ClientHttpRequestInterceptor.class);

		RestTemplate restTemplate = new RestHttpGemfireAdminTemplate(this.mockClientCache)
			.newRestOperations(mockClientHttpRequestFactory, Arrays.asList(mockInterceptorOne, mockInterceptorTwo),
				Collections.emptyList());

		assertThat(restTemplate).isNotNull();
		assertThat(restTemplate.getInterceptors()).containsExactly(mockInterceptorOne, mockInterceptorTwo);
		assertThat(restTemplate.getRequestFactory()).isNotSameAs(mockClientHttpRequestFactory);
		assertThat(restTemplate.getRequestFactory()).isInstanceOf(InterceptingClientHttpRequestFactory.class);
	}

	@Test
	public void newRestOperationsWithNoInterceptors() {

		ClientHttpRequestFactory mockClientHttpRequestFactory = mock(ClientHttpRequestFactory.class);

		RestTemplate restTemplate = new RestHttpGemfireAdminTemplate(this.mockClientCache)
			.newRestOperations(mockClientHttpRequestFactory, Collections.emptyList(), Collections.emptyList());

		assertThat(restTemplate).isNotNull();
		assertThat(restTemplate.getInterceptors()).isEmpty();
		assertThat(restTemplate.getRequestFactory()).isSameAs(mockClientHttpRequestFactory);
	}

	@Test
	public void newRestOperationsWithRestTemplateConfigurersApplied() {

		ClientHttpRequestFactory mockClientHttpRequestFactory = mock(ClientHttpRequestFactory.class);

		RestTemplateConfigurer mockRestTemplateConfigurerOne = mock(RestTemplateConfigurer.class);
		RestTemplateConfigurer mockRestTemplateConfigurerTwo = mock(RestTemplateConfigurer.class);

		Answer answer = invocation -> {

			assertThat(invocation.getArgument(0, RestTemplate.class)).isInstanceOf(RestTemplate.class);

			return null;
		};

		doAnswer(answer).when(mockRestTemplateConfigurerOne).configure(isA(RestTemplate.class));
		doAnswer(answer).when(mockRestTemplateConfigurerTwo).configure(isA(RestTemplate.class));

		List<RestTemplateConfigurer> mockRestTemplateConfigurers =
			Arrays.asList(mockRestTemplateConfigurerOne, null, mockRestTemplateConfigurerTwo);

		RestTemplate restTemplate = new RestHttpGemfireAdminTemplate(this.mockClientCache)
			.newRestOperations(mockClientHttpRequestFactory, Collections.emptyList(), mockRestTemplateConfigurers);

		assertThat(restTemplate).isNotNull();

		verify(mockRestTemplateConfigurerOne, times(1)).configure(eq(restTemplate));
		verify(mockRestTemplateConfigurerTwo, times(1)).configure(eq(restTemplate));
	}

	@Test
	public void resolvesManagementRestApiUrlCorrectly() {

		assertThat(this.template.resolveManagementRestApiUrl("http", "boombox", 80))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_URL_TEMPLATE,
				"http", "boombox", 80));

		assertThat(this.template.resolveManagementRestApiUrl("https", "cardboardbox", 443))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_URL_TEMPLATE,
				"https", "cardboardbox", 443));

		assertThat(this.template.resolveManagementRestApiUrl("ftp", "lunchbox", 21))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_URL_TEMPLATE,
				"ftp", "lunchbox", 21));

		assertThat(this.template.resolveManagementRestApiUrl("sftp", "mailbox", 22))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_URL_TEMPLATE,
				"sftp", "mailbox", 22));

		assertThat(this.template.resolveManagementRestApiUrl("smtp", "skullbox", 25))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_URL_TEMPLATE,
				"smtp", "skullbox", 25));
	}

	@Test
	public void resolvesManagementRestApiUrlCorrectlyWhenInvalidPortIsGiven() {

		assertThat(this.template.resolveManagementRestApiUrl("https", "box", -1))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_NO_PORT_URL_TEMPLATE,
				"https", "box"));

		assertThat(this.template.resolveManagementRestApiUrl("http", "dropbox", 0))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_NO_PORT_URL_TEMPLATE,
				"http", "dropbox"));

		assertThat(this.template.resolveManagementRestApiUrl("https", "jambox", 65536))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_NO_PORT_URL_TEMPLATE,
				"https", "jambox"));

		assertThat(this.template.resolveManagementRestApiUrl("http", "shoebox", 101123))
			.isEqualTo(String.format(RestHttpGemfireAdminTemplate.MANAGEMENT_REST_API_NO_PORT_URL_TEMPLATE,
				"http", "shoebox"));
	}
}
