/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.ClientCacheFactory;
import com.vmware.gemfire.internal.version.GemFireVersion;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverMetadata;
import com.vmware.gemfire.driver.spi.GemFireDriver;

/** ServiceLoader provider for the exact GemFire 10.4 version stamped at build time. */
public final class GemFire104Driver implements GemFireDriver {

	@Override
	public DriverMetadata metadata() {
		return () -> "GemFire 10.4 driver";
	}

	@Override
	public String runtimeGemFireVersion() {
		return GemFireVersion.getGemFireVersion();
	}

	@Override
	public DriverConnection connect(DriverConfiguration configuration) {
		if (!(configuration instanceof CacheConfiguration cacheConfiguration)) {
			throw new DriverBindException("The GemFire driver requires CacheConfiguration.");
		}
		try {
			return new GemFireConnection(new ClientCacheFactory(cacheConfiguration.properties()).create());
		}
		catch (RuntimeException ex) {
			throw new DriverBindException("Unable to create the GemFire cache.", ex);
		}
	}
}
