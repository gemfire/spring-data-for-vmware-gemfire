/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.RegionService;
import java.util.function.Supplier;

final class GemFireRegionService implements DriverRegionService {
	private final RegionService service;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireRegionService(RegionService service) { this.service = service; }
	@Override public <K, V> DriverRegion<K, V> getRegion(String path) {
		return invoke(() -> { var value = service.<K, V>getRegion(path); return value == null ? null : new GemFireRegion<>(value); });
	}
	@Override public java.util.Set<DriverRegion<?, ?>> rootRegions() { return invoke(() -> new GemFireRegionViews().regions(service.rootRegions())); }
	@Override public boolean isClosed() { return invoke(service::isClosed); }
	@Override public void close() { invoke(() -> { service.close(); return null; }); }
	private <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("regionService", ex); }
	}
}
