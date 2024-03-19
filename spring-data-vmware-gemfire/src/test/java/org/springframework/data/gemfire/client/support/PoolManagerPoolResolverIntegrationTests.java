/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnablePool;
import org.springframework.data.gemfire.config.annotation.EnablePools;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link PoolManagerPoolResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.client.PoolManager
 * @see org.springframework.data.gemfire.client.PoolResolver
 * @see org.springframework.data.gemfire.client.support.PoolManagerPoolResolver
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class PoolManagerPoolResolverIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache clientCache;

	@Autowired
	@Qualifier("CarPool")
	private Pool carPool;

	private Pool defaultPool;

	@Autowired
	@Qualifier("SwimmingPool")
	private Pool swimmingPool;

	private final PoolResolver poolResolver = new PoolManagerPoolResolver();

	@Autowired
	@Qualifier("RegionWithDefaultPool")
	private Region<?, ?> regionWithDefaultPool;

	@Autowired
	@Qualifier("RegionWithSwimmingPool")
	private Region<?, ?> regionWithSwimmingPool;

	@Before
	public void setup() {

		assertThat(this.clientCache).isNotNull();
		assertThat(this.clientCache.getName()).isEqualTo(PoolManagerPoolResolverIntegrationTests.class.getSimpleName());
		assertThat(this.carPool).isNotNull();
		assertThat(this.carPool.getName()).isEqualTo("CarPool");
		assertThat(this.swimmingPool).isNotNull();
		assertThat(this.swimmingPool.getName()).isEqualTo("SwimmingPool");

		this.defaultPool = this.clientCache.getDefaultPool();

		assertThat(this.defaultPool).isNotNull();
		assertThat(this.defaultPool).isNotSameAs(this.carPool);
		assertThat(this.defaultPool).isNotSameAs(this.swimmingPool);
	}

	@Test
	public void resolvePoolFromName() {

		assertThat(this.poolResolver.resolve("DEFAULT")).isEqualTo(this.defaultPool);
		assertThat(this.poolResolver.resolve("CarPool")).isEqualTo(this.carPool);
		assertThat(this.poolResolver.resolve("SwimmingPool")).isEqualTo(this.swimmingPool);
	}

	@Test
	public void resolvePoolFromBlankPoolNameReturnsNull() {
		assertThat(this.poolResolver.resolve("  ")).isNull();
	}

	@Test
	public void resolvePoolFromEmptyPoolNameReturnsNull() {
		assertThat(this.poolResolver.resolve("")).isNull();
	}

	@Test
	public void resolvePoolFromNullPoolNameReturnsNull() {
		assertThat(this.poolResolver.resolve((String) null)).isNull();
	}

	@Test
	public void resolvePoolFromRegion() {

		assertThat(this.poolResolver.resolve(this.regionWithDefaultPool)).isEqualTo(this.defaultPool);
		assertThat(this.poolResolver.resolve(this.regionWithSwimmingPool)).isEqualTo(this.swimmingPool);
	}

	@Test
	public void resolvePoolFromNullRegionIsNullSafeAndReturnsNull() {
		assertThat(this.poolResolver.resolve((Region<?, ?>) null)).isNull();
	}

	@ClientCacheApplication(name = "PoolManagerPoolResolverIntegrationTests")
	@EnablePools(pools = {
		@EnablePool(name = "DEFAULT", servers = @EnablePool.Server),
		@EnablePool(name = "CarPool", locators = @EnablePool.Locator),
		@EnablePool(name = "SwimmingPool", locators = @EnablePool.Locator)
	})
	static class TestConfiguration {

		@Bean("RegionWithDefaultPool")
		ClientRegionFactoryBean<Object, Object> regionWithDefaultPool(ClientCache clientCache) {

			ClientRegionFactoryBean<Object, Object> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(clientCache);
			clientRegion.setShortcut(ClientRegionShortcut.PROXY);

			return clientRegion;
		}

		@Bean("RegionWithSwimmingPool")
		@DependsOn("SwimmingPool")
		ClientRegionFactoryBean<Object, Object> regionWithNamedPool(ClientCache clientCache) {

			ClientRegionFactoryBean<Object, Object> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(clientCache);
			clientRegion.setPoolName("SwimmingPool");
			clientRegion.setShortcut(ClientRegionShortcut.PROXY);

			return clientRegion;
		}
	}
}
