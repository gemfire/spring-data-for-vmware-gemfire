/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;

import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;

/**
 * Unit Tests asserting that when the {@link ClientCacheFactory} is spied on, the {@literal DEFAULT} {@link Pool}
 * is eagerly created, configured and initialized.
 *
 * @author John Blum
 * @see Test
 * @see ClientCache
 * @see ClientCacheFactory
 * @see Pool
 * @see PoolManager
 * @since 0.0.8
 */
public class ClientCacheFactorySpyEagerlyInitializesDefaultPoolUnitTests {

	@After
	public void tearDown() {
		GemFireMockObjectsSupport.destroy();
	}

	@Test
	public void clientCacheFactorySpyEagerlyInitializesDefaultPool() {

		ClientCacheFactory clientCacheFactory = GemFireMockObjectsSupport.spyOn(new ClientCacheFactory())
			.set("name", "TestClientCache")
			.setPoolFreeConnectionTimeout(30000)
			.setPoolIdleTimeout(120000)
			.setPoolLoadConditioningInterval(60000)
			.setPoolMaxConnections(250)
			.setPoolMinConnections(75)
			.setPoolMultiuserAuthentication(true)
			.setPoolPingInterval(5000)
			.setPoolPRSingleHopEnabled(true)
			.setPoolReadTimeout(10000)
			.setPoolRetryAttempts(2)
			.setPoolServerGroup("TestServerGroup")
			.setPoolSocketBufferSize(16384)
			.setPoolSocketConnectTimeout(20000)
			.setPoolStatisticInterval(2000)
			.setPoolSubscriptionAckInterval(15000)
			.setPoolSubscriptionEnabled(true)
			.setPoolSubscriptionMessageTrackingTimeout(300000)
			.setPoolSubscriptionRedundancy(2)
			.setPoolThreadLocalConnections(false);

		assertThat(PoolManager.find("DEFAULT")).isNull();

		ClientCache testClientCache = clientCacheFactory.create();

		assertThat(testClientCache).isNotNull();
		assertThat(testClientCache.getName()).isEqualTo("TestClientCache");

		Pool defaultPool = PoolManager.find("DEFAULT");

		assertThat(defaultPool).isNotNull();
		assertThat(defaultPool).isSameAs(testClientCache.getDefaultPool());
		assertThat(defaultPool.getName()).isEqualTo("DEFAULT");
		assertThat(defaultPool.getFreeConnectionTimeout()).isEqualTo(30000);
		assertThat(defaultPool.getIdleTimeout()).isEqualTo(120000);
		assertThat(defaultPool.getLoadConditioningInterval()).isEqualTo(60000);
		assertThat(defaultPool.getMaxConnections()).isEqualTo(250);
		assertThat(defaultPool.getMinConnections()).isEqualTo(75);
		assertThat(defaultPool.getMultiuserAuthentication()).isEqualTo(true);
		assertThat(defaultPool.getPingInterval()).isEqualTo(5000);
		assertThat(defaultPool.getPRSingleHopEnabled()).isEqualTo(true);
		assertThat(defaultPool.getReadTimeout()).isEqualTo(10000);
		assertThat(defaultPool.getRetryAttempts()).isEqualTo(2);
		assertThat(defaultPool.getServerGroup()).isEqualTo("TestServerGroup");
		assertThat(defaultPool.getSocketBufferSize()).isEqualTo(16384);
		assertThat(defaultPool.getSocketBufferSize()).isEqualTo(16384);
		assertThat(defaultPool.getSocketConnectTimeout()).isEqualTo(20000);
		assertThat(defaultPool.getStatisticInterval()).isEqualTo(2000);
		assertThat(defaultPool.getSubscriptionAckInterval()).isEqualTo(15000);
		assertThat(defaultPool.getSubscriptionEnabled()).isEqualTo(true);
		assertThat(defaultPool.getSubscriptionMessageTrackingTimeout()).isEqualTo(300000);
		assertThat(defaultPool.getSubscriptionRedundancy()).isEqualTo(2);
		assertThat(defaultPool.getThreadLocalConnections()).isEqualTo(false);
	}
}
