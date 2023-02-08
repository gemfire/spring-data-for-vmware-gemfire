/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.mock.env.MockPropertySource;

/**
 * Integration Tests for {@link AbstractCacheConfiguration}.
 *
 * @author John Blum
 * @see Test
 * @see Cache
 * @see GemFireCache
 * @see ClientCache
 * @see ConfigurableApplicationContext
 * @see PropertySource
 * @see AbstractCacheConfiguration
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @since 2.0.2
 */
public class AbstractCacheConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private void assertName(GemFireCache gemfireCache, String name) {

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties().getProperty("name")).isEqualTo(name);
	}

	@Override
	protected ConfigurableApplicationContext newApplicationContext(Class<?>... annotatedClasses) {
		return newApplicationContext((PropertySource<?>) null, annotatedClasses);
	}

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
			Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer =
			testPropertySource != null ? applicationContext -> {
				Optional.ofNullable(testPropertySource).ifPresent(it -> {

					MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

					propertySources.addFirst(testPropertySource);
				});

				return applicationContext;
			}
			: Function.identity();

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	public void clientCacheNameUsesAnnotationNameAttributeDefaultValue() {

		newApplicationContext(TestClientCacheConfiguration.class);

		GemFireCache peerCache = getBean("gemfireCache", ClientCache.class);

		assertName(peerCache, ClientCacheConfiguration.DEFAULT_NAME);
	}

	@Test
	public void clientCacheNameUsesSpringDataGemFireNameProperty() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.name", "TestClient");

		newApplicationContext(testPropertySource, TestClientCacheConfiguration.class);

		GemFireCache peerCache = getBean("gemfireCache", ClientCache.class);

		assertName(peerCache, "TestClient");
	}

	@Test
	public void peerCacheNameUsesAnnotationNameAttributeConfiguredValue() {

		newApplicationContext(TestPeerCacheConfiguration.class);

		GemFireCache peerCache = getBean("gemfireCache", Cache.class);

		assertName(peerCache, "TestPeerCacheApp");
	}

	@Test
	public void peerCacheNameUsesSpringDataGemFireCacheNameProperty() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.cache.name", "TestPeer");

		newApplicationContext(testPropertySource, TestPeerCacheConfiguration.class);

		GemFireCache peerCache = getBean("gemfireCache", Cache.class);

		assertName(peerCache, "TestPeer");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestClientCacheConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication(name = "TestPeerCacheApp")
	static class TestPeerCacheConfiguration { }

}
