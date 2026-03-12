/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudExcludedEvent interface for GUD API
 * 2026-03-12: Changed from interface to enum to match GemFire ExcludedEvent
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ExcludedEvent.
 * Enumeration of event types that can be excluded from CQ results.
 */
public enum GudExcludedEvent {
    UPDATE,
    CREATE,
    INVALIDATE,
    DESTROY
}
