// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.support;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;

import org.springframework.data.gemfire.CacheResolver;

/**
 * Cacheable {@link CacheResolver} implementation resolving a {@link Cache}
 * using the {@link CacheFactory} API.
 *
 * @author John Blum
 * @see Cache
 * @see CacheFactory
 * @see CacheResolver
 * @see AbstractCachingCacheResolver
 * @since 2.3.0
 */
public class CacheFactoryCacheResolver extends AbstractCachingCacheResolver<Cache> {

	public static final CacheFactoryCacheResolver INSTANCE = new CacheFactoryCacheResolver();

	@Override
	protected Cache doResolve() {
		return CacheFactory.getAnyInstance();
	}
}
