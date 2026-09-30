/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class SpiVersionIncompatibleException extends DriverBindException {

	private final String providerClass;
	private final SpiVersion builtAgainst;
	private final SpiVersion runtime;

	public SpiVersionIncompatibleException(String providerClass, SpiVersion builtAgainst, SpiVersion runtime) {
		super("Driver Impl " + providerClass + " was built against SPI " + builtAgainst
			+ " but the SPI on the classpath is " + runtime + "; a driver needs the same SPI major version and an SPI"
			+ " minor version at least as new as the one it was built against.");
		this.providerClass = providerClass;
		this.builtAgainst = builtAgainst;
		this.runtime = runtime;
	}

	public String providerClass() {
		return providerClass;
	}

	public SpiVersion builtAgainst() {
		return builtAgainst;
	}

	public SpiVersion runtime() {
		return runtime;
	}

}
