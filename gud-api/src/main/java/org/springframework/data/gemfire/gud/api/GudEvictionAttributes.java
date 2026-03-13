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
 * 2026-03-11: Created GudEvictionAttributes interface as 1:1 mapping of GemFire EvictionAttributes
 * 2026-03-13: Added static factory methods for creating eviction attributes
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire EvictionAttributes interface.
 * Defines eviction settings for region entries.
 */
public interface GudEvictionAttributes {

    int DEFAULT_ENTRIES_MAXIMUM = 0;
    int DEFAULT_MEMORY_MAXIMUM = 0;

    GudEvictionAlgorithm getAlgorithm();

    GudEvictionAction getAction();

    int getMaximum();

    GudObjectSizer getObjectSizer();

    static GudEvictionAttributes createLRUHeapAttributes() {
        return createLRUHeapAttributes(null, GudEvictionAction.DEFAULT_EVICTION_ACTION);
    }

    static GudEvictionAttributes createLRUHeapAttributes(GudObjectSizer sizer, GudEvictionAction action) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    static GudEvictionAttributes createLRUMemoryAttributes(int maximum, GudObjectSizer sizer, GudEvictionAction action) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    static GudEvictionAttributes createLRUMemoryAttributes(GudObjectSizer sizer, GudEvictionAction action) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    static GudEvictionAttributes createLRUEntryAttributes() {
        return createLRUEntryAttributes(DEFAULT_ENTRIES_MAXIMUM, GudEvictionAction.DEFAULT_EVICTION_ACTION);
    }

    static GudEvictionAttributes createLRUEntryAttributes(int maximum, GudEvictionAction action) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
}
