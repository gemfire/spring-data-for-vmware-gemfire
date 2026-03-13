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
 * 2026-03-11: Created GudPoolManager interface for GUD API
 * 2026-03-13: Changed to abstract class with static methods
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Map;

/**
 * GUD API abstraction for GemFire PoolManager.
 * This is an interface that drivers implement to provide pool management.
 * Use {@link GudCacheProvider#getPoolManager()} to get an instance.
 */
public interface GudPoolManager {

    /**
     * Creates a new pool factory.
     */
    GudPoolFactory createFactory();
    
    /**
     * Finds a pool by name.
     * @param name the pool name
     * @return the pool or null if not found
     */
    GudPool find(String name);
    
    /**
     * Finds the pool associated with a region.
     * @param region the region
     * @return the pool or null if not found
     */
    GudPool find(GudRegion<?, ?> region);
    
    /**
     * Gets all pools.
     * @return a map of pool name to pool
     */
    Map<String, GudPool> getAll();
    
    /**
     * Closes all pools.
     */
    void close();
    
    /**
     * Closes all pools with keepAlive option.
     * @param keepAlive whether to keep connections alive
     */
    void close(boolean keepAlive);
}
