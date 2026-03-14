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
 * 2026-03-13: Created GemFire 10.0 EvictionAttributes adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire100;

import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;

import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * GUD API adapter for GemFire 10.0 EvictionAttributes.
 */
public class GemFire100EvictionAttributes implements GudEvictionAttributes, NativeWrapper<EvictionAttributes> {

    private final EvictionAttributes nativeAttributes;

    public GemFire100EvictionAttributes(EvictionAttributes nativeAttributes) {
        this.nativeAttributes = nativeAttributes;
    }

    @Override
    public EvictionAttributes getNative() {
        return nativeAttributes;
    }

    @Override
    public GudEvictionAlgorithm getAlgorithm() {
        EvictionAlgorithm algorithm = nativeAttributes.getAlgorithm();
        if (algorithm == null) return GudEvictionAlgorithm.NONE;
        if (algorithm.isLRUEntry()) return GudEvictionAlgorithm.LRU_ENTRY;
        if (algorithm.isLRUMemory()) return GudEvictionAlgorithm.LRU_MEMORY;
        if (algorithm.isLRUHeap()) return GudEvictionAlgorithm.LRU_HEAP;
        return GudEvictionAlgorithm.NONE;
    }

    @Override
    public GudEvictionAction getAction() {
        EvictionAction action = nativeAttributes.getAction();
        if (action == null) return GudEvictionAction.NONE;
        if (action.equals(EvictionAction.LOCAL_DESTROY)) return GudEvictionAction.LOCAL_DESTROY;
        if (action.equals(EvictionAction.OVERFLOW_TO_DISK)) return GudEvictionAction.OVERFLOW_TO_DISK;
        return GudEvictionAction.NONE;
    }

    @Override
    public int getMaximum() {
        return nativeAttributes.getMaximum();
    }

    @Override
    public GudObjectSizer getObjectSizer() {
        org.apache.geode.cache.util.ObjectSizer sizer = nativeAttributes.getObjectSizer();
        return sizer != null ? new GemFire100ObjectSizerWrapper(sizer) : null;
    }
}
