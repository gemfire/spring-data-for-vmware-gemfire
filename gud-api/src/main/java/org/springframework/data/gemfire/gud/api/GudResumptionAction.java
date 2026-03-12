/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudResumptionAction enum as 1:1 mapping of GemFire ResumptionAction
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ResumptionAction.
 * Action when required roles are regained.
 */
public enum GudResumptionAction {

    NONE,
    REINITIALIZE;

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isReinitialize() {
        return this == REINITIALIZE;
    }
}
