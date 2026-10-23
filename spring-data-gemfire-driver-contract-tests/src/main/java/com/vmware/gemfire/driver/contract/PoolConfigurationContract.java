/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.*;
import java.net.Socket;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

/** Shared configuration semantics, not assertions of networking on the mock. */
public abstract class PoolConfigurationContract extends RegionCallbackContract {
	@Test void namedPoolSettingsRoundTripAndCanBeMergedWithoutLosingExplicitValues() {
		try (var connection = connect()) {
			DriverSocketFactory sockets = Socket::new;
			var requested = PoolConfiguration.builder().servers(List.of(new PoolConfiguration.Endpoint("localhost", 40404)))
				.minimumConnections(0).maximumConnections(7).minimumConnectionsPerServer(1).maximumConnectionsPerServer(-1)
				.socketConnectTimeout(2000).freeConnectionTimeout(3210).serverConnectionTimeout(1234)
				.loadConditioningInterval(3000).socketBufferSize(65536).readTimeout(4000).pingTimeout(5000)
				.idleTimeout(6000).pingInterval(7000).statisticInterval(-1).retryAttempts(2)
				.subscriptionEnabled(false).prSingleHopEnabled(false).subscriptionRedundancy(1)
				.subscriptionMessageTrackingTimeout(12000).subscriptionAckInterval(300).subscriptionTimeoutMultiplier(2)
				.serverGroup("workers").availabilityZone("zone-a").multiuserAuthentication(true).socketFactory(sockets).build();
			var pool = connection.createPool("settings-" + UUID.randomUUID(), requested);
			try {
				var actual = pool.configuration();
				assertThat(actual.minimumConnections()).as("minimumConnections").isEqualTo(requested.minimumConnections());
				assertThat(actual.maximumConnections()).as("maximumConnections").isEqualTo(requested.maximumConnections());
				assertThat(actual.minimumConnectionsPerServer()).as("minimumConnectionsPerServer").isEqualTo(requested.minimumConnectionsPerServer());
				assertThat(actual.maximumConnectionsPerServer()).as("maximumConnectionsPerServer").isEqualTo(requested.maximumConnectionsPerServer());
				assertThat(actual.socketConnectTimeout()).as("socketConnectTimeout").isEqualTo(requested.socketConnectTimeout());
				assertThat(actual.freeConnectionTimeout()).as("freeConnectionTimeout").isEqualTo(requested.freeConnectionTimeout());
				assertThat(actual.serverConnectionTimeout()).as("serverConnectionTimeout").isEqualTo(requested.serverConnectionTimeout());
				assertThat(actual.loadConditioningInterval()).as("loadConditioningInterval").isEqualTo(requested.loadConditioningInterval());
				assertThat(actual.socketBufferSize()).as("socketBufferSize").isEqualTo(requested.socketBufferSize());
				assertThat(actual.readTimeout()).as("readTimeout").isEqualTo(requested.readTimeout());
				assertThat(actual.pingTimeout()).as("pingTimeout").isEqualTo(requested.pingTimeout());
				assertThat(actual.idleTimeout()).as("idleTimeout").isEqualTo(requested.idleTimeout());
				assertThat(actual.pingInterval()).as("pingInterval").isEqualTo(requested.pingInterval());
				assertThat(actual.statisticInterval()).as("statisticInterval").isEqualTo(requested.statisticInterval());
				assertThat(actual.retryAttempts()).as("retryAttempts").isEqualTo(requested.retryAttempts());
				assertThat(actual.subscriptionEnabled()).as("subscriptionEnabled").isEqualTo(requested.subscriptionEnabled());
				assertThat(actual.prSingleHopEnabled()).as("prSingleHopEnabled").isEqualTo(requested.prSingleHopEnabled());
				assertThat(actual.subscriptionRedundancy()).as("subscriptionRedundancy").isEqualTo(requested.subscriptionRedundancy());
				assertThat(actual.subscriptionMessageTrackingTimeout()).as("subscriptionMessageTrackingTimeout").isEqualTo(requested.subscriptionMessageTrackingTimeout());
				assertThat(actual.subscriptionAckInterval()).as("subscriptionAckInterval").isEqualTo(requested.subscriptionAckInterval());
				assertThat(actual.subscriptionTimeoutMultiplier()).as("subscriptionTimeoutMultiplier").isEqualTo(requested.subscriptionTimeoutMultiplier());
				assertThat(actual.serverGroup()).as("serverGroup").isEqualTo(requested.serverGroup());
				assertThat(actual.availabilityZone()).as("availabilityZone").isEqualTo(requested.availabilityZone());
				assertThat(actual.multiuserAuthentication()).as("multiuserAuthentication").isEqualTo(requested.multiuserAuthentication());
				assertThat(actual.socketFactory()).as("socketFactory").isEqualTo(requested.socketFactory());
				assertThat(connection.getPool(pool.name()).configuration().socketFactory()).containsSame(sockets);
				var overlay = PoolConfiguration.builder().serverGroup("").multiuserAuthentication(false).retryAttempts(0).build().withFallback(actual);
				assertThat(overlay.serverGroup()).contains(""); assertThat(overlay.multiuserAuthentication()).contains(false);
				assertThat(overlay.retryAttempts()).hasValue(0); assertThat(overlay.servers()).isEqualTo(actual.servers());
			} finally { pool.destroy(); }
		}
	}
	@Test void invalidSettingsDoNotPublishAPoolOrReplaceAnExistingOne() {
		try (var connection = connect()) {
			var endpoints = List.of(new PoolConfiguration.Endpoint("localhost", 40404));
			List<PoolConfiguration> invalid = List.of(
				PoolConfiguration.builder().build(),
				PoolConfiguration.builder().servers(endpoints).freeConnectionTimeout(0).build(),
				PoolConfiguration.builder().servers(endpoints).minimumConnections(-1).build(),
				PoolConfiguration.builder().servers(endpoints).maximumConnections(0).build(),
				PoolConfiguration.builder().servers(endpoints).minimumConnections(0).maximumConnectionsPerServer(3).build(),
				PoolConfiguration.builder().servers(endpoints).minimumConnectionsPerServer(0).prSingleHopEnabled(false).build(),
				PoolConfiguration.builder().servers(endpoints).minimumConnections(4).maximumConnections(2).build(),
				PoolConfiguration.builder().servers(endpoints).readTimeout(1).build());
			for (var config : invalid) {
				String name = "invalid-" + UUID.randomUUID();
				assertThatThrownBy(() -> connection.createPool(name, config)).isInstanceOf(DriverOperationException.class);
				assertThat(connection.getPool(name)).isNull();
			}
			var pool = connection.createPool("existing-" + UUID.randomUUID(), new PoolConfiguration(endpoints, List.of(), 0));
			try {
				assertThatThrownBy(() -> connection.createPool(pool.name(), new PoolConfiguration(endpoints, List.of(), 0)))
					.isInstanceOf(DriverOperationException.class);
				assertThat(connection.getPool(pool.name()).isDestroyed()).isFalse();
			} finally { pool.destroy(); }
		}
	}
	@Test void threadLocalConnectionRequestsAreExplicitlyUnsupportedIncludingFalse() {
		try (var connection = connect()) {
			for (boolean setting : List.of(false, true)) {
				var c = PoolConfiguration.builder().servers(List.of(new PoolConfiguration.Endpoint("localhost", 40404)))
					.minimumConnections(0).threadLocalConnections(setting).build();
				assertThatThrownBy(() -> connection.createPool("unsupported", c)).isInstanceOfSatisfying(UnsupportedFeatureException.class,
					e -> assertThat(e.feature()).isEqualTo("Pool.threadLocalConnections"));
				assertThat(connection.getPool("unsupported")).isNull();
			}
		}
	}
}
