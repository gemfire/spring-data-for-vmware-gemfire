/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.CacheConfiguration.Mode;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.SpiVersion;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;
import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;

/**
 * Extend in a driver's own test source set. Exactly one provider must be on its runtime classpath.
 * Native runs use isolated local regions; no locator/server or fixed port is required. Each driver
 * declares its unsupported methods explicitly; adding a default SPI method without a probe fails.
 */
public abstract class GemFireDriverContract {

	protected abstract String expectedNativeVersion();

	protected abstract Map<Method, Reason> expectedUnsupportedMethods();

	protected GemFireDriver driver() { return new GemFireDriverManager().driver(); }

	protected CacheConfiguration configuration(Mode mode) {
		return new CacheConfiguration(mode, Map.of("mcast-port", "0", "locators", "",
			"log-level", "error", "disable-auto-reconnect", "true", "statistic-sampling-enabled", "false"));
	}

	@Test
	void discoversProviderAndChecksGeneratedStamp() throws Exception {
		GemFireDriver driver = driver();
		assertThat(driver.metadata().driverName()).isNotBlank();
		assertThat(driver.runtimeGemFireVersion()).isEqualTo(expectedNativeVersion());
		String path = "META-INF/gemfire-driver/" + driver.getClass().getName() + ".properties";
		try (var input = driver.getClass().getClassLoader().getResourceAsStream(path)) {
			assertThat(input).as("Generated driver stamp").isNotNull();
			Properties stamp = new Properties();
			stamp.load(input);
			assertThat(stamp.getProperty("spi.version")).isEqualTo(SpiVersion.runtime().toString());
			assertThat(stamp.getProperty("gemfire.version")).isEqualTo(expectedNativeVersion());
		}
	}

	@Test
	void everyDefaultMethodMatchesTheDriversDeclaration() throws Exception {
		new DefaultMethodContract().verify(probes(), expectedUnsupportedMethods(), expectedNativeVersion());
	}

