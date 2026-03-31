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
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.2 PoolFactory adapter
 * 2026-03-17: Added per-server connection methods (supported in 10.1+)
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.client.PoolFactory;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

/**
 * GUD API adapter for GemFire 10.2 PoolFactory.
 * <p>Per-server connection limits (setMinConnectionsPerServer, setMaxConnectionsPerServer)
 * are supported in GemFire 10.1 and later.
 */
public class GemFirePoolFactory implements GudPoolFactory, NativeWrapper<PoolFactory> {

    private final PoolFactory nativeFactory;

    public GemFirePoolFactory(PoolFactory nativeFactory) {
        this.nativeFactory = nativeFactory;
    }

    @Override
    public PoolFactory getNative() {
        return nativeFactory;
    }

    @Override
    public GudPoolFactory setFreeConnectionTimeout(int connectionTimeout) {
        nativeFactory.setFreeConnectionTimeout(connectionTimeout);
        return this;
    }

    @Override
    public GudPoolFactory setLoadConditioningInterval(int loadConditioningInterval) {
        nativeFactory.setLoadConditioningInterval(loadConditioningInterval);
        return this;
    }

    @Override
    public GudPoolFactory setSocketBufferSize(int bufferSize) {
        nativeFactory.setSocketBufferSize(bufferSize);
        return this;
    }

    @Override
    public GudPoolFactory setReadTimeout(int timeout) {
        nativeFactory.setReadTimeout(timeout);
        return this;
    }

    @Override
    public GudPoolFactory setSocketConnectTimeout(int timeout) {
        nativeFactory.setSocketConnectTimeout(timeout);
        return this;
    }

    @Override
    public GudPoolFactory setThreadLocalConnections(boolean threadLocalConnections) {
        // setThreadLocalConnections is deprecated/removed in 10.2
        return this;
    }

    @Override
    public GudPoolFactory setMinConnections(int minConnections) {
        nativeFactory.setMinConnections(minConnections);
        return this;
    }

    @Override
    public GudPoolFactory setMaxConnections(int maxConnections) {
        nativeFactory.setMaxConnections(maxConnections);
        return this;
    }

    @Override
    public GudPoolFactory setMinConnectionsPerServer(int minConnections) {
        nativeFactory.setMinConnectionsPerServer(minConnections);
        return this;
    }

    @Override
    public GudPoolFactory setMaxConnectionsPerServer(int maxConnections) {
        nativeFactory.setMaxConnectionsPerServer(maxConnections);
        return this;
    }

    @Override
    public GudPoolFactory setServerConnectionTimeout(int timeout) {
        nativeFactory.setServerConnectionTimeout(timeout);
        return this;
    }

    @Override
    public GudPoolFactory setStatisticInterval(int interval) {
        nativeFactory.setStatisticInterval(interval);
        return this;
    }

    @Override
    public GudPoolFactory setIdleTimeout(long idleTimeout) {
        nativeFactory.setIdleTimeout(idleTimeout);
        return this;
    }

    @Override
    public GudPoolFactory setPingInterval(long pingInterval) {
        nativeFactory.setPingInterval(pingInterval);
        return this;
    }

    @Override
    public GudPoolFactory setRetryAttempts(int retryAttempts) {
        nativeFactory.setRetryAttempts(retryAttempts);
        return this;
    }

    @Override
    public GudPoolFactory setSubscriptionEnabled(boolean enabled) {
        nativeFactory.setSubscriptionEnabled(enabled);
        return this;
    }

    @Override
    public GudPoolFactory setSubscriptionRedundancy(int redundancy) {
        nativeFactory.setSubscriptionRedundancy(redundancy);
        return this;
    }

    @Override
    public GudPoolFactory setSubscriptionMessageTrackingTimeout(int messageTrackingTimeout) {
        nativeFactory.setSubscriptionMessageTrackingTimeout(messageTrackingTimeout);
        return this;
    }

    @Override
    public GudPoolFactory setSubscriptionAckInterval(int ackInterval) {
        nativeFactory.setSubscriptionAckInterval(ackInterval);
        return this;
    }

    @Override
    public GudPoolFactory setSubscriptionTimeoutMultiplier(int multiplier) {
        nativeFactory.setSubscriptionTimeoutMultiplier(multiplier);
        return this;
    }

    @Override
    public GudPoolFactory setServerGroup(String group) {
        nativeFactory.setServerGroup(group);
        return this;
    }

    @Override
    public GudPoolFactory addLocator(String host, int port) {
        nativeFactory.addLocator(host, port);
        return this;
    }

    @Override
    public GudPoolFactory addServer(String host, int port) {
        nativeFactory.addServer(host, port);
        return this;
    }

    @Override
    public GudPoolFactory setMultiuserAuthentication(boolean multiuserAuthentication) {
        nativeFactory.setMultiuserAuthentication(multiuserAuthentication);
        return this;
    }

    @Override
    public GudPoolFactory setPRSingleHopEnabled(boolean enabled) {
        nativeFactory.setPRSingleHopEnabled(enabled);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudPoolFactory setSocketFactory(GudSocketFactory socketFactory) {
        if (socketFactory instanceof NativeWrapper) {
            nativeFactory.setSocketFactory(((NativeWrapper<org.apache.geode.cache.client.SocketFactory>) socketFactory).getNative());
        }
        return this;
    }

    @Override
    public GudPoolFactory reset() {
        nativeFactory.reset();
        return this;
    }

    @Override
    public GudPool create(String name) {
        return new GemFirePool(nativeFactory.create(name));
    }
}
