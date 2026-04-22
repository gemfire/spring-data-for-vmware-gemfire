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
 * 2026-03-11: Created GudQuery interface as 1:1 mapping of GemFire Query
 * 2026-03-31: Changed execute(Object[]) to execute(Object...) for varargs support
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Query interface.
 * Represents a compiled query that can be executed.
 */
public interface GudQuery {

    String getQueryString();

    Object execute() throws GudQueryException;

    Object execute(Object... params) throws GudQueryException;

    GudQueryStatistics getStatistics();

    void compile();

    boolean isCompiled();
}
