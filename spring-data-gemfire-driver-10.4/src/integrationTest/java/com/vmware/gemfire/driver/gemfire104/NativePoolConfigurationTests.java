/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.apache.geode.cache.client.*;
import com.vmware.gemfire.driver.spi.*;

class NativePoolConfigurationTests {
	@Test void allSettingsAndDefaultsMatchNativePoolObservations() throws Exception {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var endpoints = List.of(new PoolConfiguration.Endpoint("localhost", 40404));
			List<PoolConfiguration> settings = List.of(
				PoolConfiguration.builder().servers(endpoints).minimumConnections(0).build(),
				PoolConfiguration.builder().servers(endpoints).minimumConnectionsPerServer(0).maximumConnectionsPerServer(3).build(),
				PoolConfiguration.builder().servers(endpoints).minimumConnections(0).maximumConnections(4)
					.socketConnectTimeout(3000).freeConnectionTimeout(7654).serverConnectionTimeout(2345)
					.loadConditioningInterval(-1).socketBufferSize(12345).readTimeout(0).pingTimeout(0)
					.idleTimeout(-1).pingInterval(4000).statisticInterval(4321).retryAttempts(0)
					.subscriptionEnabled(false).prSingleHopEnabled(false).subscriptionRedundancy(-1)
					.subscriptionMessageTrackingTimeout(3210).subscriptionAckInterval(123).subscriptionTimeoutMultiplier(4)
					.serverGroup("group").availabilityZone("zone").multiuserAuthentication(true).socketFactory(Socket::new).build());
			for (var config : settings) {
				var pool = connection.createPool("observed-" + UUID.randomUUID(), config);
				try {
					var nativePool = PoolManager.find(pool.name()); var actual = pool.configuration();
					assertThat(actual.minimumConnections().orElseThrow()).as("minimumConnections snapshot").isEqualTo(nativePool.getMinConnections());
					config.minimumConnections().ifPresent(value -> assertThat(nativePool.getMinConnections()).as("minimumConnections applied").isEqualTo(value));
					assertThat(actual.maximumConnections().orElseThrow()).as("maximumConnections snapshot").isEqualTo(nativePool.getMaxConnections());
					config.maximumConnections().ifPresent(value -> assertThat(nativePool.getMaxConnections()).as("maximumConnections applied").isEqualTo(value));
					assertThat(actual.minimumConnectionsPerServer().orElseThrow()).as("minimumConnectionsPerServer snapshot").isEqualTo(nativePool.getMinConnectionsPerServer());
					config.minimumConnectionsPerServer().ifPresent(value -> assertThat(nativePool.getMinConnectionsPerServer()).as("minimumConnectionsPerServer applied").isEqualTo(value));
					assertThat(actual.maximumConnectionsPerServer().orElseThrow()).as("maximumConnectionsPerServer snapshot").isEqualTo(nativePool.getMaxConnectionsPerServer());
					config.maximumConnectionsPerServer().ifPresent(value -> assertThat(nativePool.getMaxConnectionsPerServer()).as("maximumConnectionsPerServer applied").isEqualTo(value));
					assertThat(actual.socketConnectTimeout().orElseThrow()).as("socketConnectTimeout snapshot").isEqualTo(nativePool.getSocketConnectTimeout());
					config.socketConnectTimeout().ifPresent(value -> assertThat(nativePool.getSocketConnectTimeout()).as("socketConnectTimeout applied").isEqualTo(value));
					assertThat(actual.freeConnectionTimeout().orElseThrow()).as("freeConnectionTimeout snapshot").isEqualTo(nativePool.getFreeConnectionTimeout());
					config.freeConnectionTimeout().ifPresent(value -> assertThat(nativePool.getFreeConnectionTimeout()).as("freeConnectionTimeout applied").isEqualTo(value));
					assertThat(actual.serverConnectionTimeout().orElseThrow()).as("serverConnectionTimeout snapshot").isEqualTo(nativePool.getServerConnectionTimeout());
					config.serverConnectionTimeout().ifPresent(value -> assertThat(nativePool.getServerConnectionTimeout()).as("serverConnectionTimeout applied").isEqualTo(value));
					assertThat(actual.loadConditioningInterval().orElseThrow()).as("loadConditioningInterval snapshot").isEqualTo(nativePool.getLoadConditioningInterval());
					config.loadConditioningInterval().ifPresent(value -> assertThat(nativePool.getLoadConditioningInterval()).as("loadConditioningInterval applied").isEqualTo(value));
					assertThat(actual.socketBufferSize().orElseThrow()).as("socketBufferSize snapshot").isEqualTo(nativePool.getSocketBufferSize());
					config.socketBufferSize().ifPresent(value -> assertThat(nativePool.getSocketBufferSize()).as("socketBufferSize applied").isEqualTo(value));
					assertThat(actual.readTimeout().orElseThrow()).as("readTimeout snapshot").isEqualTo(nativePool.getReadTimeout());
					config.readTimeout().ifPresent(value -> assertThat(nativePool.getReadTimeout()).as("readTimeout applied").isEqualTo(value));
					assertThat(actual.pingTimeout().orElseThrow()).as("pingTimeout snapshot").isEqualTo(nativePool.getPingTimeout());
					config.pingTimeout().ifPresent(value -> assertThat(nativePool.getPingTimeout()).as("pingTimeout applied").isEqualTo(value));
					assertThat(actual.idleTimeout().orElseThrow()).as("idleTimeout snapshot").isEqualTo(nativePool.getIdleTimeout());
					config.idleTimeout().ifPresent(value -> assertThat(nativePool.getIdleTimeout()).as("idleTimeout applied").isEqualTo(value));
					assertThat(actual.pingInterval().orElseThrow()).as("pingInterval snapshot").isEqualTo(nativePool.getPingInterval());
					config.pingInterval().ifPresent(value -> assertThat(nativePool.getPingInterval()).as("pingInterval applied").isEqualTo(value));
					assertThat(actual.statisticInterval().orElseThrow()).as("statisticInterval snapshot").isEqualTo(nativePool.getStatisticInterval());
					config.statisticInterval().ifPresent(value -> assertThat(nativePool.getStatisticInterval()).as("statisticInterval applied").isEqualTo(value));
					assertThat(actual.retryAttempts().orElseThrow()).as("retryAttempts snapshot").isEqualTo(nativePool.getRetryAttempts());
					config.retryAttempts().ifPresent(value -> assertThat(nativePool.getRetryAttempts()).as("retryAttempts applied").isEqualTo(value));
					assertThat(actual.subscriptionEnabled().orElseThrow()).as("subscriptionEnabled snapshot").isEqualTo(nativePool.getSubscriptionEnabled());
					config.subscriptionEnabled().ifPresent(value -> assertThat(nativePool.getSubscriptionEnabled()).as("subscriptionEnabled applied").isEqualTo(value));
					assertThat(actual.prSingleHopEnabled().orElseThrow()).as("prSingleHopEnabled snapshot").isEqualTo(nativePool.getPRSingleHopEnabled());
					config.prSingleHopEnabled().ifPresent(value -> assertThat(nativePool.getPRSingleHopEnabled()).as("prSingleHopEnabled applied").isEqualTo(value));
					assertThat(actual.subscriptionRedundancy().orElseThrow()).as("subscriptionRedundancy snapshot").isEqualTo(nativePool.getSubscriptionRedundancy());
					config.subscriptionRedundancy().ifPresent(value -> assertThat(nativePool.getSubscriptionRedundancy()).as("subscriptionRedundancy applied").isEqualTo(value));
					assertThat(actual.subscriptionMessageTrackingTimeout().orElseThrow()).as("subscriptionMessageTrackingTimeout snapshot").isEqualTo(nativePool.getSubscriptionMessageTrackingTimeout());
					config.subscriptionMessageTrackingTimeout().ifPresent(value -> assertThat(nativePool.getSubscriptionMessageTrackingTimeout()).as("subscriptionMessageTrackingTimeout applied").isEqualTo(value));
					assertThat(actual.subscriptionAckInterval().orElseThrow()).as("subscriptionAckInterval snapshot").isEqualTo(nativePool.getSubscriptionAckInterval());
					config.subscriptionAckInterval().ifPresent(value -> assertThat(nativePool.getSubscriptionAckInterval()).as("subscriptionAckInterval applied").isEqualTo(value));
					assertThat(actual.subscriptionTimeoutMultiplier().orElseThrow()).as("subscriptionTimeoutMultiplier snapshot").isEqualTo(nativePool.getSubscriptionTimeoutMultiplier());
					config.subscriptionTimeoutMultiplier().ifPresent(value -> assertThat(nativePool.getSubscriptionTimeoutMultiplier()).as("subscriptionTimeoutMultiplier applied").isEqualTo(value));
					assertThat(actual.serverGroup().orElseThrow()).as("serverGroup snapshot").isEqualTo(nativePool.getServerGroup());
					config.serverGroup().ifPresent(value -> assertThat(nativePool.getServerGroup()).as("serverGroup applied").isEqualTo(value));
					assertThat(actual.availabilityZone().orElseThrow()).as("availabilityZone snapshot").isEqualTo(nativePool.getAvailabilityZone());
					config.availabilityZone().ifPresent(value -> assertThat(nativePool.getAvailabilityZone()).as("availabilityZone applied").isEqualTo(value));
					assertThat(actual.multiuserAuthentication().orElseThrow()).as("multiuserAuthentication snapshot").isEqualTo(nativePool.getMultiuserAuthentication());
					config.multiuserAuthentication().ifPresent(value -> assertThat(nativePool.getMultiuserAuthentication()).as("multiuserAuthentication applied").isEqualTo(value));
					config.socketFactory().ifPresent(callback -> assertThat(actual.socketFactory()).containsSame(callback));
					assertThat(actual.socketFactory()).isPresent();
					assertThat(actual.servers()).extracting(PoolConfiguration.Endpoint::port).containsExactly(40404);
					assertThat(actual.locators()).isEmpty();
				} finally { pool.destroy(); }
			}
			var locator = connection.createPool("locator", PoolConfiguration.builder()
				.locators(List.of(new PoolConfiguration.Endpoint("localhost", 10334))).minimumConnections(0).build());
			try { assertThat(locator.configuration().servers()).isEmpty(); assertThat(locator.configuration().locators()).hasSize(1); }
			finally { locator.destroy(); }
		}
	}
	@Test void unspecifiedSettingsRetainTheNativeFactoryDefaults() throws Exception {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var control = PoolManager.createFactory().addServer("localhost", 40404).setMinConnections(0).create("control");
			var actual = connection.createPool("default-settings", PoolConfiguration.builder()
				.servers(List.of(new PoolConfiguration.Endpoint("localhost", 40404))).minimumConnections(0).build());
			try {
				var settings = actual.configuration();
				assertThat(settings.minimumConnections().orElseThrow()).as("minimumConnections").isEqualTo(control.getMinConnections());
				assertThat(settings.maximumConnections().orElseThrow()).as("maximumConnections").isEqualTo(control.getMaxConnections());
				assertThat(settings.minimumConnectionsPerServer().orElseThrow()).as("minimumConnectionsPerServer").isEqualTo(control.getMinConnectionsPerServer());
				assertThat(settings.maximumConnectionsPerServer().orElseThrow()).as("maximumConnectionsPerServer").isEqualTo(control.getMaxConnectionsPerServer());
				assertThat(settings.socketConnectTimeout().orElseThrow()).as("socketConnectTimeout").isEqualTo(control.getSocketConnectTimeout());
				assertThat(settings.freeConnectionTimeout().orElseThrow()).as("freeConnectionTimeout").isEqualTo(control.getFreeConnectionTimeout());
				assertThat(settings.serverConnectionTimeout().orElseThrow()).as("serverConnectionTimeout").isEqualTo(control.getServerConnectionTimeout());
				assertThat(settings.loadConditioningInterval().orElseThrow()).as("loadConditioningInterval").isEqualTo(control.getLoadConditioningInterval());
				assertThat(settings.socketBufferSize().orElseThrow()).as("socketBufferSize").isEqualTo(control.getSocketBufferSize());
				assertThat(settings.readTimeout().orElseThrow()).as("readTimeout").isEqualTo(control.getReadTimeout());
				assertThat(settings.pingTimeout().orElseThrow()).as("pingTimeout").isEqualTo(control.getPingTimeout());
				assertThat(settings.idleTimeout().orElseThrow()).as("idleTimeout").isEqualTo(control.getIdleTimeout());
				assertThat(settings.pingInterval().orElseThrow()).as("pingInterval").isEqualTo(control.getPingInterval());
				assertThat(settings.statisticInterval().orElseThrow()).as("statisticInterval").isEqualTo(control.getStatisticInterval());
				assertThat(settings.retryAttempts().orElseThrow()).as("retryAttempts").isEqualTo(control.getRetryAttempts());
				assertThat(settings.subscriptionEnabled().orElseThrow()).as("subscriptionEnabled").isEqualTo(control.getSubscriptionEnabled());
				assertThat(settings.prSingleHopEnabled().orElseThrow()).as("prSingleHopEnabled").isEqualTo(control.getPRSingleHopEnabled());
				assertThat(settings.subscriptionRedundancy().orElseThrow()).as("subscriptionRedundancy").isEqualTo(control.getSubscriptionRedundancy());
				assertThat(settings.subscriptionMessageTrackingTimeout().orElseThrow()).as("subscriptionMessageTrackingTimeout").isEqualTo(control.getSubscriptionMessageTrackingTimeout());
				assertThat(settings.subscriptionAckInterval().orElseThrow()).as("subscriptionAckInterval").isEqualTo(control.getSubscriptionAckInterval());
				assertThat(settings.subscriptionTimeoutMultiplier().orElseThrow()).as("subscriptionTimeoutMultiplier").isEqualTo(control.getSubscriptionTimeoutMultiplier());
				assertThat(settings.serverGroup().orElseThrow()).as("serverGroup").isEqualTo(control.getServerGroup());
				assertThat(settings.availabilityZone().orElseThrow()).as("availabilityZone").isEqualTo(control.getAvailabilityZone());
				assertThat(settings.multiuserAuthentication().orElseThrow()).as("multiuserAuthentication").isEqualTo(control.getMultiuserAuthentication());
				try (var socket = settings.socketFactory().orElseThrow().createSocket()) { assertThat(socket.isConnected()).isFalse(); }
			} finally { actual.destroy(); control.destroy(); }
		}
	}
	@Test void socketAdapterPreservesCheckedFailureIdentityAndFreshSockets() throws Exception {
		IOException expected = new IOException("socket failure");
		var throwing = new GemFireSocketFactory(() -> { throw expected; });
		assertThatThrownBy(throwing::createSocket).isSameAs(expected);
		try (Socket socket = new DefaultDriverSocketFactory().createSocket()) { assertThat(socket.isConnected()).isFalse(); }
	}
	@Test void removedThreadLocalRequestNamesExactBoundVersion() {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			assertThatThrownBy(() -> connection.createPool("removed", PoolConfiguration.builder().threadLocalConnections(false).build()))
				.isInstanceOfSatisfying(UnsupportedFeatureException.class, e -> {
					assertThat(e.reason()).isEqualTo(UnsupportedFeatureException.Reason.REMOVED);
					assertThat(e.gemFireVersion()).contains(new GemFire104Driver().runtimeGemFireVersion());
				});
			assertThat(PoolManager.find("removed")).isNull();
		}
	}
}
