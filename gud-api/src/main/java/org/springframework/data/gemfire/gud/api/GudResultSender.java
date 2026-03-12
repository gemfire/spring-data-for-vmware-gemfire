/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudResultSender interface as 1:1 mapping of GemFire ResultSender
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ResultSender interface.
 * Sends results from function executions.
 *
 * @param <T> the type of results to send
 */
public interface GudResultSender<T> {

    void sendResult(T oneResult);

    void lastResult(T lastResult);

    void sendException(Throwable t);
}
