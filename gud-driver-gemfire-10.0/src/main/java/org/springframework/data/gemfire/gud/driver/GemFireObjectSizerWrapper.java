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
 * 2026-03-13: Created GemFire 10.0 ObjectSizer wrapper
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.util.ObjectSizer;

import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * Wrapper for native ObjectSizer to expose as GudObjectSizer.
 */
public class GemFireObjectSizerWrapper implements GudObjectSizer, NativeWrapper<ObjectSizer> {

    private final ObjectSizer nativeSizer;

    public GemFireObjectSizerWrapper(ObjectSizer nativeSizer) {
        this.nativeSizer = nativeSizer;
    }

    @Override
    public ObjectSizer getNative() {
        return nativeSizer;
    }

    @Override
    public int sizeof(Object o) {
        return nativeSizer.sizeof(o);
    }
}
