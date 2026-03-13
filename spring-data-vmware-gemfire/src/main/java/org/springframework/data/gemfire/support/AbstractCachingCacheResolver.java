/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.support;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.gud.api.GudClientCache;

/**
 * Thread-safe, abstract {@link CacheResolver} implementation to "cache" the instance reference to the (single)
 * {@link GudClientCache} so that the {@link GudClientCache} object is only ever resolved once.
 *
 * @author John Blum
 * @see GudClientCache
 * @see CacheResolver
 * @since 2.3.0
 */
public abstract class AbstractCachingCacheResolver<T extends GudClientCache> implements CacheResolver<T> {

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
	 * Performs the actual resolution process of the {@link GudClientCache} object iff the cache reference
	 * is not already cached.
	 *
	 * @see #resolve()
	 */
	protected abstract T doResolve();

}
