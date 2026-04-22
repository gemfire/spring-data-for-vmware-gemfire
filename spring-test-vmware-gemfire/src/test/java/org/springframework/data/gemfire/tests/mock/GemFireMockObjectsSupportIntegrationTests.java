/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-02: Migrated from native ClientCacheFactory/ClientCache to GudClientCacheFactory/GudClientCache;
 *             properties are now set via factory.set() rather than constructor argument
 */
package org.springframework.data.gemfire.tests.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.support.AbstractSecurityManager;

/**
 * Integration tests for {@link GemFireMockObjectsSupport}.
 *
 * @author John Blum
 * @see Properties
 * @see GudClientCacheFactory
 * @see GudClientCache
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

		assertThat(TestSecurityManager.constructed.get()).isFalse();

		GudClientCacheFactory factory = GemFireMockObjectsSupport.spyOn(mock(GudClientCacheFactory.class));

		factory.set("name", "TestConstructsGemFireObjectsFromPropertiesSuccessfully");
		factory.set("security-manager", TestSecurityManager.class.getName());
		factory.create();

		assertThat(TestSecurityManager.constructed.get()).isTrue();
	}

	@Test
	public void destroysConstructedGemFireObjectsFromPropertiesSuccessfully() {

		assertThat(TestSecurityManager.constructed.get()).isFalse();
		assertThat(TestSecurityManager.destroyed.get()).isFalse();
		assertThat(TestSecurityPostProcessor.constructed.get()).isFalse();

		GudClientCacheFactory factory = GemFireMockObjectsSupport.spyOn(mock(GudClientCacheFactory.class));

		factory.set("name", "TestConstructsGemFireObjectsFromPropertiesSuccessfully");
		factory.set("security-manager", TestSecurityManager.class.getName());
		factory.set("security-post-processor", TestSecurityPostProcessor.class.getName());
		factory.create();

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

			GudClientCacheFactory mockCacheFactory = GemFireMockObjectsSupport.spyOn(mock(GudClientCacheFactory.class));

			mockCacheFactory.set("log-level", "info");
			mockCacheFactory.set("jmx-manager-port", "1199");
			mockCacheFactory.set("groups", "test,mock");
			mockCacheFactory.set("groups", "qa,test,testers");
			mockCacheFactory.set("conserve-sockets", "true");

			GudClientCache mockCache = mockCacheFactory.create();

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
