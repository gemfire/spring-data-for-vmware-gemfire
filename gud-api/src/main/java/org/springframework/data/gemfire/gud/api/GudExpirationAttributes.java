/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
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
 * 2026-03-11: Created GudExpirationAttributes interface as 1:1 mapping of GemFire ExpirationAttributes
 * 2026-04-01: Added DEFAULT constant and equals/hashCode support via of() record-style impl
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ExpirationAttributes.
 * Defines expiration settings for region entries.
 */
public interface GudExpirationAttributes {

    GudExpirationAttributes DEFAULT = of(0, GudExpirationAction.INVALIDATE);

    int getTimeout();

    GudExpirationAction getAction();

    /**
     * Creates a new GudExpirationAttributes instance with the specified timeout and action.
     *
     * @param timeout the expiration timeout in seconds
     * @param action the action to take when the entry expires
     * @return a new GudExpirationAttributes instance
     */
    static GudExpirationAttributes of(int timeout, GudExpirationAction action) {
        return new GudExpirationAttributes() {
            @Override
            public int getTimeout() {
                return timeout;
            }

            @Override
            public GudExpirationAction getAction() {
                return action;
            }
        };
    }
}
