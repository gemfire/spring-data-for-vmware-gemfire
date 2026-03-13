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
 * 2026-03-11: Created GemFire 10.3 ClientCache adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.data.gemfire.gud.api.*;

import java.util.Set;

/**
 * GUD API adapter for GemFire 10.3 ClientCache.
 */
public class GemFire103ClientCache implements GudClientCache, NativeWrapper<ClientCache> {

    private final ClientCache nativeClientCache;

    public GemFire103ClientCache(ClientCache nativeClientCache) {
        this.nativeClientCache = nativeClientCache;
    }

    @Override
    public ClientCache getNative() {
        return nativeClientCache;
    }

    @Override
    public String getName() {
        return nativeClientCache.getName();
    }

    @Override
    public GudDistributedSystem getDistributedSystem() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudPool getDefaultPool() {
        return new GemFire103Pool(nativeClientCache.getDefaultPool());
    }

    @Override
    public Set<GudPool> getPools() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <K, V> GudClientRegionFactory<K, V> createClientRegionFactory(GudClientRegionShortcut shortcut) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudQueryService getQueryService() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudQueryService getQueryService(String poolName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setPdxSerializer(GudPdxSerializer serializer) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudPdxSerializer getPdxSerializer() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setPdxReadSerialized(boolean readSerialized) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean getPdxReadSerialized() {
        return nativeClientCache.getPdxReadSerialized();
    }

    @Override
    public void setPdxDiskStore(String diskStoreName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getPdxDiskStore() {
        return nativeClientCache.getPdxDiskStore();
    }

    @Override
    public void setPdxPersistent(boolean persistent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean getPdxPersistent() {
        return nativeClientCache.getPdxPersistent();
    }

    @Override
    public void setPdxIgnoreUnreadFields(boolean ignoreUnreadFields) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean getPdxIgnoreUnreadFields() {
        return nativeClientCache.getPdxIgnoreUnreadFields();
    }

    @Override
    public GudDiskStoreFactory createDiskStoreFactory() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudDiskStore findDiskStore(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setCopyOnRead(boolean copyOnRead) {
        nativeClientCache.setCopyOnRead(copyOnRead);
    }

    @Override
    public boolean getCopyOnRead() {
        return nativeClientCache.getCopyOnRead();
    }

    @Override
    public boolean isClosed() {
        return nativeClientCache.isClosed();
    }

    @Override
    public void close() {
        nativeClientCache.close();
    }

    @Override
    public void close(boolean keepAlive) {
        nativeClientCache.close(keepAlive);
    }

    @Override
    public boolean isReconnecting() {
        return false; // ClientCache doesn't support reconnecting
    }

    @Override
    public GudClientCache getReconnectedCache() {
        return null; // ClientCache doesn't support reconnecting
    }

    @Override
    public GudCacheTransactionManager getCacheTransactionManager() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudLogWriter getLogger() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudLogWriter getSecurityLogger() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <K, V> GudRegion<K, V> getRegion(String path) {
        org.apache.geode.cache.Region<K, V> region = nativeClientCache.getRegion(path);
        return region != null ? new GemFire103Region<>(region) : null;
    }

    @Override
    public Set<GudRegion<?, ?>> rootRegions() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudPdxInstanceFactory createPdxInstanceFactory(String className) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudResourceManager getResourceManager() {
        return new GemFire103ResourceManager(nativeClientCache.getResourceManager());
    }

    @Override
    public void loadCacheXml(java.io.InputStream inputStream) {
        nativeClientCache.loadCacheXml(inputStream);
    }

    @Override
    public void readyForEvents() {
        nativeClientCache.readyForEvents();
    }

    @Override
    public GudQueryService getLocalQueryService() {
        return new GemFire103QueryService(nativeClientCache.getLocalQueryService());
    }

    @Override
    public java.util.Set<?> getCurrentServers() {
        return nativeClientCache.getCurrentServers();
    }
}
