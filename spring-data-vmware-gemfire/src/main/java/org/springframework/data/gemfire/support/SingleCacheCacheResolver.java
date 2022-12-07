/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.lang.Nullable;

/**
 * {@link CacheResolver} implementation that resolves to a configured, single {@link GemFireCache} instance.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @since 2.3.0
 */
public abstract class SingleCacheCacheResolver {

	/**
	 * Factory method used to resolve a single, configured instance of a {@literal peer} {@link Cache}.
	 *
	 * @param cache {@link Cache} to resolve.
	 * @return a single, configured instance of a {@literal peer} {@link Cache}.
	 * @see org.springframework.data.gemfire.CacheResolver
	 * @see org.apache.geode.cache.Cache
	 */
	public static CacheResolver<Cache> from(@Nullable Cache cache) {
		return () -> cache;
	}

	/**
	 * Factory method used to resolve a single, configured instance of a {@literal peer} {@link ClientCache}.
	 *
	 * @param clientCache {@link ClientCache} to resolve.
	 * @return a single, configured instance of a {@literal peer} {@link ClientCache}.
	 * @see org.springframework.data.gemfire.CacheResolver
	 * @see org.apache.geode.cache.client.ClientCache
	 */
	public static CacheResolver<ClientCache> from(@Nullable ClientCache clientCache) {
		return () -> clientCache;
	}
}