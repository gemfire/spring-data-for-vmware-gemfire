/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudTransactionException for GUD API transaction operations
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire TransactionException.
 * Base exception for transaction-related errors in GemFire operations.
 */
public class GudTransactionException extends GudCacheRuntimeException {

    public GudTransactionException() {
        super();
    }

    public GudTransactionException(String message) {
        super(message);
    }

    public GudTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GudTransactionException(Throwable cause) {
        super(cause);
    }
}
