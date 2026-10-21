/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

import org.apache.geode.cache.client.Pool;

import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

final class GemFirePool implements DriverPool {

	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	private final Pool pool;

	GemFirePool(Pool pool) { this.pool = pool; }

	@Override public PoolConfiguration configuration() {
		return invoke("configuration", () -> {
			var builder = PoolConfiguration.builder().servers(servers()).locators(locators());
			builder.minimumConnections(pool.getMinConnections());
			builder.maximumConnections(pool.getMaxConnections());
			builder.minimumConnectionsPerServer(pool.getMinConnectionsPerServer());
			builder.maximumConnectionsPerServer(pool.getMaxConnectionsPerServer());
			builder.socketConnectTimeout(pool.getSocketConnectTimeout());
			builder.freeConnectionTimeout(pool.getFreeConnectionTimeout());
			builder.serverConnectionTimeout(pool.getServerConnectionTimeout());
			builder.loadConditioningInterval(pool.getLoadConditioningInterval());
			builder.socketBufferSize(pool.getSocketBufferSize());
			builder.readTimeout(pool.getReadTimeout());
			builder.pingTimeout(pool.getPingTimeout());
			builder.idleTimeout(pool.getIdleTimeout());
			builder.pingInterval(pool.getPingInterval());
			builder.statisticInterval(pool.getStatisticInterval());
			builder.retryAttempts(pool.getRetryAttempts());
			builder.subscriptionEnabled(pool.getSubscriptionEnabled());
			builder.prSingleHopEnabled(pool.getPRSingleHopEnabled());
			builder.subscriptionRedundancy(pool.getSubscriptionRedundancy());
			builder.subscriptionMessageTrackingTimeout(pool.getSubscriptionMessageTrackingTimeout());
			builder.subscriptionAckInterval(pool.getSubscriptionAckInterval());
			builder.subscriptionTimeoutMultiplier(pool.getSubscriptionTimeoutMultiplier());
			builder.serverGroup(pool.getServerGroup());
			builder.availabilityZone(pool.getAvailabilityZone());
			builder.multiuserAuthentication(pool.getMultiuserAuthentication());
			var socketFactory = GemFireSocketFactory.adapt(pool.getSocketFactory());
			if (socketFactory != null) builder.socketFactory(socketFactory);
			return builder.build();
		});
	}

	@Override public String name() { return invoke("name", pool::getName); }
	@Override public List<Endpoint> servers() { return invoke("servers", () -> endpoints(pool.getServers())); }
	@Override public List<Endpoint> locators() { return invoke("locators", () -> endpoints(pool.getLocators())); }
	@Override public int minimumConnections() { return invoke("minimumConnections", pool::getMinConnections); }
	@Override public boolean isDestroyed() { return invoke("isDestroyed", pool::isDestroyed); }
	@Override public void destroy() { invoke("destroy", () -> { pool.destroy(); return null; }); }

	private List<Endpoint> endpoints(List<InetSocketAddress> addresses) {
		return addresses.stream().map(address -> new Endpoint(address.getHostString(), address.getPort())).toList();
	}

	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException failure) {
			throw exceptions.translate(operation, failure);
		}
	}
}
