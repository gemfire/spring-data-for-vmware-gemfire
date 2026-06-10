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
 * 2026-03-11: Created GudInterestResultPolicy enum as 1:1 mapping of GemFire InterestResultPolicy
 * 2026-04-01: Added fromOrdinal() method
 * 2026-06-06: fromOrdinal() now throws ArrayIndexOutOfBoundsException for invalid ordinals
 * 2026-06-06: DEFAULT removed as enum constant; now a static alias for KEYS_VALUES (matches native behaviour)
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire InterestResultPolicy.
 * Determines which entries are returned when interest is registered.
 */
public enum GudInterestResultPolicy {

    NONE,
    KEYS,
    KEYS_VALUES;

    public static final GudInterestResultPolicy DEFAULT = KEYS_VALUES;

    private static final GudInterestResultPolicy[] VALUES = values();

    public static GudInterestResultPolicy fromOrdinal(byte ordinal) {
        return VALUES[ordinal];
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isKeys() {
        return this == KEYS;
    }

    public boolean isKeysValues() {
        return this == KEYS_VALUES;
    }
}
