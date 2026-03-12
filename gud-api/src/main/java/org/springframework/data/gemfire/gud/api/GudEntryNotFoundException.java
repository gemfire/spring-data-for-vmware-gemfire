/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudEntryNotFoundException for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EntryNotFoundException.
 */
public class GudEntryNotFoundException extends GudCacheRuntimeException {

    public GudEntryNotFoundException() {
        super();
    }

    public GudEntryNotFoundException(String message) {
        super(message);
    }

    public GudEntryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GudEntryNotFoundException(Throwable cause) {
        super(cause);
    }
}
