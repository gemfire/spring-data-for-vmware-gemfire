/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;

/** Local configuration policy only; no networking, authentication, statistics or queue simulation. */
final class MockPoolConfiguration {
	PoolConfiguration resolve(PoolConfiguration c) {
		if (c.threadLocalConnections().isPresent()) throw UnsupportedFeatureException.notIntroduced("Pool.threadLocalConnections");
		if (c.servers().isEmpty() && c.locators().isEmpty()) invalid();
		var defaults = PoolConfiguration.builder()
			.minimumConnections(1)
			.maximumConnections(-1)
			.minimumConnectionsPerServer(1)
			.maximumConnectionsPerServer(-1)
			.socketConnectTimeout(59000)
			.freeConnectionTimeout(10000)
			.serverConnectionTimeout(0)
			.loadConditioningInterval(300000)
			.socketBufferSize(32768)
			.readTimeout(10000)
			.pingTimeout(10000)
			.idleTimeout(120000L)
			.pingInterval(10000L)
			.statisticInterval(-1)
			.retryAttempts(-1)
			.subscriptionEnabled(false)
			.prSingleHopEnabled(true)
			.subscriptionRedundancy(0)
			.subscriptionMessageTrackingTimeout(900000)
			.subscriptionAckInterval(100)
			.subscriptionTimeoutMultiplier(0)
			.serverGroup("")
			.availabilityZone("")
			.multiuserAuthentication(false)
			.socketFactory(new DefaultDriverSocketFactory())
			.build();
		var effective = c.withFallback(defaults);
		validate(effective);
		return effective;
	}
	private void validate(PoolConfiguration c) {
		int min = c.minimumConnections().orElseThrow(), max = c.maximumConnections().orElseThrow();
		int perMin = c.minimumConnectionsPerServer().orElseThrow(), perMax = c.maximumConnectionsPerServer().orElseThrow();
		if (min < 0 || max < -1 || max == 0 || (max != -1 && min > max)) invalid();
		if (perMin < 0 || perMax < -1 || perMax == 0 || (perMax != -1 && perMin > perMax)) invalid();
		boolean perServer = perMin != 1 || perMax != -1;
		if (perServer && (min != 1 || max != -1 || !c.prSingleHopEnabled().orElseThrow())) invalid();
		if (c.freeConnectionTimeout().orElseThrow() <= 0 || c.socketBufferSize().orElseThrow() <= 0
			|| c.subscriptionMessageTrackingTimeout().orElseThrow() <= 0 || c.subscriptionAckInterval().orElseThrow() <= 0) invalid();
		if (c.serverConnectionTimeout().orElseThrow() < 0 || c.subscriptionTimeoutMultiplier().orElseThrow() < 0
			|| c.statisticInterval().orElseThrow() < -1 || c.retryAttempts().orElseThrow() < -1
			|| c.subscriptionRedundancy().orElseThrow() < -1) invalid();
		long connect = c.socketConnectTimeout().orElseThrow(), read = c.readTimeout().orElseThrow(), ping = c.pingTimeout().orElseThrow();
		long idle = c.idleTimeout().orElseThrow(), load = c.loadConditioningInterval().orElseThrow();
		if ((connect != 0 && connect < 1000) || (read != 0 && read < 100) || (ping != 0 && ping < 100)
			|| (idle != -1 && idle < 1000) || (load != -1 && load < 1000) || c.pingInterval().orElseThrow() < 1000) invalid();
	}
	private void invalid() { throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Invalid pool configuration", null); }
}
