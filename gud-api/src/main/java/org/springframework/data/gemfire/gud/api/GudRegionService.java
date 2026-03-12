/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionService interface as 1:1 mapping of GemFire RegionService
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire RegionService interface.
 * Provides access to existing regions and query service.
 */
public interface GudRegionService {

    <K, V> GudRegion<K, V> getRegion(String path);

    Set<GudRegion<?, ?>> rootRegions();

    GudQueryService getQueryService();

    GudPdxInstanceFactory createPdxInstanceFactory(String className);

    boolean isClosed();

    void close();
}
