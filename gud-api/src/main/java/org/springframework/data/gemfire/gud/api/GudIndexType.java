/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudIndexType enum as 1:1 mapping of GemFire IndexType
 * 2026-03-17: Marked entire enum and HASH constant as deprecated per GemFire deprecations
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire IndexType enum.
 * Represents the type of an index.
 *
 * @deprecated IndexType has been deprecated in GemFire. Use the non-IndexType overloads
 *             of createIndex() and defineIndex() methods instead. The HASH index type
 *             is no longer recommended.
 */
@Deprecated
public enum GudIndexType {

    FUNCTIONAL,
    PRIMARY_KEY,

    /**
     * @deprecated Hash indexes have been deprecated. Use FUNCTIONAL indexes instead.
     */
    @Deprecated
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
