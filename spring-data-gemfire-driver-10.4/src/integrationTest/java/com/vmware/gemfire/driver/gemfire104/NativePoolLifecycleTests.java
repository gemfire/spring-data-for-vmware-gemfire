// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.apache.geode.cache.InterestResultPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/** Pool registry, readiness and keep-alive lifecycle against the native engine; durable cases use a separate server JVM. */
@Timeout(180)
class NativePoolLifecycleTests {
	@TempDir Path directory;

	private static PoolConfiguration unconnected() {
		return PoolConfiguration.builder().servers(List.of(new Endpoint("localhost", 40404))).minimumConnections(0).build();
	}

	private static ClientCache newCache() { return new ClientCacheFactory().set("log-level", "error").create(); }

	private static ClientCache durableCache(int port, String durableId) {
		return new ClientCacheFactory().set("log-level", "error").set("durable-client-id", durableId)
			.set("durable-client-timeout", "300").setPoolSubscriptionEnabled(true).setPoolMinConnections(0)
			.addPoolServer("localhost", port).create();
	}

	private static Region<String, String> durableRegion(ClientCache cache) {
		Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY).create("distributed");
		region.registerInterestForAllKeys(InterestResultPolicy.NONE, true);
		return region;
	}

	private static boolean await(BooleanSupplier condition) throws InterruptedException {
		for (int i = 0; i < 100; i++) { if (condition.getAsBoolean()) return true; Thread.sleep(100); }
		return condition.getAsBoolean();
	}

	@Test
	void registryOperationsSeeCreatedPoolsAndMapRegionsToTheirPool() {
		try (ClientCache cache = newCache()) {
			var connection = new GemFireConnection(cache);
			connection.createPool("p52", unconnected());
			cache.createClientRegionFactory(ClientRegionShortcut.PROXY).setPoolName("p52").create("bound");
			cache.createClientRegionFactory(ClientRegionShortcut.LOCAL).create("local");
			assertThat(connection.getPools()).containsKey("p52");
			var snapshot = connection.getPools();
			snapshot.clear();
			assertThat(connection.getPools()).containsKey("p52");
			assertThat(connection.getPoolForRegion("bound").name()).isEqualTo("p52");
			assertThat(connection.getPoolForRegion("local")).isNull();
			assertThatThrownBy(() -> connection.getPoolForRegion("absent")).isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		}
	}

	@Test
	void readyForEventsRequiresADurablePoolWhenPoolsExistAndPendingCountRejectsNondurablePools() {
		try (ClientCache cache = newCache()) {
			var connection = new GemFireConnection(cache);
			connection.readyForEvents();
			var pool = connection.createPool("plain", unconnected());
			assertThatThrownBy(connection::readyForEvents).isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE));
			assertThatThrownBy(pool::getPendingEventCount).isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE));
		}
	}

	@Test
	void closePoolsAndDestroyRemovePoolsFromTheRegistryAndAttachedPoolsCannotBeDestroyed() {
		try (ClientCache cache = newCache()) {
			var connection = new GemFireConnection(cache);
			var attached = connection.createPool("attached", unconnected());
			cache.createClientRegionFactory(ClientRegionShortcut.PROXY).setPoolName("attached").create("user");
			assertThatThrownBy(attached::destroy).isInstanceOf(DriverOperationException.class);
			assertThat(attached.isDestroyed()).isFalse();
			var other = connection.createPool("other", unconnected());
			other.destroy(false);
			assertThat(other.isDestroyed()).isTrue();
			assertThat(connection.getPools()).doesNotContainKey("other");
			connection.closePools(false);
			assertThat(attached.isDestroyed()).isTrue();
			assertThat(connection.getPools()).isEmpty();
		}
	}

	@Test
	void keepAliveCloseRetainsTheDurableQueueAndReconnectReplaysQueuedEvents() throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var first = durableCache(server.port, "client52keep");
			try {
				var connection = new GemFireConnection(first);
				durableRegion(first);
				// Native requires the pending count to be read before readyForEvents.
				assertThat(connection.getDefaultPool().getPendingEventCount()).isIn(0, -2);
				connection.readyForEvents();
				connection.close(true);
				assertThat(connection.isClosed()).isTrue();
			}
			finally {
				if (!first.isClosed()) first.close();
			}
			server.command("PUT");
			try (var second = durableCache(server.port, "client52keep")) {
				Region<String, String> region = durableRegion(second);
				var reconnected = new GemFireConnection(second);
				reconnected.readyForEvents();
				assertThat(await(() -> region.getEntry("key") != null && "two".equals(region.getEntry("key").getValue()))).isTrue();
			}
		}
	}

	@Test
	void nonKeepAliveCloseDiscardsTheDurableQueue() throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var first = durableCache(server.port, "client52drop");
			try {
				var connection = new GemFireConnection(first);
				durableRegion(first);
				connection.readyForEvents();
				connection.close(false);
			}
			finally {
				if (!first.isClosed()) first.close();
			}
			server.command("PUT");
			try (var second = durableCache(server.port, "client52drop")) {
				Region<String, String> region = durableRegion(second);
				new GemFireConnection(second).readyForEvents();
				Thread.sleep(1000);
				assertThat(region.containsKey("key")).isFalse();
			}
		}
	}
}
