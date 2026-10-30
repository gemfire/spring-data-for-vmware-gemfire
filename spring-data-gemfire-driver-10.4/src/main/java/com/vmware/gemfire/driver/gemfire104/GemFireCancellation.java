/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import org.apache.geode.CancelCriterion;
import org.apache.geode.CancelException;

import com.vmware.gemfire.driver.spi.DriverCancellation;
import com.vmware.gemfire.driver.spi.GemFireDriverException;

final class GemFireCancellation implements DriverCancellation {

	private static final String CANCELLED_REASON = "Client service cancellation is in progress.";

	private final CancelCriterion criterion;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	GemFireCancellation(CancelCriterion criterion) { this.criterion = criterion; }

	GemFireCancellation(BooleanSupplier cancelled) {
		Objects.requireNonNull(cancelled, "cancelled");
		this.criterion = new CancelCriterion() {
			@Override public String cancelInProgress() {
				String failure = checkFailure();
				return failure != null ? failure : cancelled.getAsBoolean() ? CANCELLED_REASON : null;
			}
			@Override public RuntimeException generateCancelledException(Throwable cause) {
				return cancelInProgress() == null ? null : new ServiceCancelledException(cause);
			}
		};
	}

	@Override public String cancelInProgress() {
		try { return criterion.cancelInProgress() == null ? null : CANCELLED_REASON; }
		catch (RuntimeException failure) { throw exceptions.translate("Cancellation.cancelInProgress", failure); }
	}

	@Override public boolean isCancelInProgress() { return cancelInProgress() != null; }
	@Override public void checkCancelInProgress() { checkCancelInProgress(null); }

	@Override public void checkCancelInProgress(Throwable cause) {
		try { criterion.checkCancelInProgress(cause); }
		catch (RuntimeException failure) { throw exceptions.translate("Cancellation.checkCancelInProgress", failure); }
	}

	@Override public GemFireDriverException generateCancelledException(Throwable cause) {
		try {
			RuntimeException failure = criterion.generateCancelledException(cause);
			return failure == null ? null : exceptions.translate("Cancellation.generateCancelledException", failure);
		}
		catch (RuntimeException failure) { throw exceptions.translate("Cancellation.generateCancelledException", failure); }
	}

	private static final class ServiceCancelledException extends CancelException {
		ServiceCancelledException(Throwable cause) { super(CANCELLED_REASON, cause); }
	}
}
