/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.Iterator;

import org.apache.geode.cache.client.Pool;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.PoolAdapter;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.support.ConnectionEndpointList;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link PoolParser} and {@link PoolFactoryBean}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.data.gemfire.client.PoolAdapter
 * @see org.springframework.data.gemfire.client.PoolFactoryBean
 * @see org.springframework.data.gemfire.config.xml.PoolParser
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@SuppressWarnings("unused")
public class PoolNamespaceIntegrationTests {

	@Autowired
	ApplicationContext applicationContext;

	private void assertConnectionEndpoint(ConnectionEndpointList connectionEndpoints,
			String expectedHost, int expectedPort) {

		Assertions.assertThat(connectionEndpoints).isNotNull();
		Assertions.assertThat(connectionEndpoints.size()).isEqualTo(1);
		assertConnectionEndpoint(connectionEndpoints.get(0), expectedHost, expectedPort);
	}

	private void assertConnectionEndpoint(ConnectionEndpoint connectionEndpoint,
			String expectedHost, int expectedPort) {

		Assertions.assertThat(connectionEndpoint).isNotNull();
		Assertions.assertThat(connectionEndpoint.getHost()).isEqualTo(expectedHost);
		Assertions.assertThat(connectionEndpoint.getPort()).isEqualTo(expectedPort);
	}

	private void assertNoConnectionEndpoints(ConnectionEndpointList connectionEndpoints) {
		Assertions.assertThat(connectionEndpoints).isNotNull();
		Assertions.assertThat(connectionEndpoints.isEmpty()).isTrue();
	}

