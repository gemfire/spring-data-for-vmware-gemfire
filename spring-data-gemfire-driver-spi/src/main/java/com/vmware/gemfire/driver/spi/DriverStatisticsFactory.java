/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

/**
 * Runtime-owned creation and lookup of local statistics; no server administration.
 * With statistics enabled, a captured factory and caller-created instances remain
 * usable after connection shutdown. Explicitly close instances to deregister them.
 * A newly connected runtime has a separate instance registry; compatible connections
 * reusing one native runtime share its registry. Mocks may isolate every connection.
 * Disabled statistics remain unregistered: all lookup results are empty. A captured
 * factory retains its mode after shutdown; changing configuration requires a new runtime.
 */
public interface DriverStatisticsFactory {
	/**
	 * Creates an instance using a type from this driver. With statistics enabled,
	 * null/empty text uses the runtime name; zero numeric ID uses the process ID. Neither is a unique key.
	 * The unique instance ID is runtime-local. Atomic controls concurrent updates.
	 * With statistics disabled, text/numeric IDs are retained exactly (including
	 * null/empty/zero), unique ID is zero and the handle always reports atomic.
	 */
	default DriverStatistics create(DriverStatisticsType type, String textId, long numericId, boolean atomic) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsFactory.create");
	}
	/** Immutable membership snapshot of live handles, matching the registered type identity. */
	default List<DriverStatistics> findByType(DriverStatisticsType type) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsFactory.findByType");
	}
	/** Immutable membership snapshot; duplicate text IDs are permitted. */
	default List<DriverStatistics> findByTextId(String textId) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsFactory.findByTextId");
	}
	/** Immutable membership snapshot; duplicate numeric IDs are permitted. */
	default List<DriverStatistics> findByNumericId(long numericId) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsFactory.findByNumericId");
	}
}
