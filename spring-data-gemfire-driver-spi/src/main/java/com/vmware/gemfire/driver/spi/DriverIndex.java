/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A handle to an index. Valid only on the query service that produced it. */
public interface DriverIndex {
	String name();
	DriverIndexType type();
	/** Full path of the indexed region. */
	String regionPath();
	String fromClause();
	String canonicalizedFromClause();
	String indexedExpression();
	String canonicalizedIndexedExpression();
	String projectionAttributes();
	String canonicalizedProjectionAttributes();
	boolean isValid();
	DriverIndexStatistics statistics();
}
