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
 * 2026-03-13: Created GemFire 10.2 FixedPartitionAttributes adapter
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.FixedPartitionAttributes;

import org.springframework.data.gemfire.gud.api.GudFixedPartitionAttributes;

/**
 * GUD API adapter for GemFire 10.2 FixedPartitionAttributes.
 */
public class GemFireFixedPartitionAttributes implements GudFixedPartitionAttributes, NativeWrapper<FixedPartitionAttributes> {

    private final FixedPartitionAttributes nativeAttributes;

    public GemFireFixedPartitionAttributes(FixedPartitionAttributes nativeAttributes) {
        this.nativeAttributes = nativeAttributes;
    }

    @Override
    public FixedPartitionAttributes getNative() {
        return nativeAttributes;
    }

    @Override
    public String getPartitionName() {
        return nativeAttributes.getPartitionName();
    }

    @Override
    public boolean isPrimary() {
        return nativeAttributes.isPrimary();
    }

    @Override
    public int getNumBuckets() {
        return nativeAttributes.getNumBuckets();
    }
}
