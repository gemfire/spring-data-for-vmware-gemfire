/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

public class AmbiguousDriverException extends DriverBindException {

	// transient: implementor-provided DriverMetadata is not guaranteed Serializable.
	private final transient List<DriverMetadata> candidates;

	public AmbiguousDriverException(List<DriverMetadata> candidates) {
		super("Multiple GemFireDriver implementations found on the classpath (" + candidates.size()
			+ "); exactly one is required. Remove the extra Driver Impl jars from the classpath.");
		this.candidates = List.copyOf(candidates);
	}

	public List<DriverMetadata> candidates() {
		return candidates;
	}

}
