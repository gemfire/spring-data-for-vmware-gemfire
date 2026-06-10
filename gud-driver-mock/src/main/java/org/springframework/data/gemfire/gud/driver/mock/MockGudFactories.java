/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: Central factory helper that builds Gud* fluent-builder mocks using
 *             Answers.RETURNS_SELF; create(...) terminals return real in-memory
 *             GudClientCache / GudRegion / GudPool / GudDiskStore instances
 * 2026-04-17: Removed peer GudCacheFactory mock helper
 * 2026-06-06: Added newExecution() factory for MockGudFunctionService on*() methods
 */

package org.springframework.data.gemfire.gud.driver.mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.Properties;

import org.mockito.Answers;

import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudDiskStoreFactory;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudResultCollector;

/**
 * Produces Mockito-backed fluent-builder mocks for the Gud* factory interfaces.
 * <p>
 * Fluent setters are handled via {@link Answers#RETURNS_SELF}, which automatically returns
 * the mock itself for every method whose return type matches the mock's type.  The
 * {@code create(...)} terminals are explicitly stubbed to return real in-memory
 * implementations (e.g. {@link MockGudClientCache}, {@link MockGudRegion}).
 * <p>
 * Individual setter calls are NOT captured into the resulting cache/pool/diskstore state;
 * tests that need to verify "was setX called with Y" should use
 * {@code Mockito.verify(factory).setX(Y)} directly on the returned factory mock.
 */
final class MockGudFactories {

    static final String MOCK_VERSION = "10.99.0-MOCK";

    private MockGudFactories() {
    }

    // ===== ClientCacheFactory =====

    static GudClientCacheFactory newClientCacheFactory(MockGudPoolManager poolManager) {
        GudClientCacheFactory factory = mock(
            GudClientCacheFactory.class,
            withSettings().defaultAnswer(Answers.RETURNS_SELF));

        doAnswer(inv -> new MockGudClientCache("MockClientCache", poolManager))
            .when(factory).create();
        doAnswer(inv -> new MockGudClientCache("MockClientCache", poolManager))
            .when(factory).create(any(Properties.class));

        doAnswer(inv -> MOCK_VERSION).when(factory).getVersion();
        doAnswer(inv -> new MockGudClientCache("MockClientCache", poolManager))
            .when(factory).getAnyInstance();

        return factory;
    }

    // ===== PoolFactory =====

    static GudPoolFactory newPoolFactory(MockGudPoolManager poolManager) {
        GudPoolFactory factory = mock(
            GudPoolFactory.class,
            withSettings().defaultAnswer(Answers.RETURNS_SELF));

        doAnswer(inv -> {
            String name = inv.getArgument(0);
            MockGudPool pool = new MockGudPool(name);
            poolManager.register(pool);
            return pool;
        }).when(factory).create(anyString());

        return factory;
    }

    // ===== ClientRegionFactory =====

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <K, V> GudClientRegionFactory<K, V> newClientRegionFactory(MockGudClientCache cache) {
        GudClientRegionFactory<K, V> factory = mock(
            GudClientRegionFactory.class,
            withSettings().defaultAnswer(Answers.RETURNS_SELF));

        doAnswer(inv -> {
            String regionName = inv.getArgument(0);
            MockGudRegion<K, V> region = new MockGudRegion<>(regionName, cache);
            cache.registerRegion(region);
            return region;
        }).when(factory).create(anyString());

        doAnswer(inv -> {
            GudRegion<?, ?> parent = inv.getArgument(0);
            String subName = inv.getArgument(1);
            String path = parent.getFullPath() + GudRegion.SEPARATOR + subName;
            MockGudRegion<K, V> sub = new MockGudRegion<>(subName, path, parent, cache);
            cache.registerRegion(sub);
            return sub;
        }).when(factory).createSubregion(any(GudRegion.class), anyString());

        return factory;
    }

    // ===== DiskStoreFactory =====

    static GudDiskStoreFactory newDiskStoreFactory(MockGudClientCache cache) {
        return newDiskStoreFactoryInternal(cache::registerDiskStore);
    }

    private static GudDiskStoreFactory newDiskStoreFactoryInternal(DiskStoreSink sink) {
        GudDiskStoreFactory factory = mock(
            GudDiskStoreFactory.class,
            withSettings().defaultAnswer(Answers.RETURNS_SELF));

        MockGudDiskStore.MockGudDiskStoreState state = new MockGudDiskStore.MockGudDiskStoreState();

        doAnswer(inv -> {
            String name = inv.getArgument(0);
            MockGudDiskStore diskStore = new MockGudDiskStore(name, state);
            sink.register(diskStore);
            return diskStore;
        }).when(factory).create(anyString());

        return factory;
    }

    // ===== Execution =====

    /**
     * Returns a Mockito-backed {@link GudExecution} stub.
     * <p>
     * Fluent setter methods ({@code withFilter}, {@code withArgs}, {@code setArguments},
     * {@code withCollector}) return the mock itself via {@link Answers#RETURNS_SELF}.
     * {@code execute(String)} and {@code execute(GudFunction)} return a mock
     * {@link GudResultCollector}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static GudExecution newExecution() {
        GudResultCollector resultCollector = mock(GudResultCollector.class);
        GudExecution execution = mock(
            GudExecution.class,
            withSettings().defaultAnswer(Answers.RETURNS_SELF));
        doReturn(resultCollector).when(execution).execute(anyString());
        doReturn(resultCollector).when(execution).execute(any(GudFunction.class));
        return execution;
    }

    @FunctionalInterface
    private interface DiskStoreSink {
        void register(GudDiskStore diskStore);
    }
}
