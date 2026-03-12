/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRebalanceFactory interface as 1:1 mapping of GemFire RebalanceFactory
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire RebalanceFactory interface.
 * Factory for creating rebalance operations.
 */
public interface GudRebalanceFactory {

    GudRebalanceFactory includeRegions(Set<String> regions);
    GudRebalanceFactory excludeRegions(Set<String> regions);

    GudRebalanceOperation start();
    GudRebalanceOperation simulate();
}
