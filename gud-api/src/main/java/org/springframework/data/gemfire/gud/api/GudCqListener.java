/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqListener interface as 1:1 mapping of GemFire CqListener
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqListener interface.
 * Listener for continuous query events.
 */
public interface GudCqListener {

    void onEvent(GudCqEvent event);

    void onError(GudCqEvent event);

    default void close() {
        // Default no-op
    }
}
