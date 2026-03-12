/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudEvictionAlgorithm enum as 1:1 mapping of GemFire EvictionAlgorithm
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EvictionAlgorithm.
 * The algorithm used for eviction.
 */
public enum GudEvictionAlgorithm {

    NONE,
    LRU_ENTRY,
    LRU_HEAP,
    LRU_MEMORY,
    LIFO_ENTRY,
    LIFO_MEMORY;

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isLRUEntry() {
        return this == LRU_ENTRY;
    }

    public boolean isLRUHeap() {
        return this == LRU_HEAP;
    }

    public boolean isLRUMemory() {
        return this == LRU_MEMORY;
    }

    public boolean isLIFOEntry() {
        return this == LIFO_ENTRY;
    }

    public boolean isLIFOMemory() {
        return this == LIFO_MEMORY;
    }

    public boolean isLRU() {
        return name().startsWith("LRU");
    }

    public boolean isLIFO() {
        return name().startsWith("LIFO");
    }
}
