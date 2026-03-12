/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionSnapshotService interface as 1:1 mapping of GemFire RegionSnapshotService
 */

package org.springframework.data.gemfire.gud.api;

import java.io.File;

/**
 * GUD API abstraction for GemFire RegionSnapshotService interface.
 * Service for importing/exporting region data.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public interface GudRegionSnapshotService<K, V> {

    void save(File snapshot, GudSnapshotFormat format);

    void load(File snapshot, GudSnapshotFormat format);
}
