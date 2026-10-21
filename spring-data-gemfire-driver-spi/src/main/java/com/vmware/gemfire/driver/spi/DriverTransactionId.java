/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Opaque typed transaction identity. Equality follows the native transaction; no raw handle escape. */
public interface DriverTransactionId {
	DriverMember getMemberId();
}
