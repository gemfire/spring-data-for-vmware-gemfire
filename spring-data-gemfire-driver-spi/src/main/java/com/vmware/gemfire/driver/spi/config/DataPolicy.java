/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi.config;

public enum DataPolicy {

	EMPTY, NORMAL, PRELOADED, PARTITION,
	PERSISTENT_PARTITION, REPLICATE, PERSISTENT_REPLICATE

}
