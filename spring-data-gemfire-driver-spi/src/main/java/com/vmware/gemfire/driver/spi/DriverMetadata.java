/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Set;

public interface DriverMetadata {

	// Axis A — [implMin, implMax], §6.1/§6.2
	GenerationRange supportedGenerationRange();

	// Axis B — [nativeMin, nativeMax], §6.3
	NativeVersionRange supportedNativeRange();

	Set<Capability> capabilities();

}
