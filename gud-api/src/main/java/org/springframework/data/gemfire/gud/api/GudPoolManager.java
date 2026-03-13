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
 */
public abstract class GudPoolManager {

    public static GudPoolFactory createFactory() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
    
    public static GudPool find(String name) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
    
    public static GudPool find(GudRegion<?, ?> region) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
    
    public static Map<String, GudPool> getAll() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
    
    public static void close() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
    
    public static void close(boolean keepAlive) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
}
