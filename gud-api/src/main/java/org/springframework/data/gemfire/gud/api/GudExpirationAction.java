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
 * 2026-03-11: Created GudExpirationAction enum as 1:1 mapping of GemFire ExpirationAction
 * 2026-04-01: Added fromOrdinal() method
 * 2026-06-06: fromOrdinal() now throws ArrayIndexOutOfBoundsException for invalid ordinals
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ExpirationAction.
 * The action to take when an entry expires.
 */
public enum GudExpirationAction {

    DESTROY,
    LOCAL_DESTROY,
    INVALIDATE,
    LOCAL_INVALIDATE;

    private static final GudExpirationAction[] VALUES = values();

    public static GudExpirationAction fromOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

    public boolean isDestroy() {
        return this == DESTROY;
    }

    public boolean isLocalDestroy() {
        return this == LOCAL_DESTROY;
    }

    public boolean isInvalidate() {
        return this == INVALIDATE;
    }

    public boolean isLocalInvalidate() {
        return this == LOCAL_INVALIDATE;
    }

    public boolean isLocal() {
        return this == LOCAL_DESTROY || this == LOCAL_INVALIDATE;
    }

    public boolean isDistributed() {
        return this == DESTROY || this == INVALIDATE;
    }
}
