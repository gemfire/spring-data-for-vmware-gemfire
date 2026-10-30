/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.function.BooleanSupplier;

import com.vmware.gemfire.driver.spi.DriverCancellation;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

/** Injected local cancellation state; the mock does not simulate native JVM failure handling. */
final class MockCancellation implements DriverCancellation {

	private final BooleanSupplier closed;

	MockCancellation(BooleanSupplier closed) { this.closed = closed; }

	@Override public String cancelInProgress() {
		return closed.getAsBoolean() ? "Client service cancellation is in progress." : null;
	}
	@Override public boolean isCancelInProgress() { return closed.getAsBoolean(); }
	@Override public void checkCancelInProgress() { checkCancelInProgress(null); }
	@Override public void checkCancelInProgress(Throwable cause) {
		DriverOperationException failure = generateCancelledException(cause);
		if (failure != null) throw failure;
	}
	@Override public DriverOperationException generateCancelledException(Throwable cause) {
		return closed.getAsBoolean()
			? new DriverOperationException(DriverFailureKind.CANCELLED, "Client service cancellation is in progress.", cause)
			: null;
	}
}
