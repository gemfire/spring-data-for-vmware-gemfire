/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import org.apache.geode.cache.client.ClientCache;
import org.springframework.data.gemfire.CacheResolver;

/**
 * Thread-safe, abstract {@link CacheResolver} implementation to "cache" the instance reference to the (single)
 * {@link ClientCache} so that the {@link ClientCache} object is only ever resolved once.
 *
 * @author John Blum
 * @see ClientCache
 * @see CacheResolver
 * @since 2.3.0
 */
public abstract class AbstractCachingCacheResolver<T extends ClientCache> implements CacheResolver<T> {

	private T cacheReference;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized T resolve() {

		if (this.cacheReference == null) {
			this.cacheReference = doResolve();
		}

		return this.cacheReference;
	}

	/**
	 * Performs the actual resolution process of the {@link ClientCache} object iff the cache reference
	 * is not already cached.
	 *
	 * @see #resolve()
	 */
	protected abstract T doResolve();

}
