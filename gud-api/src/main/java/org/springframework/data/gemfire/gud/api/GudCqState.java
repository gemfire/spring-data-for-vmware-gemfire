/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqState enum as 1:1 mapping of GemFire CqState
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqState.
 * Represents the state of a continuous query.
 */
public enum GudCqState {

    STOPPED,
    RUNNING,
    CLOSED,
    CLOSING;

    public boolean isStopped() {
        return this == STOPPED;
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }

    public boolean isClosing() {
        return this == CLOSING;
    }
}
