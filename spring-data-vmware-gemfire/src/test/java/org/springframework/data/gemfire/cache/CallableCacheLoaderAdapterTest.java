/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

class CallableCacheLoaderAdapterTest {

	private DriverConnection connection;
	private DriverRegion<String, String> region;

	@BeforeEach
	void createRegion() {
		connection = new GemFireDriverManager().driver()
			.connect(new CacheConfiguration(Map.of()));
		region = connection.createClientRegion("entries", ClientRegionShortcut.LOCAL);
	}

	@AfterEach
	void closeConnection() { connection.close(); }

	@Test
	void preservesConstructorArguments() {
		RecordingLoader loader = new RecordingLoader(helper -> "value");
		var adapter = new CallableCacheLoaderAdapter<>(loader, "key", region, "argument");
		assertThat(adapter.getCacheLoader()).isSameAs(loader);
		assertThat(adapter.getKey()).isEqualTo("key");
		assertThat(adapter.getRegion()).isSameAs(region);
		assertThat(adapter.getArgument()).isEqualTo("argument");
		assertThat(new CallableCacheLoaderAdapter<>(loader, "key", region).getArgument()).isNull();
		var unbound = new CallableCacheLoaderAdapter<>(loader);
		assertThat(unbound.getKey()).isNull();
		assertThat(unbound.getRegion()).isNull();
		assertThat(unbound.getArgument()).isNull();
	}

	@Test
	void rejectsNullLoader() {
		assertThatThrownBy(() -> new CallableCacheLoaderAdapter<>(null))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("CacheLoader must not be null");
	}

	@Test
	void rejectsUnboundKeyAndRegionBeforeLoading() {
		RecordingLoader loader = new RecordingLoader(helper -> "value");
		assertThatThrownBy(() -> new CallableCacheLoaderAdapter<>(loader, null, region).call())
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("The key for which the value is loaded for cannot be null");
		assertThatThrownBy(() -> new CallableCacheLoaderAdapter<>(loader, "key", null).call())
			.isInstanceOf(IllegalStateException.class).hasMessage("The Region to load cannot be null");
		assertThat(loader.loads.get()).isZero();
	}

	@Test
	void callSuppliesContextWithoutInsertingTheResult() throws Exception {
		Object argument = new Object();
		RecordingLoader loader = new RecordingLoader(helper -> {
			assertThat(helper.getKey()).isEqualTo("key");
			assertThat(helper.getRegion()).isSameAs(region);
			assertThat(helper.getArgument()).isSameAs(argument);
			return "value";
		});
		assertThat(new CallableCacheLoaderAdapter<>(loader, "key", region, argument).call()).isEqualTo("value");
		assertThat(loader.loads.get()).isEqualTo(1);
		assertThat(region.containsKey("key")).isFalse();
	}

	@Test
	void loadForwardsTheSuppliedHelperUnchanged() throws Exception {
		RecordingLoader loader = new RecordingLoader(helper -> "value");
		var adapter = new CallableCacheLoaderAdapter<>(loader, "key", region);
		adapter.call();
		DriverLoaderHelper<String, String> supplied = loader.lastHelper;
		assertThat(adapter.load(supplied)).isEqualTo("value");
		assertThat(loader.lastHelper).isSameAs(supplied);
		assertThat(loader.loads.get()).isEqualTo(2);
	}

	@Test
	void preservesNullLoadResult() throws Exception {
		assertThat(new CallableCacheLoaderAdapter<>(new RecordingLoader(helper -> null), "key", region).call()).isNull();
	}

	@Test
	void propagatesCallbackFailureUnchanged() {
		IllegalArgumentException failure = new IllegalArgumentException("load failed");
		var adapter = new CallableCacheLoaderAdapter<>(new RecordingLoader(helper -> { throw failure; }), "key", region);
		assertThatThrownBy(adapter::call).isSameAs(failure);
		assertThatThrownBy(() -> adapter.load(null)).isSameAs(failure);
	}

	@Test
	void closeForwardsEachInvocation() {
		RecordingLoader loader = new RecordingLoader(helper -> "value");
		var adapter = new CallableCacheLoaderAdapter<>(loader);
		adapter.close();
		adapter.close();
		assertThat(loader.closes.get()).isEqualTo(2);
	}

	@Test
	void closePropagatesCallbackFailure() {
		IllegalStateException failure = new IllegalStateException("close failed");
		DriverCacheLoader<String, String> loader = new DriverCacheLoader<>() {
			@Override public String load(DriverLoaderHelper<String, String> helper) { return null; }
			@Override public void close() { throw failure; }
		};
		assertThatThrownBy(new CallableCacheLoaderAdapter<>(loader)::close).isSameAs(failure);
	}

	@Test
	void installedAdapterUsesDriverContextAndLifecycle() {
		RecordingLoader loader = new RecordingLoader(helper -> {
			assertThat(helper.getRegion().name()).isEqualTo("entries");
			return helper.getKey() + ":" + helper.getArgument();
		});
		region.setCacheLoader(new CallableCacheLoaderAdapter<>(loader));
		assertThat(region.get("key", "argument")).isEqualTo("key:argument");
		assertThat(region.get("key", "other")).isEqualTo("key:argument");
		assertThat(loader.loads.get()).isEqualTo(1);
		region.setCacheLoader(null);
		assertThat(loader.closes.get()).isEqualTo(1);
	}

	private static final class RecordingLoader implements DriverCacheLoader<String, String> {
		private final Function<DriverLoaderHelper<String, String>, String> action;
		private final AtomicInteger loads = new AtomicInteger();
		private final AtomicInteger closes = new AtomicInteger();
		private DriverLoaderHelper<String, String> lastHelper;
		RecordingLoader(Function<DriverLoaderHelper<String, String>, String> action) { this.action = action; }
		@Override public String load(DriverLoaderHelper<String, String> helper) {
			loads.incrementAndGet();
			lastHelper = helper;
			return action.apply(helper);
		}
		@Override public void close() { closes.incrementAndGet(); }
	}
}
