/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Region-creation failure retaining the existing typed region. */
public final class DriverRegionExistsException extends DriverOperationException {
	private final DriverRegion<?, ?> region;
	public DriverRegionExistsException(DriverRegion<?, ?> region, Throwable cause) {
		this("Region already exists", region, cause);
	}
	public DriverRegionExistsException(String message, DriverRegion<?, ?> region, Throwable cause) {
		super(DriverFailureKind.REGION_EXISTS, message, cause); this.region = region;
	}
	public DriverRegion<?, ?> getRegion() { return region; }
}
