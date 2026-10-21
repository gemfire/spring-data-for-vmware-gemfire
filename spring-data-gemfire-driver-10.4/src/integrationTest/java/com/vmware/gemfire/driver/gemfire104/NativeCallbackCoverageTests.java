/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.apache.geode.cache.*;
import org.apache.geode.cache.util.TimestampedEntryEvent;
import org.apache.geode.distributed.DistributedMember;
import com.vmware.gemfire.driver.spi.*;

class NativeCallbackCoverageTests {
	private Cache cache() { return new CacheFactory().set("mcast-port", "0").set("locators", "").set("log-level", "error").create(); }

	@Test
	void everyOperationConstantAndPredicateMatchesTheResolvedNativeRelease() throws Exception {
		int count = 0;
		for (Field field : Operation.class.getFields()) {
			if (field.getType() != Operation.class) continue;
			Operation nativeOp = (Operation) field.get(null);
			DriverOperation spi = DriverOperation.valueOf(field.getName());
			count++;
			assertThat(spi.nativeOrdinal()).isEqualTo(nativeOp.ordinal);
			assertThat(DriverOperation.fromOrdinal(nativeOp.ordinal)).isSameAs(spi);
			assertThat(spi.toString()).isEqualTo(nativeOp.toString());
			assertThat(spi.getCorrespondingCreateOp().nativeOrdinal()).isEqualTo(nativeOp.getCorrespondingCreateOp().ordinal);
			assertThat(spi.getCorrespondingUpdateOp().nativeOrdinal()).isEqualTo(nativeOp.getCorrespondingUpdateOp().ordinal);
			for (Method method : Operation.class.getMethods()) {
				if (method.getReturnType() == boolean.class && method.getParameterCount() == 0) {
					assertThat(DriverOperation.class.getMethod(method.getName()).invoke(spi))
						.as("%s.%s", field.getName(), method.getName()).isEqualTo(method.invoke(nativeOp));
				}
			}
		}
		assertThat(count).isEqualTo(DriverOperation.values().length);
		for (int value = Byte.MIN_VALUE; value <= Byte.MAX_VALUE; value++) {
			byte ordinal = (byte) value;
			if (value < 0 || value >= 55) {
				assertThatThrownBy(() -> Operation.fromOrdinal(ordinal)).isInstanceOf(ArrayIndexOutOfBoundsException.class);
				assertThatThrownBy(() -> DriverOperation.fromOrdinal(ordinal)).isInstanceOf(ArrayIndexOutOfBoundsException.class);
			} else if (value >= 50) {
				assertThat(Operation.fromOrdinal(ordinal)).isNull(); assertThat(DriverOperation.fromOrdinal(ordinal)).isNull();
			}
		}
	}

