/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionEvent;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.junit.jupiter.api.Test;

class NativeRegionLifecycleTests {

	@Test
	void peerDestroyRegionCloseAndCacheCloseDeliverNativeLifecycle() {
		for (String operation : List.of("destroy", "regionClose", "cacheClose")) {
			try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", "")
				.set("log-level", "error").create()) {
				Region<Object, Object> region = cache.createRegionFactory(RegionShortcut.LOCAL).create("entries");
				RecordingListener listener = registerTwice(region);
				switch (operation) {
					case "destroy" -> region.localDestroyRegion();
					case "regionClose" -> region.close();
					default -> cache.close();
				}
				assertThat(listener.events).as(operation).containsExactly("destroy:entries", "close");
			}
		}
	}

	@Test
	void clientCacheCloseDeliversNativeLifecycle() {
		try (var cache = new ClientCacheFactory().set("mcast-port", "0").set("locators", "")
			.set("log-level", "error").create()) {
			Region<Object, Object> region = cache.createClientRegionFactory(ClientRegionShortcut.LOCAL).create("entries");
			RecordingListener listener = registerTwice(region);
			cache.close();
			assertThat(listener.events).containsExactly("destroy:entries", "close");
		}
	}

	@Test
	void adapterDeliversNativeDestroyAndCleanupAcrossHandles() {
		try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", "")
			.set("log-level", "error").create()) {
			Region<Object, Object> region = cache.createRegionFactory(RegionShortcut.LOCAL).create("entries");
			List<String> events = new ArrayList<>();
			var listener = new com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener() {
				@Override public void afterRegionDestroy(com.vmware.gemfire.driver.spi.DriverRegion<?, ?> destroyed) {
					events.add("destroy:" + destroyed.name());
					assertThat(destroyed.isDestroyed()).isTrue();
				}
				@Override public void close() { events.add("close"); }
			};
			new GemFireRegion<>(region).addLifecycleListener(listener);
			new GemFireRegion<>(region).addLifecycleListener(listener);
			assertThat(region.getAttributes().getCacheListeners()).hasSize(1);
			region.localDestroyRegion();
			assertThat(events).containsExactly("destroy:entries", "close");
			org.assertj.core.api.Assertions.assertThatThrownBy(() -> new GemFireRegion<>(region).addLifecycleListener(listener))
				.isInstanceOf(com.vmware.gemfire.driver.spi.DriverOperationException.class);
		}
	}

	@Test
	void nativeRuntimeCallbackFailuresDoNotPreventOtherNotificationsOrCleanup() {
		try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", "")
			.set("log-level", "error").create()) {
			Region<Object, Object> region = cache.createRegionFactory(RegionShortcut.LOCAL).create("entries");
			region.getAttributesMutator().addCacheListener(new CacheListenerAdapter<>() {
				@Override public void afterRegionDestroy(RegionEvent<Object, Object> event) {
					throw new IllegalStateException("expected destruction callback failure");
				}
				@Override public void close() { throw new IllegalStateException("expected cleanup failure"); }
			});
			RecordingListener listener = new RecordingListener();
			region.getAttributesMutator().addCacheListener(listener);
			cache.close();
			assertThat(listener.events).containsExactly("destroy:entries", "close");
		}
	}

	private RecordingListener registerTwice(Region<Object, Object> region) {
		RecordingListener listener = new RecordingListener();
		region.getAttributesMutator().addCacheListener(listener);
		region.getAttributesMutator().addCacheListener(listener);
		assertThat(region.getAttributes().getCacheListeners()).containsExactly(listener);
		return listener;
	}

	private static class RecordingListener extends CacheListenerAdapter<Object, Object> {
		private final List<String> events = new ArrayList<>();
		@Override public void afterRegionDestroy(RegionEvent<Object, Object> event) {
			events.add("destroy:" + event.getRegion().getName());
		}
		@Override public void close() { events.add("close"); }
	}
}
