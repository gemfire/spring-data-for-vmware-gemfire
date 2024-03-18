/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.util.Optional;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.internal.cache.GemFireCacheImpl;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link CacheUtils} is an abstract utility class encapsulating common operations for working with
 * {@link Cache} and {@link ClientCache} instances.
 *
 * @author John Blum
 * @see Cache
 * @see CacheFactory
 * @see GemFireCache
 * @see org.apache.geode.cache.Region
 * @see ClientCache
 * @see ClientCacheFactory
 * @see DistributedSystem
 * @see GemFireCacheImpl
 * @see DistributedSystemUtils
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class CacheUtils extends DistributedSystemUtils {

	public static final String DEFAULT_POOL_NAME = "DEFAULT";

	public static boolean isClient(@Nullable GemFireCache cache) {

		boolean client = cache instanceof ClientCache;

		if (cache instanceof GemFireCacheImpl) {
			client &= ((GemFireCacheImpl) cache).isClient();
		}

		return client;
	}

	public static boolean isDefaultPool(@Nullable Pool pool) {

		return Optional.ofNullable(pool)
			.map(Pool::getName)
			.filter(CacheUtils::isDefaultPool)
			.isPresent();
	}

	public static boolean isNotDefaultPool(@Nullable Pool pool) {
		return !isDefaultPool(pool);
	}

	public static boolean isDefaultPool(@Nullable String poolName) {
		return DEFAULT_POOL_NAME.equals(poolName);
	}

	public static boolean isNotDefaultPool(@Nullable String poolName) {
		return !isDefaultPool(poolName);
	}

	public static boolean isDurable(@Nullable ClientCache clientCache) {

		// NOTE: Technically, the following code snippet would be more useful/valuable but is not "testable"!
		//((InternalDistributedSystem) distributedSystem).getConfig().getDurableClientId();

		return Optional.ofNullable(clientCache)
			.<DistributedSystem>map(CacheUtils::getDistributedSystem)
			.filter(DistributedSystem::isConnected)
			.map(DistributedSystem::getProperties)
			.map(properties -> properties.getProperty(DURABLE_CLIENT_ID_PROPERTY_NAME, null))
			.filter(StringUtils::hasText)
			.isPresent();
	}

	public static boolean isPeer(@Nullable GemFireCache cache) {

		boolean peer = cache instanceof Cache;

		if (cache instanceof GemFireCacheImpl) {
			peer &= !((GemFireCacheImpl) cache).isClient();
		}

		return peer;
	}

	public static boolean close() {
		return close(resolveGemFireCache());
	}

	public static boolean close(@NonNull GemFireCache gemfireCache) {
		return close(gemfireCache, () -> {});
	}

	public static boolean close(@NonNull GemFireCache gemfireCache, @Nullable Runnable shutdownHook) {

		try {
			gemfireCache.close();
			return true;
		}
		catch (Throwable ignore) {
			return false;
		}
		finally {
			Optional.ofNullable(shutdownHook).ifPresent(Runnable::run);
		}
	}

	public static boolean closeCache() {

		try {
			CacheFactory.getAnyInstance().close();
			return true;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	public static boolean closeClientCache() {

		try {
			ClientCacheFactory.getAnyInstance().close();
			return true;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	public static Cache getCache() {

		try {
			return CacheFactory.getAnyInstance();
		}
		catch (CacheClosedException ignore) {
			return null;
		}
	}

	public static ClientCache getClientCache() {

		try {
			return ClientCacheFactory.getAnyInstance();
		}
		catch (CacheClosedException | IllegalStateException ignore) {
			return null;
		}
	}

	public static GemFireCache resolveGemFireCache() {
		return Optional.<GemFireCache>ofNullable(getClientCache()).orElseGet(CacheUtils::getCache);
	}
}
