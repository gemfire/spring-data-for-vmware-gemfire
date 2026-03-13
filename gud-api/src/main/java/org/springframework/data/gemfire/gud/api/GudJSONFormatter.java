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
 * 2026-03-11: Created GudJSONFormatter interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire JSONFormatter.
 */
public abstract class GudJSONFormatter {

    public static GudPdxInstance fromJSON(String json) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
    
    public static String toJSON(GudPdxInstance pdxInstance) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
}
