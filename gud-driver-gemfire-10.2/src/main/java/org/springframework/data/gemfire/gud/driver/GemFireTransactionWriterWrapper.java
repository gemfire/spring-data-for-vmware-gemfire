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
 * 2026-03-13: Created GemFire 10.2 TransactionWriter wrapper
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.TransactionWriter;

import org.springframework.data.gemfire.gud.api.GudTransactionEvent;
import org.springframework.data.gemfire.gud.api.GudTransactionWriter;
import org.springframework.data.gemfire.gud.api.GudTransactionWriterException;

/**
 * Wrapper for native TransactionWriter to expose as GudTransactionWriter.
 */
public class GemFireTransactionWriterWrapper implements GudTransactionWriter, NativeWrapper<TransactionWriter> {

    private final TransactionWriter nativeWriter;

    public GemFireTransactionWriterWrapper(TransactionWriter nativeWriter) {
        this.nativeWriter = nativeWriter;
    }

    @Override
    public TransactionWriter getNative() {
        return nativeWriter;
    }

    @Override
    public void beforeCommit(GudTransactionEvent event) throws GudTransactionWriterException {
        // This wrapper is for exposing native writers, not for wrapping GUD events
    }

    @Override
    public void close() {
        nativeWriter.close();
    }
}
