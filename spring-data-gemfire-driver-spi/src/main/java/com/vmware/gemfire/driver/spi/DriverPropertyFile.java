/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/** Property-file discovery result, without loading contents or exposing native configuration. */
public final class DriverPropertyFile {

	private final String fileName;
	private final URL location;

	/** A null location represents an unresolved file; the selected filename may be empty. */
	public DriverPropertyFile(String fileName, URL location) {
		this.fileName = Objects.requireNonNull(fileName, "fileName");
		this.location = location;
	}

	/** Explicit access to the selected filename, which may contain sensitive path information. */
	public String fileName() { return fileName; }

	/** Explicit access to the discovered location; absence is not a configuration error. */
	public Optional<URL> location() { return Optional.ofNullable(location); }

	@Override public String toString() { return "DriverPropertyFile[details=<redacted>]"; }
}