	protected Map<Method, Probe> probes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverConnection.class.getMethod("rootRegions"),
			new Probe("Connection.rootRegions", () -> inCache(Mode.PEER, c -> {
				assertThat(c.rootRegions()).isEmpty();
				c.createRegion("created", RegionShortcut.LOCAL);
				assertThat(c.rootRegions()).extracting(DriverRegion::name).containsExactly("created");
			})));
		probes.put(DriverRegion.class.getMethod("isDestroyed"), new Probe("Region.isDestroyed", () ->
			inCache(Mode.PEER, c -> {
				DriverRegion<Object, Object> region = c.createRegion("created", RegionShortcut.LOCAL);
				assertThat(region.isDestroyed()).isFalse();
				c.close();
				assertThat(region.isDestroyed()).isTrue();
			})));
		probes.put(DriverRegion.class.getMethod("clear"), new Probe("Region.clear", () ->
			inRegion(r -> {
				r.put("key", "value");
				r.clear();
				assertThat(r.size()).isZero();
				assertThat(r.get("key")).isNull();
			})));
		probes.put(DriverRegion.class.getMethod("putIfAbsent", Object.class, Object.class),
			new Probe("Region.putIfAbsent", () -> inRegion(r -> {
				assertThat(r.putIfAbsent("key", "first")).isNull();
				assertThat(r.putIfAbsent("key", "second")).isEqualTo("first");
				assertThat(r.get("key")).isEqualTo("first");
			})));
		probes.put(DriverConnection.class.getMethod("createRegion", String.class, RegionShortcut.class),
			new Probe("Connection.createRegion", () -> inCache(Mode.PEER, c ->
				assertThat(c.createRegion("created", RegionShortcut.LOCAL).name()).isEqualTo("created"))));
		probes.put(DriverConnection.class.getMethod("createClientRegion", String.class, ClientRegionShortcut.class),
			new Probe("Connection.createClientRegion", () -> inCache(Mode.CLIENT, c ->
				assertThat(c.createClientRegion("created", ClientRegionShortcut.LOCAL).name()).isEqualTo("created"))));
		probes.put(DriverConnection.class.getMethod("getRegion", String.class),
			new Probe("Connection.getRegion", () -> inCache(Mode.PEER, c -> {
				assertThat(c.getRegion("absent")).isNull();
				c.createRegion("created", RegionShortcut.LOCAL);
				assertThat(c.getRegion("/created").name()).isEqualTo("created");
			})));
		probes.put(DriverConnection.class.getMethod("isClosed"),
			new Probe("Connection.isClosed", () -> inCache(Mode.PEER, c -> assertThat(c.isClosed()).isFalse())));
		probes.put(DriverConnection.class.getMethod("close"),
			new Probe("Connection.close", () -> inCache(Mode.PEER, c -> {
				c.close();
				assertThat(c.isClosed()).isTrue();
			})));
		probes.put(DriverRegion.class.getMethod("name"), new Probe("Region.name", () ->
			inRegion(r -> assertThat(r.name()).isEqualTo("entries"))));
		probes.put(DriverRegion.class.getMethod("get", Object.class), new Probe("Region.get", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.get("key")).isEqualTo("value"); })));
		probes.put(DriverRegion.class.getMethod("put", Object.class, Object.class), new Probe("Region.put", () ->
			inRegion(r -> {
				assertThat(r.put("key", "first")).isNull();
				assertThat(r.put("key", "second")).isEqualTo("first");
			})));
		probes.put(DriverRegion.class.getMethod("remove", Object.class), new Probe("Region.remove", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.remove("key")).isEqualTo("value"); })));
		probes.put(DriverRegion.class.getMethod("containsKey", Object.class), new Probe("Region.containsKey", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.containsKey("key")).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("size"), new Probe("Region.size", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.size()).isEqualTo(1); })));
		probes.put(DriverRegion.class.getMethod("setCacheLoader", DriverCacheLoader.class),
			new Probe("Region.setCacheLoader", () -> inRegion(r -> {
				r.setCacheLoader(new RecordingLoader(h -> "loaded"));
				assertThat(r.get("key")).isEqualTo("loaded");
			})));
		probes.put(DriverRegion.class.getMethod("get", Object.class, Object.class),
			new Probe("Region.getWithArgument", () -> inRegion(r -> {
				r.setCacheLoader(new RecordingLoader(h -> (String) h.getArgument()));
				assertThat(r.get("key", "argument")).isEqualTo("argument");
			})));
		return probes;
	}

	@Test
	void localRegionCrudUsesSharedDataAcrossHandles() {
		inCache(Mode.PEER, c -> {
			DriverRegion<String, String> region = c.createRegion("entries", RegionShortcut.LOCAL);
			assertThat(region.get("missing")).isNull();
			assertThat(region.containsKey("missing")).isFalse();
			assertThat(region.put("key", "first")).isNull();
			assertThat(c.<String, String>getRegion("entries").get("key")).isEqualTo("first");
			assertThat(region.put("key", "second")).isEqualTo("first");
			assertThat(region.size()).isEqualTo(1);
			assertThat(region.remove("key")).isEqualTo("second");
			assertThat(region.remove("key")).isNull();
			assertThat(region.size()).isZero();
		});
	}

	@Test
	void clientLocalRegionSupportsCrud() {
		inCache(Mode.CLIENT, c -> {
			DriverRegion<String, String> region = c.createClientRegion("entries", ClientRegionShortcut.LOCAL);
			region.put("key", "value");
			assertThat(region.get("key")).isEqualTo("value");
			assertThat(c.getRegion("entries").size()).isEqualTo(1);
			assertThatThrownBy(() -> c.createRegion("wrongMode", RegionShortcut.LOCAL))
				.isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void closedCacheAndExistingRegionHandleFailAtTheBoundary() {
		inCache(Mode.PEER, c -> {
			DriverRegion<String, String> region = c.createRegion("entries", RegionShortcut.LOCAL);
			c.close();
			assertThat(c.isClosed()).isTrue();
			assertThatThrownBy(() -> c.getRegion("entries")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.get("key")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(c::rootRegions).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(region::clear).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.putIfAbsent("key", "value")).isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void invalidOperationsUseSpiExceptions() {
		inCache(Mode.PEER, c -> {
			DriverRegion<String, String> region = c.createRegion("entries", RegionShortcut.LOCAL);
			assertThatThrownBy(() -> c.createRegion("entries", RegionShortcut.LOCAL))
				.isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.getRegion(null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.getRegion("")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.createRegion("invalid", null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.get(null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.put("key", null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.createClientRegion("wrongMode", ClientRegionShortcut.LOCAL))
				.isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void rejectsUnknownConfigurationWithoutPrintingIt() {
		DriverConfiguration config = new DriverConfiguration() {
			@Override public String toString() { return "credential-secret"; }
		};
		assertThatThrownBy(() -> driver().connect(config)).isInstanceOf(DriverBindException.class)
			.hasMessageNotContaining("credential-secret");
	}

	@Test
	void loadersReceiveContextAndCacheOnlyNonNullResults() {
		for (Mode mode : Mode.values()) {
			inCache(mode, c -> {
				DriverRegion<String, String> region = localRegion(c, mode);
				RecordingLoader loader = new RecordingLoader(helper -> {
					assertThat(helper.getRegion().name()).isEqualTo("entries");
					helper.getRegion().put("side-effect", "visible");
					return "absent".equals(helper.getKey()) ? null : helper.getKey() + ":" + helper.getArgument();
				});
				region.setCacheLoader(loader);
				assertThat(region.get("key", "argument")).isEqualTo("key:argument");
				assertThat(region.get("key", "ignored")).isEqualTo("key:argument");
				assertThat(region.get("side-effect")).isEqualTo("visible");
				assertThat(loader.loads.get()).isEqualTo(1);
				assertThat(region.get("absent")).isNull();
				assertThat(region.get("absent")).isNull();
				assertThat(region.containsKey("absent")).isFalse();
				assertThat(loader.loads.get()).isEqualTo(3);
			});
		}
	}

	@Test
	void loaderReplacementRemovalAndShutdownFollowLifecycle() {
		for (Mode mode : Mode.values()) {
			inCache(mode, c -> {
				DriverRegion<String, String> region = localRegion(c, mode);
				RecordingLoader first = new RecordingLoader(h -> "first");
				RecordingLoader second = new RecordingLoader(h -> "second");
				region.setCacheLoader(first);
				c.<String, String>getRegion("entries").setCacheLoader(first);
				assertThat(first.closes.get()).isZero();
				region.setCacheLoader(second);
				assertThat(first.closes.get()).isEqualTo(1);
				region.setCacheLoader(null);
				assertThat(second.closes.get()).isEqualTo(1);
				assertThat(region.get("missing")).isNull();
				RecordingLoader last = new RecordingLoader(h -> "last");
				region.setCacheLoader(last);
				c.close();
				c.close();
				assertThat(last.closes.get()).isEqualTo(1);
				assertThatThrownBy(() -> region.setCacheLoader(last)).isInstanceOf(DriverOperationException.class);
				assertThatThrownBy(() -> region.get("key", "argument")).isInstanceOf(DriverOperationException.class);
			});
		}
	}

	@Test
	void loaderFailuresKeepTheirCauseAndDoNotInsertValues() {
		inRegion(region -> {
			RuntimeException failure = new IllegalStateException("callback-secret");
			RecordingLoader loader = new RecordingLoader(h -> { throw failure; });
			region.setCacheLoader(loader);
			assertThatThrownBy(() -> region.get("key", "argument"))
				.isInstanceOf(DriverOperationException.class).hasRootCause(failure)
				.hasMessageNotContaining("callback-secret");
			assertThat(region.containsKey("key")).isFalse();
			assertThatThrownBy(() -> region.get(null, "argument")).isInstanceOf(DriverOperationException.class);
			assertThat(loader.loads.get()).isEqualTo(1);
		});
	}

	private DriverRegion<String, String> localRegion(DriverConnection connection, Mode mode) {
		return mode == Mode.PEER ? connection.createRegion("entries", RegionShortcut.LOCAL)
			: connection.createClientRegion("entries", ClientRegionShortcut.LOCAL);
	}

	private static final class RecordingLoader implements DriverCacheLoader<String, String> {
		private final Function<DriverLoaderHelper<String, String>, String> action;
		private final AtomicInteger loads = new AtomicInteger();
		private final AtomicInteger closes = new AtomicInteger();
		RecordingLoader(Function<DriverLoaderHelper<String, String>, String> action) { this.action = action; }
		@Override public String load(DriverLoaderHelper<String, String> helper) {
			loads.incrementAndGet();
			return action.apply(helper);
		}
		@Override public void close() { closes.incrementAndGet(); }
	}

	private void inCache(Mode mode, Consumer<DriverConnection> action) {
		try (DriverConnection connection = driver().connect(configuration(mode))) {
			action.accept(connection);
		}
	}

	private void inRegion(Consumer<DriverRegion<String, String>> action) {
		inCache(Mode.PEER, c -> action.accept(c.createRegion("entries", RegionShortcut.LOCAL)));
	}
}
