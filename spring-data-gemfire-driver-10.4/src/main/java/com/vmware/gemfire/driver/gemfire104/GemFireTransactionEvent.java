/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.TransactionEvent;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Supplier;

final class GemFireTransactionEvent implements DriverTransactionEvent {
	final TransactionEvent value;
	GemFireTransactionEvent(TransactionEvent value) { this.value = value; }
	@Override public DriverTransactionId getTransactionId() { return read(() -> new GemFireTransactionId(value.getTransactionId())); }
	@Override public DriverConnection getCache() {
		return read(() -> new GemFireConnection(value.getCache(), ((org.apache.geode.internal.cache.InternalCache) value.getCache()).isClient() ? CacheConfiguration.Mode.CLIENT : CacheConfiguration.Mode.PEER));
	}
	@Override public List<DriverCacheEvent<?, ?>> getEvents() {
		List<org.apache.geode.cache.CacheEvent<?, ?>> events = read(value::getEvents);
		return new AbstractList<DriverCacheEvent<?, ?>>() {
			@Override public DriverCacheEvent<?, ?> get(int index) { return read(() -> new GemFireEventMapper().cacheEvent(events.get(index))); }
			@Override public int size() { return read(events::size); }
		};
	}

	private <T> T read(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException failure) { throw new GemFireExceptionMapper().translate("TransactionEvent", failure); }
	}
}
