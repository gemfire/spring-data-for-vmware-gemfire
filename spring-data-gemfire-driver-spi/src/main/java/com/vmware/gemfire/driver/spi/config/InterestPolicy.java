// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Client subscription policy value. Native ordinals are deliberately not part of this enum's ABI.
 * Configuration alone neither registers interest nor guarantees event delivery.
 */
public enum InterestPolicy {
	ALL, CACHE_CONTENT;
	public static final InterestPolicy DEFAULT = CACHE_CONTENT;
	public boolean isAll() { return this == ALL; }
	public boolean isCacheContent() { return this == CACHE_CONTENT; }
	public boolean isDefault() { return this == DEFAULT; }
}
