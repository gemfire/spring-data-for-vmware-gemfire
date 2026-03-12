/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudEntryEvent interface as 1:1 mapping of GemFire EntryEvent
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EntryEvent interface.
 * Contains information about an entry event.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface GudEntryEvent<K, V> extends GudCacheEvent<K, V> {

    K getKey();

    V getOldValue();

    V getNewValue();

    GudSerializedCacheValue<V> getSerializedOldValue();

    GudSerializedCacheValue<V> getSerializedNewValue();

    Object getCallbackArgument();

    boolean isCallbackArgumentAvailable();

    boolean hasNewValue();

    boolean hasOldValue();

    boolean isOldValueAvailable();

    GudTransactionId getTransactionId();
}