	@Test
	public void gemfirePoolIsConfiguredProperly() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("gemfirePool")).isTrue();
		Assertions.assertThat(applicationContext.containsBean("gemfire-pool")).isTrue();

		PoolFactoryBean poolFactoryBean = applicationContext.getBean("&gemfirePool", PoolFactoryBean.class);

		ConnectionEndpointList locators = TestUtils.readField("locators", poolFactoryBean);

		assertConnectionEndpoint(locators, "localhost", 40403);
	}

	@Test
	public void simplePoolIsConfiguredProperly() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("simple")).isTrue();

		PoolFactoryBean poolFactoryBean = applicationContext.getBean("&simple", PoolFactoryBean.class);

		ConnectionEndpointList servers = TestUtils.readField("servers", poolFactoryBean);

		assertConnectionEndpoint(servers, PoolParser.DEFAULT_HOST, PoolParser.DEFAULT_SERVER_PORT);

		ConnectionEndpointList locators = TestUtils.readField("locators", poolFactoryBean);

		assertNoConnectionEndpoints(locators);
	}

	@Test
	public void locatorPoolIsConfiguredProperly() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("locator")).isTrue();

		PoolFactoryBean poolFactoryBean = applicationContext.getBean("&locator", PoolFactoryBean.class);

		ConnectionEndpointList locators = TestUtils.readField("locators", poolFactoryBean);

		Assertions.assertThat(locators).isNotNull();
		Assertions.assertThat(locators.size()).isEqualTo(2);

		Iterator<ConnectionEndpoint> it = locators.iterator();

		assertConnectionEndpoint(it.next(), "skullbox", PoolParser.DEFAULT_LOCATOR_PORT);
		assertConnectionEndpoint(it.next(), "ghostrider", 12480);

		ConnectionEndpointList servers = TestUtils.readField("servers", poolFactoryBean);

		assertNoConnectionEndpoints(servers);
	}

	@Test
	public void serverPoolIsConfiguredProperly() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("server")).isTrue();

		PoolFactoryBean poolFactoryBean = applicationContext.getBean("&server", PoolFactoryBean.class);
		Pool pool = poolFactoryBean.getPool();

		Assertions.assertThat(pool).isInstanceOf(PoolAdapter.class);
		Assertions.assertThat(pool.getFreeConnectionTimeout()).isEqualTo(2000);
		Assertions.assertThat(pool.getIdleTimeout()).isEqualTo(20000L);
		Assertions.assertThat(pool.getLoadConditioningInterval()).isEqualTo(10000);
		Assertions.assertThat(Boolean.TRUE.equals(TestUtils.readField("keepAlive", poolFactoryBean))).isTrue();
		Assertions.assertThat(pool.getMaxConnections()).isEqualTo(100);
		Assertions.assertThat(pool.getMinConnections()).isEqualTo(5);
		Assertions.assertThat(pool.getMaxConnectionsPerServer()).isEqualTo(200);
		Assertions.assertThat(pool.getMinConnectionsPerServer()).isEqualTo(2);
		Assertions.assertThat(pool.getMultiuserAuthentication()).isTrue();
		Assertions.assertThat(pool.getPingInterval()).isEqualTo(5000L);
		Assertions.assertThat(pool.getPRSingleHopEnabled()).isFalse();
		Assertions.assertThat(pool.getReadTimeout()).isEqualTo(500);
		Assertions.assertThat(pool.getRetryAttempts()).isEqualTo(5);
		Assertions.assertThat(pool.getServerGroup()).isEqualTo("TestGroup");
		Assertions.assertThat(pool.getSocketBufferSize()).isEqualTo(65536);
		Assertions.assertThat(pool.getStatisticInterval()).isEqualTo(250);
		Assertions.assertThat(pool.getSubscriptionAckInterval()).isEqualTo(250);
		Assertions.assertThat(pool.getSubscriptionEnabled()).isTrue();
		Assertions.assertThat(pool.getSubscriptionMessageTrackingTimeout()).isEqualTo(30000);
		Assertions.assertThat(pool.getSubscriptionRedundancy()).isEqualTo(2);
		Assertions.assertThat(pool.getSubscriptionTimeoutMultiplier()).isEqualTo(3);

		ConnectionEndpointList servers = TestUtils.readField("servers", poolFactoryBean);

		Assertions.assertThat(servers).isNotNull();
		Assertions.assertThat(servers.size()).isEqualTo(2);

		Iterator<ConnectionEndpoint> serversIterator = servers.iterator();

		assertConnectionEndpoint(serversIterator.next(), "localhost", 40404);
		assertConnectionEndpoint(serversIterator.next(), "localhost", 50505);
	}

	@Test
	public void locatorsPoolIsConfiguredProperly() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("locators")).isTrue();

		PoolFactoryBean poolFactoryBean = applicationContext.getBean("&locators", PoolFactoryBean.class);

		ConnectionEndpointList locators = TestUtils.readField("locators", poolFactoryBean);

		Assertions.assertThat(locators).isNotNull();
		Assertions.assertThat(locators.size()).isEqualTo(4);

		Iterator<ConnectionEndpoint> locatorIterator = locators.iterator();

		assertConnectionEndpoint(locatorIterator.next(), "earth", 54321);
		assertConnectionEndpoint(locatorIterator.next(), "venus", 11235);
		assertConnectionEndpoint(locatorIterator.next(), "mars", 10334);
		assertConnectionEndpoint(locatorIterator.next(), "localhost", 12480);
	}

	@Test
	public void serversPoolIsConfiguredProperly() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("servers")).isTrue();

		PoolFactoryBean poolFactoryBean = applicationContext.getBean("&servers", PoolFactoryBean.class);

		ConnectionEndpointList servers = TestUtils.readField("servers", poolFactoryBean);

		Assertions.assertThat(servers).isNotNull();
		Assertions.assertThat(servers.size()).isEqualTo(3);

		Iterator<ConnectionEndpoint> serverIterator = servers.iterator();

		assertConnectionEndpoint(serverIterator.next(), "duke", 21480);
		assertConnectionEndpoint(serverIterator.next(), "nukem", 51515);
		assertConnectionEndpoint(serverIterator.next(), "skullbox", 9110);
	}
}
