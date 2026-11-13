/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Index usage counters; the per-key count is a live read of the provider's index. */
public interface DriverIndexStatistics {
	long numUpdates();
	long totalUpdateTime();
	long totalUses();
	long numberOfKeys();
	long numberOfValues();
	/** Number of values for one key; a null key is rejected. */
	long numberOfValues(Object key);
	long readLockCount();
	long numberOfMapIndexKeys();
	long numberOfBucketIndexes();
}
