/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi.config;

/** Initial values returned when registering interest. */
public enum InterestResultPolicy {
	NONE, KEYS, KEYS_VALUES;
	public static final InterestResultPolicy DEFAULT = KEYS_VALUES;
	public boolean isNone() { return this == NONE; }
	public boolean isKeys() { return this == KEYS; }
	public boolean isKeysValues() { return this == KEYS_VALUES; }
	public boolean isDefault() { return this == DEFAULT; }
}
