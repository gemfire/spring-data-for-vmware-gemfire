/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudTransactionEvent interface as 1:1 mapping of GemFire TransactionEvent
 */

package org.springframework.data.gemfire.gud.api;

import java.util.List;

/**
 * GUD API abstraction for GemFire TransactionEvent interface.
 * Contains information about a transaction.
 */
public interface GudTransactionEvent {

    GudTransactionId getTransactionId();

    GudCache getCache();

    List<GudCacheEvent<?, ?>> getEvents();

    GudCacheEvent<?, ?>[] getPutEvents();

    GudCacheEvent<?, ?>[] getCreateEvents();

    GudCacheEvent<?, ?>[] getDestroyEvents();

    GudCacheEvent<?, ?>[] getInvalidateEvents();
}
