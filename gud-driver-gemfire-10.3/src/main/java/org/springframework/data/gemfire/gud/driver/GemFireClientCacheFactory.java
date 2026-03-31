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
 * 2026-03-13: Created GemFire 10.3 ClientCacheFactory implementation
 * 2026-03-17: Added per-server connection methods (supported in 10.1+)
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudCacheClosedException;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

import java.util.Properties;

/**
 * GemFire 10.3 implementation of GudClientCacheFactory.
 * Wraps the native ClientCacheFactory and delegates all operations.
 */
public class GemFireClientCacheFactory implements GudClientCacheFactory {

    private final ClientCacheFactory nativeFactory;

    public GemFireClientCacheFactory() {
        this.nativeFactory = new ClientCacheFactory();
    }

    public GemFireClientCacheFactory(Properties properties) {
        this.nativeFactory = new ClientCacheFactory(properties);
    }

    @Override
    public GudClientCacheFactory set(String name, String value) {
        nativeFactory.set(name, value);
        return this;
    }

    @Override
    public GudClientCacheFactory setPdxSerializer(GudPdxSerializer serializer) {
        if (serializer instanceof NativeWrapper) {
            nativeFactory.setPdxSerializer(((NativeWrapper<org.apache.geode.pdx.PdxSerializer>) serializer).getNative());
        }
        return this;
    }

    @Override
    public GudClientCacheFactory setPdxReadSerialized(boolean readSerialized) {
        nativeFactory.setPdxReadSerialized(readSerialized);
        return this;
    }

    @Override
    public GudClientCacheFactory setPdxDiskStore(String diskStoreName) {
        nativeFactory.setPdxDiskStore(diskStoreName);
        return this;
    }

    @Override
    public GudClientCacheFactory setPdxPersistent(boolean persistent) {
        nativeFactory.setPdxPersistent(persistent);
        return this;
    }

    @Override
    public GudClientCacheFactory setPdxIgnoreUnreadFields(boolean ignoreUnreadFields) {
        nativeFactory.setPdxIgnoreUnreadFields(ignoreUnreadFields);
        return this;
    }

    @Override
    public GudClientCacheFactory addPoolLocator(String host, int port) {
        nativeFactory.addPoolLocator(host, port);
        return this;
    }

    @Override
    public GudClientCacheFactory addPoolServer(String host, int port) {
        nativeFactory.addPoolServer(host, port);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolFreeConnectionTimeout(int timeout) {
        nativeFactory.setPoolFreeConnectionTimeout(timeout);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolIdleTimeout(long timeout) {
        nativeFactory.setPoolIdleTimeout(timeout);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolLoadConditioningInterval(int interval) {
        nativeFactory.setPoolLoadConditioningInterval(interval);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolMinConnections(int minConnections) {
        nativeFactory.setPoolMinConnections(minConnections);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolMaxConnections(int maxConnections) {
        nativeFactory.setPoolMaxConnections(maxConnections);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolMultiuserAuthentication(boolean multiuser) {
        nativeFactory.setPoolMultiuserAuthentication(multiuser);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolPingInterval(long interval) {
        nativeFactory.setPoolPingInterval(interval);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolPRSingleHopEnabled(boolean enabled) {
        nativeFactory.setPoolPRSingleHopEnabled(enabled);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolReadTimeout(int timeout) {
        nativeFactory.setPoolReadTimeout(timeout);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolRetryAttempts(int retryAttempts) {
        nativeFactory.setPoolRetryAttempts(retryAttempts);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolServerConnectionTimeout(int timeout) {
        nativeFactory.setPoolServerConnectionTimeout(timeout);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolServerGroup(String group) {
        nativeFactory.setPoolServerGroup(group);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSocketBufferSize(int size) {
        nativeFactory.setPoolSocketBufferSize(size);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSocketConnectTimeout(int timeout) {
        nativeFactory.setPoolSocketConnectTimeout(timeout);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSocketFactory(GudSocketFactory factory) {
        if (factory instanceof NativeWrapper) {
            nativeFactory.setPoolSocketFactory(((NativeWrapper<org.apache.geode.cache.client.SocketFactory>) factory).getNative());
        }
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolStatisticInterval(int interval) {
        nativeFactory.setPoolStatisticInterval(interval);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSubscriptionAckInterval(int interval) {
        nativeFactory.setPoolSubscriptionAckInterval(interval);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSubscriptionEnabled(boolean enabled) {
        nativeFactory.setPoolSubscriptionEnabled(enabled);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSubscriptionMessageTrackingTimeout(int timeout) {
        nativeFactory.setPoolSubscriptionMessageTrackingTimeout(timeout);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolSubscriptionRedundancy(int redundancy) {
        nativeFactory.setPoolSubscriptionRedundancy(redundancy);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolMinConnectionsPerServer(int minConnections) {
        nativeFactory.setPoolMinConnectionsPerServer(minConnections);
        return this;
    }

    @Override
    public GudClientCacheFactory setPoolMaxConnectionsPerServer(int maxConnections) {
        nativeFactory.setPoolMaxConnectionsPerServer(maxConnections);
        return this;
    }

    @Override
    public GudClientCache create() {
        return new GemFireClientCache(nativeFactory.create());
    }

    @Override
    public GudClientCache create(Properties properties) {
        // Apply properties to the factory
        if (properties != null) {
            for (String name : properties.stringPropertyNames()) {
                nativeFactory.set(name, properties.getProperty(name));
            }
        }
        return new GemFireClientCache(nativeFactory.create());
    }

    @Override
    public GudClientCache getAnyInstance() {
        try {
            org.apache.geode.cache.client.ClientCache nativeCache = 
                org.apache.geode.cache.client.ClientCacheFactory.getAnyInstance();
            return new GemFireClientCache(nativeCache);
        } catch (org.apache.geode.cache.CacheClosedException e) {
            throw new GudCacheClosedException(e.getMessage(), e);
        }
    }

    @Override
    public String getVersion() {
        // Return the supported version for this driver
        return "10.3";
    }
}
