/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;
import org.apache.geode.cache.client.ServerConnectivityException;
import org.apache.geode.distributed.DistributedSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;
import com.vmware.gemfire.driver.spi.RegionConfiguration;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

@Timeout(120)
class NativeEndpointObservationTests {
	@TempDir Path directory;

	@Test
	void snapshotsPreserveUnresolvedNamesAndCannotBeChangedByLaterNativeUpdates() {
		var address = InetSocketAddress.createUnresolved("no-dns-needed.invalid", 40404);
		var servers = new HashSet<>(Set.of(address));
		var locators = new ArrayList<>(List.of(address));
		var nativeCache = mock(ClientCache.class);
		when(nativeCache.getDistributedSystem()).thenReturn(mock(DistributedSystem.class));
		var nativePool = mock(Pool.class);
		when(nativeCache.getCurrentServers()).thenReturn(servers);
		when(nativePool.getOnlineLocators()).thenReturn(locators);
		var connection = new GemFireConnection(nativeCache);
		var pool = new GemFirePool(nativePool);
		var serverSnapshot = connection.getCurrentServers();
		var locatorSnapshot = pool.onlineLocators();
		var endpoint = new Endpoint("no-dns-needed.invalid", 40404);
		assertThat(serverSnapshot).containsExactly(endpoint);
		assertThat(locatorSnapshot).containsExactly(endpoint);
		assertThatThrownBy(serverSnapshot::clear).isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(locatorSnapshot::clear).isInstanceOf(UnsupportedOperationException.class);
		servers.clear(); locators.clear();
		assertThat(connection.getCurrentServers()).isEmpty();
		assertThat(pool.onlineLocators()).isEmpty();
		assertThat(serverSnapshot).containsExactly(endpoint);
		assertThat(locatorSnapshot).containsExactly(endpoint);
	}

	@Test
	void failuresAreClassifiedWithSafeMessagesAndFatalErrorsPropagate() {
		var nativeCache = mock(ClientCache.class);
		when(nativeCache.getDistributedSystem()).thenReturn(mock(DistributedSystem.class));
		var nativePool = mock(Pool.class);
		var failure = new ServerConnectivityException("secret-credential");
		when(nativeCache.getCurrentServers()).thenThrow(failure);
		when(nativePool.getOnlineLocators()).thenThrow(failure);
		var connection = new GemFireConnection(nativeCache);
		var pool = new GemFirePool(nativePool);
		for (Runnable operation : List.<Runnable>of(connection::getCurrentServers, pool::onlineLocators)) {
			assertThatThrownBy(operation::run).isInstanceOf(DriverOperationException.class)
				.hasCause(failure).hasMessageNotContaining("secret-credential")
				.satisfies(error -> assertThat(((DriverOperationException) error).kind()).isEqualTo(DriverFailureKind.RESOURCE_FAILURE));
		}
		var fatal = new LinkageError("fatal");
		doThrow(fatal).when(nativeCache).getCurrentServers();
		doThrow(fatal).when(nativePool).getOnlineLocators();
		assertThatThrownBy(connection::getCurrentServers).isSameAs(fatal);
		assertThatThrownBy(pool::onlineLocators).isSameAs(fatal);
	}

	@Test
	void realServerConnectionDisappearsAfterCrashWhileLocalRuntimeStaysConnected() throws Exception {
		try (var server = new NativeServerFixture(directory);
			var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var pool = connection.createPool("observed", PoolConfiguration.builder()
				.servers(List.of(new Endpoint("localhost", server.port))).minimumConnections(0)
				.pingInterval(1000).readTimeout(1000).retryAttempts(0).build());
			assertThat(connection.getCurrentServers()).isEmpty();
			assertThat(pool.onlineLocators()).isEmpty();
			var region = connection.createClientRegion("observed", ClientRegionShortcut.PROXY,
				RegionConfiguration.<String, String>builder().poolName(pool.name()).serverRegionName("distributed").build());
			region.put("connected", "yes");
			await(() -> connection.getCurrentServers().stream().anyMatch(endpoint -> endpoint.port() == server.port));
			var snapshot = connection.getCurrentServers();
			assertThat(new GemFireConnection(cache).getCurrentServers()).isEqualTo(snapshot);
			server.crash();
			assertThatThrownBy(() -> region.get("connected")).isInstanceOf(DriverOperationException.class);
			await(() -> connection.getCurrentServers().isEmpty());
			assertThat(snapshot).extracting(Endpoint::port).contains(server.port);
			assertThat(pool.servers()).extracting(Endpoint::port).containsExactly(server.port);
			assertThat(connection.getRuntime().isConnected()).isTrue();
			cache.close();
			assertThat(connection.getCurrentServers()).isEmpty();
		}
	}

	@Test
	void locatorDiscoveryDoesNotEstablishServerConnectivityAndSurvivesAsLastObservation() throws Exception {
		try (var locator = new NativeServerFixture(directory, System.getProperty("nativeFixtureClasspath"), NativeLocatorProcess.class.getName());
			var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var pool = connection.createPool("discovery", PoolConfiguration.builder()
				.locators(List.of(new Endpoint("localhost", locator.port))).minimumConnections(0).pingInterval(1000).build());
			await(() -> pool.onlineLocators().stream().anyMatch(endpoint -> endpoint.port() == locator.port));
			var snapshot = pool.onlineLocators();
			var nativePool = PoolManager.find(pool.name());
			assertThat(snapshot).isEqualTo(nativePool.getOnlineLocators().stream()
				.map(address -> new Endpoint(address.getHostString(), address.getPort())).toList());
			assertThat(connection.getCurrentServers()).isEmpty();
			assertThat(connection.getRuntime().isConnected()).isTrue();
			locator.crash();
			pool.destroy();
			assertThat(pool.isDestroyed()).isTrue();
			assertThat(pool.onlineLocators()).isEqualTo(snapshot);
			assertThat(snapshot).extracting(Endpoint::port).contains(locator.port);
		}
	}

	private void await(BooleanSupplier condition) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
		while (!condition.getAsBoolean()) {
			if (System.nanoTime() >= deadline) throw new AssertionError("Endpoint observation did not converge within 30 seconds");
			TimeUnit.MILLISECONDS.sleep(20);
		}
	}
}
