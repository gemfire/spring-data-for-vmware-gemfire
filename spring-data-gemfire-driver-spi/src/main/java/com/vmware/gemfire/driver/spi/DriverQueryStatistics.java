/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Execution counters for one query. */
public record DriverQueryStatistics(long numExecutions, long totalExecutionTime) {
}
