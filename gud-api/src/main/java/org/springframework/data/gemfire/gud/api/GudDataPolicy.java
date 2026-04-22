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
 * 2026-03-11: Created GudDataPolicy enum as 1:1 mapping of GemFire DataPolicy
 * 2026-03-31: Added fromOrdinal() method
 * 2026-04-17: Document client-only policy set; server-native policies belong outside GudDataPolicy
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire DataPolicy.
 * Defines how data is stored in a region.
 * <p>Server-only native policies (e.g. REPLICATE, PARTITION) are not part of this enum; peer/server
 * region configuration belongs on the server side (e.g. GemFire Testcontainers), not in the GUD
 * client-facing data-policy contract.
 */
public enum GudDataPolicy {

    DEFAULT,
    EMPTY,
    NORMAL,
    PERSISTENT_REPLICATE,
    PRELOADED;

    private static final GudDataPolicy[] VALUES = values();

    public static GudDataPolicy fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < VALUES.length) {
            return VALUES[ordinal];
        }
        return null;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isPreloaded() {
        return this == PRELOADED;
    }

    public boolean isPersistent() {
        return this == PERSISTENT_REPLICATE;
    }

    public boolean withStorage() {
        return this != EMPTY;
    }

    public boolean withPersistence() {
        return isPersistent();
    }

    public boolean withPreloaded() {
        return isPreloaded();
    }
}
