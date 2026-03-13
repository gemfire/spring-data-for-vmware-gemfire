/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudServerMetrics interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ServerMetrics.
 * Provides metrics about a cache server.
 */
public interface GudServerMetrics {

    int getClientCount();

    int getConnectionCount();

    int getMaxConnections();

    int getSubscriptionConnectionCount();
}
