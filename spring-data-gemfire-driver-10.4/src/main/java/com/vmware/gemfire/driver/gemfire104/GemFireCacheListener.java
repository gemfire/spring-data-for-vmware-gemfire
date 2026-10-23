/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Objects;
import java.util.Properties;
import org.apache.geode.cache.*;
import com.vmware.gemfire.driver.spi.*;

/** Every native listener method is explicitly forwarded; no inherited native adapter no-ops. */
class GemFireCacheListener<K, V> implements CacheListener<K, V> {
	final DriverCacheListener<K, V> delegate;
	final GemFireEventMapper events = new GemFireEventMapper();
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireCacheListener(DriverCacheListener<K, V> delegate) { this.delegate = Objects.requireNonNull(delegate); }
	void dispatch(String name, Runnable action) {
		try { action.run(); }
		catch (RuntimeException failure) { throw exceptions.translate("Listener." + name, failure); }
	}
	@Override public void afterCreate(EntryEvent<K, V> event) { dispatch("afterCreate", () -> delegate.afterCreate(events.entry(event))); }
	@Override public void afterUpdate(EntryEvent<K, V> event) { dispatch("afterUpdate", () -> delegate.afterUpdate(events.entry(event))); }
	@Override public void afterInvalidate(EntryEvent<K, V> event) { dispatch("afterInvalidate", () -> delegate.afterInvalidate(events.entry(event))); }
	@Override public void afterDestroy(EntryEvent<K, V> event) { dispatch("afterDestroy", () -> delegate.afterDestroy(events.entry(event))); }
	@Override public void afterRegionInvalidate(RegionEvent<K, V> event) { dispatch("afterRegionInvalidate", () -> delegate.afterRegionInvalidate(events.region(event))); }
	@Override public void afterRegionDestroy(RegionEvent<K, V> event) { dispatch("afterRegionDestroy", () -> delegate.afterRegionDestroy(events.region(event))); }
	@Override public void afterRegionClear(RegionEvent<K, V> event) { dispatch("afterRegionClear", () -> delegate.afterRegionClear(events.region(event))); }
	@Override public void afterRegionCreate(RegionEvent<K, V> event) { dispatch("afterRegionCreate", () -> delegate.afterRegionCreate(events.region(event))); }
	@Override public void afterRegionLive(RegionEvent<K, V> event) { dispatch("afterRegionLive", () -> delegate.afterRegionLive(events.region(event))); }
	@Override public void close() { dispatch("close", delegate::close); }
	@Override public void init(Properties properties) { dispatch("init", () -> delegate.init(properties)); }
	@Override public void initialize(Cache cache, Properties properties) {
		dispatch("initialize", () -> delegate.initialize(new GemFireConnection(cache), properties));
	}
	@Override public boolean equals(Object other) {
		return other instanceof GemFireCacheListener<?, ?> listener
			&& delegate == listener.delegate;
	}
	@Override public int hashCode() { return System.identityHashCode(delegate); }
}
