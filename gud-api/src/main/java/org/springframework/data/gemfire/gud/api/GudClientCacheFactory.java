/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudClientCacheFactory interface for cache creation and lookup
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire ClientCacheFactory.
 * Provides methods to create and retrieve client cache instances.
 */
public interface GudClientCacheFactory {

    /**
     * Gets any existing client cache instance.
     *
     * @return the existing client cache, or null if none exists
     * @throws GudCacheClosedException if the cache is closed
     */
    GudClientCache getAnyInstance();

    /**
     * Gets the GemFire product version.
     *
     * @return the version string
     */
    String getVersion();

    /**
     * Creates a new client cache with the given properties.
     *
     * @param properties the GemFire properties
     * @return the created client cache
     */
    GudClientCache create(Properties properties);

    /**
     * Sets a PDX serializer for the cache.
     *
     * @param serializer the PDX serializer
     * @return this factory for chaining
     */
    GudClientCacheFactory setPdxSerializer(GudPdxSerializer serializer);

    /**
     * Sets whether PDX should be read serialized.
     *
     * @param readSerialized true to read PDX as serialized
     * @return this factory for chaining
     */
    GudClientCacheFactory setPdxReadSerialized(boolean readSerialized);

    /**
     * Sets the PDX disk store name.
     *
     * @param diskStoreName the disk store name
     * @return this factory for chaining
     */
    GudClientCacheFactory setPdxDiskStore(String diskStoreName);

    /**
     * Sets whether PDX is persistent.
     *
     * @param persistent true if persistent
     * @return this factory for chaining
     */
    GudClientCacheFactory setPdxPersistent(boolean persistent);

    /**
     * Sets whether to ignore unread PDX fields.
     *
     * @param ignoreUnreadFields true to ignore
     * @return this factory for chaining
     */
    GudClientCacheFactory setPdxIgnoreUnreadFields(boolean ignoreUnreadFields);

    /**
     * Adds a pool locator.
     *
     * @param host the locator host
     * @param port the locator port
     * @return this factory for chaining
     */
    GudClientCacheFactory addPoolLocator(String host, int port);

    /**
     * Adds a pool server.
     *
     * @param host the server host
     * @param port the server port
     * @return this factory for chaining
     */
    GudClientCacheFactory addPoolServer(String host, int port);

    // Pool configuration methods
    GudClientCacheFactory setPoolFreeConnectionTimeout(int timeout);
    GudClientCacheFactory setPoolIdleTimeout(long timeout);
    GudClientCacheFactory setPoolLoadConditioningInterval(int interval);
    GudClientCacheFactory setPoolMinConnections(int minConnections);
    GudClientCacheFactory setPoolMaxConnections(int maxConnections);
    GudClientCacheFactory setPoolMinConnectionsPerServer(int minConnections);
    GudClientCacheFactory setPoolMaxConnectionsPerServer(int maxConnections);
    GudClientCacheFactory setPoolMultiuserAuthentication(boolean multiuser);
    GudClientCacheFactory setPoolPingInterval(long interval);
    GudClientCacheFactory setPoolPRSingleHopEnabled(boolean enabled);
    GudClientCacheFactory setPoolReadTimeout(int timeout);
    GudClientCacheFactory setPoolRetryAttempts(int retryAttempts);
    GudClientCacheFactory setPoolServerConnectionTimeout(int timeout);
    GudClientCacheFactory setPoolServerGroup(String group);
    GudClientCacheFactory setPoolSocketBufferSize(int size);
    GudClientCacheFactory setPoolSocketConnectTimeout(int timeout);
    GudClientCacheFactory setPoolSocketFactory(GudSocketFactory factory);
    GudClientCacheFactory setPoolStatisticInterval(int interval);
    GudClientCacheFactory setPoolSubscriptionAckInterval(int interval);
    GudClientCacheFactory setPoolSubscriptionEnabled(boolean enabled);
    GudClientCacheFactory setPoolSubscriptionMessageTrackingTimeout(int timeout);
    GudClientCacheFactory setPoolSubscriptionRedundancy(int redundancy);

    /**
     * Creates the client cache.
     *
     * @return the created client cache
     */
    GudClientCache create();
}
