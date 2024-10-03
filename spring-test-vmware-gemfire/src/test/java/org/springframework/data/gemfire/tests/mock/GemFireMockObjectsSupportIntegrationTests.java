/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.After;
import org.junit.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.support.AbstractSecurityManager;

/**
 * Integration tests for {@link GemFireMockObjectsSupport}.
 *
 * @author John Blum
 * @see Properties
 * @see Test
 * @see ClientCacheFactory
 * @see IntegrationTestsSupport
 * @see GemFireMockObjectsSupport
 * @see AbstractSecurityManager
 * @since 1.0.0
 */
public class GemFireMockObjectsSupportIntegrationTests extends IntegrationTestsSupport {

	@After
	public void tearDown() {

		GemFireMockObjectsSupport.destroy();

		TestSecurityManager.constructed.set(false);
		TestSecurityManager.destroyed.set(false);

		TestSecurityPostProcessor.constructed.set(false);
	}

	@Test
	public void constructsGemFireObjectsFromPropertiesSuccessfully() {

		Properties gemfireProperties = new Properties();

		gemfireProperties.setProperty("name", "TestConstructsGemFireObjectsFromPropertiesSuccessfully");
		gemfireProperties.setProperty("security-manager", TestSecurityManager.class.getName());

		assertThat(TestSecurityManager.constructed.get()).isFalse();

		GemFireMockObjectsSupport.spyOn(new ClientCacheFactory(gemfireProperties)).create();

		assertThat(TestSecurityManager.constructed.get()).isTrue();
	}

	@Test
	public void destroysConstructedGemFireObjectsFromPropertiesSuccessfully() {

		Properties gemfireProperties = new Properties();

		gemfireProperties.setProperty("name", "TestConstructsGemFireObjectsFromPropertiesSuccessfully");
		gemfireProperties.setProperty("security-manager", TestSecurityManager.class.getName());
		gemfireProperties.setProperty("security-post-processor", TestSecurityPostProcessor.class.getName());

		assertThat(TestSecurityManager.constructed.get()).isFalse();
		assertThat(TestSecurityManager.destroyed.get()).isFalse();
		assertThat(TestSecurityPostProcessor.constructed.get()).isFalse();

		GemFireMockObjectsSupport.spyOn(new ClientCacheFactory(gemfireProperties)).create();

		assertThat(TestSecurityManager.constructed.get()).isTrue();
		assertThat(TestSecurityManager.destroyed.get()).isFalse();
		assertThat(TestSecurityPostProcessor.constructed.get()).isTrue();

		GemFireMockObjectsSupport.destroyGemFireObjects();

		assertThat(TestSecurityManager.destroyed.get()).isTrue();
	}

	@Test
	public void storesGemFirePropertiesSuccessfully() {

		try {

			System.setProperty("gemfire.name", "TestStoresGemFirePropertiesSuccessfully");
			System.setProperty("gemfire.log-level", "config");
			System.setProperty("gemfire.locators", "skullbox[12345]");
			System.setProperty("non-gemfire.property", "test");

			Properties gemfireProperties = new Properties();

			gemfireProperties.setProperty("log-level", "info");
			gemfireProperties.setProperty("jmx-manager-port", "1199");
			gemfireProperties.setProperty("groups", "test,mock");

			ClientCacheFactory mockCacheFactory =
				GemFireMockObjectsSupport.spyOn(new ClientCacheFactory(gemfireProperties));

			mockCacheFactory.set("groups", "qa,test,testers");
			mockCacheFactory.set("conserve-sockets", "true");

			ClientCache mockCache = mockCacheFactory.create();

			assertThat(mockCache).isNotNull();
			assertThat(mockCache.getDistributedSystem()).isNotNull();

			Properties actualGemFireProperties = mockCache.getDistributedSystem().getProperties();

			assertThat(actualGemFireProperties).isNotNull();
			assertThat(actualGemFireProperties).hasSize(6);
			assertThat(actualGemFireProperties.getProperty("name")).isEqualTo("TestStoresGemFirePropertiesSuccessfully");
			assertThat(actualGemFireProperties.getProperty("log-level")).isEqualTo("config");
			assertThat(actualGemFireProperties.getProperty("locators")).isEqualTo("skullbox[12345]");
			assertThat(actualGemFireProperties.getProperty("jmx-manager-port")).isEqualTo("1199");
			assertThat(actualGemFireProperties.getProperty("groups")).isEqualTo("qa,test,testers");
			assertThat(actualGemFireProperties.getProperty("conserve-sockets")).isEqualTo("true");
		}
		finally {
			System.clearProperty("gemfire.name");
			System.clearProperty("gemfire.log-level");
			System.clearProperty("non-gemfire.property");
		}
	}

	public static final class TestSecurityManager extends AbstractSecurityManager implements DisposableBean {

		private static final AtomicBoolean constructed = new AtomicBoolean(false);
		private static final AtomicBoolean destroyed = new AtomicBoolean(false);

		public TestSecurityManager() {
			constructed.set(true);
		}

		@Override
		public void destroy() {
			destroyed.set(true);
		}
	}

	public static final class TestSecurityPostProcessor {

		private static final AtomicBoolean constructed = new AtomicBoolean(false);

		public TestSecurityPostProcessor() {
			constructed.set(true);
		}
	}
}
