/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.query.IndexStatistics;

import com.vmware.gemfire.driver.spi.DriverIndexStatistics;

/** Reads the live native counters on each call. */
final class GemFireIndexStatistics implements DriverIndexStatistics {

	private final IndexStatistics statistics;
	private final GemFireExceptionMapper exceptions;

	GemFireIndexStatistics(IndexStatistics statistics, GemFireExceptionMapper exceptions) {
		this.statistics = statistics;
		this.exceptions = exceptions;
	}

	@Override public long numUpdates() { return read("numUpdates", statistics::getNumUpdates); }
	@Override public long totalUpdateTime() { return read("totalUpdateTime", statistics::getTotalUpdateTime); }
	@Override public long totalUses() { return read("totalUses", statistics::getTotalUses); }
	@Override public long numberOfKeys() { return read("numberOfKeys", statistics::getNumberOfKeys); }
	@Override public long numberOfValues() { return read("numberOfValues", statistics::getNumberOfValues); }
	@Override public long numberOfValues(Object key) {
		return read("numberOfValues", () -> {
			if (key == null) throw new IllegalArgumentException("key must not be null");
			return statistics.getNumberOfValues(key);
		});
	}
	@Override public long readLockCount() { return read("readLockCount", statistics::getReadLockCountLong); }
	@Override public long numberOfMapIndexKeys() { return read("numberOfMapIndexKeys", statistics::getNumberOfMapIndexKeys); }
	@Override public long numberOfBucketIndexes() { return read("numberOfBucketIndexes", statistics::getNumberOfBucketIndexesLong); }

	private long read(String operation, java.util.function.LongSupplier action) {
		try {
			return action.getAsLong();
		}
		catch (RuntimeException ex) {
			throw exceptions.translate("indexStatistics." + operation, ex);
		}
	}
}
