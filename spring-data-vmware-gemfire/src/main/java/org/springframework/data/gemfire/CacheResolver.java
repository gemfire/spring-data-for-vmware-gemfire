/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.function.Supplier;

import org.apache.geode.cache.GemFireCache;

/**
 * Strategy interface for resolving a instance reference to a {@link GemFireCache}.
 *
 * @author John Blum
 * @param <T> {@link Class subclass} of {@link GemFireCache}.
 * @see Supplier
 * @see GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @since 2.3.0
 */
@FunctionalInterface
public interface CacheResolver<T extends GemFireCache> extends Supplier<T> {

	/**
	 * Gets the resolved instance of the {@link GemFireCache}.
	 *
	 * @return the resolved instance of the {@link GemFireCache}.
	 * @see #resolve()
	 */
	@Override
	default T get() {
		return resolve();
	}

	/**
	 * Resolves the instance reference to the {@link GemFireCache} implementation.
	 *
	 * @return a instance reference to a {@link GemFireCache} implementation.
	 */
	T resolve();

}
