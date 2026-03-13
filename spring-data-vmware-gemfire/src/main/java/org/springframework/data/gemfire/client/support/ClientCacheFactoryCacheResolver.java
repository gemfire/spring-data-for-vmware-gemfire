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

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.support.AbstractCachingCacheResolver;

/**
 * Cacheable {@link CacheResolver} implementation resolving a {@link GudClientCache}
 * using the {@link GudCacheProvider} API.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudCacheProvider
 * @see CacheResolver
 * @see AbstractCachingCacheResolver
 * @since 2.3.0.
 */
public class ClientCacheFactoryCacheResolver extends AbstractCachingCacheResolver<GudClientCache> {

	public static final ClientCacheFactoryCacheResolver INSTANCE = new ClientCacheFactoryCacheResolver();

	@Override
	protected GudClientCache doResolve() {
		return GudCacheProvider.getAnyClientCache();
	}
}
