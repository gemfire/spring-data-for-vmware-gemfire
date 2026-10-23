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
	protected abstract CacheConfiguration configuration();
	protected DriverConnection connect() { return driver().connect(configuration()); }
	private void connection(Consumer<DriverConnection> action) {
		try (var connection = connect()) { action.accept(connection); }
	}
	protected Map<Method, Probe> regionConfigurationProbes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverConnection.class.getMethod("createClientRegion", String.class, ClientRegionShortcut.class, RegionConfiguration.class),
			new Probe("Connection.createConfiguredClientRegion", () -> connection(c -> {
				var r = c.createClientRegion("configured", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().initialCapacity(0).concurrencyChecksEnabled(false).loadFactor(0.5f).build());
				assertThat(r.getAttributes().getLoadFactor()).isEqualTo(0.5f);
				assertThat(r.getAttributes().getInitialCapacity()).isZero(); assertThat(r.getAttributes().getConcurrencyChecksEnabled()).isFalse();
			})));
		probes.put(DriverRegion.class.getMethod("getAttributes"), new Probe("Region.getAttributes", () -> connection(c -> {
			var a = c.createClientRegion("configured", ClientRegionShortcut.LOCAL).getAttributes();
			assertThat(a.getDataPolicy()).isEqualTo(DataPolicy.NORMAL); assertThat(a.getScope()).isEqualTo(Scope.LOCAL);
		})));
		probes.put(DriverRegion.class.getMethod("getAttributesMutator"), new Probe("Region.getAttributesMutator", () -> connection(c -> {
			var r = c.createClientRegion("configured", ClientRegionShortcut.LOCAL); var m = r.getAttributesMutator();
			assertThat(m.getRegion()).isEqualTo(r); m.setCloningEnabled(false); assertThat(m.getCloningEnabled()).isFalse();
		})));
		probes.put(DriverRegion.class.getMethod("createClientSubregion", String.class, ClientRegionShortcut.class, RegionConfiguration.class),
			new Probe("Region.createClientSubregion", () -> connection(c -> {
				var root = c.createClientRegion("configured", ClientRegionShortcut.LOCAL);
				var child = root.createClientSubregion("child", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().build());
				assertThat(c.getRegion("/configured/child")).isEqualTo(child);
				assertThat(child.getParentRegion()).isEqualTo(root); assertThat(c.rootRegions()).containsExactly(root);
			})));
		return probes;
	}
	@Test void configurationPreservesDefaultsAndExplicitOverrides() {
		connection(c -> {
			var defaults = c.createClientRegion("defaults", ClientRegionShortcut.LOCAL).getAttributes();
			var implicit = c.createClientRegion("implicit", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().build()).getAttributes();
			assertThat(implicit.getDataPolicy()).isEqualTo(defaults.getDataPolicy());
			assertThat(implicit.getScope()).isEqualTo(defaults.getScope());
			assertThat(implicit.getInitialCapacity()).isEqualTo(defaults.getInitialCapacity());
			assertThat(implicit.getLoadFactor()).isEqualTo(defaults.getLoadFactor());
			assertThat(implicit.getConcurrencyLevel()).isEqualTo(defaults.getConcurrencyLevel());
			assertThat(implicit.getConcurrencyChecksEnabled()).isEqualTo(defaults.getConcurrencyChecksEnabled());
			var r = c.createClientRegion("configured", ClientRegionShortcut.LOCAL, RegionConfiguration.<String, Integer>builder()
				.keyConstraint(String.class).valueConstraint(Integer.class)
				.initialCapacity(0).loadFactor(0.5f).concurrencyLevel(3).concurrencyChecksEnabled(false)
				.statisticsEnabled(false).diskSynchronous(false)
				.cloningEnabled(false)
				.poolName(null).diskStoreName(null).build());
			var a = r.getAttributes();
			assertThat(a.getKeyConstraint()).isEqualTo(String.class); assertThat(a.getValueConstraint()).isEqualTo(Integer.class);
			assertThat(a.getInitialCapacity()).isZero(); assertThat(a.getLoadFactor()).isEqualTo(0.5f); assertThat(a.getConcurrencyLevel()).isEqualTo(3);
			assertThat(a.getConcurrencyChecksEnabled()).isFalse(); assertThat(a.getStatisticsEnabled()).isFalse(); assertThat(a.getIgnoreJTA()).isEqualTo(defaults.getIgnoreJTA());
			assertThat(a.isDiskSynchronous()).isFalse(); assertThat(a.getIndexMaintenanceSynchronous()).isEqualTo(defaults.getIndexMaintenanceSynchronous()); assertThat(a.getCloningEnabled()).isFalse();
			assertThat(a.getPoolName()).isNull(); assertThat(a.getDiskStoreName()).isNull();
			r.put("one", 1); assertThat(r.get("one")).isEqualTo(1);
		});
	}
	@Test @SuppressWarnings({"rawtypes", "unchecked"}) void constraintsAndInvalidSettingsFailWithoutCreatingRegionsOrEntries() {
		connection(c -> {
			DriverRegion r = c.createClientRegion("typed", ClientRegionShortcut.LOCAL, RegionConfiguration.<String, Integer>builder().keyConstraint(String.class).valueConstraint(Integer.class).build());
			assertThatThrownBy(() -> r.put(1, 2)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> r.put("one", "bad")).isInstanceOf(DriverOperationException.class); assertThat(r.size()).isZero();
			assertThatThrownBy(() -> c.createClientRegion("bad", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().initialCapacity(-1).build())).isInstanceOf(DriverOperationException.class);
			assertThat(c.getRegion("bad")).isNull();
			assertThatThrownBy(() -> c.createClientRegion("bad", ClientRegionShortcut.LOCAL, (RegionConfiguration<Object, Object>) null)).isInstanceOf(DriverOperationException.class);
		});
	}
	@Test void configuredCallbacksAndMutatorRetainIdentityAndLifecycle() {
		connection(c -> {
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
			var r = c.createClientRegion("callbacks", ClientRegionShortcut.LOCAL, RegionConfiguration.<String, String>builder()
				.callbacks(new RegionCallbacks<>(List.of(listener), null, null)).build());
			r.setCacheLoader(loader); r.setCacheWriter(writer);
			assertThat(calls).containsExactly("create:callbacks");
			var a = r.getAttributes(); var m = r.getAttributesMutator();
			assertThat(a.getCacheListeners()).containsExactly(listener); assertThat(a.getCacheLoader()).isSameAs(loader); assertThat(a.getCacheWriter()).isSameAs(writer);
			assertThat(r.get("key")).isEqualTo("loaded");
			assertThat(m.setCacheLoader(null)).isSameAs(loader); assertThat(a.getCacheLoader()).isNull();
			assertThat(m.setCacheWriter(null)).isSameAs(writer); assertThat(a.getCacheWriter()).isNull();
			m.removeCacheListener(listener); assertThat(a.getCacheListeners()).isEmpty();
			m.addCacheListener(listener); m.initCacheListeners(List.of()); assertThat(a.getCacheListeners()).isEmpty();
			assertThat(calls).contains("loader-close", "listener-close");
		});
	}
	@Test void configuredCreationPreservesDuplicateListenerRegistrations() {
		connection(c -> {
			List<String> calls = new ArrayList<>();
			var listener = new DriverCacheListenerAdapter<String, String>() {
				@Override public void afterRegionCreate(DriverRegionEvent<String, String> event) { calls.add("created"); }
			};
			var r = c.createClientRegion("duplicates", ClientRegionShortcut.LOCAL, RegionConfiguration.<String, String>builder()
				.callbacks(new RegionCallbacks<>(List.of(listener, listener), null, null)).build());
			assertThat(r.getAttributes().getCacheListeners()).containsExactly(listener, listener);
			assertThat(calls).containsExactly("created", "created");
		});
	}

	@Test void localHierarchyTraversalInvalidationAndDestructionRemainConsistent() {
		connection(c -> {
			var config = RegionConfiguration.<String, String>builder().build();
			var root = c.createClientRegion("root", ClientRegionShortcut.LOCAL, config);
			var child = root.createClientSubregion("child", ClientRegionShortcut.LOCAL, config);
			var leaf = child.createClientSubregion("leaf", ClientRegionShortcut.LOCAL, config);
			root.put("root", "r"); child.put("child", "c"); leaf.put("leaf", "l");
			assertThat(c.rootRegions()).containsExactly(root); assertThat(root.subregions(false)).containsExactly(child);
			assertThat(root.subregions(true)).containsExactlyInAnyOrder(child, leaf); assertThat(root.getSubregion("child/leaf")).isEqualTo(leaf);
			var entries = root.entrySet(true); assertThat(entries).hasSize(3);
			leaf.put("extra", "e"); assertThat(entries).hasSize(4);
			assertThatThrownBy(() -> root.createClientSubregion("child", ClientRegionShortcut.LOCAL, config)).isInstanceOfSatisfying(DriverRegionExistsException.class, e -> assertThat(e.getRegion()).isEqualTo(child));
			root.localInvalidateRegion(); assertThat(child.containsValueForKey("child")).isFalse(); assertThat(leaf.containsValueForKey("leaf")).isFalse();
			child.localDestroyRegion(); assertThat(leaf.isDestroyed()).isTrue(); assertThat(root.subregions(true)).isEmpty();
			assertThat(c.getRegion("root/child")).isNull(); assertThat(c.getRegion("root/child/leaf")).isNull(); assertThat(root.isDestroyed()).isFalse();
			root.closeRegion(); assertThat(c.rootRegions()).isEmpty();
		});
	}
}
