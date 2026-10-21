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
import java.util.List;
import java.util.UUID;
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
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;
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
public abstract class GemFireDriverContract extends PoolConfigurationContract {

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
		probes.put(DriverRegion.class.getMethod("addLifecycleListener", DriverRegionLifecycleListener.class),
			new Probe("Region.addLifecycleListener", () -> inRegion(r -> r.addLifecycleListener(new RecordingLifecycle()))));
		probes.put(DriverConnection.class.getMethod("createPool", String.class, PoolConfiguration.class),
			new Probe("Connection.createPool", () -> inPool(pool -> assertThat(pool.name()).isNotBlank())));
		probes.put(DriverConnection.class.getMethod("getPool", String.class),
			new Probe("Connection.getPool", () -> inCache(Mode.CLIENT, c -> {
				String name = UUID.randomUUID().toString();
				assertThat(c.getPool(name)).isNull();
				DriverPool pool = c.createPool(name, poolConfiguration(false));
				try { assertThat(c.getPool(name).name()).isEqualTo(name); }
				finally { pool.destroy(); }
			})));
		probes.put(DriverPool.class.getMethod("name"), new Probe("Pool.name", () ->
			inPool(pool -> assertThat(pool.name()).isNotBlank())));
		probes.put(DriverPool.class.getMethod("servers"), new Probe("Pool.servers", () ->
			inPool(pool -> assertThat(pool.servers()).extracting(Endpoint::port).containsExactly(40404))));
		probes.put(DriverPool.class.getMethod("locators"), new Probe("Pool.locators", () ->
			inPool(pool -> assertThat(pool.locators()).isEmpty())));
		probes.put(DriverPool.class.getMethod("minimumConnections"), new Probe("Pool.minimumConnections", () ->
			inPool(pool -> assertThat(pool.minimumConnections()).isZero())));
		probes.put(DriverPool.class.getMethod("isDestroyed"), new Probe("Pool.isDestroyed", () ->
			inPool(pool -> assertThat(pool.isDestroyed()).isFalse())));
		probes.put(DriverPool.class.getMethod("destroy"), new Probe("Pool.destroy", () ->
			inPool(pool -> { pool.destroy(); assertThat(pool.isDestroyed()).isTrue(); })));
		probes.put(DriverPool.class.getMethod("configuration"), new Probe("Pool.configuration", () ->
			inPool(pool -> { assertThat(pool.configuration().minimumConnections()).hasValue(0);
				assertThat(pool.configuration().servers()).isEqualTo(pool.servers()); })));
		probes.putAll(callbackProbes());
		probes.putAll(regionOperationProbes());
		probes.putAll(regionConfigurationProbes());
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
				.hasMessageNotContaining("callback-secret")
				.satisfies(ex -> assertThat(((DriverOperationException) ex).kind()).isEqualTo(DriverFailureKind.LOADER_FAILURE));
			assertThat(region.containsKey("key")).isFalse();
			assertThatThrownBy(() -> region.get(null, "argument")).isInstanceOf(DriverOperationException.class);
			assertThat(loader.loads.get()).isEqualTo(1);
		});
	}

	@Test
	void loaderUnsupportedAndTypedVetoAreNotReclassifiedOrTreatedAsMisses() {
		inRegion(region -> {
			for (RuntimeException failure : new RuntimeException[] {
				UnsupportedFeatureException.notIntroduced("loader.remote"),
				UnsupportedFeatureException.removed("loader.remote", expectedNativeVersion()),
				new DriverOperationException(DriverFailureKind.WRITER_VETO, "Application veto", null) }) {
				region.setCacheLoader(new RecordingLoader(helper -> { throw failure; }));
				assertThatThrownBy(() -> region.get("key")).isSameAs(failure);
				assertThat(region.containsKey("key")).isFalse();
			}
		});
	}

	@Test
	void duplicateRegionAndInvalidArgumentHaveDistinctSemantics() {
		inCache(Mode.PEER, connection -> {
			var region = connection.createRegion("entries", RegionShortcut.LOCAL);
			assertThatThrownBy(() -> connection.createRegion("entries", RegionShortcut.LOCAL))
				.isInstanceOfSatisfying(DriverOperationException.class,
					failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.REGION_EXISTS));
			assertThatThrownBy(() -> region.get(null))
				.isInstanceOfSatisfying(DriverOperationException.class,
					failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		});
	}

	@Test
	void localLifecycleRegistrationIdentityAndCleanup() {
		for (Mode mode : Mode.values()) {
			inCache(mode, c -> {
				DriverRegion<String, String> region = localRegion(c, mode);
				RecordingLifecycle first = new RecordingLifecycle();
				RecordingLifecycle second = new RecordingLifecycle();
				region.addLifecycleListener(first);
				c.getRegion("entries").addLifecycleListener(first);
				region.addLifecycleListener(second);
				assertThat(first.events).isEmpty();
				assertThatThrownBy(() -> region.addLifecycleListener(null)).isInstanceOf(DriverOperationException.class);
				c.close();
				c.close();
				assertThat(first.events).containsExactly("destroy:entries", "close");
				assertThat(second.events).containsExactly("destroy:entries", "close");
				assertThatThrownBy(() -> region.addLifecycleListener(first)).isInstanceOf(DriverOperationException.class);
			});
		}
	}

	@Test
	void callbackRuntimeFailuresDoNotSkipRemainingLifecycleCallbacks() {
		for (Mode mode : Mode.values()) {
			inCache(mode, c -> {
				DriverRegion<String, String> region = localRegion(c, mode);
				region.addLifecycleListener(new DriverRegionLifecycleListener() {
					@Override public void afterRegionDestroy(DriverRegion<?, ?> destroyed) {
						throw new IllegalStateException("expected callback failure");
					}
					@Override public void close() { throw new IllegalStateException("expected cleanup failure"); }
				});
				RecordingLifecycle listener = new RecordingLifecycle();
				region.addLifecycleListener(listener);
				c.close();
				assertThat(listener.events).containsExactly("destroy:entries", "close");
			});
		}
	}

	@Test
	void olderRegionImplementationsRejectLifecycleRegistrationWithTypedFailure() {
		DriverRegion<Object, Object> older = new DriverRegion<>() { };
		assertThatThrownBy(() -> older.addLifecycleListener(new RecordingLifecycle()))
			.isInstanceOfSatisfying(com.vmware.gemfire.driver.spi.UnsupportedFeatureException.class, failure -> {
				assertThat(failure.feature()).isEqualTo("Region.addLifecycleListener");
				assertThat(failure.reason()).isEqualTo(Reason.NOT_INTRODUCED);
			});
	}

	private static class RecordingLifecycle implements DriverRegionLifecycleListener {
		private final java.util.List<String> events = new java.util.ArrayList<>();
		@Override public void afterRegionDestroy(DriverRegion<?, ?> region) { events.add("destroy:" + region.name()); }
		@Override public void close() { events.add("close"); }
		// Equal but distinct callbacks remain independent registrations.
		@Override public boolean equals(Object other) { return other instanceof RecordingLifecycle; }
		@Override public int hashCode() { return 1; }
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

	@Test
	void namedPoolMetadataDuplicateRejectionAndRecreation() {
		for (boolean locators : new boolean[] { false, true }) {
			inCache(Mode.CLIENT, connection -> {
				String name = UUID.randomUUID().toString();
				PoolConfiguration config = poolConfiguration(locators);
				DriverPool first = connection.createPool(name, config);
				try {
					assertThat(first.name()).isEqualTo(name);
					assertThat(first.minimumConnections()).isZero();
					assertThat(locators ? first.locators() : first.servers())
						.extracting(Endpoint::port).containsExactly(locators ? 10334 : 40404);
					assertThat(locators ? first.servers() : first.locators()).isEmpty();
					assertThatThrownBy(() -> connection.createPool(name, config))
						.isInstanceOf(DriverOperationException.class);
					assertThat(first.isDestroyed()).isFalse();
					assertThat(connection.getPool(name).minimumConnections()).isZero();
				}
				finally { first.destroy(); }
				assertThat(first.isDestroyed()).isTrue();
				assertThat(connection.getPool(name)).isNull();
				DriverPool replacement = connection.createPool(name, config);
				try {
					first.destroy(); // A stale handle must not remove the new pool.
					assertThat(connection.getPool(name).isDestroyed()).isFalse();
				}
				finally { replacement.destroy(); }
			});
		}
	}

	@Test
	void poolAccessRejectsWrongModeClosedConnectionAndInvalidArguments() {
		for (Mode mode : Mode.values()) {
			inCache(mode, connection -> {
				PoolConfiguration config = poolConfiguration(false);
				if (mode == Mode.PEER) {
					assertThatThrownBy(() -> connection.createPool("wrong", config)).isInstanceOf(DriverOperationException.class);
					assertThatThrownBy(() -> connection.getPool("wrong")).isInstanceOf(DriverOperationException.class);
				}
				else {
					for (String name : new String[] { null, "", "  " }) {
						assertThatThrownBy(() -> connection.createPool(name, config)).isInstanceOf(DriverOperationException.class);
						assertThatThrownBy(() -> connection.getPool(name)).isInstanceOf(DriverOperationException.class);
					}
					assertThatThrownBy(() -> connection.createPool("null", null)).isInstanceOf(DriverOperationException.class);
				}
				connection.close();
				assertThatThrownBy(() -> connection.createPool("closed", config)).isInstanceOf(DriverOperationException.class);
				assertThatThrownBy(() -> connection.getPool("closed")).isInstanceOf(DriverOperationException.class);
			});
		}
	}

	private PoolConfiguration poolConfiguration(boolean locators) {
		return new PoolConfiguration(locators ? List.of() : List.of(new Endpoint("127.0.0.1", 40404)),
			locators ? List.of(new Endpoint("127.0.0.1", 10334)) : List.of(), 0);
	}

	private void inPool(Consumer<DriverPool> action) {
		inCache(Mode.CLIENT, connection -> {
			DriverPool pool = connection.createPool(UUID.randomUUID().toString(), poolConfiguration(false));
			try { action.accept(pool); }
			finally { pool.destroy(); }
		});
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
