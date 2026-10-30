// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import com.vmware.gemfire.driver.spi.config.ExpirationAttributes;

/** Per-entry expiration policy. The region owns the callback only when the application hands it over; replacing
 * a callback returns the previous one without closing it, so the caller decides when to close it. Declarable
 * initialization is not invoked when the callback is set programmatically.
 */
public interface DriverCustomExpiry<K, V> extends DriverCacheCallback {
	/** Null means the region-level expiration applies. */
	ExpirationAttributes getExpiry(DriverRegionEntry<K, V> entry);
}
