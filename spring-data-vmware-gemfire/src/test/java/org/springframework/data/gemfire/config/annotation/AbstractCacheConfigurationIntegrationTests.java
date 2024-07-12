/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import java.util.function.Function;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;
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
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.data.gemfire.config.annotation.AbstractCacheConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.0.2
 */
public class AbstractCacheConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private void assertName(ClientCache gemfireCache, String name) {

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

		ClientCache peerCache = getBean("gemfireCache", ClientCache.class);

		assertName(peerCache, ClientCacheConfiguration.DEFAULT_NAME);
	}

	@Test
	public void clientCacheNameUsesSpringDataGemFireNameProperty() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.name", "TestClient");

		newApplicationContext(testPropertySource, TestClientCacheConfiguration.class);

		ClientCache peerCache = getBean("gemfireCache", ClientCache.class);

		assertName(peerCache, "TestClient");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestClientCacheConfiguration { }

}
