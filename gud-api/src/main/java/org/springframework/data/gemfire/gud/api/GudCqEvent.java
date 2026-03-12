/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqEvent interface as 1:1 mapping of GemFire CqEvent
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqEvent interface.
 * Contains information about a continuous query event.
 */
public interface GudCqEvent {

    GudCqQuery getCq();

    GudCqEventType getBaseOperation();

    GudCqEventType getQueryOperation();

    Object getKey();

    Object getNewValue();

    byte[] getDeltaValue();

    Throwable getThrowable();
}
