/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Commit outcome callbacks. Native delivery and exception suppression rules remain authoritative. */
public interface DriverTransactionListener extends DriverCacheCallback {
	void afterCommit(DriverTransactionEvent event);
	void afterFailedCommit(DriverTransactionEvent event);
	void afterRollback(DriverTransactionEvent event);
}
