/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class NativeVersionIncompatibleException extends DriverBindException {

	// transient: implementor-provided DriverMetadata is not guaranteed Serializable.
	private final transient DriverMetadata metadata;
	private final String runtimeVersion;

	public NativeVersionIncompatibleException(DriverMetadata metadata, String runtimeVersion) {
		super("Driver Impl declares supported native GemFire range " + metadata.supportedNativeRange()
			+ " but rejected the runtime GemFire version '" + runtimeVersion + "'.");
		this.metadata = metadata;
		this.runtimeVersion = runtimeVersion;
	}

	public DriverMetadata metadata() {
		return metadata;
	}

	public String runtimeVersion() {
		return runtimeVersion;
	}

}
