/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudSelectResults interface as 1:1 mapping of GemFire SelectResults
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * GUD API abstraction for GemFire SelectResults interface.
 * Represents the results of a query execution.
 *
 * @param <E> the type of elements in this result set
 */
public interface GudSelectResults<E> extends Collection<E> {

    boolean isModifiable();

    int occurrences(E element);

    Set<E> asSet();

    List<E> asList();

    GudCollectionType getCollectionType();

    void setElementType(GudObjectType elementType);
}
