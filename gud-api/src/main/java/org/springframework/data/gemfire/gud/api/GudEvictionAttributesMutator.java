/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudEvictionAttributesMutator interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EvictionAttributesMutator interface.
 * Used to modify eviction attributes after region creation.
 */
public interface GudEvictionAttributesMutator {

    /**
     * Sets the maximum for this evictor.
     *
     * @param maximum the new maximum value
     */
    void setMaximum(int maximum);
}
