/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 CqAttributes adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.query.CqAttributes;

import org.springframework.data.gemfire.gud.api.GudCqAttributes;
import org.springframework.data.gemfire.gud.api.GudCqListener;

/**
 * GUD API adapter for GemFire 10.3 CqAttributes.
 */
public class GemFire103CqAttributes implements GudCqAttributes, NativeWrapper<CqAttributes> {

    private final CqAttributes nativeAttributes;

    public GemFire103CqAttributes(CqAttributes nativeAttributes) {
        this.nativeAttributes = nativeAttributes;
    }

    @Override
    public CqAttributes getNative() {
        return nativeAttributes;
    }

    @Override
    public GudCqListener[] getCqListeners() {
        org.apache.geode.cache.query.CqListener[] listeners = nativeAttributes.getCqListeners();
        if (listeners == null) return new GudCqListener[0];
        GudCqListener[] result = new GudCqListener[listeners.length];
        for (int i = 0; i < listeners.length; i++) {
            result[i] = new GemFire103CqListenerWrapper(listeners[i]);
        }
        return result;
    }
}
