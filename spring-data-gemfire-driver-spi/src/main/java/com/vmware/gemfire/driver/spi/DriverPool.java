/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/**
 * A thin named-pool handle. Endpoint metadata describes configuration, not live topology.
 * Handles need not have stable identity or cache isolation. Destruction follows the provider:
 * attached regions or other native lifecycle constraints can reject it.
 */
public interface DriverPool {

	default String name() { throw UnsupportedFeatureException.notIntroduced("Pool.name"); }
	default List<Endpoint> servers() { throw UnsupportedFeatureException.notIntroduced("Pool.servers"); }
	default List<Endpoint> locators() { throw UnsupportedFeatureException.notIntroduced("Pool.locators"); }
	/** Effective provider value; does not establish connectivity. */
	default int minimumConnections() {
		throw UnsupportedFeatureException.notIntroduced("Pool.minimumConnections");
	}
	default boolean isDestroyed() { throw UnsupportedFeatureException.notIntroduced("Pool.isDestroyed"); }
	default void destroy() { throw UnsupportedFeatureException.notIntroduced("Pool.destroy"); }
}
