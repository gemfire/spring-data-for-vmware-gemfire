/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudExpirationAttributes interface as 1:1 mapping of GemFire ExpirationAttributes
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ExpirationAttributes.
 * Defines expiration settings for region entries.
 */
public interface GudExpirationAttributes {

    int getTimeout();

    GudExpirationAction getAction();
}
