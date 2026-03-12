/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudEvictionAttributes interface as 1:1 mapping of GemFire EvictionAttributes
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EvictionAttributes interface.
 * Defines eviction settings for region entries.
 */
public interface GudEvictionAttributes {

    GudEvictionAlgorithm getAlgorithm();

    GudEvictionAction getAction();

    int getMaximum();

    GudObjectSizer getObjectSizer();
}
