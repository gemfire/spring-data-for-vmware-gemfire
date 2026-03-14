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
 * 2026-03-13: Created GemFire 10.0 EvictionAttributesMutator adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire100;

import org.apache.geode.cache.EvictionAttributesMutator;

import org.springframework.data.gemfire.gud.api.GudEvictionAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * GUD API adapter for GemFire 10.0 EvictionAttributesMutator.
 */
public class GemFire100EvictionAttributesMutator implements GudEvictionAttributesMutator, NativeWrapper<EvictionAttributesMutator> {

    private final EvictionAttributesMutator nativeMutator;

    public GemFire100EvictionAttributesMutator(EvictionAttributesMutator nativeMutator) {
        this.nativeMutator = nativeMutator;
    }

    @Override
    public EvictionAttributesMutator getNative() {
        return nativeMutator;
    }

    @Override
    public void setMaximum(int maximum) {
        nativeMutator.setMaximum(maximum);
    }
}
