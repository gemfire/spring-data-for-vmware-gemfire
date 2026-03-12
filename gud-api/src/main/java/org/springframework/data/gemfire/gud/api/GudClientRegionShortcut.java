/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudClientRegionShortcut enum as 1:1 mapping of GemFire ClientRegionShortcut
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ClientRegionShortcut.
 * Shortcuts for creating common client region configurations.
 */
public enum GudClientRegionShortcut {

    PROXY,
    CACHING_PROXY,
    CACHING_PROXY_HEAP_LRU,
    CACHING_PROXY_OVERFLOW,
    LOCAL,
    LOCAL_HEAP_LRU,
    LOCAL_OVERFLOW,
    LOCAL_PERSISTENT,
    LOCAL_PERSISTENT_OVERFLOW;

    public boolean isProxy() {
        return this == PROXY;
    }

    public boolean isCachingProxy() {
        return name().startsWith("CACHING_PROXY");
    }

    public boolean isLocal() {
        return name().startsWith("LOCAL");
    }

    public boolean isPersistent() {
        return name().contains("PERSISTENT");
    }

    public boolean isOverflow() {
        return name().contains("OVERFLOW");
    }

    public boolean isHeapLru() {
        return name().contains("HEAP_LRU");
    }
}
