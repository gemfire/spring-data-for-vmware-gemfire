/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory mock GudPoolManager backed by a ConcurrentHashMap
 */

package org.springframework.data.gemfire.gud.driver.mock;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudPoolManager;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * In-memory mock {@link GudPoolManager}.
 * <p>
 * Keeps pools registered by name so {@link #find(String)} and {@link #getAll()} return
 * consistent results across the lifetime of the mock driver.
 */
public class MockGudPoolManager implements GudPoolManager {

    private final ConcurrentHashMap<String, GudPool> pools = new ConcurrentHashMap<>();

    @Override
    public GudPoolFactory createFactory() {
        return MockGudFactories.newPoolFactory(this);
    }

    @Override
    public GudPool find(String name) {
        return pools.get(name);
    }

    @Override
    public GudPool find(GudRegion<?, ?> region) {
        return null;
    }

    @Override
    public Map<String, GudPool> getAll() {
        return Collections.unmodifiableMap(pools);
    }

    @Override
    public void close() {
        for (GudPool pool : pools.values()) {
            try {
                pool.destroy();
            } catch (Exception ignore) {
                // swallow — mocks never throw
            }
        }
        pools.clear();
    }

    @Override
    public void close(boolean keepAlive) {
        close();
    }

    /**
     * Registers a pool.  Package-private: used by {@link MockGudPoolFactoryImpl}
     * when a pool is created.
     *
     * @param pool the pool to register
     */
    void register(GudPool pool) {
        pools.put(pool.getName(), pool);
    }
}
