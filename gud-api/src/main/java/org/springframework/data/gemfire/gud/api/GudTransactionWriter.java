/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudTransactionWriter interface as 1:1 mapping of GemFire TransactionWriter
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire TransactionWriter interface.
 * Writer callback for transactions.
 */
public interface GudTransactionWriter extends GudCacheCallback {

    void beforeCommit(GudTransactionEvent event) throws GudTransactionWriterException;

    default void init(Properties properties) {
        // Default no-op implementation
    }

    default void initialize(GudCache cache, Properties properties) {
        // Default no-op implementation
    }
}
