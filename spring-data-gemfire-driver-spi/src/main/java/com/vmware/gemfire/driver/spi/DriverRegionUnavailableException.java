/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Destroyed/missing-region failure. The path is payload, never part of its message. */
public final class DriverRegionUnavailableException extends DriverOperationException {
	private final String regionFullPath;
	public DriverRegionUnavailableException(String regionFullPath, Throwable cause) {
		this("Region is unavailable", regionFullPath, cause);
	}
	public DriverRegionUnavailableException(String message, String regionFullPath, Throwable cause) {
		super(DriverFailureKind.REGION_UNAVAILABLE, message, cause); this.regionFullPath = regionFullPath;
	}
	public String getRegionFullPath() { return regionFullPath; }
}
