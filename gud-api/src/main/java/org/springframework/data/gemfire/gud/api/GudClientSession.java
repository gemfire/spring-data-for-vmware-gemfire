/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudClientSession interface as 1:1 mapping of GemFire ClientSession
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ClientSession interface.
 * Represents a client session on a cache server.
 */
public interface GudClientSession {

    String getDurableId();

    boolean isDurable();

    boolean isPrimary();
}
