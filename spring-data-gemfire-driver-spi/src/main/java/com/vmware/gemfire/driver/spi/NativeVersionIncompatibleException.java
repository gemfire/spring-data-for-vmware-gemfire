/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class NativeVersionIncompatibleException extends DriverBindException {

	private final String providerClass;
	private final String stampedVersion;
	private final String runtimeVersion;

	public NativeVersionIncompatibleException(String providerClass, String stampedVersion, String runtimeVersion) {
		super("Driver Impl " + providerClass + " was built for GemFire " + stampedVersion
			+ " but the GemFire on the classpath is " + runtimeVersion
			+ "; use the driver whose version matches the GemFire version in use.");
		this.providerClass = providerClass;
		this.stampedVersion = stampedVersion;
		this.runtimeVersion = runtimeVersion;
	}

	public String providerClass() {
		return providerClass;
	}

	public String stampedVersion() {
		return stampedVersion;
	}

	public String runtimeVersion() {
		return runtimeVersion;
	}

}
