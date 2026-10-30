/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Cancellation for the owner that supplied this handle, independent of server reachability.
 * Cache and runtime handles have separate criteria; disconnection does not imply cancellation.
 * Handles remain usable after cache closure. Polling does not reserve an active state:
 * cancellation can begin immediately after a successful check. Native fatal JVM errors propagate.
 * Public messages are safe for diagnostics; attached causes are for trusted diagnostics only.
 */
public interface DriverCancellation {

	/** Null while active, otherwise a safe reason without native diagnostic/configuration text. */
	default String cancelInProgress() {
		throw UnsupportedFeatureException.notIntroduced("Cancellation.cancelInProgress");
	}

	default boolean isCancelInProgress() {
		throw UnsupportedFeatureException.notIntroduced("Cancellation.isCancelInProgress");
	}

	/** Throws a failure classified as CANCELLED if this owner's cancellation has begun. */
	default void checkCancelInProgress() {
		throw UnsupportedFeatureException.notIntroduced("Cancellation.checkCancelInProgress");
	}

	/** As above, retaining the supplied cause in the diagnostic exception graph. */
	default void checkCancelInProgress(Throwable cause) {
		throw UnsupportedFeatureException.notIntroduced("Cancellation.checkCancelInProgressWithCause");
	}

	/** Returns null while active, otherwise a CANCELLED failure retaining the supplied cause. */
	default GemFireDriverException generateCancelledException(Throwable cause) {
		throw UnsupportedFeatureException.notIntroduced("Cancellation.generateCancelledException");
	}
}
