/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Routing metadata, separate from the complete mutable region configuration contract. */
public record RegionMetadata(com.vmware.gemfire.driver.spi.config.DataPolicy dataPolicy, String poolName) { }
