/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory mock GudPool; stores configuration and tracks destroy state
 */

package org.springframework.data.gemfire.gud.driver.mock;

import static org.mockito.Mockito.mock;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

/**
 * In-memory mock {@link GudPool}.  Stores all configuration set through
 * {@link MockGudPoolFactoryState} and tracks destroyed state.
 */
public class MockGudPool implements GudPool {

    private final String name;
    private final MockGudPoolFactoryState state;
    private volatile boolean destroyed;

    public MockGudPool(String name) {
        this(name, new MockGudPoolFactoryState());
    }

    public MockGudPool(String name, MockGudPoolFactoryState state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getFreeConnectionTimeout() {
        return state.freeConnectionTimeout;
    }

    @Override
    public int getLoadConditioningInterval() {
        return state.loadConditioningInterval;
    }

    @Override
    public int getSocketBufferSize() {
        return state.socketBufferSize;
    }

    @Override
    public int getReadTimeout() {
        return state.readTimeout;
    }

    @Override
    public int getSocketConnectTimeout() {
        return state.socketConnectTimeout;
    }

    @Override
    public int getServerConnectionTimeout() {
        return state.serverConnectionTimeout;
    }

    @Override
    public boolean getThreadLocalConnections() {
        return state.threadLocalConnections;
    }

    @Override
    public int getMinConnections() {
        return state.minConnections;
    }

    @Override
    public int getMaxConnections() {
        return state.maxConnections;
    }

    @Override
    public int getMinConnectionsPerServer() {
        return state.minConnectionsPerServer;
    }

    @Override
    public int getMaxConnectionsPerServer() {
        return state.maxConnectionsPerServer;
    }

    @Override
    public long getIdleTimeout() {
        return state.idleTimeout;
    }

    @Override
    public long getPingInterval() {
        return state.pingInterval;
    }

    @Override
    public int getRetryAttempts() {
        return state.retryAttempts;
    }

    @Override
    public int getStatisticInterval() {
        return state.statisticInterval;
    }

    @Override
    public boolean getSubscriptionEnabled() {
        return state.subscriptionEnabled;
    }

    @Override
    public int getSubscriptionRedundancy() {
        return state.subscriptionRedundancy;
    }

    @Override
    public int getSubscriptionMessageTrackingTimeout() {
        return state.subscriptionMessageTrackingTimeout;
    }

    @Override
    public int getSubscriptionAckInterval() {
        return state.subscriptionAckInterval;
    }

    @Override
    public int getSubscriptionTimeoutMultiplier() {
        return state.subscriptionTimeoutMultiplier;
    }

    @Override
    public String getServerGroup() {
        return state.serverGroup;
    }

    @Override
    public List<InetSocketAddress> getLocators() {
        return Collections.unmodifiableList(new ArrayList<>(state.locators));
    }

    @Override
    public List<InetSocketAddress> getOnlineLocators() {
        return getLocators();
    }

    @Override
    public List<InetSocketAddress> getServers() {
        return Collections.unmodifiableList(new ArrayList<>(state.servers));
    }

    @Override
    public GudQueryService getQueryService() {
        return mock(GudQueryService.class);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public void destroy(boolean keepAlive) {
        destroyed = true;
    }

    @Override
    public int getPendingEventCount() {
        return 0;
    }

    @Override
    public boolean getMultiuserAuthentication() {
        return state.multiuserAuthentication;
    }

    @Override
    public boolean getPRSingleHopEnabled() {
        return state.prSingleHopEnabled;
    }

    @Override
    public GudSocketFactory getSocketFactory() {
        return state.socketFactory;
    }

    /**
     * Accumulates every setter call from a {@link GudPoolFactory}.  Applied to a
     * {@link MockGudPool} on {@code create(...)} so the resulting pool reports the
     * configured values.
     */
    public static class MockGudPoolFactoryState {

        int freeConnectionTimeout = GudPoolFactory.DEFAULT_FREE_CONNECTION_TIMEOUT;
        int loadConditioningInterval = GudPoolFactory.DEFAULT_LOAD_CONDITIONING_INTERVAL;
        int socketBufferSize = GudPoolFactory.DEFAULT_SOCKET_BUFFER_SIZE;
        int readTimeout = GudPoolFactory.DEFAULT_READ_TIMEOUT;
        int socketConnectTimeout = GudPoolFactory.DEFAULT_SOCKET_CONNECT_TIMEOUT;
        int serverConnectionTimeout = GudPoolFactory.DEFAULT_SERVER_CONNECTION_TIMEOUT;
        boolean threadLocalConnections = GudPoolFactory.DEFAULT_THREAD_LOCAL_CONNECTIONS;
        int minConnections = GudPoolFactory.DEFAULT_MIN_CONNECTIONS;
        int maxConnections = GudPoolFactory.DEFAULT_MAX_CONNECTIONS;
        int minConnectionsPerServer = GudPoolFactory.DEFAULT_MIN_CONNECTIONS_PER_SERVER;
        int maxConnectionsPerServer = GudPoolFactory.DEFAULT_MAX_CONNECTIONS_PER_SERVER;
        long idleTimeout = GudPoolFactory.DEFAULT_IDLE_TIMEOUT;
        long pingInterval = GudPoolFactory.DEFAULT_PING_INTERVAL;
        int retryAttempts = GudPoolFactory.DEFAULT_RETRY_ATTEMPTS;
        int statisticInterval = GudPoolFactory.DEFAULT_STATISTIC_INTERVAL;
        boolean subscriptionEnabled = GudPoolFactory.DEFAULT_SUBSCRIPTION_ENABLED;
        int subscriptionRedundancy = GudPoolFactory.DEFAULT_SUBSCRIPTION_REDUNDANCY;
        int subscriptionMessageTrackingTimeout = GudPoolFactory.DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT;
        int subscriptionAckInterval = GudPoolFactory.DEFAULT_SUBSCRIPTION_ACK_INTERVAL;
        int subscriptionTimeoutMultiplier = GudPoolFactory.DEFAULT_SUBSCRIPTION_TIMEOUT_MULTIPLIER;
        String serverGroup = GudPoolFactory.DEFAULT_SERVER_GROUP;
        boolean multiuserAuthentication = GudPoolFactory.DEFAULT_MULTIUSER_AUTHENTICATION;
        boolean prSingleHopEnabled = GudPoolFactory.DEFAULT_PR_SINGLE_HOP_ENABLED;
        GudSocketFactory socketFactory = GudPoolFactory.DEFAULT_SOCKET_FACTORY;
        final List<InetSocketAddress> locators = new ArrayList<>();
        final List<InetSocketAddress> servers = new ArrayList<>();
    }
}
