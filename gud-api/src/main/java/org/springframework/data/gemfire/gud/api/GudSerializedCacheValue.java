/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudSerializedCacheValue interface as 1:1 mapping of GemFire SerializedCacheValue
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire SerializedCacheValue interface.
 * Represents a serialized cache value.
 *
 * @param <V> the type of the value
 */
public interface GudSerializedCacheValue<V> {

    byte[] getSerializedValue();

    V getDeserializedValue();
}
