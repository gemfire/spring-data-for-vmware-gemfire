/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionShortcut enum as 1:1 mapping of GemFire RegionShortcut
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire RegionShortcut.
 * Shortcuts for creating common region configurations.
 */
public enum GudRegionShortcut {

    LOCAL,
    LOCAL_HEAP_LRU,
    LOCAL_OVERFLOW,
    LOCAL_PERSISTENT,
    LOCAL_PERSISTENT_OVERFLOW,

    PARTITION,
    PARTITION_HEAP_LRU,
    PARTITION_OVERFLOW,
    PARTITION_PERSISTENT,
    PARTITION_PERSISTENT_OVERFLOW,
    PARTITION_PROXY,
    PARTITION_PROXY_REDUNDANT,
    PARTITION_REDUNDANT,
    PARTITION_REDUNDANT_HEAP_LRU,
    PARTITION_REDUNDANT_OVERFLOW,
    PARTITION_REDUNDANT_PERSISTENT,
    PARTITION_REDUNDANT_PERSISTENT_OVERFLOW,

    REPLICATE,
    REPLICATE_HEAP_LRU,
    REPLICATE_OVERFLOW,
    REPLICATE_PERSISTENT,
    REPLICATE_PERSISTENT_OVERFLOW,
    REPLICATE_PROXY;

    public boolean isLocal() {
        return name().startsWith("LOCAL");
    }

    public boolean isPartition() {
        return name().startsWith("PARTITION");
    }

    public boolean isReplicate() {
        return name().startsWith("REPLICATE");
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

    public boolean isProxy() {
        return name().contains("PROXY");
    }

    public boolean isRedundant() {
        return name().contains("REDUNDANT");
    }
}
