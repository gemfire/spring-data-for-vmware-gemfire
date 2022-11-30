// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.support;

import org.apache.geode.cache.GemFireCache;

import org.springframework.data.gemfire.CacheResolver;

/**
 * Thread-safe, abstract {@link CacheResolver} implementation to "cache" the instance reference to the (single)
 * {@link GemFireCache} so that the {@link GemFireCache} object is only ever resolved once.
 *
 * @author John Blum
 * @see GemFireCache
 * @see CacheResolver
 * @since 2.3.0
 */
public abstract class AbstractCachingCacheResolver<T extends GemFireCache> implements CacheResolver<T> {

	private T cacheReference;

	/**
	 * @inheritDoc
	 */
	@Override
	public synchronized T resolve() {

		if (this.cacheReference == null) {
			this.cacheReference = doResolve();
		}

		return this.cacheReference;
	}

	/**
	 * Performs the actual resolution process of the {@link GemFireCache} object iff the cache reference
	 * is not already cached.
	 *
	 * @see #resolve()
	 */
	protected abstract T doResolve();

}
