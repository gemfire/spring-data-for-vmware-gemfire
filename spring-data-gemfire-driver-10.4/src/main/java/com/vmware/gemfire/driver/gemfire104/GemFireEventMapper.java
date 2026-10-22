/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Supplier;
import org.apache.geode.cache.*;
import org.apache.geode.cache.util.TimestampedEntryEvent;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.Role;
import com.vmware.gemfire.driver.spi.*;

/** Lazy views: native off-heap values are not read unless the application requests them. */
final class GemFireEventMapper {
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	<K, V> DriverCacheEvent<K, V> cacheEvent(CacheEvent<K, V> event) {
		if (event instanceof EntryEvent<K, V> entry) return entry(entry);
		if (event instanceof RegionEvent<K, V> region) return region(region);
		return new Event<>(event);
	}

	<K, V> DriverEntryEvent<K, V> entry(EntryEvent<K, V> event) {
		return event instanceof TimestampedEntryEvent timestamped ? new Timestamped<>(timestamped) : new Entry<>(event);
	}
	<K, V> DriverRegionEvent<K, V> region(RegionEvent<K, V> event) { return new RegionView<>(event); }
	<K, V> DriverRoleEvent<K, V> role(RoleEvent<K, V> event) { return new RoleView<>(event); }
	DriverMember member(DistributedMember member) { return member == null ? null : new Member(member); }
	private <T> T read(String name, Supplier<T> read) {
		try { return read.get(); }
		catch (RuntimeException failure) { throw exceptions.translate("Event." + name, failure); }
	}
	private class Event<K, V> implements DriverCacheEvent<K, V> {
		private final CacheEvent<K, V> event;
		Event(CacheEvent<K, V> event) { this.event = event; }
		@Override public DriverRegion<K, V> getRegion() { return read("getRegion", () -> new GemFireRegion<>(event.getRegion())); }
		@Override public DriverOperation getOperation() { return read("getOperation", () -> DriverOperation.fromOrdinal(event.getOperation().ordinal)); }
		@Override public Object getCallbackArgument() { return read("getCallbackArgument", () -> event.getCallbackArgument()); }
		@Override public boolean isCallbackArgumentAvailable() { return read("isCallbackArgumentAvailable", () -> event.isCallbackArgumentAvailable()); }
		@Override public boolean isOriginRemote() { return read("isOriginRemote", () -> event.isOriginRemote()); }
		@Override public DriverMember getDistributedMember() { return read("getDistributedMember", () -> member(event.getDistributedMember())); }
	}
	private class Entry<K, V> extends Event<K, V> implements DriverEntryEvent<K, V> {
		private final EntryEvent<K, V> event;
		Entry(EntryEvent<K, V> event) { super(event); this.event = event; }
		@Override public K getKey() { return read("getKey", () -> event.getKey()); }
		@Override public V getOldValue() { return read("getOldValue", () -> event.getOldValue()); }
		@Override public V getNewValue() { return read("getNewValue", () -> event.getNewValue()); }
		@Override public boolean hasClientOrigin() { return read("hasClientOrigin", () -> event.hasClientOrigin()); }
		@Override public boolean isOldValueAvailable() { return read("isOldValueAvailable", () -> event.isOldValueAvailable()); }
		@Override public DriverTransactionId getTransactionId() { return read("getTransactionId", () -> {
				var id = event.getTransactionId(); return id == null ? null : new GemFireTransactionId(id);
			}); }
		@Override public DriverSerializedCacheValue<V> getSerializedOldValue() { return read("getSerializedOldValue", () -> serialized(event.getSerializedOldValue())); }
		@Override public DriverSerializedCacheValue<V> getSerializedNewValue() { return read("getSerializedNewValue", () -> serialized(event.getSerializedNewValue())); }
	}
	private <V> DriverSerializedCacheValue<V> serialized(SerializedCacheValue<V> value) {
		if (value == null) return null;
		return new DriverSerializedCacheValue<>() {
			@Override public byte[] getSerializedValue() { return read("getSerializedValue", value::getSerializedValue); }
			@Override public V getDeserializedValue() { return read("getDeserializedValue", value::getDeserializedValue); }
		};
	}
	private class RegionView<K, V> extends Event<K, V> implements DriverRegionEvent<K, V> {
		private final RegionEvent<K, V> event;
		RegionView(RegionEvent<K, V> event) { super(event); this.event = event; }
		@Override public boolean isReinitializing() { return read("isReinitializing", event::isReinitializing); }
	}
	private final class RoleView<K, V> extends RegionView<K, V> implements DriverRoleEvent<K, V> {
		private final RoleEvent<K, V> event;
		RoleView(RoleEvent<K, V> event) { super(event); this.event = event; }
		@Override public Set<String> getRequiredRoles() {
			return read("getRequiredRoles", () -> {
				// 10.4 declares Set<String>, but actual RoleEventImpl carries Role objects.
				Set<?> roles = event.getRequiredRoles();
				return roles.stream().map(value -> value instanceof Role role ? role.getName() : (String) value)
					.collect(Collectors.toSet());
			});
		}
	}
	private final class Timestamped<K, V> extends Entry<K, V> implements DriverTimestampedEntryEvent<K, V> {
		private final TimestampedEntryEvent event;
		@SuppressWarnings("unchecked")
		Timestamped(TimestampedEntryEvent event) { super(event); this.event = event; }
		@Override public int getNewDistributedSystemID() { return read("getNewDistributedSystemID", event::getNewDistributedSystemID); }
		@Override public int getOldDistributedSystemID() { return read("getOldDistributedSystemID", event::getOldDistributedSystemID); }
		@Override public long getNewTimestamp() { return read("getNewTimestamp", event::getNewTimestamp); }
		@Override public long getOldTimestamp() { return read("getOldTimestamp", event::getOldTimestamp); }
	}
	private final class Member implements DriverMember {
		private final DistributedMember member;
		Member(DistributedMember member) { this.member = member; }
		@Override public String getId() { return read("Member.getId", member::getId); }
		@Override public String getUniqueId() { return read("Member.getUniqueId", member::getUniqueId); }
		@Override public String getName() { return read("Member.getName", member::getName); }
		@Override public String getHost() { return read("Member.getHost", member::getHost); }
		@Override public int getProcessId() { return read("Member.getProcessId", member::getProcessId); }
		@Override public List<String> getGroups() { return read("Member.getGroups", member::getGroups); }
		@Override public Set<DriverRole> getRoles() {
			return read("Member.getRoles", () -> member.getRoles().stream().map(RoleViewMember::new).collect(Collectors.toSet()));
		}
		@Override public DriverDurableClientAttributes getDurableClientAttributes() {
			return read("Member.getDurableClientAttributes", () -> {
				var attributes = member.getDurableClientAttributes();
				return attributes == null ? null : new DriverDurableClientAttributes(attributes.getId(), attributes.getTimeout());
			});
		}
		@Override public int compareTo(DriverMember other) {
			return read("Member.compareTo", () -> member.compareTo(((Member) other).member));
		}
		@Override public boolean equals(Object other) { return other instanceof Member value && member.equals(value.member); }
		@Override public int hashCode() { return member.hashCode(); }
		@Override public String toString() { return member.toString(); }
	}
	private final class RoleViewMember implements DriverRole {
		private final Role role;
		RoleViewMember(Role role) { this.role = role; }
		@Override public String getName() { return read("Role.getName", role::getName); }
		@Override public boolean isPresent() { return read("Role.isPresent", role::isPresent); }
		@Override public int getCount() { return read("Role.getCount", role::getCount); }
		@Override public int compareTo(DriverRole other) {
			return read("Role.compareTo", () -> role.compareTo(((RoleViewMember) other).role));
		}
		@Override public boolean equals(Object other) { return other instanceof RoleViewMember value && role.equals(value.role); }
		@Override public int hashCode() { return role.hashCode(); }
	}
}
