/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

/** Borrowed event view, valid during callback delivery; values may be backed by native off-heap data. */
public interface DriverTransactionEvent {
	DriverTransactionId getTransactionId();
	List<DriverCacheEvent<?, ?>> getEvents();
	DriverConnection getCache();
}
