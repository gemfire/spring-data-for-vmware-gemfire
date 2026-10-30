/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.distributed.DistributedSystem;
import com.vmware.gemfire.internal.version.GemFireVersion;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverMetadata;
import com.vmware.gemfire.driver.spi.DriverPropertyFile;
import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

/** ServiceLoader provider for the exact GemFire 10.4 version stamped at build time. */
public final class GemFire104Driver implements GemFireDriver {

	@Override
	public DriverPropertyFile resolvePropertiesFile() {
		try {
			return new DriverPropertyFile(DistributedSystem.getPropertiesFile(), DistributedSystem.getPropertiesFileURL());
		}
		catch (RuntimeException failure) {
			throw new GemFireExceptionMapper().translate("Driver.resolvePropertiesFile", failure);
		}
	}

	@Override
	public DriverPropertyFile resolveSecurityPropertiesFile() {
		try {
			return new DriverPropertyFile(DistributedSystem.getSecurityPropertiesFile(), DistributedSystem.getSecurityPropertiesFileURL());
		}
		catch (RuntimeException failure) {
			throw new GemFireExceptionMapper().translate("Driver.resolveSecurityPropertiesFile", failure);
		}
	}

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
			ClientCacheFactory factory = new ClientCacheFactory(cacheConfiguration.properties());
			cacheConfiguration.defaultPool().ifPresent(pool -> new GemFireDefaultPoolConfiguration().apply(pool, factory));
			return new GemFireConnection(factory.create());
		}
		catch (UnsupportedFeatureException ex) {
			throw ex;
		}
		catch (RuntimeException ex) {
			throw new DriverBindException("Unable to create the GemFire cache.", ex);
		}
	}
}
