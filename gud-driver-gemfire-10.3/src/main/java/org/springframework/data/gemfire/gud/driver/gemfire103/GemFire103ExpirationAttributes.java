/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 ExpirationAttributes adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;

import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;

/**
 * GUD API adapter for GemFire 10.3 ExpirationAttributes.
 */
public class GemFire103ExpirationAttributes implements GudExpirationAttributes, NativeWrapper<ExpirationAttributes> {

    private final ExpirationAttributes nativeAttributes;

    public GemFire103ExpirationAttributes(ExpirationAttributes nativeAttributes) {
        this.nativeAttributes = nativeAttributes;
    }

    @Override
    public ExpirationAttributes getNative() {
        return nativeAttributes;
    }

    @Override
    public int getTimeout() {
        return nativeAttributes.getTimeout();
    }

    @Override
    public GudExpirationAction getAction() {
        ExpirationAction action = nativeAttributes.getAction();
        if (action == null) return GudExpirationAction.INVALIDATE;
        if (action.equals(ExpirationAction.DESTROY)) return GudExpirationAction.DESTROY;
        if (action.equals(ExpirationAction.LOCAL_DESTROY)) return GudExpirationAction.LOCAL_DESTROY;
        if (action.equals(ExpirationAction.INVALIDATE)) return GudExpirationAction.INVALIDATE;
        if (action.equals(ExpirationAction.LOCAL_INVALIDATE)) return GudExpirationAction.LOCAL_INVALIDATE;
        return GudExpirationAction.INVALIDATE;
    }
}
