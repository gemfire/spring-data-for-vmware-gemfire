/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;

/** Local callback behavior shared by official native and mock providers. */
public abstract class RegionCallbackContract {
	protected abstract GemFireDriver driver();
	protected abstract CacheConfiguration configuration(CacheConfiguration.Mode mode);
	private void withCache(Consumer<DriverConnection> action) {
		try (var connection = driver().connect(configuration(CacheConfiguration.Mode.PEER))) { action.accept(connection); }
	}
	private void withRegion(Consumer<DriverRegion<String, String>> action) {
		withCache(c -> action.accept(c.createRegion("callbacks", RegionShortcut.LOCAL)));
	}
	protected Map<Method, Probe> callbackProbes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverRegion.class.getMethod("addCacheListener", DriverCacheListener.class),
			new Probe("Region.addCacheListener", () -> withRegion(r -> r.addCacheListener(new RecordingListener()))));
		probes.put(DriverRegion.class.getMethod("removeCacheListener", DriverCacheListener.class),
			new Probe("Region.removeCacheListener", () -> withRegion(r -> r.removeCacheListener(new RecordingListener()))));
		probes.put(DriverRegion.class.getMethod("initCacheListeners", List.class),
			new Probe("Region.initCacheListeners", () -> withRegion(r -> r.initCacheListeners(List.of(new RecordingListener())))));
		probes.put(DriverRegion.class.getMethod("getCacheListeners"),
			new Probe("Region.getCacheListeners", () -> withRegion(r -> assertThat(r.getCacheListeners()).isEmpty())));
		probes.put(DriverRegion.class.getMethod("setCacheWriter", DriverCacheWriter.class),
			new Probe("Region.setCacheWriter", () -> withRegion(r -> r.setCacheWriter(new RecordingWriter()))));
		probes.put(DriverRegion.class.getMethod("getCacheWriter"),
			new Probe("Region.getCacheWriter", () -> withRegion(r -> assertThat(r.getCacheWriter()).isNull())));
		probes.put(DriverRegion.class.getMethod("getCacheLoader"),
			new Probe("Region.getCacheLoader", () -> withRegion(r -> assertThat(r.getCacheLoader()).isNull())));
		probes.put(DriverRegion.class.getMethod("put", Object.class, Object.class, Object.class),
			new Probe("Region.putWithArgument", () -> withRegion(r -> assertThat(r.put("k", "v", "arg")).isNull())));
		probes.put(DriverRegion.class.getMethod("invalidate", Object.class, Object.class),
			new Probe("Region.invalidate", () -> withRegion(r -> { r.put("k", "v"); r.invalidate("k", "arg"); assertThat(r.get("k")).isNull(); })));
		probes.put(DriverRegion.class.getMethod("destroy", Object.class, Object.class),
			new Probe("Region.destroy", () -> withRegion(r -> { r.put("k", "v"); assertThat(r.destroy("k", "arg")).isEqualTo("v"); assertThat(r.containsKey("k")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("invalidateRegion", Object.class),
			new Probe("Region.invalidateRegion", () -> withRegion(r -> r.invalidateRegion("arg"))));
		probes.put(DriverRegion.class.getMethod("destroyRegion", Object.class),
			new Probe("Region.destroyRegion", () -> withRegion(r -> { r.destroyRegion("arg"); assertThat(r.isDestroyed()).isTrue(); })));
		probes.put(DriverDeclarable.class.getMethod("init", Properties.class),
			new Probe("Declarable.init", () -> new DriverDeclarable() { }.init(new Properties())));
		probes.put(DriverDeclarable.class.getMethod("initialize", DriverConnection.class, Properties.class),
			new Probe("Declarable.initialize", () -> withCache(c -> new DriverDeclarable() { }.initialize(c, new Properties()))));
		probes.put(DriverConnection.class.getMethod("createRegion", String.class, RegionShortcut.class, RegionCallbacks.class),
			new Probe("Connection.createRegionWithCallbacks", () -> withCache(c -> {
				var listener = new RecordingListener();
				c.createRegion("created", RegionShortcut.LOCAL, new RegionCallbacks<>(List.of(listener), null, null));
				assertThat(listener.calls).containsExactly("afterRegionCreate");
			})));
		probes.put(DriverConnection.class.getMethod("createClientRegion", String.class, ClientRegionShortcut.class, RegionCallbacks.class),
			new Probe("Connection.createClientRegionWithCallbacks", () -> {
				try (var c = driver().connect(configuration(CacheConfiguration.Mode.CLIENT))) {
					var listener = new RecordingListener();
					c.createClientRegion("created", ClientRegionShortcut.LOCAL, new RegionCallbacks<>(List.of(listener), null, null));
					assertThat(listener.calls).contains("afterRegionCreate");
				}
			}));
		return probes;
	}

	@Test
	void deliversLocalEntryAndRegionEventsWithPayloadsAndWriterOrdering() {
		withCache(c -> {
			var listener = new RecordingListener(); var writer = new RecordingWriter();
			DriverRegion<String, String> region = c.createRegion("entries", RegionShortcut.LOCAL,
				new RegionCallbacks<>(List.of(listener), null, writer));
			assertThat(listener.operations).containsExactly(DriverOperation.REGION_CREATE);
			region.put("key", "one", "create-argument");
			region.put("key", "two", "update-argument");
			region.invalidate("key", "invalidate-argument");
			assertThat(region.containsKey("key")).isTrue();
			assertThat(region.get("key")).isNull();
			region.destroy("key", "destroy-argument");
			region.put("other", "value");
			region.invalidateRegion("region-argument");
			region.clear();
			region.destroyRegion("destroy-region-argument");
			assertThat(listener.calls).containsExactly("afterRegionCreate", "afterCreate", "afterUpdate",
				"afterInvalidate", "afterDestroy", "afterCreate", "afterRegionInvalidate", "afterRegionClear", "afterRegionDestroy", "close");
			assertThat(listener.payloads).contains("key:null:one:create-argument", "key:one:two:update-argument",
				"key:two:null:invalidate-argument", "key:null:null:destroy-argument");
			assertThat(writer.calls).containsExactly("beforeCreate", "beforeUpdate", "beforeDestroy", "beforeCreate",
				"beforeRegionClear", "beforeRegionDestroy", "close");
			assertThat(c.getRegion("entries")).isNull();
			assertThat(region.isDestroyed()).isTrue();
		});
	}

	@Test
	void writerVetoPreventsEverySupportedMutationAndPreservesTypedFailures() {
		for (String operation : List.of("create", "update", "destroy", "clear", "regionDestroy")) {
			for (GemFireDriverException veto : List.of(
				new DriverOperationException(DriverFailureKind.WRITER_VETO, "Rejected", null),
				UnsupportedFeatureException.notIntroduced("writer.feature"),
				UnsupportedFeatureException.removed("writer.feature", "10.4.0"))) {
				withRegion(region -> {
					region.put("existing", "original");
					var listener = new RecordingListener(); region.addCacheListener(listener);
					region.setCacheWriter(new DriverCacheWriterAdapter<>() {
						@Override public void beforeCreate(DriverEntryEvent<String, String> e) { throw veto; }
						@Override public void beforeUpdate(DriverEntryEvent<String, String> e) { throw veto; }
						@Override public void beforeDestroy(DriverEntryEvent<String, String> e) { throw veto; }
						@Override public void beforeRegionClear(DriverRegionEvent<String, String> e) { throw veto; }
						@Override public void beforeRegionDestroy(DriverRegionEvent<String, String> e) { throw veto; }
					});
					assertThatThrownBy(() -> {
						switch (operation) {
							case "create" -> region.put("absent", "new");
							case "update" -> region.put("existing", "new");
							case "destroy" -> region.destroy("existing", null);
							case "clear" -> region.clear();
							case "regionDestroy" -> region.destroyRegion(null);
						}
					}).isSameAs(veto);
					assertThat(region.get("existing")).isEqualTo("original");
					assertThat(region.containsKey("absent")).isFalse();
					assertThat(region.isDestroyed()).isFalse();
					assertThat(listener.calls).isEmpty();
				});
			}
		}
	}

	@Test
	void registrationIdentityReplacementAndCleanupAreSharedAcrossHandles() {
		withCache(c -> {
			DriverRegion<String, String> region = c.createRegion("entries", RegionShortcut.LOCAL);
			DriverRegion<String, String> other = c.getRegion("entries");
			var first = new RecordingListener(); var second = new RecordingListener();
			region.addCacheListener(first); other.addCacheListener(first); other.addCacheListener(second);
			assertThat(other.getCacheListeners()).containsExactly(first, second);
			other.removeCacheListener(first);
			assertThat(first.calls).containsExactly("close");
			region.put("k", "v");
			assertThat(second.calls).containsExactly("afterCreate");
			region.initCacheListeners(List.of(first));
			assertThat(second.calls).containsExactly("afterCreate", "close");
			assertThat(other.getCacheListeners()).containsExactly(first);
			var writer = new RecordingWriter();
			region.setCacheWriter(writer); other.setCacheWriter(writer);
			assertThat(other.getCacheWriter()).isSameAs(writer);
			assertThat(writer.calls).isEmpty();
			other.setCacheWriter(null); assertThat(writer.calls).containsExactly("close");
			AtomicInteger destroyed = new AtomicInteger();
			region.addLifecycleListener(new DriverRegionLifecycleListener() {
				@Override public void afterRegionDestroy(DriverRegion<?, ?> r) { destroyed.incrementAndGet(); }
				@Override public void close() { }
			});
			region.initCacheListeners(List.of(second));
			c.close();
			assertThat(destroyed).hasValue(1);
		});
	}

	@Test
	void reinitializationRetainsDuplicatesAndDoesNotCloseResolverRegistrations() {
		withRegion(region -> {
			var listener = new RecordingListener(); var closes = new AtomicInteger();
			region.addLifecycleListener(new DriverRegionLifecycleListener() {
				@Override public void afterRegionDestroy(DriverRegion<?, ?> r) { }
				@Override public void close() { closes.incrementAndGet(); }
			});
			region.addCacheListener(listener);
			region.initCacheListeners(List.of(listener, listener));
			assertThat(listener.calls).containsExactly("close");
			assertThat(closes).hasValue(0);
			region.put("key", "value");
			assertThat(listener.calls).containsExactly("close", "afterCreate", "afterCreate");
			region.initCacheListeners(null);
			assertThat(region.getCacheListeners()).isEmpty();
			assertThat(listener.calls).containsExactly("close", "afterCreate", "afterCreate", "close", "close");
			assertThat(closes).hasValue(0);
			region.destroyRegion(null);
			assertThat(closes).hasValue(1);
		});
	}

	@Test
	void listenerFailureDoesNotVetoOrSkipOtherListenersAndWriterFailureDoes() {
		withRegion(region -> {
			region.addCacheListener(new DriverCacheListenerAdapter<>() {
				@Override public void afterCreate(DriverEntryEvent<String, String> event) { throw new IllegalStateException("secret"); }
				@Override public void close() { throw new IllegalStateException("secret"); }
			});
			var recording = new RecordingListener(); region.addCacheListener(recording);
			region.put("key", "value");
			assertThat(region.get("key")).isEqualTo("value");
			assertThat(recording.calls).containsExactly("afterCreate");
			var failure = new IllegalStateException("secret");
			region.setCacheWriter(new DriverCacheWriterAdapter<>() {
				@Override public void beforeUpdate(DriverEntryEvent<String, String> event) { throw failure; }
			});
			assertThatThrownBy(() -> region.put("key", "replacement"))
				.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.WRITER_VETO))
				.hasCause(failure).hasMessage("Cache writer failed");
			assertThat(region.get("key")).isEqualTo("value");
		});
	}

	@Test
	void injectedInitializationIsExplicitAndLoaderEventsRetainTheArgument() {
		withCache(c -> {
			var calls = new ArrayList<String>();
			var properties = new Properties(); properties.setProperty("name", "configured");
			DriverCacheLoader<String, String> loader = new DriverCacheLoader<>() {
				@Override public void init(Properties p) { assertThat(p).isSameAs(properties); calls.add("init"); }
				@Override public void initialize(DriverConnection cache, Properties p) {
					assertThat(cache).isSameAs(c); assertThat(p).isSameAs(properties); calls.add("initialize");
				}
				@Override public String load(DriverLoaderHelper<String, String> helper) { return "loaded"; }
				@Override public void close() { calls.add("close"); }
			};
			loader.init(properties); loader.initialize(c, properties);
			var listener = new RecordingListener();
			DriverRegion<String, String> region = c.createRegion("loaded", RegionShortcut.LOCAL,
				new RegionCallbacks<>(List.of(listener), loader, null));
			assertThat(region.getCacheLoader()).isSameAs(loader);
			assertThat(calls).containsExactly("init", "initialize");
			assertThat(region.get("key", "argument")).isEqualTo("loaded");
			assertThat(listener.operations).containsExactly(DriverOperation.REGION_CREATE, DriverOperation.LOCAL_LOAD_CREATE);
			assertThat(listener.payloads).containsExactly("key:null:loaded:argument");
			region.setCacheLoader(null);
			assertThat(calls).containsExactly("init", "initialize", "close");
		});
	}

	private class RecordingListener extends DriverCacheListenerAdapter<String, String> {
		final List<String> calls = new ArrayList<>();
		final List<String> payloads = new ArrayList<>();
		final List<DriverOperation> operations = new ArrayList<>();
		void record(String method, DriverCacheEvent<String, String> event) {
			calls.add(method); operations.add(event.getOperation());
			assertThat(event.getRegion().name()).isNotBlank();
			assertThat(event.isOriginRemote()).isFalse();
			assertThat(event.isCallbackArgumentAvailable()).isTrue();
			assertThat(event.getDistributedMember().getId()).isNotBlank();
		}
		void entry(String method, DriverEntryEvent<String, String> event) {
			record(method, event);
			payloads.add(event.getKey() + ":" + event.getOldValue() + ":" + event.getNewValue() + ":" + event.getCallbackArgument());
			assertThat(event.hasClientOrigin()).isFalse(); assertThat(event.getTransactionId()).isNull();
		}
		@Override public void afterCreate(DriverEntryEvent<String, String> e) { entry("afterCreate", e); }
		@Override public void afterUpdate(DriverEntryEvent<String, String> e) { entry("afterUpdate", e); }
		@Override public void afterInvalidate(DriverEntryEvent<String, String> e) { entry("afterInvalidate", e); }
		@Override public void afterDestroy(DriverEntryEvent<String, String> e) { entry("afterDestroy", e); }
		@Override public void afterRegionInvalidate(DriverRegionEvent<String, String> e) { record("afterRegionInvalidate", e); }
		@Override public void afterRegionDestroy(DriverRegionEvent<String, String> e) { record("afterRegionDestroy", e); }
		@Override public void afterRegionClear(DriverRegionEvent<String, String> e) { record("afterRegionClear", e); }
		@Override public void afterRegionCreate(DriverRegionEvent<String, String> e) { record("afterRegionCreate", e); }
		@Override public void afterRegionLive(DriverRegionEvent<String, String> e) { record("afterRegionLive", e); }
		@Override public void close() { calls.add("close"); }
	}
	private class RecordingWriter extends DriverCacheWriterAdapter<String, String> {
		final List<String> calls = new ArrayList<>();
		@Override public void beforeCreate(DriverEntryEvent<String, String> e) { calls.add("beforeCreate"); }
		@Override public void beforeUpdate(DriverEntryEvent<String, String> e) { calls.add("beforeUpdate"); }
		@Override public void beforeDestroy(DriverEntryEvent<String, String> e) { calls.add("beforeDestroy"); }
		@Override public void beforeRegionClear(DriverRegionEvent<String, String> e) { calls.add("beforeRegionClear"); }
		@Override public void beforeRegionDestroy(DriverRegionEvent<String, String> e) { calls.add("beforeRegionDestroy"); }
		@Override public void close() { calls.add("close"); }
	}
}
