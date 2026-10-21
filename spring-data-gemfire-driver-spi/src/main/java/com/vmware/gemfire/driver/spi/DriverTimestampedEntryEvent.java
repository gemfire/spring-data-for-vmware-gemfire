/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** WAN timestamp metadata; delivery belongs to the conflict-resolution callback. */
public interface DriverTimestampedEntryEvent<K, V> extends DriverEntryEvent<K, V> {
	int getNewDistributedSystemID();
	int getOldDistributedSystemID();
	long getNewTimestamp();
	long getOldTimestamp();
}
