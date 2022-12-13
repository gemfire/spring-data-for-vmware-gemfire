/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

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
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link CacheFactoryCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Cache
 * @see GemFireCache
 * @see PeerCacheApplication
 * @see CacheFactoryCacheResolver
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class CacheFactoryCacheResolverIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireCache peerCache;

	@Before
	public void setup() {
		assertThat(this.peerCache).isNotNull();
		assertThat(GemfireUtils.isPeer(this.peerCache)).isTrue();
	}

	@Test
	public void cacheFactoryCacheResolverResolvesPeerCache() {

		CacheFactoryCacheResolver cacheResolver = spy(CacheFactoryCacheResolver.INSTANCE);

		assertThat(cacheResolver.resolve()).isSameAs(this.peerCache);
		assertThat(cacheResolver.resolve()).isSameAs(this.peerCache);

		verify(cacheResolver, times(1)).doResolve();
	}

	@PeerCacheApplication
	static class TestConfiguration { }

}
