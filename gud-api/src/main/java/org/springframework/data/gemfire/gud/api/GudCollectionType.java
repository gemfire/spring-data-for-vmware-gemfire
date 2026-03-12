/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCollectionType interface as 1:1 mapping of GemFire CollectionType
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CollectionType interface.
 * Describes the type of a collection in query results.
 */
public interface GudCollectionType extends GudObjectType {

    GudObjectType getElementType();

    boolean isOrdered();

    boolean allowsDuplicates();
}
