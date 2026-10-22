/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Optional no-op listener base for applications interested in selected outcomes. */
public class DriverTransactionListenerAdapter implements DriverTransactionListener {
	@Override public void afterCommit(DriverTransactionEvent event) { }
	@Override public void afterFailedCommit(DriverTransactionEvent event) { }
	@Override public void afterRollback(DriverTransactionEvent event) { }
	@Override public void close() { }
}
