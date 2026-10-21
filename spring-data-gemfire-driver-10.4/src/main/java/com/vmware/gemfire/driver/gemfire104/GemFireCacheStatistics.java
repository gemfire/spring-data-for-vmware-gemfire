/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverCacheStatistics;
import org.apache.geode.cache.CacheStatistics;
import java.util.function.Supplier;

final class GemFireCacheStatistics implements DriverCacheStatistics {
	private final CacheStatistics statistics;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireCacheStatistics(CacheStatistics statistics) { this.statistics = statistics; }
	@Override public long getLastModifiedTime() { return invoke(statistics::getLastModifiedTime); }
	@Override public long getLastAccessedTime() { return invoke(statistics::getLastAccessedTime); }
	@Override public long getMissCount() { return invoke(statistics::getMissCount); }
	@Override public long getHitCount() { return invoke(statistics::getHitCount); }
	@Override public float getHitRatio() { return invoke(statistics::getHitRatio); }
	@Override public void resetCounts() { invoke(() -> { statistics.resetCounts(); return null; }); }
	private <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("statistics", ex); }
	}
}
