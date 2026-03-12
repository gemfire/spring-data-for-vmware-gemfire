/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPdxSerializer interface as 1:1 mapping of GemFire PdxSerializer
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire PdxSerializer interface.
 * Serializes objects to PDX format.
 */
public interface GudPdxSerializer {

    boolean toData(Object o, GudPdxWriter out);

    Object fromData(Class<?> clazz, GudPdxReader in);
}