	@Test
	void registeredAdapterForwardsEveryNativeCallbackIncludingInheritedAndSpecializedMethods() throws Exception {
		try (var cache = cache()) {
			Region<String, String> nativeRegion = cache.<String, String>createRegionFactory(RegionShortcut.LOCAL).create("callbacks");
			var calls = new ArrayList<String>();
			@SuppressWarnings("unchecked") DriverCacheListener<String, String> listener = (DriverCacheListener<String, String>) Proxy.newProxyInstance(
				getClass().getClassLoader(), new Class<?>[] { DriverRegionMembershipListener.class, DriverRegionRoleListener.class },
				(proxy, method, arguments) -> {
					if (method.getDeclaringClass() == Object.class) return switch (method.getName()) {
						case "equals" -> proxy == arguments[0]; case "hashCode" -> System.identityHashCode(proxy); default -> "listener";
					};
					calls.add(method.getName());
					if (method.getName().equals("initialize")) {
						assertThat(arguments[0]).isInstanceOf(DriverConnection.class);
						assertThat(((DriverConnection) arguments[0]).getRegion("callbacks").name()).isEqualTo("callbacks");
					} else if (method.getName().equals("initialMembers")) {
						assertThat(arguments[0]).isInstanceOf(DriverRegion.class);
						assertThat((DriverMember[]) arguments[1]).hasSize(1);
					} else if (arguments != null && arguments.length == 1 && arguments[0] instanceof DriverCacheEvent<?, ?> event) {
						assertThat(event.getRegion().name()).isEqualTo("callbacks");
						assertThat(event.getCallbackArgument()).isEqualTo("argument");
						assertThat(event.isCallbackArgumentAvailable()).isFalse();
						assertThat(event.isOriginRemote()).isTrue();
						assertThat(event.getDistributedMember().getId()).isEqualTo(cache.getDistributedSystem().getDistributedMember().getId());
						if (event instanceof DriverRegionEvent<?, ?> regionEvent) assertThat(regionEvent.isReinitializing()).isTrue();
						if (event instanceof DriverRoleEvent<?, ?> roleEvent) assertThat(roleEvent.getRequiredRoles()).containsExactly("required");
					}
					return null;
				});
			new GemFireRegion<>(nativeRegion).addCacheListener(listener);
			var handle = new GemFireRegion<>(nativeRegion);
			handle.addCacheListener(listener);
			assertThat(handle.getCacheListeners()).containsExactly(listener);
			Object registered = new GemFireCacheListenerFactory().applicationAdapter(nativeRegion.getAttributes().getCacheListeners()[0]);
			assertThat(registered).isInstanceOf(RegionMembershipListener.class).isInstanceOf(RegionRoleListener.class);
			Set<String> expected = new HashSet<>();
			for (Class<?> contract : List.of(CacheListener.class, RegionMembershipListener.class, RegionRoleListener.class)) {
				for (Method method : contract.getMethods()) {
					if (!expected.add(method.getName())) continue;
					Object[] arguments = new Object[method.getParameterCount()];
					for (int i = 0; i < arguments.length; i++) {
						Class<?> type = method.getParameterTypes()[i];
						if (CacheEvent.class.isAssignableFrom(type)) arguments[i] = event(type, nativeRegion, cache);
						else if (type == Cache.class) arguments[i] = cache;
						else if (type == Properties.class) arguments[i] = new Properties();
						else if (type == Region.class) arguments[i] = nativeRegion;
						else if (type == DistributedMember[].class) arguments[i] = new DistributedMember[] { cache.getDistributedSystem().getDistributedMember() };
						else throw new AssertionError("New native callback parameter requires a probe: " + type);
					}
					method.invoke(registered, arguments);
				}
			}
			assertThat(calls).containsExactlyInAnyOrderElementsOf(expected);
			assertThat(expected).hasSize(18); // New native methods must be added to the SPI and probe.
			handle.removeCacheListener(listener);
			assertThat(handle.getCacheListeners()).isEmpty();
		}
	}

