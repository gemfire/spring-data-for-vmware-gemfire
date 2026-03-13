/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 CqAttributesMutator adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.query.CqAttributesMutator;

import org.springframework.data.gemfire.gud.api.GudCqAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudCqListener;

/**
 * GUD API adapter for GemFire 10.3 CqAttributesMutator.
 */
public class GemFire103CqAttributesMutator implements GudCqAttributesMutator, NativeWrapper<CqAttributesMutator> {

    private final CqAttributesMutator nativeMutator;

    public GemFire103CqAttributesMutator(CqAttributesMutator nativeMutator) {
        this.nativeMutator = nativeMutator;
    }

    @Override
    public CqAttributesMutator getNative() {
        return nativeMutator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCqListener(GudCqListener listener) {
        if (listener instanceof NativeWrapper) {
            nativeMutator.addCqListener(((NativeWrapper<org.apache.geode.cache.query.CqListener>) listener).getNative());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeCqListener(GudCqListener listener) {
        if (listener instanceof NativeWrapper) {
            nativeMutator.removeCqListener(((NativeWrapper<org.apache.geode.cache.query.CqListener>) listener).getNative());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initCqListeners(GudCqListener[] listeners) {
        if (listeners != null) {
            org.apache.geode.cache.query.CqListener[] nativeListeners = new org.apache.geode.cache.query.CqListener[listeners.length];
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] instanceof NativeWrapper) {
                    nativeListeners[i] = ((NativeWrapper<org.apache.geode.cache.query.CqListener>) listeners[i]).getNative();
                }
            }
            nativeMutator.initCqListeners(nativeListeners);
        }
    }
}
