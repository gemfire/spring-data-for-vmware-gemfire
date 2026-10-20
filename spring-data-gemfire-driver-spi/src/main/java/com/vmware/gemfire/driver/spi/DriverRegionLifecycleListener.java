/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Region lifecycle callbacks, delivered on the underlying engine's threads and timing.
 * Destruction and cleanup are distinct notifications; the SPI adds no delivery guarantee.
 * A listener may be registered with several regions and must tolerate cleanup by each.
 */
public interface DriverRegionLifecycleListener {

	/** Reports the destroyed region handle; its name remains available to identify cached lookups. */
	void afterRegionDestroy(DriverRegion<?, ?> region);

	/** Releases listener resources when the underlying engine closes this registration. */
	void close();
}
