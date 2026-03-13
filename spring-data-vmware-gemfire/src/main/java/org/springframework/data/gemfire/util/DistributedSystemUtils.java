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
import java.util.Properties;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * DistributedSystemUtils is an abstract utility class for working with the GemFire DistributedSystem.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudDistributedSystem
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public abstract class DistributedSystemUtils extends SpringExtensions {

	public static final int DEFAULT_CACHE_SERVER_PORT = 40404;
	public static final int DEFAULT_LOCATOR_PORT = 10334;

	public static final String DURABLE_CLIENT_ID_PROPERTY_NAME = GemFireProperties.DURABLE_CLIENT_ID.getName();
	public static final String DURABLE_CLIENT_TIMEOUT_PROPERTY_NAME = GemFireProperties.DURABLE_CLIENT_TIMEOUT.getName();
	public static final String GEMFIRE_PREFIX = GemFireProperties.GEMFIRE_PROPERTY_NAME_PREFIX;
	public static final String NAME_PROPERTY_NAME = GemFireProperties.NAME.getName();

	public static @NonNull Properties configureDurableClient(@NonNull Properties gemfireProperties,
			@Nullable String durableClientId, @Nullable Integer durableClientTimeout) {

		if (StringUtils.hasText(durableClientId)) {

			Assert.notNull(gemfireProperties, "gemfireProperties are required");

			gemfireProperties.setProperty(DURABLE_CLIENT_ID_PROPERTY_NAME, durableClientId);

			if (durableClientTimeout != null) {
				gemfireProperties.setProperty(DURABLE_CLIENT_TIMEOUT_PROPERTY_NAME, durableClientTimeout.toString());
			}
		}

		return gemfireProperties;
	}

	public static boolean isConnected(@Nullable GudDistributedSystem distributedSystem) {

		return Optional.ofNullable(distributedSystem)
			.filter(GudDistributedSystem::isConnected)
			.isPresent();
	}

	public static boolean isNotConnected(@Nullable GudDistributedSystem distributedSystem) {
		return !isConnected(distributedSystem);
	}

	@SuppressWarnings("unchecked")
	public static @Nullable <T extends GudDistributedSystem> T getDistributedSystem() {
		return (T) GudCacheProvider.getDistributedSystem();
	}

	@SuppressWarnings("unchecked")
	public static @Nullable <T extends GudDistributedSystem> T getDistributedSystem(GudClientCache gemfireCache) {

		return (T) Optional.ofNullable(gemfireCache)
			.map(GudClientCache::getDistributedSystem)
			.orElse(null);
	}

}
