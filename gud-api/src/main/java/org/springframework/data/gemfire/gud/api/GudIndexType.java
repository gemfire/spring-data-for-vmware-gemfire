/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudIndexType enum as 1:1 mapping of GemFire IndexType
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire IndexType enum.
 * Represents the type of an index.
 */
public enum GudIndexType {

    FUNCTIONAL,
    PRIMARY_KEY,
    HASH;

    public boolean isFunctional() {
        return this == FUNCTIONAL;
    }

    public boolean isPrimaryKey() {
        return this == PRIMARY_KEY;
    }

    public boolean isHash() {
        return this == HASH;
    }
}
