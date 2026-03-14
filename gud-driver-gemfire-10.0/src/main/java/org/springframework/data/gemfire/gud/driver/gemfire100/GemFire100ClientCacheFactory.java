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
 * 2026-03-13: Created GemFire 10.0 ClientCacheFactory implementation
 * 2026-03-14: Removed per-server connection methods (use default methods that throw)
 */

package org.springframework.data.gemfire.gud.driver.gemfire100;

import java.util.Properties;

import org.apache.geode.cache.client.ClientCacheFactory;

import org.springframework.data.gemfire.gud.api.GudCacheClosedException;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

/**
 * GemFire 10.0 implementation of GudClientCacheFactory.
 * Wraps the native ClientCacheFactory and delegates all operations.
 * <p>Note: Per-server connection limits (setPoolMinConnectionsPerServer, setPoolMaxConnectionsPerServer)
 * are not supported in GemFire 10.0. The inherited default methods will throw
 * {@link org.springframework.data.gemfire.gud.api.GudUnsupportedOperationException}.
 */
public class GemFire100ClientCacheFactory implements GudClientCacheFactory {

    private final ClientCacheFactory nativeFactory;

    public GemFire100ClientCacheFactory() {
        this.nativeFactory = new ClientCacheFactory();
    }

    public GemFire100ClientCacheFactory(Properties properties) {
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

    // Note: setPoolMinConnectionsPerServer and setPoolMaxConnectionsPerServer are NOT overridden.
    // The default methods in GudClientCacheFactory will throw GudUnsupportedOperationException
    // since these features are not available in GemFire 10.0.

    @Override
    public GudClientCache create() {
        return new GemFire100ClientCache(nativeFactory.create());
    }

    @Override
    public GudClientCache create(Properties properties) {
        // Apply properties to the factory
        if (properties != null) {
            for (String name : properties.stringPropertyNames()) {
                nativeFactory.set(name, properties.getProperty(name));
            }
        }
        return new GemFire100ClientCache(nativeFactory.create());
    }

    @Override
    public GudClientCache getAnyInstance() {
        try {
            org.apache.geode.cache.client.ClientCache nativeCache = 
                org.apache.geode.cache.client.ClientCacheFactory.getAnyInstance();
            return new GemFire100ClientCache(nativeCache);
        } catch (org.apache.geode.cache.CacheClosedException e) {
            throw new GudCacheClosedException(e.getMessage(), e);
        }
    }

    @Override
    public String getVersion() {
        // Return the supported version for this driver
        return "10.0";
    }
}
