/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory mock GudClientCache that tracks regions, pools, and PDX settings
 */

package org.springframework.data.gemfire.gud.driver.mock;

import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.data.gemfire.gud.api.GudCacheTransactionManager;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudDiskStoreFactory;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.springframework.data.gemfire.gud.api.GudLogWriter;
import org.springframework.data.gemfire.gud.api.GudPdxInstanceFactory;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudResourceManager;

/**
 * In-memory mock {@link GudClientCache} implementation.  Tracks created regions, pools, and
 * PDX configuration.  Auxiliary subsystems (query service, distributed system, log writer,
 * resource manager) are returned as Mockito mocks created on demand.
 */
public class MockGudClientCache implements GudClientCache {

    private final String name;
    private final ConcurrentMap<String, GudRegion<?, ?>> regions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, GudDiskStore> diskStores = new ConcurrentHashMap<>();
    private final MockGudPoolManager poolManager;

    private final GudDistributedSystem distributedSystem = mock(GudDistributedSystem.class);
    private final GudQueryService queryService = mock(GudQueryService.class);
    private final GudCacheTransactionManager txManager = mock(GudCacheTransactionManager.class);
    private final GudLogWriter logger = mock(GudLogWriter.class);
    private final GudResourceManager resourceManager = mock(GudResourceManager.class);

    private volatile GudPdxSerializer pdxSerializer;
    private volatile String pdxDiskStore;
    private volatile boolean pdxReadSerialized;
    private volatile boolean pdxPersistent;
    private volatile boolean pdxIgnoreUnreadFields;
    private volatile boolean copyOnRead;
    private volatile boolean closed;

    public MockGudClientCache(String name, MockGudPoolManager poolManager) {
        this.name = name;
        this.poolManager = poolManager;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GudDistributedSystem getDistributedSystem() {
        return distributedSystem;
    }

    @Override
    public GudPool getDefaultPool() {
        GudPool existing = poolManager.find("DEFAULT");
        if (existing != null) {
            return existing;
        }
        MockGudPool defaultPool = new MockGudPool("DEFAULT");
        poolManager.register(defaultPool);
        return defaultPool;
    }

    @Override
    public Set<GudPool> getPools() {
        return new HashSet<>(poolManager.getAll().values());
    }

    @Override
    public <K, V> GudClientRegionFactory<K, V> createClientRegionFactory(GudClientRegionShortcut shortcut) {
        return MockGudFactories.newClientRegionFactory(this);
    }

    @Override
    public GudQueryService getQueryService() {
        return queryService;
    }

    @Override
    public GudQueryService getQueryService(String poolName) {
        return queryService;
    }

    @Override
    public void setPdxSerializer(GudPdxSerializer serializer) {
        this.pdxSerializer = serializer;
    }

    @Override
    public GudPdxSerializer getPdxSerializer() {
        return pdxSerializer;
    }

    @Override
    public void setPdxReadSerialized(boolean readSerialized) {
        this.pdxReadSerialized = readSerialized;
    }

    @Override
    public boolean getPdxReadSerialized() {
        return pdxReadSerialized;
    }

    @Override
    public void setPdxDiskStore(String diskStoreName) {
        this.pdxDiskStore = diskStoreName;
    }

    @Override
    public String getPdxDiskStore() {
        return pdxDiskStore;
    }

    @Override
    public void setPdxPersistent(boolean persistent) {
        this.pdxPersistent = persistent;
    }

    @Override
    public boolean getPdxPersistent() {
        return pdxPersistent;
    }

    @Override
    public void setPdxIgnoreUnreadFields(boolean ignoreUnreadFields) {
        this.pdxIgnoreUnreadFields = ignoreUnreadFields;
    }

    @Override
    public boolean getPdxIgnoreUnreadFields() {
        return pdxIgnoreUnreadFields;
    }

    @Override
    public GudDiskStoreFactory createDiskStoreFactory() {
        return MockGudFactories.newDiskStoreFactory(this);
    }

    @Override
    public GudDiskStore findDiskStore(String name) {
        return diskStores.get(name);
    }

    @Override
    public void setCopyOnRead(boolean copyOnRead) {
        this.copyOnRead = copyOnRead;
    }

    @Override
    public boolean getCopyOnRead() {
        return copyOnRead;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
        for (GudRegion<?, ?> region : regions.values()) {
            try {
                region.close();
            } catch (Exception ignore) {
                // no-op; mock regions never throw
            }
        }
        regions.clear();
    }

    @Override
    public void close(boolean keepAlive) {
        close();
    }

    @Override
    public boolean isReconnecting() {
        return false;
    }

    @Override
    public GudClientCache getReconnectedCache() {
        return null;
    }

    @Override
    public GudCacheTransactionManager getCacheTransactionManager() {
        return txManager;
    }

    @Override
    public GudLogWriter getLogger() {
        return logger;
    }

    @Override
    public GudLogWriter getSecurityLogger() {
        return logger;
    }

    @Override
    public GudResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public void loadCacheXml(InputStream inputStream) {
    }

    @Override
    public void readyForEvents() {
    }

    @Override
    public GudQueryService getLocalQueryService() {
        return queryService;
    }

    @Override
    public Set<?> getCurrentServers() {
        return Collections.emptySet();
    }

    // GudRegionService methods

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> GudRegion<K, V> getRegion(String path) {
        return (GudRegion<K, V>) regions.get(path);
    }

    @Override
    public Set<GudRegion<?, ?>> rootRegions() {
        return new HashSet<>(regions.values());
    }

    @Override
    public GudPdxInstanceFactory createPdxInstanceFactory(String className) {
        return mock(GudPdxInstanceFactory.class);
    }

    // === Mock-only helpers ===

    /**
     * Registers a region under its full path so {@link #getRegion(String)} can find it.
     *
     * @param region the region to register
     */
    void registerRegion(GudRegion<?, ?> region) {
        regions.put(region.getFullPath(), region);
        regions.put(region.getName(), region);
    }

    /**
     * Registers a disk store so {@link #findDiskStore(String)} can find it.
     *
     * @param diskStore the disk store to register
     */
    void registerDiskStore(GudDiskStore diskStore) {
        diskStores.put(diskStore.getName(), diskStore);
    }
}
