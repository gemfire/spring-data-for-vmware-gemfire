/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFunction interface as 1:1 mapping of GemFire Function
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Function interface.
 * Represents a function that can be executed on GemFire members.
 */
public interface GudFunction {

    void execute(GudFunctionContext context);

    String getId();

    default boolean hasResult() {
        return true;
    }

    default boolean optimizeForWrite() {
        return false;
    }

    default boolean isHA() {
        return true;
    }
}
