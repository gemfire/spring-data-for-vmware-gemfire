/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
 * 2026-03-11: Created GudPoolFactory interface as 1:1 mapping of GemFire PoolFactory
 * 2026-03-14: Changed per-server connection methods to default methods for API evolution
 * 2026-03-17: Fixed per-server connection method versions from 10.4 to 10.1
 * 2026-03-17: Added @Deprecated annotations for deprecated GemFire features
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire PoolFactory interface.
 * Factory for creating Pool instances.
 */
public interface GudPoolFactory {

    // Default values
    int DEFAULT_FREE_CONNECTION_TIMEOUT = 10000;
    int DEFAULT_LOAD_CONDITIONING_INTERVAL = 1000 * 60 * 5;
    int DEFAULT_SOCKET_BUFFER_SIZE = 32768;
    int DEFAULT_READ_TIMEOUT = 10000;
    int DEFAULT_SOCKET_CONNECT_TIMEOUT = 59000;
    int DEFAULT_SERVER_CONNECTION_TIMEOUT = 0;
    boolean DEFAULT_THREAD_LOCAL_CONNECTIONS = false;
    int DEFAULT_MIN_CONNECTIONS = 1;
    int DEFAULT_MAX_CONNECTIONS = -1;
    int DEFAULT_MIN_CONNECTIONS_PER_SERVER = 0;
    int DEFAULT_MAX_CONNECTIONS_PER_SERVER = Integer.MAX_VALUE;
    long DEFAULT_IDLE_TIMEOUT = 5000;
    long DEFAULT_PING_INTERVAL = 10000;
    int DEFAULT_RETRY_ATTEMPTS = -1;
    int DEFAULT_STATISTIC_INTERVAL = -1;
    boolean DEFAULT_SUBSCRIPTION_ENABLED = false;
    int DEFAULT_SUBSCRIPTION_REDUNDANCY = 0;
    int DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT = 900000;
    int DEFAULT_SUBSCRIPTION_ACK_INTERVAL = 100;
    int DEFAULT_SUBSCRIPTION_TIMEOUT_MULTIPLIER = 3;
    String DEFAULT_SERVER_GROUP = "";
    boolean DEFAULT_MULTIUSER_AUTHENTICATION = false;
    boolean DEFAULT_PR_SINGLE_HOP_ENABLED = true;
    GudSocketFactory DEFAULT_SOCKET_FACTORY = null;

    GudPoolFactory setFreeConnectionTimeout(int connectionTimeout);
    GudPoolFactory setLoadConditioningInterval(int loadConditioningInterval);
    GudPoolFactory setSocketBufferSize(int bufferSize);
    GudPoolFactory setReadTimeout(int timeout);
    GudPoolFactory setSocketConnectTimeout(int timeout);

    /**
     * Sets whether the pool should use thread local connections.
     *
     * @param threadLocalConnections whether to use thread local connections
     * @return this factory
     * @deprecated Since GemFire 10.0 (Geode 1.10.0). Thread local connections are ignored.
     *             This method is a no-op. Will be removed in a future major release.
     */
    @Deprecated
    GudPoolFactory setThreadLocalConnections(boolean threadLocalConnections);

    GudPoolFactory setMinConnections(int minConnections);
    GudPoolFactory setMaxConnections(int maxConnections);

    /**
     * Sets the minimum number of connections to each server.
     * <p>This feature was added in GemFire 10.1. Drivers for older versions
     * will throw {@link GudUnsupportedOperationException}.
     *
     * @param minConnections the minimum connections per server
     * @return this factory
     * @throws GudUnsupportedOperationException if not supported by the driver
     * @since GemFire 10.1
     */
    default GudPoolFactory setMinConnectionsPerServer(int minConnections) {
        throw new GudUnsupportedOperationException(
            "setMinConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
            "PER_SERVER_CONNECTION_LIMITS", "10.1");
    }

    /**
     * Sets the maximum number of connections to each server.
     * <p>This feature was added in GemFire 10.1. Drivers for older versions
     * will throw {@link GudUnsupportedOperationException}.
     *
     * @param maxConnections the maximum connections per server
     * @return this factory
     * @throws GudUnsupportedOperationException if not supported by the driver
     * @since GemFire 10.1
     */
    default GudPoolFactory setMaxConnectionsPerServer(int maxConnections) {
        throw new GudUnsupportedOperationException(
            "setMaxConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
            "PER_SERVER_CONNECTION_LIMITS", "10.1");
    }

    GudPoolFactory setServerConnectionTimeout(int timeout);
    GudPoolFactory setStatisticInterval(int interval);
    GudPoolFactory setIdleTimeout(long idleTimeout);
    GudPoolFactory setPingInterval(long pingInterval);
    GudPoolFactory setRetryAttempts(int retryAttempts);
    GudPoolFactory setSubscriptionEnabled(boolean enabled);
    GudPoolFactory setSubscriptionRedundancy(int redundancy);
    GudPoolFactory setSubscriptionMessageTrackingTimeout(int messageTrackingTimeout);
    GudPoolFactory setSubscriptionAckInterval(int ackInterval);
    GudPoolFactory setSubscriptionTimeoutMultiplier(int multiplier);
    GudPoolFactory setServerGroup(String group);
    GudPoolFactory addLocator(String host, int port);
    GudPoolFactory addServer(String host, int port);
    GudPoolFactory setMultiuserAuthentication(boolean multiuserAuthentication);
    GudPoolFactory setPRSingleHopEnabled(boolean enabled);
    GudPoolFactory setSocketFactory(GudSocketFactory socketFactory);

    GudPoolFactory reset();

    GudPool create(String name);
}
