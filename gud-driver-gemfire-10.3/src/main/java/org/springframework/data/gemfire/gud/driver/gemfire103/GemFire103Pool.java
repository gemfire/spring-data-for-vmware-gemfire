/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GemFire 10.3 Pool adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * GUD API adapter for GemFire 10.3 Pool.
 */
public class GemFire103Pool implements GudPool, NativeWrapper<Pool> {

    private final Pool nativePool;

    public GemFire103Pool(Pool nativePool) {
        this.nativePool = nativePool;
    }

    @Override
    public Pool getNative() {
        return nativePool;
    }

    @Override
    public String getName() {
        return nativePool.getName();
    }

    @Override
    public int getFreeConnectionTimeout() {
        return nativePool.getFreeConnectionTimeout();
    }

    @Override
    public int getLoadConditioningInterval() {
        return nativePool.getLoadConditioningInterval();
    }

    @Override
    public int getSocketBufferSize() {
        return nativePool.getSocketBufferSize();
    }

    @Override
    public int getSocketConnectTimeout() {
        return nativePool.getSocketConnectTimeout();
    }

    @Override
    public int getReadTimeout() {
        return nativePool.getReadTimeout();
    }

    @Override
    public boolean getThreadLocalConnections() {
        return false; // Deprecated in newer GemFire versions
    }

    @Override
    public int getMinConnections() {
        return nativePool.getMinConnections();
    }

    @Override
    public int getMaxConnections() {
        return nativePool.getMaxConnections();
    }

    @Override
    public long getIdleTimeout() {
        return nativePool.getIdleTimeout();
    }

    @Override
    public long getPingInterval() {
        return nativePool.getPingInterval();
    }

    @Override
    public int getRetryAttempts() {
        return nativePool.getRetryAttempts();
    }

    @Override
    public List<InetSocketAddress> getLocators() {
        return nativePool.getLocators();
    }

    @Override
    public List<InetSocketAddress> getOnlineLocators() {
        return nativePool.getOnlineLocators();
    }

    @Override
    public List<InetSocketAddress> getServers() {
        return nativePool.getServers();
    }

    @Override
    public String getServerGroup() {
        return nativePool.getServerGroup();
    }

    @Override
    public int getSubscriptionRedundancy() {
        return nativePool.getSubscriptionRedundancy();
    }

    @Override
    public int getSubscriptionMessageTrackingTimeout() {
        return nativePool.getSubscriptionMessageTrackingTimeout();
    }

    @Override
    public int getSubscriptionAckInterval() {
        return nativePool.getSubscriptionAckInterval();
    }

    @Override
    public int getSubscriptionTimeoutMultiplier() {
        return nativePool.getSubscriptionTimeoutMultiplier();
    }

    @Override
    public boolean getMultiuserAuthentication() {
        return nativePool.getMultiuserAuthentication();
    }

    @Override
    public boolean getPRSingleHopEnabled() {
        return nativePool.getPRSingleHopEnabled();
    }

    @Override
    public GudSocketFactory getSocketFactory() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int getPendingEventCount() {
        return nativePool.getPendingEventCount();
    }

    @Override
    public boolean isDestroyed() {
        return nativePool.isDestroyed();
    }

    @Override
    public void destroy() {
        nativePool.destroy();
    }

    @Override
    public void destroy(boolean keepAlive) {
        nativePool.destroy(keepAlive);
    }

    @Override
    public GudQueryService getQueryService() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
