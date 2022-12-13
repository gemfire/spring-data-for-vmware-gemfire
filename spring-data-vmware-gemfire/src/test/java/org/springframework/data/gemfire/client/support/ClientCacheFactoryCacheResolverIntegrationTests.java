/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link ClientCacheFactoryCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @see ClientCacheFactoryCacheResolver
 * @see ClientCacheApplication
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheFactoryCacheResolverIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireCache clientCache;

	@Before
	public void setup() {
		assertThat(this.clientCache).isNotNull();
		assertThat(GemfireUtils.isClient(this.clientCache)).isTrue();
	}

	@Test
	public void clientCacheFactoryCacheResolver() {

		ClientCacheFactoryCacheResolver cacheResolver = spy(ClientCacheFactoryCacheResolver.INSTANCE);

		assertThat(cacheResolver.resolve()).isSameAs(this.clientCache);
		assertThat(cacheResolver.resolve()).isSameAs(this.clientCache);

		verify(cacheResolver, times(1)).doResolve();
	}

	@ClientCacheApplication
	static class TestConfiguration { }

}
