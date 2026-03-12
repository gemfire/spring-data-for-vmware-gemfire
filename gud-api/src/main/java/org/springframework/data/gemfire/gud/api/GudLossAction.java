/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudLossAction enum as 1:1 mapping of GemFire LossAction
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire LossAction.
 * Action when required roles are lost.
 */
public enum GudLossAction {

    NO_ACCESS,
    LIMITED_ACCESS,
    FULL_ACCESS,
    RECONNECT;

    public boolean isNoAccess() {
        return this == NO_ACCESS;
    }

    public boolean isLimitedAccess() {
        return this == LIMITED_ACCESS;
    }

    public boolean isFullAccess() {
        return this == FULL_ACCESS;
    }

    public boolean isReconnect() {
        return this == RECONNECT;
    }
}
