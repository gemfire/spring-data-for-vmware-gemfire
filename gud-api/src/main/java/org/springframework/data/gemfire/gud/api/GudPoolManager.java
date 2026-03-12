/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPoolManager interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Map;

/**
 * GUD API abstraction for GemFire PoolManager.
 */
public interface GudPoolManager {

    GudPoolFactory createFactory();
    
    GudPool find(String name);
    
    Map<String, GudPool> getAll();
    
    void close();
    
    void close(boolean keepAlive);
}
