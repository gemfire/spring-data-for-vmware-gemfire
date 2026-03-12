/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDataPolicy enum as 1:1 mapping of GemFire DataPolicy
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire DataPolicy.
 * Defines how data is stored in a region.
 */
public enum GudDataPolicy {

    DEFAULT,
    EMPTY,
    NORMAL,
    REPLICATE,
    PERSISTENT_REPLICATE,
    PARTITION,
    PERSISTENT_PARTITION,
    PRELOADED;

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isReplicate() {
        return this == REPLICATE || this == PERSISTENT_REPLICATE;
    }

    public boolean isPartition() {
        return this == PARTITION || this == PERSISTENT_PARTITION;
    }

    public boolean isPreloaded() {
        return this == PRELOADED;
    }

    public boolean isPersistent() {
        return this == PERSISTENT_REPLICATE || this == PERSISTENT_PARTITION;
    }

    public boolean withStorage() {
        return this != EMPTY;
    }

    public boolean withReplication() {
        return isReplicate();
    }

    public boolean withPartitioning() {
        return isPartition();
    }

    public boolean withPersistence() {
        return isPersistent();
    }

    public boolean withPreloaded() {
        return isPreloaded();
    }
}
