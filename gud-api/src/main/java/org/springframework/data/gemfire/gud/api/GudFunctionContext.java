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
 * 2026-03-11: Created GudFunctionContext interface as 1:1 mapping of GemFire FunctionContext
 * 2026-03-31: Changed getResultSender() to raw type for flexibility
 * 2026-04-17: Deprecated getCache() return type (peer GudCache) for client-only GUD direction
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire FunctionContext interface.
 * Provides context information for function execution.
 */
public interface GudFunctionContext {

    Object getArguments();

    String getFunctionId();

    @SuppressWarnings("rawtypes")
    GudResultSender getResultSender();

    boolean isPossibleDuplicate();

    /**
     * @deprecated Returns {@link GudCache} (peer cache). The GUD application contract is client-only;
     *             server-side function execution should use native types or integration infrastructure.
     */
    @Deprecated(since = "4.0")
    GudCache getCache();
}
