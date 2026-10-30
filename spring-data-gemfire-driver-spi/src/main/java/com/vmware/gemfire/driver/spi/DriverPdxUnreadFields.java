// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Opaque carrier of fields a reader did not consume. Obtained from {@link DriverPdxReader#readUnreadFields()}
 * and handed back to {@link DriverPdxWriter#writeUnreadFields}; it is only valid with the provider that produced it.
 * When unread fields are ignored by the cache configuration the carrier is empty.
 */
public interface DriverPdxUnreadFields {
}
