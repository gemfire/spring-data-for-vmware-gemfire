/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import com.vmware.gemfire.driver.spi.DriverConnection;
import org.springframework.data.gemfire.CacheResolver;
import org.springframework.lang.Nullable;

/**
 * {@link CacheResolver} implementation that resolves to a configured, single {@link DriverConnection} instance.
 *
 * @author John Blum
 * @see DriverConnection
 * @since 2.3.0
 */
public abstract class SingleCacheCacheResolver {

	/**
	 * Factory method used to resolve a single, configured instance of a {@link DriverConnection}.
	 *
	 * @param connection {@link DriverConnection} to resolve.
	 * @return a single, configured instance of a {@link DriverConnection}.
	 * @see CacheResolver
	 * @see DriverConnection
	 */
	public static CacheResolver<DriverConnection> from(@Nullable DriverConnection connection) {
		return () -> connection;
	}
}
