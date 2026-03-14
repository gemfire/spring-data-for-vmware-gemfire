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
 * 2026-03-13: Created GemFire 10.0 CqListener wrapper
 */

package org.springframework.data.gemfire.gud.driver.gemfire100;

import org.apache.geode.cache.query.CqListener;

import org.springframework.data.gemfire.gud.api.GudCqEvent;
import org.springframework.data.gemfire.gud.api.GudCqListener;

/**
 * Wrapper for native CqListener to expose as GudCqListener.
 */
public class GemFire100CqListenerWrapper implements GudCqListener, NativeWrapper<CqListener> {

    private final CqListener nativeListener;

    public GemFire100CqListenerWrapper(CqListener nativeListener) {
        this.nativeListener = nativeListener;
    }

    @Override
    public CqListener getNative() {
        return nativeListener;
    }

    @Override
    public void onEvent(GudCqEvent event) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void onError(GudCqEvent event) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void close() {
        nativeListener.close();
    }
}
