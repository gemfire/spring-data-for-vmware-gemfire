/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;

/** Region operations exercised against both official providers. */
public abstract class RegionOperationsContract extends RegionConfigurationContract {
	protected abstract GemFireDriver driver();
	protected abstract CacheConfiguration configuration();
	private void region(Consumer<DriverRegion<String, String>> action) {
		try (var c = connect()) {
			DriverRegion<String, String> r = c.createClientRegion("operations", ClientRegionShortcut.LOCAL);
			r.put("key", "one"); action.accept(r);
		}
	}
	protected Map<Method, Probe> regionOperationProbes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverRegion.class.getMethod("create" , Object.class, Object.class), new Probe("Region.create", () -> region(r -> { r.create("new", null); assertThat(r.containsKey("new")).isTrue(); assertThat(r.containsValueForKey("new")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("create" , Object.class, Object.class, Object.class), new Probe("Region.create", () -> region(r -> { r.create("new", "two", "arg"); assertThat(r.get("new")).isEqualTo("two"); })));
		probes.put(DriverRegion.class.getMethod("destroy" , Object.class), new Probe("Region.destroy", () -> region(r -> { assertThat(r.destroy("key")).isEqualTo("one"); assertThat(r.containsKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("invalidate" , Object.class), new Probe("Region.invalidate", () -> region(r -> { r.invalidate("key"); assertThat(r.get("key")).isNull(); assertThat(r.containsKey("key")).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("destroyRegion"), new Probe("Region.destroyRegion", () -> region(r -> { r.destroyRegion(); assertThat(r.isDestroyed()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("invalidateRegion"), new Probe("Region.invalidateRegion", () -> region(r -> { r.invalidateRegion(); assertThat(r.get("key")).isNull(); })));
		probes.put(DriverRegion.class.getMethod("closeRegion"), new Probe("Region.closeRegion", () -> region(r -> { r.closeRegion(); assertThat(r.isDestroyed()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("containsValue" , Object.class), new Probe("Region.containsValue", () -> region(r -> { assertThat(r.containsValue("one")).isTrue(); assertThat(r.containsValue("absent")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("containsValueForKey" , Object.class), new Probe("Region.containsValueForKey", () -> region(r -> { assertThat(r.containsValueForKey("key")).isTrue(); r.invalidate("key"); assertThat(r.containsValueForKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("isEmpty"), new Probe("Region.isEmpty", () -> region(r -> { assertThat(r.isEmpty()).isFalse(); r.clear(); assertThat(r.isEmpty()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("sizeLong"), new Probe("Region.sizeLong", () -> region(r -> { assertThat(r.sizeLong()).isEqualTo(1L); })));
		probes.put(DriverRegion.class.getMethod("replace" , Object.class, Object.class), new Probe("Region.replace", () -> region(r -> { assertThat(r.replace("key", "two")).isEqualTo("one"); assertThat(r.replace("absent", "v")).isNull(); })));
		probes.put(DriverRegion.class.getMethod("replace" , Object.class, Object.class, Object.class), new Probe("Region.replace", () -> region(r -> { assertThat(r.replace("key", "bad", "two")).isFalse(); assertThat(r.replace("key", "one", "two")).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("remove" , Object.class, Object.class), new Probe("Region.remove", () -> region(r -> { assertThat(r.remove("key", "bad")).isFalse(); assertThat(r.remove("key", "one")).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("getOrDefault" , Object.class, Object.class), new Probe("Region.getOrDefault", () -> region(r -> { assertThat(r.getOrDefault("absent", "fallback")).isEqualTo("fallback"); assertThat(r.getOrDefault("key", "fallback")).isEqualTo("one"); })));
		probes.put(DriverRegion.class.getMethod("compute" , Object.class, java.util.function.BiFunction.class), new Probe("Region.compute", () -> region(r -> { assertThat(r.compute("key", (k,v) -> v + "!")).isEqualTo("one!"); assertThat(r.compute("key", (k,v) -> null)).isNull(); assertThat(r.containsKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("computeIfAbsent" , Object.class, java.util.function.Function.class), new Probe("Region.computeIfAbsent", () -> region(r -> { assertThat(r.computeIfAbsent("new", k -> "two")).isEqualTo("two"); assertThat(r.computeIfAbsent("key", k -> "bad")).isEqualTo("one"); })));
		probes.put(DriverRegion.class.getMethod("computeIfPresent" , Object.class, java.util.function.BiFunction.class), new Probe("Region.computeIfPresent", () -> region(r -> { assertThat(r.computeIfPresent("key", (k,v) -> v + "!")).isEqualTo("one!"); assertThat(r.computeIfPresent("absent", (k,v) -> "bad")).isNull(); })));
		probes.put(DriverRegion.class.getMethod("merge" , Object.class, Object.class, java.util.function.BiFunction.class), new Probe("Region.merge", () -> region(r -> { assertThat(r.merge("key", "!", (a,b) -> a + b)).isEqualTo("one!"); assertThat(r.merge("new", "two", (a,b) -> "bad")).isEqualTo("two"); })));
		probes.put(DriverRegion.class.getMethod("forEach" , java.util.function.BiConsumer.class), new Probe("Region.forEach", () -> region(r -> { Map<String, String> seen = new HashMap<>(); r.forEach(seen::put); assertThat(seen).containsExactlyEntriesOf(Map.of("key", "one")); })));
		probes.put(DriverRegion.class.getMethod("replaceAll" , java.util.function.BiFunction.class), new Probe("Region.replaceAll", () -> region(r -> { r.replaceAll((k,v) -> v + "!"); assertThat(r.get("key")).isEqualTo("one!"); })));
		probes.put(DriverRegion.class.getMethod("putAll" , java.util.Map.class), new Probe("Region.putAll", () -> region(r -> { r.putAll(Map.of("key", "two", "new", "three")); assertThat(r.size()).isEqualTo(2); assertThat(r.get("key")).isEqualTo("two"); })));
		probes.put(DriverRegion.class.getMethod("putAll" , java.util.Map.class, Object.class), new Probe("Region.putAll", () -> region(r -> { r.putAll(Map.of("key", "two", "new", "three"), "arg"); assertThat(r.get("new")).isEqualTo("three"); })));
		probes.put(DriverRegion.class.getMethod("getAll" , java.util.Collection.class), new Probe("Region.getAll", () -> region(r -> { assertThat(r.getAll(List.of("key", "absent"))).containsEntry("key", "one").containsEntry("absent", null); })));
		probes.put(DriverRegion.class.getMethod("getAll" , java.util.Collection.class, Object.class), new Probe("Region.getAll", () -> region(r -> { assertThat(r.getAll(List.of("key", "absent"), "arg")).containsEntry("key", "one").containsEntry("absent", null); })));
		probes.put(DriverRegion.class.getMethod("removeAll" , java.util.Collection.class), new Probe("Region.removeAll", () -> region(r -> { r.removeAll(List.of("key", "absent")); assertThat(r.isEmpty()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("removeAll" , java.util.Collection.class, Object.class), new Probe("Region.removeAll", () -> region(r -> { r.removeAll(List.of("key", "absent"), "arg"); assertThat(r.isEmpty()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("localClear"), new Probe("Region.localClear", () -> region(r -> { r.localClear(); assertThat(r.isEmpty()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("localDestroy" , Object.class), new Probe("Region.localDestroy", () -> region(r -> { r.localDestroy("key"); assertThat(r.isEmpty()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("localDestroy" , Object.class, Object.class), new Probe("Region.localDestroy", () -> region(r -> { r.localDestroy("key", "arg"); assertThat(r.isEmpty()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("localInvalidate" , Object.class), new Probe("Region.localInvalidate", () -> region(r -> { r.localInvalidate("key"); assertThat(r.containsValueForKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("localInvalidate" , Object.class, Object.class), new Probe("Region.localInvalidate", () -> region(r -> { r.localInvalidate("key", "arg"); assertThat(r.containsValueForKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("localDestroyRegion"), new Probe("Region.localDestroyRegion", () -> region(r -> { r.localDestroyRegion(); assertThat(r.isDestroyed()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("localDestroyRegion" , Object.class), new Probe("Region.localDestroyRegion", () -> region(r -> { r.localDestroyRegion("arg"); assertThat(r.isDestroyed()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("localInvalidateRegion"), new Probe("Region.localInvalidateRegion", () -> region(r -> { r.localInvalidateRegion(); assertThat(r.containsValueForKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("localInvalidateRegion" , Object.class), new Probe("Region.localInvalidateRegion", () -> region(r -> { r.localInvalidateRegion("arg"); assertThat(r.containsValueForKey("key")).isFalse(); })));
		probes.put(DriverRegion.class.getMethod("getFullPath"), new Probe("Region.getFullPath", () -> region(r -> { assertThat(r.getFullPath()).isEqualTo("/operations"); })));
		probes.put(DriverRegion.class.getMethod("getParentRegion"), new Probe("Region.getParentRegion", () -> region(r -> { assertThat(r.getParentRegion()).isNull(); })));
		probes.put(DriverRegion.class.getMethod("getSubregion" , String.class), new Probe("Region.getSubregion", () -> region(r -> { assertThat(r.getSubregion("missing")).isNull(); assertThat(r.getSubregion("")).isEqualTo(r); })));
		probes.put(DriverRegion.class.getMethod("subregions" , boolean.class), new Probe("Region.subregions", () -> region(r -> { assertThat(r.subregions(true)).isEmpty(); })));
		probes.put(DriverRegion.class.getMethod("getEntry" , Object.class), new Probe("Region.getEntry", () -> region(r -> { assertThat(r.getEntry("key").getValue()).isEqualTo("one"); assertThat(r.getEntry("missing")).isNull(); })));
		probes.put(DriverRegion.class.getMethod("keySet"), new Probe("Region.keySet", () -> region(r -> { var view = r.keySet(); r.put("new", "two"); assertThat(view).containsExactlyInAnyOrder("key", "new"); })));
		probes.put(DriverRegion.class.getMethod("values"), new Probe("Region.values", () -> region(r -> { var view = r.values(); r.create("invalid", null); assertThat(view).containsExactly("one"); })));
		probes.put(DriverRegion.class.getMethod("entrySet"), new Probe("Region.entrySet", () -> region(r -> { assertThat(r.entrySet()).hasSize(1); assertThat(r.entrySet().iterator().next().getKey()).isEqualTo("key"); })));
		probes.put(DriverRegion.class.getMethod("entrySet" , boolean.class), new Probe("Region.entrySet", () -> region(r -> { assertThat(r.entrySet(true)).hasSize(1); assertThat(r.entrySet(true).iterator().next().getRegion()).isEqualTo(r); })));
		probes.put(DriverRegion.class.getMethod("getRegionService"), new Probe("Region.getRegionService", () -> region(r -> { assertThat(r.getRegionService().getRegion("operations")).isEqualTo(r); assertThat(r.getRegionService().rootRegions()).contains(r); assertThat(r.getRegionService().isClosed()).isFalse(); r.getRegionService().close(); assertThat(r.getRegionService().isClosed()).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("getCache"), new Probe("Region.getCache", () -> region(r -> { assertThat(r.getCache().getRegion("operations")).isEqualTo(r); })));
		probes.put(DriverRegion.class.getMethod("getUserAttribute"), new Probe("Region.getUserAttribute", () -> region(r -> { assertThat(r.getUserAttribute()).isNull(); })));
		probes.put(DriverRegion.class.getMethod("setUserAttribute" , Object.class), new Probe("Region.setUserAttribute", () -> region(r -> { r.setUserAttribute("marker"); assertThat(r.getUserAttribute()).isEqualTo("marker"); })));
		probes.put(DriverRegion.class.getMethod("getStatistics"), new Probe("Region.getStatistics", () -> region(r -> { assertThatThrownBy(r::getStatistics).isInstanceOf(DriverOperationException.class); })));
		probes.put(DriverRegion.class.getMethod("metadata"), new Probe("Region.metadata", () -> region(r -> { assertThat(r.metadata().dataPolicy()).isEqualTo(com.vmware.gemfire.driver.spi.config.DataPolicy.NORMAL); assertThat(r.metadata().poolName()).isNull(); })));
		probes.put(DriverRegion.class.getMethod("containsKeyOnServer" , Object.class), new Probe("Region.containsKeyOnServer", () -> region(r -> { try { r.containsKeyOnServer("key"); fail("Local regions have no server"); } catch (DriverOperationException expected) { assertThat(expected.kind()).isEqualTo(DriverFailureKind.UNKNOWN); } })));
		probes.put(DriverRegion.class.getMethod("keySetOnServer"), new Probe("Region.keySetOnServer", () -> region(r -> { try { r.keySetOnServer(); fail("Local regions have no server"); } catch (DriverOperationException expected) { assertThat(expected.kind()).isEqualTo(DriverFailureKind.UNKNOWN); } })));
		probes.put(DriverRegion.class.getMethod("sizeOnServer"), new Probe("Region.sizeOnServer", () -> region(r -> { try { r.sizeOnServer(); fail("Local regions have no server"); } catch (DriverOperationException expected) { assertThat(expected.kind()).isEqualTo(DriverFailureKind.UNKNOWN); } })));
		probes.put(DriverRegion.class.getMethod("sizeLongOnServer"), new Probe("Region.sizeLongOnServer", () -> region(r -> { try { r.sizeLongOnServer(); fail("Local regions have no server"); } catch (DriverOperationException expected) { assertThat(expected.kind()).isEqualTo(DriverFailureKind.UNKNOWN); } })));
		probes.put(DriverRegion.class.getMethod("isEmptyOnServer"), new Probe("Region.isEmptyOnServer", () -> region(r -> { try { r.isEmptyOnServer(); fail("Local regions have no server"); } catch (DriverOperationException expected) { assertThat(expected.kind()).isEqualTo(DriverFailureKind.UNKNOWN); } })));
		return probes;
	}
	@Test void conditionalUpdatesAndEntryFailuresPreservePayloads() {
		region(r -> {
			assertThatThrownBy(() -> r.create("key", "two"))
				.isInstanceOfSatisfying(DriverEntryExistsException.class, ex -> {
					assertThat(ex.getOldValue()).isEqualTo("one"); ex.setOldValue("updated");
					assertThat(ex.getOldValue()).isEqualTo("updated"); assertThat(ex.getMessage()).doesNotContain("one", "key");
				});
			assertThatThrownBy(() -> r.getCache().createClientRegion("operations", ClientRegionShortcut.LOCAL))
				.isInstanceOfSatisfying(DriverRegionExistsException.class, ex -> assertThat(ex.getRegion()).isEqualTo(r));
			r.invalidate("key");
			assertThat(r.replace("key", null, "two")).isTrue();
			assertThat(r.remove("key", "bad")).isFalse();
			assertThat(r.remove("key", "two")).isTrue();
			r.destroyRegion();
			assertThatThrownBy(r::size).isInstanceOfSatisfying(DriverRegionUnavailableException.class,
				ex -> assertThat(ex.getRegionFullPath()).isEqualTo("/operations"));
		});
	}
	@Test void entryValuesFollowTheKeyAndDestroyedObservationIsSticky() {
		region(r -> {
			var entry = r.getEntry("key"); assertThat(entry.isLocal()).isTrue(); assertThat(entry.isDestroyed()).isFalse();
			assertThat(entry.getRegion()).isEqualTo(r);
			assertThat(entry.setUserAttribute("tag")).isNull(); assertThat(entry.getUserAttribute()).isEqualTo("tag");
			assertThat(entry.setValue("two")).isEqualTo("one"); assertThat(r.get("key")).isEqualTo("two");
			r.put("key", "three"); assertThat(entry.getValue()).isEqualTo("three");
			assertThat(r.getEntry("key")).isEqualTo(entry).hasSameHashCodeAs(entry);
			r.destroy("key"); assertThat(entry.isDestroyed()).isTrue();
			assertThatThrownBy(entry::getValue).isInstanceOf(DriverOperationException.class);
			r.create("key", "four"); assertThat(entry.isDestroyed()).isTrue();
			assertThat(entry.getValue()).isEqualTo("four");
			assertThat(entry.getKey()).isEqualTo("key"); assertThat(entry).isEqualTo(r.getEntry("key"));
			var unobserved = r.getEntry("key"); r.destroy("key"); r.create("key", "five");
			assertThat(unobserved.isDestroyed()).isFalse();
			r.setCacheLoader(new DriverCacheLoader<>() {
				@Override public String load(DriverLoaderHelper<String, String> helper) { return "loaded"; }
				@Override public void close() { }
			});
			r.invalidate("key"); assertThat(unobserved.getValue()).isNull(); // Entry access never invokes a loader.

		});
	}
	@Test void liveViewsRetainDuplicatesAndRejectMutationsOnlyWhenNeeded() {
		region(r -> {
			var keys = r.keySet(); var values = r.values(); var entries = r.entrySet();
			r.put("duplicate", "one"); r.create("invalid", null);
			assertThat(values).containsExactly("one", "one"); assertThat(keys).hasSize(3); assertThat(entries).hasSize(3);
			assertThat(keys.remove("absent")).isFalse();
			assertThat(keys.removeAll(List.of("absent"))).isFalse();
			assertThatThrownBy(() -> keys.remove("key")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> values.add("new")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(entries::clear).isInstanceOf(DriverOperationException.class);
			r.clear(); keys.clear(); assertThat(keys).isEmpty();
		});
	}
	@Test void mapFunctionFailuresAreTypedAndLeaveEntriesUnchanged() {
		region(r -> {
			assertThatThrownBy(() -> r.compute("key", (k, v) -> { throw new IllegalArgumentException("private-value"); }))
				.isInstanceOfSatisfying(DriverOperationException.class, ex -> {
					assertThat(ex.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT);
					assertThat(ex.getMessage()).doesNotContain("private-value");
				});
			assertThat(r.get("key")).isEqualTo("one");
			assertThatThrownBy(() -> r.computeIfAbsent("key", null)).isInstanceOf(DriverOperationException.class);
		});
	}
	@Test void bulkAndLocalOperationsPreserveEventKindsArgumentsAndWriterRules() {
		region(r -> {
			List<String> events = new ArrayList<>(); List<DriverOperation> writer = new ArrayList<>();
			r.addCacheListener(new DriverCacheListenerAdapter<>() {
				private void record(DriverCacheEvent<String, String> e) { events.add(e.getOperation() + ":" + e.getCallbackArgument()); }
				@Override public void afterCreate(DriverEntryEvent<String, String> e) { record(e); }
				@Override public void afterUpdate(DriverEntryEvent<String, String> e) { record(e); }
				@Override public void afterDestroy(DriverEntryEvent<String, String> e) { record(e); }
				@Override public void afterInvalidate(DriverEntryEvent<String, String> e) { record(e); }
				@Override public void afterRegionClear(DriverRegionEvent<String, String> e) { record(e); }
			});
			r.setCacheWriter(new DriverCacheWriterAdapter<>() {
				@Override public void beforeCreate(DriverEntryEvent<String, String> e) { writer.add(e.getOperation()); }
				@Override public void beforeUpdate(DriverEntryEvent<String, String> e) { writer.add(e.getOperation()); }
				@Override public void beforeDestroy(DriverEntryEvent<String, String> e) { writer.add(e.getOperation()); }
				@Override public void beforeRegionClear(DriverRegionEvent<String, String> e) { writer.add(e.getOperation()); }
			});
			r.putAll(Map.of("key", "two", "new", "three"), "bulk");
			assertThat(events).containsExactlyInAnyOrder("PUTALL_UPDATE:bulk", "PUTALL_CREATE:bulk");
			assertThat(writer).containsExactlyInAnyOrder(DriverOperation.PUTALL_UPDATE, DriverOperation.PUTALL_CREATE);
			events.clear(); writer.clear();
			r.removeAll(List.of("new", "absent"), "remove"); assertThat(events).containsExactly("REMOVEALL_DESTROY:remove");
			events.clear(); writer.clear();
			r.localInvalidate("key", "invalidate"); r.localDestroy("key", "destroy"); r.localClear();
			assertThat(events).containsExactly("LOCAL_INVALIDATE:invalidate", "LOCAL_DESTROY:destroy", "REGION_LOCAL_CLEAR:null");
			assertThat(writer).isEmpty();
		});
	}
}
