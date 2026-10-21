/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A sanitized execution failure. The original cause is retained for trusted diagnostics only. */
public class DriverOperationException extends GemFireDriverException {

	private final DriverFailureKind kind;

	/** Compatibility constructor for providers without a more specific classification. */
	public DriverOperationException(String message, Throwable cause) {
		this(DriverFailureKind.UNKNOWN, message, cause);
	}

	/** Message must contain only safe diagnostics, never native messages or operation arguments. */
	public DriverOperationException(DriverFailureKind kind, String message, Throwable cause) {
		super(message, cause);
		this.kind = java.util.Objects.requireNonNull(kind, "kind");
	}

	/** Deepest original cause, or null when there is none. Causes are trusted diagnostics only. */
	public Throwable getRootCause() {
		Throwable root = getCause();
		var seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<Throwable, Boolean>());
		while (root != null && seen.add(root) && root.getCause() != null && !seen.contains(root.getCause())) {
			root = root.getCause();
		}
		return root;
	}

	public DriverFailureKind kind() { return kind; }

}
