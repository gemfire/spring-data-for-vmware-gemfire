/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class UnsupportedCapabilityException extends GemFireDriverException {

	private final Capability capability;
	// transient: implementor-provided CapabilityAware is not guaranteed Serializable.
	private final transient CapabilityAware source;

	public UnsupportedCapabilityException(Capability capability, CapabilityAware source) {
		super("Capability " + capability + " is not supported by " + source.getClass().getName() + ".");
		this.capability = capability;
		this.source = source;
	}

	public Capability capability() {
		return capability;
	}

	public CapabilityAware source() {
		return source;
	}

}
