/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
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
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.1 EvictionAttributesMutator adapter
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.EvictionAttributesMutator;

import org.springframework.data.gemfire.gud.api.GudEvictionAttributesMutator;

/**
 * GUD API adapter for GemFire 10.1 EvictionAttributesMutator.
 */
public class GemFireEvictionAttributesMutator implements GudEvictionAttributesMutator, NativeWrapper<EvictionAttributesMutator> {

    private final EvictionAttributesMutator nativeMutator;

    public GemFireEvictionAttributesMutator(EvictionAttributesMutator nativeMutator) {
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
