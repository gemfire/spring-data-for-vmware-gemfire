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
 * 2026-03-11: Created GudCqEvent interface as 1:1 mapping of GemFire CqEvent
 * 2026-03-31: Changed getBaseOperation/getQueryOperation to return GudOperation
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqEvent interface.
 * Contains information about a continuous query event.
 */
public interface GudCqEvent {

    GudCqQuery getCq();

    GudOperation getBaseOperation();

    GudOperation getQueryOperation();

    Object getKey();

    Object getNewValue();

    byte[] getDeltaValue();

    Throwable getThrowable();
}
