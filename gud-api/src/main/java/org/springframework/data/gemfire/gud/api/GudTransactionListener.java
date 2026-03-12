/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudTransactionListener interface as 1:1 mapping of GemFire TransactionListener
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire TransactionListener interface.
 * Listener for transaction events.
 */
public interface GudTransactionListener extends GudCacheCallback {

    void afterCommit(GudTransactionEvent event);

    void afterFailedCommit(GudTransactionEvent event);

    void afterRollback(GudTransactionEvent event);
}
