/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPoolFactory interface as 1:1 mapping of GemFire PoolFactory
 */

package org.springframework.data.gemfire.gud.api;

import java.net.InetSocketAddress;

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
    GudPoolFactory setThreadLocalConnections(boolean threadLocalConnections);
    GudPoolFactory setMinConnections(int minConnections);
    GudPoolFactory setMaxConnections(int maxConnections);
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
