/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;

import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/**
 * A thin named-pool handle with separate configured and discovered endpoint observations.
 * Handles need not have stable identity or cache isolation. Destruction follows the provider:
 * attached regions or other native lifecycle constraints can reject it.
 */
public interface DriverPool {

	/** Effective immutable settings, including version-specific defaults. This is not live topology. */
	default PoolConfiguration configuration() { throw UnsupportedFeatureException.notIntroduced("Pool.configuration"); }

	default String name() { throw UnsupportedFeatureException.notIntroduced("Pool.name"); }
	default List<Endpoint> servers() { throw UnsupportedFeatureException.notIntroduced("Pool.servers"); }
	default List<Endpoint> locators() { throw UnsupportedFeatureException.notIntroduced("Pool.locators"); }
	/**
	 * Detached, immutable snapshot from the provider's most recent successful locator-list update.
	 * May be empty before discovery, stale after locator failure, or retained after pool destruction.
	 * This does not probe reachability or establish any server connection. Direct-server pools and
	 * the non-networking mock return empty; configured locators alone are not evidence of discovery.
	 */
	default List<Endpoint> onlineLocators() {
		throw UnsupportedFeatureException.notIntroduced("Pool.onlineLocators");
	}
	/** Effective provider value; does not establish connectivity. */
	default int minimumConnections() {
		throw UnsupportedFeatureException.notIntroduced("Pool.minimumConnections");
	}
	default boolean isDestroyed() { throw UnsupportedFeatureException.notIntroduced("Pool.isDestroyed"); }
	default void destroy() { throw UnsupportedFeatureException.notIntroduced("Pool.destroy"); }
}
