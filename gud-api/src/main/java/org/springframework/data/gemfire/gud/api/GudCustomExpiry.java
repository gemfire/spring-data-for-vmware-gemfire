/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCustomExpiry interface as 1:1 mapping of GemFire CustomExpiry
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CustomExpiry interface.
 * Custom expiration logic for region entries.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public interface GudCustomExpiry<K, V> extends GudCacheCallback {

    GudExpirationAttributes getExpiry(GudRegionEntry<K, V> entry);
}
