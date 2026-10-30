/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Native-free message identity and pattern. Explicit text access can reveal sensitive content;
 * diagnostic toString output is redacted. No equality or interning contract is promised.
 * Formatting follows JDK MessageFormat, including quoting and null argument arrays, and uses
 * the default formatting locale at invocation. Invalid patterns retain JDK runtime exceptions.
 * A null raw pattern can be retained but fails when formatted. Fatal errors propagate.
 */
public interface DriverLogMessage {
	int id();
	String rawText();
	String format(Object... parameters);

	/**
	 * Uses the driver's translation catalog and raw-text fallback. The native catalog locale is
	 * captured when its message subsystem initializes; translated output may include a message ID.
	 * The mock always formats raw text without a catalog or ID prefix.
	 */
	String formatLocalized(Object... parameters);
}
