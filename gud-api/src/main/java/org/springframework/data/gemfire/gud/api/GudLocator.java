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
 * 2026-03-11: Removed - Locator is a server-side construct not needed for client API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Locator.
 * Primarily a server-side construct, but needed for test support utilities.
 */
public abstract class GudLocator {

    public static final int DEFAULT_LOCATOR_PORT = 10334;

    /**
     * Returns the current locator, if any.
     * @return the current locator or null if none is running
     */
    public static GudLocator getLocator() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    /**
     * Returns whether a locator is currently running.
     * @return true if a locator is running, false otherwise
     */
    public static boolean hasLocator() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    /**
     * Stops this locator.
     */
    public abstract void stop();
}
