/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.function.Supplier;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudPoolFactory;

import org.springframework.data.gemfire.GemfireUtils;

/**
 * Unit Tests for {@link FactoryDefaultsPoolAdapter}.
 *
 * @author John Blum
 * @see java.net.InetSocketAddress
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.client.GudPoolFactory
 * @see org.springframework.data.gemfire.client.support.FactoryDefaultsPoolAdapter
 * @since 1.8.0
 */
public class FactoryDefaultsPoolAdapterTest {

	protected static final int DEFAULT_CACHE_SERVER_PORT = GemfireUtils.DEFAULT_CACHE_SERVER_PORT;

	private FactoryDefaultsPoolAdapter poolAdapter = new FactoryDefaultsPoolAdapter() { };

	private InetSocketAddress newSocketAddress(String host, int port) {
		return new InetSocketAddress(host, port);
	}

	@Test
	public void defaultPoolAdapterConfigurationPropertiesReturnDefaultFactorySettings() {

		assertThat(this.poolAdapter.getFreeConnectionTimeout()).isEqualTo(GudPoolFactory.DEFAULT_FREE_CONNECTION_TIMEOUT);
		assertThat(this.poolAdapter.getIdleTimeout()).isEqualTo(GudPoolFactory.DEFAULT_IDLE_TIMEOUT);
		assertThat(this.poolAdapter.getLoadConditioningInterval()).isEqualTo(GudPoolFactory.DEFAULT_LOAD_CONDITIONING_INTERVAL);
		assertThat(this.poolAdapter.getMaxConnections()).isEqualTo(GudPoolFactory.DEFAULT_MAX_CONNECTIONS);
		assertThat(this.poolAdapter.getMinConnections()).isEqualTo(GudPoolFactory.DEFAULT_MIN_CONNECTIONS);
		assertThat(this.poolAdapter.getMultiuserAuthentication()).isEqualTo(GudPoolFactory.DEFAULT_MULTIUSER_AUTHENTICATION);
		assertThat(this.poolAdapter.getPRSingleHopEnabled()).isEqualTo(GudPoolFactory.DEFAULT_PR_SINGLE_HOP_ENABLED);
		assertThat(this.poolAdapter.getPingInterval()).isEqualTo(GudPoolFactory.DEFAULT_PING_INTERVAL);
		assertThat(this.poolAdapter.getReadTimeout()).isEqualTo(GudPoolFactory.DEFAULT_READ_TIMEOUT);
		assertThat(this.poolAdapter.getRetryAttempts()).isEqualTo(GudPoolFactory.DEFAULT_RETRY_ATTEMPTS);
		assertThat(this.poolAdapter.getServerConnectionTimeout()).isEqualTo(GudPoolFactory.DEFAULT_SERVER_CONNECTION_TIMEOUT);
		assertThat(this.poolAdapter.getServerGroup()).isEqualTo(GudPoolFactory.DEFAULT_SERVER_GROUP);
		assertThat(this.poolAdapter.getSocketBufferSize()).isEqualTo(GudPoolFactory.DEFAULT_SOCKET_BUFFER_SIZE);
		assertThat(this.poolAdapter.getSocketConnectTimeout()).isEqualTo(GudPoolFactory.DEFAULT_SOCKET_CONNECT_TIMEOUT);
		assertThat(this.poolAdapter.getSocketFactory()).isEqualTo(GudPoolFactory.DEFAULT_SOCKET_FACTORY);
		assertThat(this.poolAdapter.getStatisticInterval()).isEqualTo(GudPoolFactory.DEFAULT_STATISTIC_INTERVAL);
		assertThat(this.poolAdapter.getSubscriptionAckInterval()).isEqualTo(GudPoolFactory.DEFAULT_SUBSCRIPTION_ACK_INTERVAL);
		assertThat(this.poolAdapter.getSubscriptionEnabled()).isEqualTo(GudPoolFactory.DEFAULT_SUBSCRIPTION_ENABLED);
		assertThat(this.poolAdapter.getSubscriptionMessageTrackingTimeout()).isEqualTo(GudPoolFactory.DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT);
		assertThat(this.poolAdapter.getSubscriptionRedundancy()).isEqualTo(GudPoolFactory.DEFAULT_SUBSCRIPTION_REDUNDANCY);
		assertThat(this.poolAdapter.getSubscriptionTimeoutMultiplier()).isEqualTo(GudPoolFactory.DEFAULT_SUBSCRIPTION_TIMEOUT_MULTIPLIER);
	}

	@Test
	public void locatorsReturnsEmptyList() {
		assertThat(this.poolAdapter.getLocators()).isEqualTo(Collections.<InetSocketAddress>emptyList());
	}

	@Test
	public void nameReturnsDefault() {
		assertThat(this.poolAdapter.getName()).isEqualTo(FactoryDefaultsPoolAdapter.DEFAULT_POOL_NAME);
	}

	@Test
	public void onlineLocatorsIsEmptyList() {
		assertThat(this.poolAdapter.getOnlineLocators()).isEqualTo(Collections.EMPTY_LIST);
	}

	@Test
	public void queryServiceIsNull() {
		assertThat(this.poolAdapter.getQueryService()).isNull();
	}

	@Test
	public void serversReturnsLocalhostListeningOnDefaultCacheServerPort() {
		assertThat(this.poolAdapter.getServers()).isEqualTo(Collections.singletonList(
			newSocketAddress("localhost", DEFAULT_CACHE_SERVER_PORT)));
	}

	private <T> T testPoolOperationIsUnsupported(Supplier<T> poolOperation) {

		try {
			return poolOperation.get();
		}
		catch (UnsupportedOperationException expected) {

			assertThat(expected).hasMessage(FactoryDefaultsPoolAdapter.NOT_IMPLEMENTED);
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void isDestroyedIsUnsupported() {
		testPoolOperationIsUnsupported(() -> this.poolAdapter.isDestroyed());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getPendingEventCountIsUnsupported() {
		testPoolOperationIsUnsupported(() -> this.poolAdapter.getPendingEventCount());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void destroyedIsUnsupported() {
		testPoolOperationIsUnsupported(() -> { this.poolAdapter.destroy(); return null; });
	}

	@Test(expected = UnsupportedOperationException.class)
	public void destroyedWithKeepAliveIsUnsupported() {
		testPoolOperationIsUnsupported(() -> { this.poolAdapter.destroy(false); return null; });
	}
}
