/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudObjectSizer interface as 1:1 mapping of GemFire ObjectSizer
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ObjectSizer interface.
 * Used to determine memory size of objects for eviction.
 */
public interface GudObjectSizer {

    int sizeof(Object o);
}
