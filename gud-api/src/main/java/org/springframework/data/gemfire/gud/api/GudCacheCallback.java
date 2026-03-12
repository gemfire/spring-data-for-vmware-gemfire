/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheCallback interface as 1:1 mapping of GemFire CacheCallback
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheCallback interface.
 * Base interface for cache callbacks.
 */
public interface GudCacheCallback {

    default void close() {
        // Default no-op implementation
    }
}
