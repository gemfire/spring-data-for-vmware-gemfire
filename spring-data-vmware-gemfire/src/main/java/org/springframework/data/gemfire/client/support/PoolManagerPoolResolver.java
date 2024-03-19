/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;

import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link PoolManagerPoolResolver} is an implementation of {@link PoolResolver} that delegates all {@link Pool}
 * resolution logic to the Apache Geode {@link PoolManager}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.client.PoolManager
 * @see org.springframework.data.gemfire.client.PoolResolver
 * @since 2.3.0
 */
public class PoolManagerPoolResolver implements PoolResolver {

	/**
	 * Resolves the {@link Pool} used by the given {@link Region} by delegating to {@link PoolManager#find(Region)}.
	 *
	 * @param region {@link Region} from which to resolve the associated {@link Pool}.
	 * @return the {@link Pool} used by the given {@link Region}.
	 * @see org.apache.geode.cache.client.PoolManager#find(Region)
	 * @see org.apache.geode.cache.client.Pool
	 */
	@Override
	public @Nullable Pool resolve(@Nullable Region<?, ?> region) {
		return region != null ? PoolManager.find(region) : null;
	}

	/**
	 * Resolves the {@link Pool} with the given {@link String name} by delegating to {@link PoolManager#find(String)}.
	 *
	 * @param poolName {@link String name} of the {@link Pool} to resolve.
	 * @return the {@link Pool} with the given {@link String name} or {@link null} if no {@link Pool} exists with
	 * the {@link String name}.
	 * @see org.apache.geode.cache.client.PoolManager#find(String)
	 * @see org.apache.geode.cache.client.Pool
	 */
	@Override
	public @Nullable Pool resolve(@Nullable String poolName) {
		return StringUtils.hasText(poolName) ? PoolManager.find(poolName) : null;
	}
}
