/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.cache.Cache;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

class GemfireCacheUnitTests {

	private DriverConnection connection;
	private DriverRegion<Object, Object> region;
	private GemfireCache cache;

	@BeforeEach
	void createCache() {
		connection = new GemFireDriverManager().driver()
				.connect(new CacheConfiguration(CacheConfiguration.Mode.CLIENT, Map.of()));
		region = connection.createClientRegion("entries", ClientRegionShortcut.LOCAL);
		cache = new GemfireCache(region);
	}

	@AfterEach
	void closeCache() {
		connection.close();
	}

	@Test
	void wrapsTheSpiHandleAndRejectsNull() {
		assertThat(cache.getNativeCache()).isSameAs(region);
		assertThat(cache.getName()).isEqualTo("entries");
		assertThatThrownBy(() -> new GemfireCache(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("GemFire Region must not be null");
	}

	@Test
	void getsWrappedAndTypedValues() {
		assertThat(cache.get("key")).isNull();
		assertThat(cache.get("key", String.class)).isNull();
		cache.put("key", 42);
		assertThat(Objects.requireNonNull(cache.get("key")).get()).isEqualTo(42);
		assertThat(cache.get("key", Integer.class)).isEqualTo(42);
		assertThat(cache.get("key", (Class<?>) null)).isEqualTo(42);
		assertThatThrownBy(() -> cache.get("key", String.class)).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void nullPutsDoNotReplaceExistingValues() {
		cache.put("absent", null);
		assertThat(region.containsKey("absent")).isFalse();
		cache.put("present", "value");
		cache.put("present", null);
		assertThat(cache.get("present", String.class)).isEqualTo("value");
	}

	@Test
	void putIfAbsentPreservesTheFirstValue() {
		assertThat(cache.putIfAbsent("key", "first")).isNull();
		assertThat(Objects.requireNonNull(cache.putIfAbsent("key", "second")).get()).isEqualTo("first");
		assertThat(region.get("key")).isEqualTo("first");
	}

	@Test
	void evictAndClearRemoveValuesAndAbsentEvictionIsHarmless() {
		cache.put("one", 1);
		cache.put("two", 2);
		cache.evict("absent");
		cache.evict("one");
		assertThat(cache.get("one")).isNull();
		assertThat(Objects.requireNonNull(cache.get("two")).get()).isEqualTo(2);
		cache.clear();
		assertThat(region.size()).isZero();
	}

	@Test
	void valueLoaderRunsOnlyOnACacheMiss() {
		AtomicInteger loads = new AtomicInteger();
		Callable<Integer> loader = loads::incrementAndGet;
		assertThat(cache.get("key", loader)).isEqualTo(1);
		assertThat(cache.get("key", loader)).isEqualTo(1);
		assertThat(loads.get()).isEqualTo(1);
	}

	@Test
	void nullLoaderResultsAreNotCachedAndFailuresRetainTheCause() {
		assertThat(cache.<Object> get("key", () -> null)).isNull();
		assertThat(region.containsKey("key")).isFalse();
		Exception failure = new Exception("loader failed");
		assertThatThrownBy(() -> cache.get("key", () -> {
			throw failure;
		})).isInstanceOf(Cache.ValueRetrievalException.class).hasCause(failure);
		assertThat(region.containsKey("key")).isFalse();
	}

	@Test
	void closedRegionFailuresRemainSpiExceptions() {
		connection.close();
		assertThatThrownBy(() -> cache.get("key")).isInstanceOf(DriverOperationException.class);
	}

	@Test
	@Timeout(10)
	void concurrentLoadsForTheSameCacheRunOneLoader() throws Exception {
		var executor = Executors.newFixedThreadPool(2);
		CountDownLatch entered = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		CountDownLatch secondStarted = new CountDownLatch(1);
		AtomicInteger loads = new AtomicInteger();
		try {
			var first = executor.submit(() -> cache.get("key", () -> {
				loads.incrementAndGet();
				entered.countDown();
				assertThat(release.await(5, TimeUnit.SECONDS)).isTrue();
				return "value";
			}));
			assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
			var second = executor.submit(() -> {
				secondStarted.countDown();
				return cache.get("key", () -> {
					loads.incrementAndGet();
					return "wrong";
				});
			});
			assertThat(secondStarted.await(5, TimeUnit.SECONDS)).isTrue();
			release.countDown();
			assertThat(first.get(5, TimeUnit.SECONDS)).isEqualTo("value");
			assertThat(second.get(5, TimeUnit.SECONDS)).isEqualTo("value");
			assertThat(loads.get()).isEqualTo(1);
		} finally {
			release.countDown();
			executor.shutdownNow();
			assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
		}
	}
}
