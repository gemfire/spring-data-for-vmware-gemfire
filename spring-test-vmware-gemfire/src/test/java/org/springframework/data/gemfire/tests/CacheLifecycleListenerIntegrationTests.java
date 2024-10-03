/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.extensions.spring.context.annotation.DependencyOf;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing and asserting {@link GemFireCache} lifecycle events published as
 * {@link ApplicationEvent ApplicationEvents} in a Spring context.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GemFireCache
 * @see EventListener
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 0.0.23
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class CacheLifecycleListenerIntegrationTests extends IntegrationTestsSupport {

	private static GemFireCache staticCache;
	private static TestCacheLifecycleListener staticListener;

	@Autowired
	private GemFireCache cache;

	@Autowired
	private TestCacheLifecycleListener listener;

	@Before
	public void setup() {

		assertThat(this.cache).isNotNull();
		assertThat(this.cache.isClosed()).isFalse();
		assertThat(this.cache.getName()).isEqualTo("CacheLifecycleListenerIntegrationTests");
		assertThat(this.listener).isNotNull();

		staticCache = this.cache;
		staticListener = this.listener;
	}

	@Test
	@DirtiesContext
	public void verifyCacheCreatedEventFired() {

		verify(this.listener, times(1)).handleCacheCreated(argThat(cacheCreatedEvent -> {

			assertThat(cacheCreatedEvent).isNotNull();
			assertThat(cacheCreatedEvent.getCache()).isEqualTo(this.cache);

			return true;
		}));
	}

	@AfterClass
	public static void verifyCacheClosedEventFired() {

		assertThat(staticCache).isNotNull();
		assertThat(staticListener).isNotNull();

		verify(staticListener, times(1)).handleCacheClosed(argThat(cacheClosedEvent -> {

			assertThat(cacheClosedEvent).isNotNull();
			assertThat(cacheClosedEvent.getCache()).isEqualTo(staticCache);

			return true;
		}));
	}

	@ClientCacheApplication(name = "CacheLifecycleListenerIntegrationTests")
	static class TestConfiguration {

		@Bean
		TestCacheLifecycleListener cacheLifecycleListener() {
			return spy(new TestCacheLifecycleListener());
		}
	}

	@Component
	@DependencyOf("gemfireCache")
	static class TestCacheLifecycleListener {

		@EventListener(CacheCreatedEvent.class)
		public void handleCacheCreated(CacheCreatedEvent event) {
			assertThat(event).isNotNull();
		}

		@EventListener(CacheClosedEvent.class)
		public void handleCacheClosed(CacheClosedEvent event) {
			assertThat(event).isNotNull();
		}
	}
}
