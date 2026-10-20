/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

class MockIsolationTests {

	@Test
	void connectionsHaveIndependentStateEvenWithinOneDriver() {
		MockGemFireDriver driver = new MockGemFireDriver();
		CacheConfiguration config = new CacheConfiguration(CacheConfiguration.Mode.PEER, Map.of());
		try (DriverConnection first = driver.connect(config); DriverConnection second = driver.connect(config)) {
			first.createRegion("entries", RegionShortcut.LOCAL).put("key", "first");
			assertThat(second.getRegion("entries")).isNull();
			second.createRegion("entries", RegionShortcut.LOCAL).put("key", "second");
			first.close();
			assertThat(second.getRegion("entries").get("key")).isEqualTo("second");
		}
	}

	@Test
	void compatibleClientConnectionsRemainIndependentInTheLocalMock() {
		var driver = new MockGemFireDriver();
		var configuration = new CacheConfiguration(CacheConfiguration.Mode.CLIENT, Map.of());
		try (DriverConnection first = driver.connect(configuration); DriverConnection second = driver.connect(configuration)) {
			first.createClientRegion("entries", ClientRegionShortcut.LOCAL).put("key", "first");
			assertThat(second.getRegion("entries")).isNull();
			second.createClientRegion("entries", ClientRegionShortcut.LOCAL).put("key", "second");
			first.close();
			assertThat(second.isClosed()).isFalse();
			assertThat(second.getRegion("entries").get("key")).isEqualTo("second");
		}
	}

	@Test
	void distributedAndPersistentShortcutsFailExplicitly() {
		try (DriverConnection connection = new MockGemFireDriver().connect(
			new CacheConfiguration(CacheConfiguration.Mode.PEER, Map.of()))) {
			for (RegionShortcut shortcut : RegionShortcut.values()) {
				if (shortcut == RegionShortcut.LOCAL) continue;
				assertThatThrownBy(() -> connection.createRegion("entries", shortcut))
					.isInstanceOfSatisfying(UnsupportedFeatureException.class, ex -> {
						assertThat(ex.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED);
						assertThat(ex.feature()).isEqualTo("RegionShortcut." + shortcut);
					});
			}
			assertThat(connection.getRegion("entries")).isNull();
		}
	}
	@Test
	void mockNetworkSearchFailsExplicitlyWithoutPretendingToDistribute() {
		try (DriverConnection connection = new MockGemFireDriver().connect(
			new CacheConfiguration(CacheConfiguration.Mode.PEER, Map.of()))) {
			DriverRegion<String, String> region = connection.createRegion("entries", RegionShortcut.LOCAL);
			region.setCacheLoader(new DriverCacheLoader<>() {
				@Override public String load(DriverLoaderHelper<String, String> helper) { return helper.netSearch(true); }
				@Override public void close() { }
			});
			assertThatThrownBy(() -> region.get("key")).isInstanceOf(DriverOperationException.class)
				.cause().isInstanceOfSatisfying(UnsupportedFeatureException.class, ex -> {
					assertThat(ex.feature()).isEqualTo("LoaderHelper.netSearch");
					assertThat(ex.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED);
				});
		}
	}

	@Test
	void shutdownReleasesEveryLoaderEvenWhenCallbacksFail() {
		DriverConnection connection = new MockGemFireDriver().connect(
			new CacheConfiguration(CacheConfiguration.Mode.PEER, Map.of()));
		AtomicInteger closes = new AtomicInteger();
		for (String name : new String[] { "first", "second" }) {
			connection.createRegion(name, RegionShortcut.LOCAL).setCacheLoader(new DriverCacheLoader<>() {
				@Override public Object load(DriverLoaderHelper<Object, Object> helper) { return null; }
				@Override public void close() {
					closes.incrementAndGet();
					throw new IllegalStateException("callback-secret");
				}
			});
		}
		assertThatThrownBy(connection::close).isInstanceOf(DriverOperationException.class)
			.hasMessageNotContaining("callback-secret");
		connection.close();
		assertThat(connection.isClosed()).isTrue();
		assertThat(closes.get()).isEqualTo(2);
	}

}
