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
 * 2026-03-11: Created GudPool interface as 1:1 mapping of GemFire Pool
 * 2026-03-17: Changed getMin/MaxConnectionsPerServer() to default methods for 10.1+ feature
 */

package org.springframework.data.gemfire.gud.api;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * GUD API abstraction for GemFire Pool interface.
 * Represents a pool of connections to GemFire servers.
 */
public interface GudPool {

    String getName();

    // Connection configuration
    int getFreeConnectionTimeout();
    int getLoadConditioningInterval();
    int getSocketBufferSize();
    int getReadTimeout();
    int getSocketConnectTimeout();
    int getServerConnectionTimeout();
    boolean getThreadLocalConnections();
    int getMinConnections();
    int getMaxConnections();

    /**
     * Returns the minimum number of connections per server.
     * <p>This feature was added in GemFire 10.1. Drivers for older versions
     * will throw {@link GudUnsupportedOperationException}.
     *
     * @return the minimum connections per server
     * @throws GudUnsupportedOperationException if not supported by the driver
     * @since GemFire 10.1
     */
    default int getMinConnectionsPerServer() {
        throw new GudUnsupportedOperationException(
            "getMinConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
            "PER_SERVER_CONNECTION_LIMITS", "10.1");
    }

    /**
     * Returns the maximum number of connections per server.
     * <p>This feature was added in GemFire 10.1. Drivers for older versions
     * will throw {@link GudUnsupportedOperationException}.
     *
     * @return the maximum connections per server
     * @throws GudUnsupportedOperationException if not supported by the driver
     * @since GemFire 10.1
     */
    default int getMaxConnectionsPerServer() {
        throw new GudUnsupportedOperationException(
            "getMaxConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
            "PER_SERVER_CONNECTION_LIMITS", "10.1");
    }

    long getIdleTimeout();
    long getPingInterval();
    int getRetryAttempts();
    int getStatisticInterval();

    // Subscription configuration
    boolean getSubscriptionEnabled();
    int getSubscriptionRedundancy();
    int getSubscriptionMessageTrackingTimeout();
    int getSubscriptionAckInterval();
    int getSubscriptionTimeoutMultiplier();

    // Server groups and locators
    String getServerGroup();
    List<InetSocketAddress> getLocators();
    List<InetSocketAddress> getOnlineLocators();
    List<InetSocketAddress> getServers();

    // Query service
    GudQueryService getQueryService();

    // Connection status
    boolean isDestroyed();
    void destroy();
    void destroy(boolean keepAlive);
    int getPendingEventCount();

    // Multiuser authentication
    boolean getMultiuserAuthentication();

    // PR single hop
    boolean getPRSingleHopEnabled();

    // Socket factory
    GudSocketFactory getSocketFactory();
}
