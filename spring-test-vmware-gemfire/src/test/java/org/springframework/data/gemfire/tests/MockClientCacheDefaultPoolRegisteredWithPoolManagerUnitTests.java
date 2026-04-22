/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-02: Migrated from native Pool/PoolManager to GudPool/GudPoolManager; replaced
 *             new PoolFactoryBean() with anonymous subclass (PoolFactoryBean is now abstract)
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.InetSocketAddress;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnablePool;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit Tests for registering the DEFAULT {@link GudPool} with the pool manager.
 *
 * @author John Blum
 * @see GudPool
 * @see GudCacheProvider
 * @see ClientCacheApplication
 * @see EnablePool
 * @see EnableGemFireMockObjects
 * @see DirtiesContext
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class MockClientCacheDefaultPoolRegisteredWithPoolManagerUnitTests {

	@Autowired
	@Qualifier("DEFAULT")
	private GudPool defaultPool;

	@Autowired
	@Qualifier("MOCK")
	private GudPool mockPool;

	@Autowired
	@Qualifier("TEST")
	private GudPool testPool;

	@AfterClass
	public static void tearDown() {
		GemFireMockObjectsSupport.destroy();
	}

	@Before
	public void setup() {

		assertThat(this.defaultPool).isNotNull();
		assertThat(this.defaultPool.getName()).isEqualTo("DEFAULT");

		assertThat(this.mockPool).isNotNull();
		assertThat(this.mockPool.getName()).isEqualTo("MOCK");
	}

	@Test
	@DirtiesContext
	public void defaultPoolRegisteredWithPoolManager() {

		GudPool defaultPool = GudCacheProvider.getPoolManager().find("DEFAULT");

		assertThat(defaultPool).isNotNull();
		assertThat(defaultPool.getName()).isEqualTo("DEFAULT");
		assertThat(defaultPool).isSameAs(this.defaultPool);
	}

	@Test
	public void mockPoolRegisteredWithPoolManager() {

		GudPool mockPool = GudCacheProvider.getPoolManager().find("MOCK");

		assertThat(mockPool).isNotNull();
		assertThat(mockPool.getName()).isEqualTo("MOCK");
		assertThat(mockPool).isSameAs(this.mockPool);
	}

	@Test
	public void testPoolRegisteredWithPoolManager() {

		GudPool testPool = GudCacheProvider.getPoolManager().find("TEST");

		assertThat(testPool).isNotNull();
		assertThat(testPool.getName()).isEqualTo("TEST");
		assertThat(testPool.getLocators()).containsExactly(new InetSocketAddress("skullbox", 12345));
		assertThat(testPool).isSameAs(this.testPool);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnablePool(name = "DEFAULT")
	static class TestConfiguration {

		@Bean("MOCK")
		GudPool mockPool() {
			return GemFireMockObjectsSupport.mockGudPoolFactory().create("MOCK");
		}

		@Bean("TEST")
		PoolFactoryBean testPool() {

			PoolFactoryBean testPool = new PoolFactoryBean() {
				@Override
				protected GudPoolFactory createPoolFactory() {
					return mock(GudPoolFactory.class);
				}

				@Override
				protected boolean isClientCachePresent() {
					return false;
				}
			};

			testPool.setName("TEST");
			testPool.setLocators(Collections.singleton(new ConnectionEndpoint("skullbox", 12345)));

			return testPool;
		}
	}
}
