/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Set;

public interface CapabilityAware {

	Set<Capability> supportedCapabilities();

	default void requireCapability(Capability capability) {
		if (!supportedCapabilities().contains(capability)) {
			throw new UnsupportedCapabilityException(capability, this);
		}
	}

}
