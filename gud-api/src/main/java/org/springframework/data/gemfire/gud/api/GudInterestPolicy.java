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
 * 2026-03-11: Created GudInterestPolicy enum as 1:1 mapping of GemFire InterestPolicy
 * 2026-04-01: Added fromOrdinal() method
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire InterestPolicy.
 * Determines interest registration behavior.
 */
public enum GudInterestPolicy {
    ALL,
    CACHE_CONTENT;

    public static final GudInterestPolicy DEFAULT = CACHE_CONTENT;

    private static final GudInterestPolicy[] VALUES = values();

    public static GudInterestPolicy fromOrdinal(byte ordinal) {
        if (ordinal >= 0 && ordinal < VALUES.length) {
            return VALUES[ordinal];
        }
        return null;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isAll() {
        return this == ALL;
    }

    public boolean isCacheContent() {
        return this == CACHE_CONTENT;
    }
}
