/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudEntryOperation interface as 1:1 mapping of GemFire EntryOperation
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EntryOperation interface.
 * Provides information about an entry operation.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface GudEntryOperation<K, V> {

    GudRegion<K, V> getRegion();

    GudOperation getOperation();

    K getKey();

    V getNewValue();

    Object getCallbackArgument();

    boolean isCallbackArgumentAvailable();
}
