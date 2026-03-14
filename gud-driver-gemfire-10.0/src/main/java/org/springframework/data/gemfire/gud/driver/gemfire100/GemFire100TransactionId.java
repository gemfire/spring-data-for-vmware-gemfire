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
 * 2026-03-13: Created GemFire 10.0 TransactionId adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire100;

import org.apache.geode.cache.TransactionId;

import org.springframework.data.gemfire.gud.api.GudTransactionId;

/**
 * GUD API adapter for GemFire 10.0 TransactionId.
 */
public class GemFire100TransactionId implements GudTransactionId, NativeWrapper<TransactionId> {

    private final TransactionId nativeTransactionId;

    public GemFire100TransactionId(TransactionId nativeTransactionId) {
        this.nativeTransactionId = nativeTransactionId;
    }

    @Override
    public TransactionId getNative() {
        return nativeTransactionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GemFire100TransactionId) {
            return nativeTransactionId.equals(((GemFire100TransactionId) obj).nativeTransactionId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nativeTransactionId.hashCode();
    }

    @Override
    public String toString() {
        return nativeTransactionId.toString();
    }
}
