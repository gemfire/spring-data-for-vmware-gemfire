/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.function.Supplier;
import com.vmware.gemfire.driver.spi.DriverConnection;

/**
 * Strategy interface for resolving an instance reference to a {@link DriverConnection}.
 *
 * @author John Blum
 * @param <T> {@link Class subclass} of {@link DriverConnection}.
 * @see Supplier
 * @see DriverConnection
 * @since 2.3.0
 */
@FunctionalInterface
public interface CacheResolver<T extends DriverConnection> extends Supplier<T> {

	/**
	 * Gets the resolved instance of the {@link DriverConnection}.
	 *
	 * @return the resolved instance of the {@link DriverConnection}.
	 * @see #resolve()
	 */
	@Override
	default T get() {
		return resolve();
	}

	/**
	 * Resolves the instance reference to the {@link DriverConnection} implementation.
	 *
	 * @return an instance reference to a {@link DriverConnection} implementation.
	 */
	T resolve();

}
