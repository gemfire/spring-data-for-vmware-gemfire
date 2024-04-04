/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.util.Optional;
import org.apache.geode.cache.CacheClosedException;
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
 * {@link ClientCache} instances.
 *
 * @author John Blum
 * @see ClientCache
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

	public static boolean close() {
		ClientCache clientCache = getClientCache();
		if (clientCache != null) {
			return close(clientCache);
		}
		return true;
	}

	public static boolean close(@NonNull ClientCache gemfireCache) {
		return close(gemfireCache, () -> {});
	}

	public static boolean close(@NonNull ClientCache gemfireCache, @Nullable Runnable shutdownHook) {

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
			ClientCacheFactory.getAnyInstance().close();
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

	public static ClientCache getClientCache() {

		try {
			return ClientCacheFactory.getAnyInstance();
		}
		catch (CacheClosedException | IllegalStateException ignore) {
			return null;
		}
	}

	public static ClientCache resolveGemFireCache() {
		return getClientCache();
	}
}
