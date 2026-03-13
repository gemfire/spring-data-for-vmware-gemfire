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
import org.springframework.lang.Nullable;

/**
 * {@link CacheResolver} implementation that resolves to a configured, single {@link GudClientCache} instance.
 *
 * @author John Blum
 * @see GudClientCache
 * @since 2.3.0
 */
public abstract class SingleCacheCacheResolver {

	/**
	 * Factory method used to resolve a single, configured instance of a {@link GudClientCache}.
	 *
	 * @param clientCache {@link GudClientCache} to resolve.
	 * @return a single, configured instance of a {@link GudClientCache}.
	 * @see CacheResolver
	 * @see GudClientCache
	 */
	public static CacheResolver<GudClientCache> from(@Nullable GudClientCache clientCache) {
		return () -> clientCache;
	}
}
