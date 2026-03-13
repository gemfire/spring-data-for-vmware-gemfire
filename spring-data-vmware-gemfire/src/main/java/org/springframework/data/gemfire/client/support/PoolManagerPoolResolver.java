/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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

package org.springframework.data.gemfire.client.support;

import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolManager;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link PoolManagerPoolResolver} is an implementation of {@link PoolResolver} that delegates all {@link GudPool}
 * resolution logic to the Apache Geode {@link GudPoolManager}.
 *
 * @author John Blum
 * @see GudRegion
 * @see GudPool
 * @see GudPoolManager
 * @see PoolResolver
 * @since 2.3.0
 */
public class PoolManagerPoolResolver implements PoolResolver {

	/**
	 * Resolves the {@link GudPool} used by the given {@link GudRegion} by delegating to {@link GudPoolManager#find(GudRegion)}.
	 *
	 * @param region {@link GudRegion} from which to resolve the associated {@link GudPool}.
	 * @return the {@link GudPool} used by the given {@link GudRegion}.
	 * @see GudPoolManager#find(GudRegion)
	 * @see GudPool
	 */
	@Override
	public @Nullable GudPool resolve(@Nullable GudRegion<?, ?> region) {
		GudPoolManager poolManager = GudCacheProvider.getPoolManager();
		return region != null && poolManager != null ? poolManager.find(region) : null;
	}

	/**
	 * Resolves the {@link GudPool} with the given {@link String name} by delegating to {@link GudPoolManager#find(String)}.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} to resolve.
	 * @return the {@link GudPool} with the given {@link String name} or {@literal null} if no {@link GudPool} exists with
	 * the {@link String name}.
	 * @see GudPoolManager#find(String)
	 * @see GudPool
	 */
	@Override
	public @Nullable GudPool resolve(@Nullable String poolName) {
		GudPoolManager poolManager = GudCacheProvider.getPoolManager();
		return StringUtils.hasText(poolName) && poolManager != null ? poolManager.find(poolName) : null;
	}
}
