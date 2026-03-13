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
 * 2026-03-11: Created GudClientCacheFactory interface for cache creation and lookup
 * 2026-03-13: Changed to abstract class with static factory methods
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire ClientCacheFactory.
 * Provides methods to create and retrieve client cache instances.
 */
public abstract class GudClientCacheFactory {

    /**
     * Gets any existing client cache instance.
     *
     * @return the existing client cache, or null if none exists
     * @throws GudCacheClosedException if the cache is closed
     */
    public static GudClientCache getAnyInstance() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    /**
     * Gets the GemFire product version.
     *
     * @return the version string
     */
    public static String getVersion() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    /**
     * Creates a new client cache with the given properties.
     *
     * @param properties the GemFire properties
     * @return the created client cache
     */
    public abstract GudClientCache create(Properties properties);

    /**
     * Sets a PDX serializer for the cache.
     *
     * @param serializer the PDX serializer
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory setPdxSerializer(GudPdxSerializer serializer);

    /**
     * Sets whether PDX should be read serialized.
     *
     * @param readSerialized true to read PDX as serialized
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory setPdxReadSerialized(boolean readSerialized);

    /**
     * Sets the PDX disk store name.
     *
     * @param diskStoreName the disk store name
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory setPdxDiskStore(String diskStoreName);

    /**
     * Sets whether PDX is persistent.
     *
     * @param persistent true if persistent
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory setPdxPersistent(boolean persistent);

    /**
     * Sets whether to ignore unread PDX fields.
     *
     * @param ignoreUnreadFields true to ignore
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory setPdxIgnoreUnreadFields(boolean ignoreUnreadFields);

    /**
     * Adds a pool locator.
     *
     * @param host the locator host
     * @param port the locator port
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory addPoolLocator(String host, int port);

    /**
     * Adds a pool server.
     *
     * @param host the server host
     * @param port the server port
     * @return this factory for chaining
     */
    public abstract GudClientCacheFactory addPoolServer(String host, int port);

    // Pool configuration methods
    public abstract GudClientCacheFactory setPoolFreeConnectionTimeout(int timeout);
    public abstract GudClientCacheFactory setPoolIdleTimeout(long timeout);
    public abstract GudClientCacheFactory setPoolLoadConditioningInterval(int interval);
    public abstract GudClientCacheFactory setPoolMinConnections(int minConnections);
    public abstract GudClientCacheFactory setPoolMaxConnections(int maxConnections);
    public abstract GudClientCacheFactory setPoolMinConnectionsPerServer(int minConnections);
    public abstract GudClientCacheFactory setPoolMaxConnectionsPerServer(int maxConnections);
    public abstract GudClientCacheFactory setPoolMultiuserAuthentication(boolean multiuser);
    public abstract GudClientCacheFactory setPoolPingInterval(long interval);
    public abstract GudClientCacheFactory setPoolPRSingleHopEnabled(boolean enabled);
    public abstract GudClientCacheFactory setPoolReadTimeout(int timeout);
    public abstract GudClientCacheFactory setPoolRetryAttempts(int retryAttempts);
    public abstract GudClientCacheFactory setPoolServerConnectionTimeout(int timeout);
    public abstract GudClientCacheFactory setPoolServerGroup(String group);
    public abstract GudClientCacheFactory setPoolSocketBufferSize(int size);
    public abstract GudClientCacheFactory setPoolSocketConnectTimeout(int timeout);
    public abstract GudClientCacheFactory setPoolSocketFactory(GudSocketFactory factory);
    public abstract GudClientCacheFactory setPoolStatisticInterval(int interval);
    public abstract GudClientCacheFactory setPoolSubscriptionAckInterval(int interval);
    public abstract GudClientCacheFactory setPoolSubscriptionEnabled(boolean enabled);
    public abstract GudClientCacheFactory setPoolSubscriptionMessageTrackingTimeout(int timeout);
    public abstract GudClientCacheFactory setPoolSubscriptionRedundancy(int redundancy);

    /**
     * Creates the client cache.
     *
     * @return the created client cache
     */
    public abstract GudClientCache create();
}
