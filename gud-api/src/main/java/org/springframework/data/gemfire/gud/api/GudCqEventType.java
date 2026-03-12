/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqEventType enum as equivalent to GemFire CqEvent operations
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CQ event types.
 * Represents the type of operation that triggered a CQ event.
 */
public enum GudCqEventType {

    CREATE,
    UPDATE,
    DESTROY,
    INVALIDATE,
    REGION_DESTROY,
    REGION_INVALIDATE,
    REGION_CLEAR;

    public boolean isCreate() {
        return this == CREATE;
    }

    public boolean isUpdate() {
        return this == UPDATE;
    }

    public boolean isDestroy() {
        return this == DESTROY;
    }

    public boolean isInvalidate() {
        return this == INVALIDATE;
    }

    public boolean isRegionOperation() {
        return name().startsWith("REGION");
    }
}
