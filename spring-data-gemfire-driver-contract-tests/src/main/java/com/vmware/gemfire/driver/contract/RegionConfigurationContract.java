/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;
import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;

/** Configured local-region behavior shared by mock and native providers. */
public abstract class RegionConfigurationContract {
	protected abstract GemFireDriver driver();
	protected abstract CacheConfiguration configuration(CacheConfiguration.Mode mode);
	private void connection(CacheConfiguration.Mode mode, Consumer<DriverConnection> action) {
		try (var connection = driver().connect(configuration(mode))) { action.accept(connection); }
	}
	protected Map<Method, Probe> regionConfigurationProbes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverConnection.class.getMethod("createRegion", String.class, RegionShortcut.class, RegionConfiguration.class),
			new Probe("Connection.createConfiguredRegion", () -> connection(CacheConfiguration.Mode.PEER, c -> {
				var r = c.createRegion("configured", RegionShortcut.LOCAL, RegionConfiguration.builder().initialCapacity(0).concurrencyChecksEnabled(false).build());
				assertThat(r.getAttributes().getInitialCapacity()).isZero(); assertThat(r.getAttributes().getConcurrencyChecksEnabled()).isFalse();
			})));
		probes.put(DriverConnection.class.getMethod("createClientRegion", String.class, ClientRegionShortcut.class, RegionConfiguration.class),
			new Probe("Connection.createConfiguredClientRegion", () -> connection(CacheConfiguration.Mode.CLIENT, c -> {
				var r = c.createClientRegion("configured", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().loadFactor(0.5f).build());
				assertThat(r.getAttributes().getLoadFactor()).isEqualTo(0.5f);
			})));
		probes.put(DriverRegion.class.getMethod("getAttributes"), new Probe("Region.getAttributes", () -> connection(CacheConfiguration.Mode.PEER, c -> {
			var a = c.createRegion("configured", RegionShortcut.LOCAL).getAttributes();
			assertThat(a.getDataPolicy()).isEqualTo(DataPolicy.NORMAL); assertThat(a.getScope()).isEqualTo(Scope.LOCAL);
		})));
		probes.put(DriverRegion.class.getMethod("getAttributesMutator"), new Probe("Region.getAttributesMutator", () -> connection(CacheConfiguration.Mode.PEER, c -> {
			var r = c.createRegion("configured", RegionShortcut.LOCAL); var m = r.getAttributesMutator();
			assertThat(m.getRegion()).isEqualTo(r); m.setCloningEnabled(false); assertThat(m.getCloningEnabled()).isFalse();
		})));
		probes.put(DriverRegion.class.getMethod("createSubregion", String.class, RegionShortcut.class, RegionConfiguration.class),
			new Probe("Region.createSubregion", () -> connection(CacheConfiguration.Mode.PEER, c -> {
				var root = c.createRegion("configured", RegionShortcut.LOCAL);
				var child = root.createSubregion("child", RegionShortcut.LOCAL, RegionConfiguration.builder().build());
				assertThat(child.getParentRegion()).isEqualTo(root); assertThat(c.getRegion("/configured/child")).isEqualTo(child);
			})));
		probes.put(DriverRegion.class.getMethod("createClientSubregion", String.class, ClientRegionShortcut.class, RegionConfiguration.class),
			new Probe("Region.createClientSubregion", () -> connection(CacheConfiguration.Mode.CLIENT, c -> {
				var root = c.createClientRegion("configured", ClientRegionShortcut.LOCAL);
				var child = root.createClientSubregion("child", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().build());
				assertThat(child.getParentRegion()).isEqualTo(root); assertThat(c.rootRegions()).containsExactly(root);
			})));
		return probes;
	}
	@Test void configurationPreservesDefaultsAndExplicitOverrides() {
		connection(CacheConfiguration.Mode.PEER, c -> {
			var defaults = c.createRegion("defaults", RegionShortcut.LOCAL).getAttributes();
			var implicit = c.createRegion("implicit", RegionShortcut.LOCAL, RegionConfiguration.builder().build()).getAttributes();
			assertThat(implicit.getDataPolicy()).isEqualTo(defaults.getDataPolicy());
			assertThat(implicit.getScope()).isEqualTo(defaults.getScope());
			assertThat(implicit.getInitialCapacity()).isEqualTo(defaults.getInitialCapacity());
			assertThat(implicit.getLoadFactor()).isEqualTo(defaults.getLoadFactor());
			assertThat(implicit.getConcurrencyLevel()).isEqualTo(defaults.getConcurrencyLevel());
			assertThat(implicit.getConcurrencyChecksEnabled()).isEqualTo(defaults.getConcurrencyChecksEnabled());
			var r = c.createRegion("configured", RegionShortcut.LOCAL, RegionConfiguration.<String, Integer>builder()
				.keyConstraint(String.class).valueConstraint(Integer.class).scope(Scope.LOCAL).dataPolicy(DataPolicy.NORMAL)
				.initialCapacity(0).loadFactor(0.5f).concurrencyLevel(3).concurrencyChecksEnabled(false)
				.statisticsEnabled(false).ignoreJTA(true).diskSynchronous(false).indexMaintenanceSynchronous(false)
				.cloningEnabled(false).lockGrantor(false).multicastEnabled(false).enableSubscriptionConflation(false)
				.earlyAck(false).poolName(null).diskStoreName(null).gatewaySenderIds(Set.of()).asyncEventQueueIds(Set.of()).build());
			var a = r.getAttributes();
			assertThat(a.getKeyConstraint()).isEqualTo(String.class); assertThat(a.getValueConstraint()).isEqualTo(Integer.class);
			assertThat(a.getInitialCapacity()).isZero(); assertThat(a.getLoadFactor()).isEqualTo(0.5f); assertThat(a.getConcurrencyLevel()).isEqualTo(3);
			assertThat(a.getConcurrencyChecksEnabled()).isFalse(); assertThat(a.getStatisticsEnabled()).isFalse(); assertThat(a.getIgnoreJTA()).isTrue();
			assertThat(a.isDiskSynchronous()).isFalse(); assertThat(a.getIndexMaintenanceSynchronous()).isFalse(); assertThat(a.getCloningEnabled()).isFalse();
			assertThat(a.isLockGrantor()).isFalse(); assertThat(a.getMulticastEnabled()).isFalse(); assertThat(a.getEnableSubscriptionConflation()).isFalse();
			assertThat(a.getEarlyAck()).isFalse(); assertThat(a.getPublisher()).isFalse(); assertThat(a.getPoolName()).isNull(); assertThat(a.getDiskStoreName()).isNull();
			assertThat(a.getGatewaySenderIds()).isEmpty(); assertThat(a.getAsyncEventQueueIds()).isEmpty();
			r.put("one", 1); assertThat(r.get("one")).isEqualTo(1);
		});
	}
	@Test @SuppressWarnings({"rawtypes", "unchecked"}) void constraintsAndInvalidSettingsFailWithoutCreatingRegionsOrEntries() {
		connection(CacheConfiguration.Mode.PEER, c -> {
			DriverRegion r = c.createRegion("typed", RegionShortcut.LOCAL, RegionConfiguration.<String, Integer>builder().keyConstraint(String.class).valueConstraint(Integer.class).build());
			assertThatThrownBy(() -> r.put(1, 2)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> r.put("one", "bad")).isInstanceOf(DriverOperationException.class); assertThat(r.size()).isZero();
			assertThatThrownBy(() -> c.createRegion("bad", RegionShortcut.LOCAL, RegionConfiguration.builder().initialCapacity(-1).build())).isInstanceOf(DriverOperationException.class);
			assertThat(c.getRegion("bad")).isNull();
			assertThatThrownBy(() -> c.createRegion("bad", RegionShortcut.LOCAL, (RegionConfiguration<Object, Object>) null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> r.createClientSubregion("wrong", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().build())).isInstanceOf(DriverOperationException.class);
		});
	}
	@Test void configuredCallbacksAndMutatorRetainIdentityAndLifecycle() {
		connection(CacheConfiguration.Mode.PEER, c -> {
			List<String> calls = new ArrayList<>();
			var listener = new DriverCacheListenerAdapter<String, String>() {
				@Override public void afterRegionCreate(DriverRegionEvent<String, String> e) { calls.add("create:" + e.getRegion().name()); }
				@Override public void close() { calls.add("listener-close"); }
			};
			var loader = new DriverCacheLoader<String, String>() {
				@Override public String load(DriverLoaderHelper<String, String> h) { return "loaded"; }
				@Override public void close() { calls.add("loader-close"); }
			};
			var writer = new DriverCacheWriterAdapter<String, String>() {};
			var r = c.createRegion("callbacks", RegionShortcut.LOCAL, RegionConfiguration.<String, String>builder()
				.callbacks(new RegionCallbacks<>(List.of(listener), loader, writer)).build());
			assertThat(calls).containsExactly("create:callbacks");
			var a = r.getAttributes(); var m = r.getAttributesMutator();
			assertThat(a.getCacheListeners()).containsExactly(listener); assertThat(a.getCacheLoader()).isSameAs(loader); assertThat(a.getCacheWriter()).isSameAs(writer);
			assertThat(r.get("key")).isEqualTo("loaded");
			assertThat(m.setCacheLoader(null)).isSameAs(loader); assertThat(a.getCacheLoader()).isNull();
			assertThat(m.setCacheWriter(null)).isSameAs(writer); assertThat(a.getCacheWriter()).isNull();
			m.removeCacheListener(listener); assertThat(a.getCacheListeners()).isEmpty();
			m.addCacheListener(listener); m.initCacheListeners(List.of()); assertThat(a.getCacheListeners()).isEmpty();
			m.removeGatewaySenderId("absent"); m.removeAsyncEventQueueId("absent");
			assertThat(calls).contains("loader-close", "listener-close");
		});
	}
	@Test void configuredCreationPreservesDuplicateListenerRegistrations() {
		connection(CacheConfiguration.Mode.PEER, c -> {
			List<String> calls = new ArrayList<>();
			var listener = new DriverCacheListenerAdapter<String, String>() {
				@Override public void afterRegionCreate(DriverRegionEvent<String, String> event) { calls.add("created"); }
			};
			var r = c.createRegion("duplicates", RegionShortcut.LOCAL, RegionConfiguration.<String, String>builder()
				.callbacks(new RegionCallbacks<>(List.of(listener, listener), null, null)).build());
			assertThat(r.getAttributes().getCacheListeners()).containsExactly(listener, listener);
			assertThat(calls).containsExactly("created", "created");
		});
	}

	@Test void localHierarchyTraversalInvalidationAndDestructionRemainConsistent() {
		connection(CacheConfiguration.Mode.PEER, c -> {
			var config = RegionConfiguration.<String, String>builder().build();
			var root = c.createRegion("root", RegionShortcut.LOCAL, config);
			var child = root.createSubregion("child", RegionShortcut.LOCAL, config);
			var leaf = child.createSubregion("leaf", RegionShortcut.LOCAL, config);
			root.put("root", "r"); child.put("child", "c"); leaf.put("leaf", "l");
			assertThat(c.rootRegions()).containsExactly(root); assertThat(root.subregions(false)).containsExactly(child);
			assertThat(root.subregions(true)).containsExactlyInAnyOrder(child, leaf); assertThat(root.getSubregion("child/leaf")).isEqualTo(leaf);
			var entries = root.entrySet(true); assertThat(entries).hasSize(3);
			leaf.put("extra", "e"); assertThat(entries).hasSize(4);
			assertThatThrownBy(() -> root.createSubregion("child", RegionShortcut.LOCAL, config)).isInstanceOfSatisfying(DriverRegionExistsException.class, e -> assertThat(e.getRegion()).isEqualTo(child));
			root.localInvalidateRegion(); assertThat(child.containsValueForKey("child")).isFalse(); assertThat(leaf.containsValueForKey("leaf")).isFalse();
			child.localDestroyRegion(); assertThat(leaf.isDestroyed()).isTrue(); assertThat(root.subregions(true)).isEmpty();
			assertThat(c.getRegion("root/child")).isNull(); assertThat(c.getRegion("root/child/leaf")).isNull(); assertThat(root.isDestroyed()).isFalse();
			root.closeRegion(); assertThat(c.rootRegions()).isEmpty();
		});
	}
	@Test void clientRejectsPeerOnlyConfigurationBeforeRegionCreation() {
		connection(CacheConfiguration.Mode.CLIENT, c -> {
			assertThatThrownBy(() -> c.createClientRegion("invalid", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().scope(Scope.LOCAL).build()))
				.isInstanceOfSatisfying(UnsupportedFeatureException.class, e -> assertThat(e.feature()).isEqualTo("ClientRegionConfiguration.scope"));
			assertThat(c.getRegion("invalid")).isNull();
		});
	}
}
