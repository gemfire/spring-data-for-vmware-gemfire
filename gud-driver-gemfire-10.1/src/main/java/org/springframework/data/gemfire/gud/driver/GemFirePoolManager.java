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
 * 2026-03-13: Created GemFire 10.1 PoolManager implementation
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudPoolManager;
import org.springframework.data.gemfire.gud.api.GudRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * GemFire 10.1 implementation of GudPoolManager.
 * Delegates to native PoolManager.
 */
public class GemFirePoolManager implements GudPoolManager {

    @Override
    public GudPoolFactory createFactory() {
        return new GemFirePoolFactory(PoolManager.createFactory());
    }

    @Override
    public GudPool find(String name) {
        Pool pool = PoolManager.find(name);
        return pool != null ? new GemFirePool(pool) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudPool find(GudRegion<?, ?> region) {
        if (region instanceof NativeWrapper) {
            org.apache.geode.cache.Region<?, ?> nativeRegion = 
                ((NativeWrapper<org.apache.geode.cache.Region<?, ?>>) region).getNative();
            Pool pool = PoolManager.find(nativeRegion);
            return pool != null ? new GemFirePool(pool) : null;
        }
        return null;
    }

    @Override
    public Map<String, GudPool> getAll() {
        Map<String, GudPool> result = new HashMap<>();
        for (Map.Entry<String, Pool> entry : PoolManager.getAll().entrySet()) {
            result.put(entry.getKey(), new GemFirePool(entry.getValue()));
        }
        return result;
    }

    @Override
    public void close() {
        PoolManager.close();
    }

    @Override
    public void close(boolean keepAlive) {
        PoolManager.close(keepAlive);
    }
}
