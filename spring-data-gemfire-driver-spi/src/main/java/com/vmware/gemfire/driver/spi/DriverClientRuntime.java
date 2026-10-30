/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.function.BooleanSupplier;

/** A connection-owned view of the local client runtime, with no peer administration. */
public interface DriverClientRuntime {

	/** System logging, using the current native destination or a connection-local mock recorder. */
	default DriverLog getLog() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getLog");
	}

	/** Security logging, sharing native configured destinations where applicable. */
	default DriverLog getSecurityLog() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getSecurityLog");
	}

	/** Local statistic instances and registry; explicitly close instances when finished. */
	default DriverStatisticsFactory getStatisticsFactory() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getStatisticsFactory");
	}


	/** Local statistics definitions; not a server statistics or membership management service. */
	default DriverStatisticsTypes getStatisticsTypes() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getStatisticsTypes");
	}

	/**
	 * A detached snapshot of the current effective string configuration, including native defaults.
	 * Diagnostic output is redacted; explicitly extracted values may contain credentials.
	 * Reading properties does not enable peer/server operations. The mock reports only supplied
	 * configuration and does not resolve files, system properties or native defaults.
	 * Captured snapshots and readback remain available after ordinary connection closure.
	 */
	default DriverPropertySnapshot getProperties() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getProperties");
	}

	/**
	 * A detached snapshot of security string configuration, with the same access and diagnostic
	 * rules as {@link #getProperties()}. Mutations to extracted copies never update runtime security.
	 * The mock selects supplied keys beginning with {@code security-}; it performs no authentication.
	 */
	default DriverPropertySnapshot getSecurityProperties() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getSecurityProperties");
	}

	/**
	 * A criterion that never reports cancellation, even after cache closure.
	 * Its check methods still perform the native fatal JVM-failure check.
	 * The mock has no JVM-failure machinery. No shared handle identity is promised.
	 */
	default DriverCancellation noCancellation() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.noCancellation");
	}

	/**
	 * Creates an independent criterion for a caller-owned service, replacing native subclassing.
	 * The non-null supplier must be thread-safe, non-throwing and remain true once cancelled.
	 * It is evaluated on demand; closing this runtime does not cancel the caller's service.
	 * Cancellation produces a safe CANCELLED failure retaining the supplied diagnostic cause.
	 * Native polling also observes JVM failure, and checks propagate native fatal errors before
	 * consulting the supplier. The mock evaluates only the supplier.
	 */
	default DriverCancellation createCancellation(BooleanSupplier cancelled) {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.createCancellation");
	}

	/**
	 * System-scoped cancellation, distinct from {@link DriverConnection#getCancellation()}.
	 * A disconnected local runtime need not be cancelling: in the native 10.4 client runtime,
	 * ordinary cache closure leaves this criterion clear. The mock preserves that distinction.
	 * Captured handles remain usable after closure; checks retain native fatal-error handling.
	 */
	default DriverCancellation getCancellation() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getCancellation");
	}

	/**
	 * Whether the local runtime is connected. This does not establish pool connectivity,
	 * server reachability or successful authentication. The mock reports its local open state.
	 * A previously obtained handle remains usable after the connection closes.
	 */
	default boolean isConnected() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.isConnected");
	}

	/** Configured local client name, empty when unnamed; available after ordinary closure. */
	default String getName() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getName");
	}

	/**
	 * Legacy numeric connection ID, not the configured distributed-system-id and not globally
	 * unique. The native loner client and mock return zero. Prefer {@link #getMember()} for identity.
	 */
	default long getLegacyId() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getLegacyId");
	}

	/** Local client identity, using the same identity representation as cache events. */
	default DriverMember getMember() {
		throw UnsupportedFeatureException.notIntroduced("ClientRuntime.getMember");
	}
}
