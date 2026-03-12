/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionFunctionContext interface as 1:1 mapping of GemFire RegionFunctionContext
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire RegionFunctionContext interface.
 * Provides region-specific context for function execution.
 */
public interface GudRegionFunctionContext extends GudFunctionContext {

    <K, V> GudRegion<K, V> getDataSet();

    Set<?> getFilter();

    GudRegion<?, ?> getLocalDataSet(GudRegion<?, ?> region);
}
