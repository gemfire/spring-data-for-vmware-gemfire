/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Properties;
import org.apache.geode.cache.*;
import com.vmware.gemfire.driver.spi.*;

final class GemFireCacheWriter<K, V> implements CacheWriter<K, V> {
	final DriverCacheWriter<K, V> delegate;
	private final GemFireEventMapper events = new GemFireEventMapper();
	GemFireCacheWriter(DriverCacheWriter<K, V> delegate) { this.delegate = delegate; }
	private void invoke(Runnable action) {
		try { action.run(); }
		catch (GemFireDriverException failure) { throw new CacheWriterException(failure); }
		catch (RuntimeException failure) {
			throw new CacheWriterException(new DriverOperationException(DriverFailureKind.WRITER_VETO, "Cache writer failed", failure));
		}
	}
	@Override public void beforeCreate(EntryEvent<K, V> event) { invoke(() -> delegate.beforeCreate(events.entry(event))); }
	@Override public void beforeUpdate(EntryEvent<K, V> event) { invoke(() -> delegate.beforeUpdate(events.entry(event))); }
	@Override public void beforeDestroy(EntryEvent<K, V> event) { invoke(() -> delegate.beforeDestroy(events.entry(event))); }
	@Override public void beforeRegionClear(RegionEvent<K, V> event) { invoke(() -> delegate.beforeRegionClear(events.region(event))); }
	@Override public void beforeRegionDestroy(RegionEvent<K, V> event) { invoke(() -> delegate.beforeRegionDestroy(events.region(event))); }
	@Override public void close() { invoke(delegate::close); }
	@Override public void init(Properties properties) { invoke(() -> delegate.init(properties)); }
	@Override public void initialize(Cache cache, Properties properties) {
		invoke(() -> delegate.initialize(new GemFireConnection(cache), properties));
	}
}
