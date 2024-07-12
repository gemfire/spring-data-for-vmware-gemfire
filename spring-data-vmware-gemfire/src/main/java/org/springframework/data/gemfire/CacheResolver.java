/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.function.Supplier;
import org.apache.geode.cache.client.ClientCache;

/**
 * Strategy interface for resolving a instance reference to a {@link ClientCache}.
 *
 * @author John Blum
 * @param <T> {@link Class subclass} of {@link ClientCache}.
 * @see Supplier
 * @see ClientCache
 * @see org.apache.geode.cache.client.ClientCache
 * @since 2.3.0
 */
@FunctionalInterface
public interface CacheResolver<T extends ClientCache> extends Supplier<T> {

	/**
	 * Gets the resolved instance of the {@link ClientCache}.
	 *
	 * @return the resolved instance of the {@link ClientCache}.
	 * @see #resolve()
	 */
	@Override
	default T get() {
		return resolve();
	}

	/**
	 * Resolves the instance reference to the {@link ClientCache} implementation.
	 *
	 * @return a instance reference to a {@link ClientCache} implementation.
	 */
	T resolve();

}
