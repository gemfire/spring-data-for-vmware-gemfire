/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.admin.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate.FollowRedirectsSimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * Unit Tests for {@link FollowRedirectsSimpleClientHttpRequestFactory}.
 *
 * @author John Blum
 * @see HttpURLConnection
 * @see Test
 * @see Mockito
 * @see FollowRedirectsSimpleClientHttpRequestFactory
 * @since 2.2.0
 */
public class RestHttpGemfireAdminTemplateFollowRedirectsClientHttpRequestFactoryUnitTests {

	@Test
	public void doesNotFollowRedirectsClientHttpRequestFactory() throws IOException {

		FollowRedirectsSimpleClientHttpRequestFactory clientHttpRequestFactory =
			new FollowRedirectsSimpleClientHttpRequestFactory(false);

		assertThat(clientHttpRequestFactory).isNotNull();
		assertThat(clientHttpRequestFactory.isFollowRedirects()).isFalse();

		HttpURLConnection mockHttpUrlConnection = mock(HttpURLConnection.class);

		doCallRealMethod().when(mockHttpUrlConnection).setInstanceFollowRedirects(anyBoolean());
		doCallRealMethod().when(mockHttpUrlConnection).getInstanceFollowRedirects();

		clientHttpRequestFactory.prepareConnection(mockHttpUrlConnection, "GET");

		assertThat(mockHttpUrlConnection.getInstanceFollowRedirects()).isFalse();

		InOrder inOrder = Mockito.inOrder(mockHttpUrlConnection);

		inOrder.verify(mockHttpUrlConnection, times(1))
			.setInstanceFollowRedirects(eq(true));

		inOrder.verify(mockHttpUrlConnection, times(1))
			.setInstanceFollowRedirects(eq(false));
	}

	@Test
	public void followsRedirectsClientHttpRequestFactory() throws IOException {

		FollowRedirectsSimpleClientHttpRequestFactory clientHttpRequestFactory =
			new FollowRedirectsSimpleClientHttpRequestFactory(true);

		assertThat(clientHttpRequestFactory).isNotNull();
		assertThat(clientHttpRequestFactory.isFollowRedirects()).isTrue();

		HttpURLConnection mockHttpUrlConnection = mock(HttpURLConnection.class);

		doCallRealMethod().when(mockHttpUrlConnection).setInstanceFollowRedirects(anyBoolean());
		doCallRealMethod().when(mockHttpUrlConnection).getInstanceFollowRedirects();

		clientHttpRequestFactory.prepareConnection(mockHttpUrlConnection, "POST");

		assertThat(mockHttpUrlConnection.getInstanceFollowRedirects()).isTrue();

		InOrder inOrder = Mockito.inOrder(mockHttpUrlConnection);

		inOrder.verify(mockHttpUrlConnection, times(1))
			.setInstanceFollowRedirects(eq(false));

		inOrder.verify(mockHttpUrlConnection, times(1))
			.setInstanceFollowRedirects(eq(true));
	}
}
