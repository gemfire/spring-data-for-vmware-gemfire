/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudServerLoadProbe interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ServerLoadProbe.
 * Used to provide load information for a cache server.
 */
public interface GudServerLoadProbe {

    GudServerLoad getLoad(GudServerMetrics metrics);

    void open();

    void close();
}
