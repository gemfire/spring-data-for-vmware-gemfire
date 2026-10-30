/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.List;
import java.util.function.Supplier;
import org.apache.geode.cache.*;
import org.apache.geode.distributed.DistributedMember;
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
		return new Entry<>(event);
	}
	<K, V> DriverRegionEvent<K, V> region(RegionEvent<K, V> event) { return new RegionView<>(event); }
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
	private final class Member implements DriverMember {
		private final DistributedMember member;
		Member(DistributedMember member) { this.member = member; }
		@Override public String getId() { return read("Member.getId", member::getId); }
		@Override public String getUniqueId() { return read("Member.getUniqueId", member::getUniqueId); }
		@Override public String getName() { return read("Member.getName", member::getName); }
		@Override public String getHost() { return read("Member.getHost", member::getHost); }
		@Override public int getProcessId() { return read("Member.getProcessId", member::getProcessId); }
		@Override public List<String> getGroups() { return read("Member.getGroups", member::getGroups); }
		@Override public DriverDurableClientAttributes getDurableClientAttributes() {
			return read("Member.getDurableClientAttributes", () -> {
				var attributes = member.getDurableClientAttributes();
				return attributes == null ? null : new DriverDurableClientAttributes(attributes.getId(), attributes.getTimeout())
					.withPoolName(attributes.getPoolName());
			});
		}
		@Override public int compareTo(DriverMember other) {
			return read("Member.compareTo", () -> member.compareTo(((Member) other).member));
		}
		@Override public boolean equals(Object other) { return other instanceof Member value && member.equals(value.member); }
		@Override public int hashCode() { return member.hashCode(); }
		@Override public String toString() { return member.toString(); }
	}
}
