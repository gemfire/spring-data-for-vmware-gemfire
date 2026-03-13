/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheTransactionManager interface as 1:1 mapping of GemFire CacheTransactionManager
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheTransactionManager interface.
 * Manages cache transactions.
 */
public interface GudCacheTransactionManager {

    void begin();

    void commit() throws GudCommitConflictException;

    void rollback();

    GudTransactionId suspend();

    void resume(GudTransactionId transactionId);

    boolean exists();

    boolean exists(GudTransactionId transactionId);

    GudTransactionId getTransactionId();

    boolean isSuspended(GudTransactionId transactionId);

    boolean tryResume(GudTransactionId transactionId);

    boolean tryResume(GudTransactionId transactionId, long time, java.util.concurrent.TimeUnit unit);

    GudTransactionWriter getWriter();

    void setWriter(GudTransactionWriter writer);

    GudTransactionListener[] getListeners();

    GudTransactionListener setListener(GudTransactionListener listener);

    void addListener(GudTransactionListener listener);

    void removeListener(GudTransactionListener listener);

    void initListeners(GudTransactionListener[] listeners);

    void setDistributed(boolean distributed);

    boolean isDistributed();
}
