/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

public class AmbiguousDriverException extends DriverBindException {

	private final List<String> providerClasses;

	public AmbiguousDriverException(List<String> providerClasses) {
		super("Multiple GemFireDriver implementations found on the classpath (" + providerClasses.size()
			+ "): " + providerClasses + "; exactly one is required. Remove the extra Driver Impl jars from the classpath.");
		this.providerClasses = List.copyOf(providerClasses);
	}

	public List<String> providerClasses() {
		return providerClasses;
	}

}
