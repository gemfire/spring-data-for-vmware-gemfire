/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.Region;

final class GemFireRegionEntry<K, V> extends GemFireMapEntry<K, V> implements DriverRegionEntry<K, V> {
	private final Region.Entry<K, V> regionEntry;
	GemFireRegionEntry(Region.Entry<K, V> entry) { super(entry); this.regionEntry = entry; }
	@Override public DriverRegion<K, V> getRegion() { return invoke(() -> new GemFireRegion<>(regionEntry.getRegion())); }
	@Override public boolean isLocal() { return invoke(regionEntry::isLocal); }
	@Override public boolean isDestroyed() { return invoke(regionEntry::isDestroyed); }
	@Override public Object getUserAttribute() { return invoke(regionEntry::getUserAttribute); }
	@Override public Object setUserAttribute(Object value) { return invoke(() -> regionEntry.setUserAttribute(value)); }
	@Override public DriverCacheStatistics getStatistics() { return invoke(() -> new GemFireCacheStatistics(regionEntry.getStatistics())); }
}
