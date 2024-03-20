/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.util.Optional;
import java.util.Properties;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.Locator;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.InternalLocator;

import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * DistributedSystemUtils is an abstract utility class for working with the GemFire DistributedSystem.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.distributed.DistributedSystem
 * @see org.apache.geode.distributed.internal.InternalDistributedSystem
 * @see org.apache.geode.internal.DistributionLocator
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public abstract class DistributedSystemUtils extends SpringExtensions {

	public static final int DEFAULT_CACHE_SERVER_PORT = CacheServer.DEFAULT_PORT;
	public static final int DEFAULT_LOCATOR_PORT = 10334;

	public static final String DURABLE_CLIENT_ID_PROPERTY_NAME = GemFireProperties.DURABLE_CLIENT_ID.getName();
	public static final String DURABLE_CLIENT_TIMEOUT_PROPERTY_NAME = GemFireProperties.DURABLE_CLIENT_TIMEOUT.getName();
	public static final String GEMFIRE_PREFIX = GemFireProperties.GEMFIRE_PROPERTY_NAME_PREFIX;
	public static final String NAME_PROPERTY_NAME = GemFireProperties.NAME.getName();

	public static Properties configureDurableClient(Properties gemfireProperties,
			String durableClientId, Integer durableClientTimeout) {

		if (StringUtils.hasText(durableClientId)) {

			Assert.notNull(gemfireProperties, "gemfireProperties are required");

			gemfireProperties.setProperty(DURABLE_CLIENT_ID_PROPERTY_NAME, durableClientId);

			if (durableClientTimeout != null) {
				gemfireProperties.setProperty(DURABLE_CLIENT_TIMEOUT_PROPERTY_NAME, durableClientTimeout.toString());
			}
		}

		return gemfireProperties;
	}

	public static boolean isConnected(DistributedSystem distributedSystem) {
		return Optional.ofNullable(distributedSystem).filter(DistributedSystem::isConnected).isPresent();
	}

	public static boolean isNotConnected(DistributedSystem distributedSystem) {
		return !isConnected(distributedSystem);
	}

	@SuppressWarnings("unchecked")
	public static <T extends DistributedSystem> T getDistributedSystem() {
		return (T) InternalDistributedSystem.getAnyInstance();
	}

	@SuppressWarnings("unchecked")
	public static <T extends DistributedSystem> T getDistributedSystem(GemFireCache gemfireCache) {
		return (T) Optional.ofNullable(gemfireCache).map(GemFireCache::getDistributedSystem).orElse(null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Locator> T getLocator() {
		return (T) InternalLocator.getLocator();
	}
}