	private Object event(Class<?> type, Region<?, ?> region, Cache cache) {
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { type }, (proxy, method, arguments) -> switch (method.getName()) {
			case "getRegion" -> region;
			case "getOperation" -> Operation.UPDATE;
			case "getCallbackArgument" -> "argument";
			case "getDistributedMember" -> cache.getDistributedSystem().getDistributedMember();
			case "isCallbackArgumentAvailable" -> false;
			case "isOriginRemote", "isReinitializing" -> true;
			case "getRequiredRoles" -> Set.of("required");
			default -> throw new AssertionError("Unprobed payload " + method);
		});
	}

	@Test
	void serializedAndTimestampedEntryViewsStayLazyAndPreserveNativeAvailabilityAndFailures() {
		try (var cache = cache()) {
			Region<String, String> region = cache.<String, String>createRegionFactory(RegionShortcut.LOCAL).create("entries");
			var reads = new AtomicInteger();
			byte[] bytes = { 1, 2, 3 };
			SerializedCacheValue<String> serialized = new SerializedCacheValue<>() {
				@Override public byte[] getSerializedValue() { reads.incrementAndGet(); return bytes; }
				@Override public String getDeserializedValue() { reads.incrementAndGet(); return "serialized"; }
			};
			TimestampedEntryEvent event = (TimestampedEntryEvent) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { TimestampedEntryEvent.class }, (proxy, method, arguments) -> switch (method.getName()) {
					case "getRegion" -> region;
					case "getOperation" -> Operation.UPDATE;
					case "getKey" -> "key";
					case "getOldValue" -> { reads.incrementAndGet(); yield "old"; }
					case "getNewValue" -> { reads.incrementAndGet(); yield "new"; }
					case "getSerializedOldValue", "getSerializedNewValue" -> serialized;
					case "getOldTimestamp" -> 12L;
					case "getNewTimestamp" -> 34L;
					case "getOldDistributedSystemID" -> 1;
					case "getNewDistributedSystemID" -> 2;
					case "hasClientOrigin" -> true;
					case "isOldValueAvailable" -> false;
					case "getTransactionId" -> null;
					case "getCallbackArgument" -> throw new IllegalStateException("secret");
					default -> throw new AssertionError(method);
				});
			@SuppressWarnings("unchecked") var mapped = (DriverTimestampedEntryEvent<String, String>) new GemFireEventMapper().entry(event);
			assertThat(reads).hasValue(0);
			assertThat(mapped.isOldValueAvailable()).isFalse();
			assertThat(mapped.hasClientOrigin()).isTrue();
			assertThat(mapped.getKey()).isEqualTo("key");
			assertThat(mapped.getNewValue()).isEqualTo("new");
			assertThat(mapped.getOldValue()).isEqualTo("old");
			assertThat(mapped.getSerializedOldValue().getSerializedValue()).isSameAs(bytes);
			assertThat(mapped.getSerializedNewValue().getDeserializedValue()).isEqualTo("serialized");
			assertThat(reads).hasValue(4);
			assertThat(mapped.getNewTimestamp()).isEqualTo(34L);
			assertThat(mapped.getOldTimestamp()).isEqualTo(12L);
			assertThat(mapped.getNewDistributedSystemID()).isEqualTo(2);
			assertThat(mapped.getOldDistributedSystemID()).isEqualTo(1);
			assertThat(mapped.getTransactionId()).isNull();
			assertThatThrownBy(mapped::getCallbackArgument).isInstanceOfSatisfying(DriverOperationException.class,
				e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_STATE)).hasMessage("GemFire operation failed: Event.getCallbackArgument");
		}
	}

	@Test
	void realTransactionEventsRetainIdentityAndMemberMetadata() {
		try (var cache = cache()) {
			Region<String, String> region = cache.<String, String>createRegionFactory(RegionShortcut.LOCAL).create("entries");
			var identities = new ArrayList<DriverTransactionId>();
			var members = new ArrayList<DriverMember>();
			new GemFireRegion<>(region).addCacheListener(new DriverCacheListenerAdapter<>() {
				@Override public void afterCreate(DriverEntryEvent<String, String> event) {
					identities.add(event.getTransactionId()); members.add(event.getDistributedMember());
				}
			});
			cache.getCacheTransactionManager().begin();
			var nativeId = cache.getCacheTransactionManager().getTransactionId();
			region.put("one", "1"); region.put("two", "2");
			cache.getCacheTransactionManager().commit();
			assertThat(identities).hasSize(2).doesNotContainNull();
			assertThat(identities.get(0)).isEqualTo(identities.get(1)).hasSameHashCodeAs(identities.get(1));
			assertThat(identities.get(0).getMemberId().getId()).isEqualTo(nativeId.getMemberId().getId());
			DriverMember member = members.get(0); var nativeMember = cache.getDistributedSystem().getDistributedMember();
			assertThat(member).isEqualTo(members.get(1));
			assertThat(member.compareTo(members.get(1))).isZero();
			assertThat(member.getId()).isEqualTo(nativeMember.getId());
			assertThat(member.getUniqueId()).isEqualTo(nativeMember.getUniqueId());
			assertThat(member.getName()).isEqualTo(nativeMember.getName());
			assertThat(member.getHost()).isEqualTo(nativeMember.getHost());
			assertThat(member.getProcessId()).isEqualTo(nativeMember.getProcessId());
			assertThat(member.getGroups()).isEqualTo(nativeMember.getGroups());
			assertThat(member.getRoles()).isEmpty();
			assertThat(member.getDurableClientAttributes().timeout()).isEqualTo(nativeMember.getDurableClientAttributes().getTimeout());
		}
	}

	@Test
	void registeredWriterAndLoaderForwardBothDeclarableHooksWithoutAutomaticRegistrationInitialization() {
		try (var cache = cache()) {
			var calls = new ArrayList<String>(); var props = new Properties();
			Region<String, String> region = cache.<String, String>createRegionFactory(RegionShortcut.LOCAL).create("entries");
			var handle = new GemFireRegion<>(region);
			handle.setCacheWriter(new DriverCacheWriterAdapter<>() {
				@Override public void init(Properties p) { assertThat(p).isSameAs(props); calls.add("writer.init"); }
				@Override public void initialize(DriverConnection c, Properties p) { assertThat(c.getRegion("entries")).isNotNull(); calls.add("writer.initialize"); }
			});
			handle.setCacheLoader(new DriverCacheLoader<>() {
				@Override public void init(Properties p) { assertThat(p).isSameAs(props); calls.add("loader.init"); }
				@Override public void initialize(DriverConnection c, Properties p) { assertThat(c.getRegion("entries")).isNotNull(); calls.add("loader.initialize"); }
				@Override public String load(DriverLoaderHelper<String, String> h) { return null; }
				@Override public void close() { }
			});
			assertThat(calls).isEmpty();
			for (Declarable callback : List.of(region.getAttributes().getCacheWriter(), region.getAttributes().getCacheLoader())) {
				callback.init(props); callback.initialize(cache, props);
			}
			assertThat(calls).containsExactly("writer.init", "writer.initialize", "loader.init", "loader.initialize");
		}
	}
}
