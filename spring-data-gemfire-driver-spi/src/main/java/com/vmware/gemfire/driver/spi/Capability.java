/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

// Closed set — never extended; a capability's semantics are immutable once introduced (spec §5.3).
public enum Capability {

	CONTINUOUS_QUERY,
	PDX_SERIALIZATION,
	WAN_GATEWAY_REPLICATION,
	LUCENE_INDEXING,
	OFF_HEAP_REGIONS

}
