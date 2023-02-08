/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.distributed.Locator;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link LocatorApplication} and {@link LocatorApplicationConfiguration} asserting that
 * {@link GemFireCache} and {@link Locator} instances are mutually exclusive.
 *
 * @author John Blum
 * @see Test
 * @see GemFireCache
 * @see ClientCache
 * @see Locator
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see LocatorApplication
 * @see LocatorApplicationConfiguration
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @since 2.2.0
 */
@SuppressWarnings("unused")
public class LocatorApplicationCannotCoexistWithCacheApplicationIntegrationTests
		extends SpringApplicationContextIntegrationTestsSupport {

	private static final String GEMFIRE_LOG_LEVEL = "error";

	private void testCacheAndLocatorApplication(Class<?> testConfiguration) {

		try {

			newApplicationContext(testConfiguration);
			getBean(GemFireCache.class);
			getBean(Locator.class);

			fail("Caches and Locators cannot coexist");

		}
		catch (BeanDefinitionStoreException expected) {

			assertThat(expected)
				.hasMessage(LocatorApplicationConfiguration.LOCATOR_APPLICATION_MUTEX_ERROR_MESSAGE);

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = BeanDefinitionStoreException.class)
	public void clientCacheAndLocatorApplicationThrowsException() {
		testCacheAndLocatorApplication(ClientCacheAndLocatorTestConfiguration.class);
	}

	@Test(expected = BeanDefinitionStoreException.class)
	public void locatorAndPeerCacheApplicationThrowsException() {
		testCacheAndLocatorApplication(LocatorAndPeerCacheTestConfiguration.class);
	}

	@EnableGemFireMockObjects
	@LocatorApplication(logLevel = GEMFIRE_LOG_LEVEL, port = 0)
	static class ClientCacheAndLocatorTestConfiguration {

		@Bean
		ClientCache mockClientCache() {
			return mock(ClientCache.class);
		}
	}

	@EnableGemFireMockObjects
	@LocatorApplication(logLevel = GEMFIRE_LOG_LEVEL, port = 0)
	@PeerCacheApplication(logLevel = GEMFIRE_LOG_LEVEL)
	static class LocatorAndPeerCacheTestConfiguration { }

}
