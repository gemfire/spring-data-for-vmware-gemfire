/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A transaction-wide before-commit callback. Throw the checked exception to veto the commit. */
public interface DriverTransactionWriter extends DriverCacheCallback {
	void beforeCommit(DriverTransactionEvent event) throws DriverTransactionWriterException;
}
