/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Deferred index creation failed for one or more indexes; each failure is preserved by index name. */
public class DriverMultiIndexCreationException extends GemFireDriverException {

	private final java.util.Map<String, DriverOperationException> failures;

	/** The message must contain only safe diagnostics, never native messages or index expressions. */
	public DriverMultiIndexCreationException(String message, java.util.Map<String, DriverOperationException> failures, Throwable cause) {
		super(message, cause);
		this.failures = java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(java.util.Objects.requireNonNull(failures, "failures")));
	}

	/** Immutable, keyed by index name. */
	public java.util.Map<String, DriverOperationException> failures() { return failures; }

	public java.util.Set<String> failedIndexNames() { return failures.keySet(); }
}
