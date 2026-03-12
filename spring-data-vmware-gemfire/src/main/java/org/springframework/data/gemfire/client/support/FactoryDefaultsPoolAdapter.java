/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client.support;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.PoolAdapter;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

/**
 * {@link FactoryDefaultsPoolAdapter} is an abstract implementation of the {@link GudPool} interface and extension of
 * {@link PoolAdapter} that provides default factory values for all configuration properties
 * (e.g. freeConnectionTimeout, idleTimeout, etc).
 *
 * @author John Blum
 * @see InetSocketAddress
 * @see GudPool
 * @see GudPoolFactory
 * @see GudSocketFactory
 * @see PoolAdapter
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class FactoryDefaultsPoolAdapter extends PoolAdapter {

	protected static final boolean DEFAULT_KEEP_ALIVE = false;

	protected static final String DEFAULT_POOL_NAME = "DEFAULT";
	protected static final String LOCALHOST = "localhost";

	@Override
	public int getFreeConnectionTimeout() {
		return GudPoolFactory.DEFAULT_FREE_CONNECTION_TIMEOUT;
	}

	@Override
	public long getIdleTimeout() {
		return GudPoolFactory.DEFAULT_IDLE_TIMEOUT;
	}

	@Override
	public int getLoadConditioningInterval() {
		return GudPoolFactory.DEFAULT_LOAD_CONDITIONING_INTERVAL;
	}

	@Override
	public List<InetSocketAddress> getLocators() {
		return Collections.emptyList();
	}

	@Override
	public int getMaxConnections() {
		return GudPoolFactory.DEFAULT_MAX_CONNECTIONS;
	}

	@Override
	public int getMinConnections() {
		return GudPoolFactory.DEFAULT_MIN_CONNECTIONS;
	}

	@Override
	public int getMaxConnectionsPerServer() {
		return GudPoolFactory.DEFAULT_MAX_CONNECTIONS_PER_SERVER;
	}

	@Override
	public int getMinConnectionsPerServer() {
		return GudPoolFactory.DEFAULT_MIN_CONNECTIONS_PER_SERVER;
	}

	@Override
	public boolean getMultiuserAuthentication() {
		return GudPoolFactory.DEFAULT_MULTIUSER_AUTHENTICATION;
	}

	@Override
	public String getName() {
		return DEFAULT_POOL_NAME;
	}

	@Override
	public List<InetSocketAddress> getOnlineLocators() {
		return Collections.emptyList();
	}

	@Override
	public long getPingInterval() {
		return GudPoolFactory.DEFAULT_PING_INTERVAL;
	}

	@Override
	public boolean getPRSingleHopEnabled() {
		return GudPoolFactory.DEFAULT_PR_SINGLE_HOP_ENABLED;
	}

	@Override
	public GudQueryService getQueryService() {
		return null;
	}

	@Override
	public int getReadTimeout() {
		return GudPoolFactory.DEFAULT_READ_TIMEOUT;
	}

	@Override
	public int getRetryAttempts() {
		return GudPoolFactory.DEFAULT_RETRY_ATTEMPTS;
	}

	@Override
	public int getServerConnectionTimeout() {
		return GudPoolFactory.DEFAULT_SERVER_CONNECTION_TIMEOUT;
	}

	@Override
	public String getServerGroup() {
		return GudPoolFactory.DEFAULT_SERVER_GROUP;
	}

	@Override
	public List<InetSocketAddress> getServers() {
		return Collections.singletonList(new InetSocketAddress(LOCALHOST, GemfireUtils.DEFAULT_CACHE_SERVER_PORT));
	}

	@Override
	public int getSocketBufferSize() {
		return GudPoolFactory.DEFAULT_SOCKET_BUFFER_SIZE;
	}

	@Override
	public int getSocketConnectTimeout() {
		return GudPoolFactory.DEFAULT_SOCKET_CONNECT_TIMEOUT;
	}

	@Override
	public GudSocketFactory getSocketFactory() {
		return GudPoolFactory.DEFAULT_SOCKET_FACTORY;
	}

	@Override
	public int getStatisticInterval() {
		return GudPoolFactory.DEFAULT_STATISTIC_INTERVAL;
	}

	@Override
	public int getSubscriptionAckInterval() {
		return GudPoolFactory.DEFAULT_SUBSCRIPTION_ACK_INTERVAL;
	}

	@Override
	public boolean getSubscriptionEnabled() {
		return GudPoolFactory.DEFAULT_SUBSCRIPTION_ENABLED;
	}

	@Override
	public int getSubscriptionMessageTrackingTimeout() {
		return GudPoolFactory.DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT;
	}

	@Override
	public int getSubscriptionRedundancy() {
		return GudPoolFactory.DEFAULT_SUBSCRIPTION_REDUNDANCY;
	}

	@Override
	public int getSubscriptionTimeoutMultiplier() {
		return GudPoolFactory.DEFAULT_SUBSCRIPTION_TIMEOUT_MULTIPLIER;
	}

	public void destroy() {
		destroy(DEFAULT_KEEP_ALIVE);
	}
}
