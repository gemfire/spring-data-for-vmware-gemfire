/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPartitionResolver interface as 1:1 mapping of GemFire PartitionResolver
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire PartitionResolver interface.
 * Determines which bucket an entry belongs to.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public interface GudPartitionResolver<K, V> extends GudCacheCallback {

    Object getRoutingObject(GudEntryOperation<K, V> entryOperation);

    String getName();
}
