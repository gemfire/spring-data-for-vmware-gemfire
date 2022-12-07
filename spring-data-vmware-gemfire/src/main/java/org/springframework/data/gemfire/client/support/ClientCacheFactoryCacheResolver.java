/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.support.AbstractCachingCacheResolver;

/**
 * Cacheable {@link CacheResolver} implementation resolving a {@link ClientCache}
 * using the {@link ClientCacheFactory} API.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCacheFactory
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.support.AbstractCachingCacheResolver
 * @since 2.3.0.
 */
public class ClientCacheFactoryCacheResolver extends AbstractCachingCacheResolver<ClientCache> {

	public static final ClientCacheFactoryCacheResolver INSTANCE = new ClientCacheFactoryCacheResolver();

	@Override
	protected ClientCache doResolve() {
		return ClientCacheFactory.getAnyInstance();
	}
}
