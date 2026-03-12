/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudObjectType interface as 1:1 mapping of GemFire ObjectType
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ObjectType interface.
 * Describes a type in the GemFire type system.
 */
public interface GudObjectType {

    boolean isCollectionType();

    boolean isMapType();

    boolean isStructType();

    Class<?> resolveClass();

    String getSimpleClassName();
}
