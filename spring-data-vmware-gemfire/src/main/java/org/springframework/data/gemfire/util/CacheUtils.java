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
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.util;

import java.util.Optional;
import org.springframework.data.gemfire.gud.api.GudCacheClosedException;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link CacheUtils} is an abstract utility class encapsulating common operations for working with
 * {@link GudClientCache} instances.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudClientCacheFactory
 * @see GudDistributedSystem
 * @see DistributedSystemUtils
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class CacheUtils extends DistributedSystemUtils {

	public static final String DEFAULT_POOL_NAME = "DEFAULT";

	public static boolean isDefaultPool(@Nullable GudPool pool) {

		return Optional.ofNullable(pool)
			.map(GudPool::getName)
			.filter(CacheUtils::isDefaultPool)
			.isPresent();
	}

	public static boolean isNotDefaultPool(@Nullable GudPool pool) {
		return !isDefaultPool(pool);
	}

	public static boolean isDefaultPool(@Nullable String poolName) {
		return DEFAULT_POOL_NAME.equals(poolName);
	}

	public static boolean isNotDefaultPool(@Nullable String poolName) {
		return !isDefaultPool(poolName);
	}

	public static boolean isDurable(@Nullable GudClientCache clientCache) {

		return Optional.ofNullable(clientCache)
			.<GudDistributedSystem>map(CacheUtils::getDistributedSystem)
			.filter(GudDistributedSystem::isConnected)
			.map(GudDistributedSystem::getProperties)
			.map(properties -> properties.getProperty(DURABLE_CLIENT_ID_PROPERTY_NAME, null))
			.filter(StringUtils::hasText)
			.isPresent();
	}

	public static boolean close() {
		GudClientCache clientCache = getClientCache();
		if (clientCache != null) {
			return close(clientCache);
		}
		return true;
	}

	public static boolean close(@NonNull GudClientCache gemfireCache) {
		return close(gemfireCache, () -> {});
	}

	public static boolean close(@NonNull GudClientCache gemfireCache, @Nullable Runnable shutdownHook) {

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
			GudClientCache cache = GudCacheProvider.getAnyClientCache();
			if (cache != null) {
				cache.close();
				return true;
			}
			return false;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	public static boolean closeClientCache() {

		try {
			GudClientCache cache = GudCacheProvider.getAnyClientCache();
			if (cache != null) {
				cache.close();
				return true;
			}
			return false;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	public static GudClientCache getClientCache() {

		try {
			return GudCacheProvider.getAnyClientCache();
		}
		catch (GudCacheClosedException | IllegalStateException ignore) {
			return null;
		}
	}

	public static GudClientCache resolveGemFireCache() {
		return getClientCache();
	}
}
