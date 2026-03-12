/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFunctionContext interface as 1:1 mapping of GemFire FunctionContext
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire FunctionContext interface.
 * Provides context information for function execution.
 */
public interface GudFunctionContext {

    Object getArguments();

    String getFunctionId();

    GudResultSender<?> getResultSender();

    boolean isPossibleDuplicate();

    GudCache getCache();
}
