/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudEvictionAction enum as 1:1 mapping of GemFire EvictionAction
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EvictionAction.
 * The action to take when an entry is evicted.
 */
public enum GudEvictionAction {

    DEFAULT,
    NONE,
    LOCAL_DESTROY,
    OVERFLOW_TO_DISK;

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isLocalDestroy() {
        return this == LOCAL_DESTROY;
    }

    public boolean isOverflowToDisk() {
        return this == OVERFLOW_TO_DISK;
    }
}
