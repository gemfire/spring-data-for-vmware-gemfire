/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

/** Typed distributed identity and metadata. Comparisons follow the underlying member implementation. */
public interface DriverMember extends Comparable<DriverMember> {
	String getId();
	String getUniqueId();
	String getName();
	String getHost();
	int getProcessId();
	List<String> getGroups();
	/**
	 * Detached durable metadata. Native capture can reflect the provider's current pool context;
	 * the returned value never reads that context again. Explicit copies cannot reconfigure pools.
	 */
	DriverDurableClientAttributes getDurableClientAttributes();
}
