/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudScope enum as 1:1 mapping of GemFire Scope
 * 2026-04-01: Added fromOrdinal() method
 * 2026-06-06: fromOrdinal() now throws ArrayIndexOutOfBoundsException for invalid ordinals
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Scope.
 * Defines the scope of distribution for a region.
 */
public enum GudScope {

    LOCAL,
    DISTRIBUTED_NO_ACK,
    DISTRIBUTED_ACK,
    GLOBAL;

    private static final GudScope[] VALUES = values();

    public static GudScope fromOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

    public boolean isLocal() {
        return this == LOCAL;
    }

    public boolean isDistributed() {
        return this != LOCAL;
    }

    public boolean isDistributedNoAck() {
        return this == DISTRIBUTED_NO_ACK;
    }

    public boolean isDistributedAck() {
        return this == DISTRIBUTED_ACK;
    }

    public boolean isGlobal() {
        return this == GLOBAL;
    }

    public boolean isAck() {
        return this == DISTRIBUTED_ACK || this == GLOBAL;
    }
}
