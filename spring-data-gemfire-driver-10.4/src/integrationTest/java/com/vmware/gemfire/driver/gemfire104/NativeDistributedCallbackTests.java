/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.apache.geode.cache.*;
import org.apache.geode.cache.client.*;
import com.vmware.gemfire.driver.spi.*;

@Timeout(120)
class NativeDistributedCallbackTests {
	@TempDir Path directory;

	@Test
	void subscribedClientReceivesRemoteEntryValuesAndMemberMetadata() throws Exception {
		try (var server = new NativeServerFixture(directory); var cache = new ClientCacheFactory().set("log-level", "error")
			.addPoolServer("localhost", server.port).setPoolSubscriptionEnabled(true).create()) {
			var values = new LinkedBlockingQueue<String>(); var errors = new LinkedBlockingQueue<Throwable>();
			var listener = new DriverCacheListenerAdapter<String, String>() {
				@Override public void afterCreate(DriverEntryEvent<String, String> event) { capture(event); }
				@Override public void afterUpdate(DriverEntryEvent<String, String> event) { capture(event); }
				private void capture(DriverEntryEvent<String, String> event) {
					try {
						assertThat(event.isOriginRemote()).isTrue();
						assertThat(event.getDistributedMember().getId()).isNotEmpty();
						var serialized = event.getSerializedNewValue();
						if (serialized != null) {
							assertThat(serialized.getSerializedValue()).isNotEmpty();
							assertThat(serialized.getDeserializedValue()).isEqualTo(event.getNewValue());
						}
						values.add(event.getKey() + ":" + event.getOldValue() + ":" + event.getNewValue());
					} catch (Throwable failure) { errors.add(failure); }
				}
			};
			new GemFireConnection(cache).createClientRegion("distributed", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.CACHING_PROXY,
				new RegionCallbacks<>(List.of(listener), null, null));
			cache.getRegion("distributed").registerInterestForAllKeys();
			server.command("PUT");
			String first = values.poll(10, TimeUnit.SECONDS), second = values.poll(10, TimeUnit.SECONDS);
			assertThat(errors).isEmpty();
			assertThat(first).isEqualTo("key:null:one"); assertThat(second).isEqualTo("key:one:two");
		}
	}

