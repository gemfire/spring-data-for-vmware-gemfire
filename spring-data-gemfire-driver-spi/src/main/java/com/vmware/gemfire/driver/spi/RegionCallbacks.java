/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

/** Callbacks installed before region creation. These are injected instances, not class names.
 * Registration does not call declarative initialization hooks. The region owns cleanup after creation.
 */
public record RegionCallbacks<K, V>(List<DriverCacheListener<K, V>> listeners,
	DriverCacheLoader<K, V> loader, DriverCacheWriter<K, V> writer) {
	public RegionCallbacks { listeners = List.copyOf(listeners); }
}
