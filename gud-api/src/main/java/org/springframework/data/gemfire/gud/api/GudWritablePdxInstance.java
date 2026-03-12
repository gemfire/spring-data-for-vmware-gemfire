/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudWritablePdxInstance interface as 1:1 mapping of GemFire WritablePdxInstance
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire WritablePdxInstance interface.
 * A writable version of PdxInstance that allows field modification.
 */
public interface GudWritablePdxInstance extends GudPdxInstance {

    void setField(String fieldName, Object value);
}
