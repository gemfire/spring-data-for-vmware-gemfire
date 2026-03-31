/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
 * 2026-03-13: Created GemFire 10.2 TransactionListener wrapper
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.TransactionListener;

import org.springframework.data.gemfire.gud.api.GudTransactionEvent;
import org.springframework.data.gemfire.gud.api.GudTransactionListener;

/**
 * Wrapper for native TransactionListener to expose as GudTransactionListener.
 */
public class GemFireTransactionListenerWrapper implements GudTransactionListener, NativeWrapper<TransactionListener> {

    private final TransactionListener nativeListener;

    public GemFireTransactionListenerWrapper(TransactionListener nativeListener) {
        this.nativeListener = nativeListener;
    }

    @Override
    public TransactionListener getNative() {
        return nativeListener;
    }

    @Override
    public void afterCommit(GudTransactionEvent event) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void afterFailedCommit(GudTransactionEvent event) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void afterRollback(GudTransactionEvent event) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void close() {
        nativeListener.close();
    }
}
