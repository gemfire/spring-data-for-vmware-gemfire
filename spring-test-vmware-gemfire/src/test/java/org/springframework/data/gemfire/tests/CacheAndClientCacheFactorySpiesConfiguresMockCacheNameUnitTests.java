/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-02: Migrated from native ClientCacheFactory/ClientCache/DistributionConfig/GemFireCacheImpl
 *             to GudClientCacheFactory/GudClientCache/GudConfigurationProperties
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudConfigurationProperties;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;

/**
 * Unit Tests for spying on {@link GudClientCacheFactory} objects and asserting
 * the configuration of the mock cache instance created by the factories.
 *
 * @author John Blum
 * @see GudClientCacheFactory
 * @see GudClientCache
 * @see GemFireMockObjectsSupport
 * @since 0.0.6
 */
public class CacheAndClientCacheFactorySpiesConfiguresMockCacheNameUnitTests {

	@After
	public void tearDown() {
		GemFireMockObjectsSupport.destroy();
	}

	@Test
	public void cacheFactorySpyConfiguresMockCacheName() {

		GudClientCacheFactory cacheFactory = GemFireMockObjectsSupport.spyOn(mock(GudClientCacheFactory.class));

		cacheFactory.set(GudConfigurationProperties.NAME_NAME, "MockCacheName");

		assertThat(cacheFactory).isNotNull();

		GudClientCache mockCache = cacheFactory.create();

		assertThat(mockCache).isNotNull();
		assertThat(mockCache.getDistributedSystem()).isNotNull();
		assertThat(mockCache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(mockCache.getDistributedSystem().getProperties()
			.getProperty(GudConfigurationProperties.NAME_NAME)).isEqualTo("MockCacheName");
		assertThat(mockCache.getName()).isEqualTo("MockCacheName");
	}

	@Test
	public void clientCacheFactorySpyConfigureMockClientCacheName() {

		GudClientCacheFactory clientCacheFactory = GemFireMockObjectsSupport.spyOn(mock(GudClientCacheFactory.class));

		clientCacheFactory.set(GudConfigurationProperties.NAME_NAME, "MockClientCacheName");

		assertThat(clientCacheFactory).isNotNull();

		GudClientCache mockClientCache = clientCacheFactory.create();

		assertThat(mockClientCache).isNotNull();
		assertThat(mockClientCache.getDistributedSystem()).isNotNull();
		assertThat(mockClientCache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(mockClientCache.getDistributedSystem().getProperties()
			.getProperty(GudConfigurationProperties.NAME_NAME)).isEqualTo("MockClientCacheName");
		assertThat(mockClientCache.getName()).isEqualTo("MockClientCacheName");
	}
}
