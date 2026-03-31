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
 * 2026-03-13: Created GemFire 10.2 PartitionListener wrapper
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.partition.PartitionListener;

import org.springframework.data.gemfire.gud.api.GudPartitionListener;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Wrapper for native PartitionListener to expose as GudPartitionListener.
 */
public class GemFirePartitionListenerWrapper implements GudPartitionListener, NativeWrapper<PartitionListener> {

    private final PartitionListener nativeListener;

    public GemFirePartitionListenerWrapper(PartitionListener nativeListener) {
        this.nativeListener = nativeListener;
    }

    @Override
    public PartitionListener getNative() {
        return nativeListener;
    }

    @Override
    public void afterPrimary(int bucketId) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void afterRegionCreate(GudRegion<?, ?> region) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void afterBucketRemoved(int bucketId, Iterable<?> keys) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }

    @Override
    public void afterBucketCreated(int bucketId, Iterable<?> keys) {
        // This wrapper is for exposing native listeners, not for wrapping GUD events
    }
}