	@Test
	void subscribedClientReceivesRealRegionLiveMarker() throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var live = new CompletableFuture<DriverOperation>();
			try (var cache = new ClientCacheFactory().set("log-level", "error").set("durable-client-id", "callback-" + UUID.randomUUID())
				.addPoolServer("localhost", server.port).setPoolSubscriptionEnabled(true).create()) {
				var connection = new GemFireConnection(cache);
				var listener = new DriverCacheListenerAdapter<String, String>() {
					@Override public void afterRegionLive(DriverRegionEvent<String, String> event) { live.complete(event.getOperation()); }
				};
				connection.createClientRegion("distributed", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.CACHING_PROXY,
					new RegionCallbacks<>(List.of(listener), null, null));
				cache.getRegion("distributed").registerInterestForAllKeys(InterestResultPolicy.KEYS_VALUES, true);
				cache.readyForEvents();
				assertThat(live.get(30, TimeUnit.SECONDS)).isEqualTo(DriverOperation.MARKER);
			}
		}
	}

	@Test
	void proxyRegionOperationsUseServerStateAndExposePoolRoutingMetadata() throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			try (var cache = new ClientCacheFactory().set("log-level", "error")
				.addPoolServer("localhost", server.port).create()) {
				var connection = new GemFireConnection(cache);
				DriverRegion<String, String> region = connection.createClientRegion("distributed",
					com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.PROXY);
				assertThat(region.isEmptyOnServer()).isTrue();
				region.putAll(Map.of("one", "1", "two", "2"));
				assertThat(region.size()).isZero();
				assertThat(region.sizeOnServer()).isEqualTo(2); assertThat(region.sizeLongOnServer()).isEqualTo(2L);
				assertThat(region.containsKeyOnServer("one")).isTrue(); assertThat(region.containsKeyOnServer("absent")).isFalse();
				assertThat(region.keySetOnServer()).containsExactlyInAnyOrder("one", "two");
				assertThat(region.getAll(List.of("one", "two"))).containsExactlyInAnyOrderEntriesOf(Map.of("one", "1", "two", "2"));
				assertThat(region.replace("one", "1", "new")).isTrue();
				assertThat(region.remove("one", "bad")).isFalse(); assertThat(region.remove("one", "new")).isTrue();
				region.removeAll(List.of("two")); assertThat(region.isEmptyOnServer()).isTrue();
				assertThat(region.metadata().dataPolicy()).isEqualTo(com.vmware.gemfire.driver.spi.config.DataPolicy.EMPTY);
				assertThat(region.metadata().poolName()).isEqualTo(cache.getDefaultPool().getName());
				assertThat(region.getCache().getPool(region.metadata().poolName())).isNotNull();
				assertThat(region.getRegionService().getRegion("distributed")).isEqualTo(region);
			}
		}
	}

	@Test void configuredClientRegionCanRouteToADifferentlyNamedServerRegion() throws Exception {
		try (var server = new NativeServerFixture(directory); var cache = new ClientCacheFactory().set("log-level", "error")
			.addPoolServer("localhost", server.port).create()) {
			var connection = new GemFireConnection(cache);
			var region = connection.createClientRegion("alias", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.PROXY,
				RegionConfiguration.<String, String>builder().serverRegionName("distributed").poolName(cache.getDefaultPool().getName()).build());
			region.put("routed", "value");
			assertThat(region.get("routed")).isEqualTo("value"); assertThat(region.containsKeyOnServer("routed")).isTrue();
			assertThat(region.name()).isEqualTo("alias"); assertThat(region.getAttributes().getPoolName()).isEqualTo(cache.getDefaultPool().getName());
		}
	}

	@Test void configuredSocketFactoryCreatesSocketsUsedForRealServerOperations() throws Exception {
		try (var server = new NativeServerFixture(directory); var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var created = new java.util.concurrent.atomic.AtomicInteger();
			DriverSocketFactory sockets = () -> { created.incrementAndGet(); return new java.net.Socket(); };
			var connection = new GemFireConnection(cache);
			var pool = connection.createPool("custom-sockets", PoolConfiguration.builder()
				.servers(List.of(new PoolConfiguration.Endpoint("localhost", server.port)))
				.minimumConnections(0).subscriptionEnabled(true).socketFactory(sockets).build());
			var region = connection.createClientRegion("socket-alias", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.PROXY,
				RegionConfiguration.<String, String>builder().poolName(pool.name()).serverRegionName("distributed").build());
			region.put("socket", "value"); assertThat(region.get("socket")).isEqualTo("value");
			assertThat(created.get()).isPositive(); assertThat(pool.configuration().socketFactory()).containsSame(sockets);
			assertThat(PoolManager.find(pool.name()).getSubscriptionEnabled()).isTrue();
		}
	}

	@Test
	void clientPdxFactoriesRegisterSchemasAndEnumsWithARealServer() throws Exception {
		try (var server = new NativeServerFixture(directory); var cache = new ClientCacheFactory().set("log-level", "error")
			.addPoolServer("localhost", server.port).setPdxReadSerialized(true).create()) {
			var connection = new GemFireConnection(cache);
			var region = cache.<String, Object>createClientRegionFactory(ClientRegionShortcut.PROXY).create("distributed");
			var value = connection.createPdxInstanceFactory("ClientValue").neverDeserialize()
				.writeInt("id", 42).markIdentityField("id").writeString("name", "client").create();
			var enumValue = connection.createPdxEnum("ClientEnum", "READY", 0);
			assertThat(enumValue.getField("name")).isEqualTo("READY");
			// Native region is a wire fixture; SPI region value transport is an explicit blocking child.
			region.put("pdx", new GemFirePdxValues().toNative(value));
			region.put("enum", new GemFirePdxValues().toNative(enumValue));
			assertThat(new GemFirePdxValues().fromNative(region.get("pdx"))).isEqualTo(value);
			assertThat(new GemFirePdxValues().fromNative(region.get("enum"))).isEqualTo(enumValue);
		}
	}

	@Test
	void clientTransactionsCommitAndRollbackAgainstARealServer() throws Exception {
		try (var server = new NativeServerFixture(directory); var cache = new ClientCacheFactory().set("log-level", "error")
			.addPoolServer("localhost", server.port).create()) {
			var connection = new GemFireConnection(cache);
			DriverRegion<String, String> region = connection.createClientRegion("distributed", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.PROXY);
			var manager = connection.getTransactionManager();
			manager.begin(); region.put("first", "committed"); region.put("second", "committed"); var id = manager.suspend();
			assertThat(region.get("first")).isNull(); manager.resume(id); manager.commit();
			assertThat(region.getAll(List.of("first", "second"))).containsExactlyInAnyOrderEntriesOf(Map.of("first", "committed", "second", "committed"));
			manager.begin(); region.put("first", "rolled back"); region.destroy("second"); manager.rollback();
			assertThat(region.get("first")).isEqualTo("committed"); assertThat(region.get("second")).isEqualTo("committed");
			assertThat(manager.exists()).isFalse();
		}
	}

}
