/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.TransactionId;

/** Shared by manager, entry events and transaction events, so identity is consistent across handles. */
final class GemFireTransactionId implements DriverTransactionId {
	final TransactionId value;
	GemFireTransactionId(TransactionId value) { this.value = value; }
	@Override public DriverMember getMemberId() {
		try { return new GemFireEventMapper().member(value.getMemberId()); }
		catch (RuntimeException failure) { throw new GemFireExceptionMapper().translate("TransactionId.member", failure); }
	}
	@Override public boolean equals(Object other) { return other instanceof GemFireTransactionId id && value.equals(id.value); }
	@Override public int hashCode() { return value.hashCode(); }
	@Override public String toString() { return value.toString(); }
}
