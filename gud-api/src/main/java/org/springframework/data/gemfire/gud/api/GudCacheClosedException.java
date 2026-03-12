/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheClosedException as 1:1 mapping of GemFire CacheClosedException
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API exception indicating the cache is closed.
 */
public class GudCacheClosedException extends GudException {

    public GudCacheClosedException() {
        super();
    }

    public GudCacheClosedException(String message) {
        super(message);
    }

    public GudCacheClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GudCacheClosedException(Throwable cause) {
        super(cause);
    }
}
