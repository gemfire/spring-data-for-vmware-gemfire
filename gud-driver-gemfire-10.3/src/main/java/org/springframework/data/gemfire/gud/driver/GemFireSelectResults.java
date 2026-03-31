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
 * 2026-03-13: Created GemFire 10.3 SelectResults adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.types.CollectionType;
import org.apache.geode.cache.query.types.ObjectType;

import org.springframework.data.gemfire.gud.api.GudCollectionType;
import org.springframework.data.gemfire.gud.api.GudObjectType;
import org.springframework.data.gemfire.gud.api.GudSelectResults;

/**
 * GUD API adapter for GemFire 10.3 SelectResults.
 *
 * @param <E> the element type
 */
public class GemFireSelectResults<E> implements GudSelectResults<E>, NativeWrapper<SelectResults<E>> {

    private final SelectResults<E> nativeResults;

    public GemFireSelectResults(SelectResults<E> nativeResults) {
        this.nativeResults = nativeResults;
    }

    @Override
    public SelectResults<E> getNative() {
        return nativeResults;
    }

    @Override
    public boolean isModifiable() {
        return nativeResults.isModifiable();
    }

    @Override
    public int occurrences(E element) {
        return nativeResults.occurrences(element);
    }

    @Override
    public Set<E> asSet() {
        return nativeResults.asSet();
    }

    @Override
    public List<E> asList() {
        return nativeResults.asList();
    }

    @Override
    public GudCollectionType getCollectionType() {
        CollectionType type = nativeResults.getCollectionType();
        return new GemFireCollectionType(type);
    }

    @Override
    public void setElementType(GudObjectType elementType) {
        if (elementType instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            ObjectType nativeType = ((NativeWrapper<ObjectType>) elementType).getNative();
            nativeResults.setElementType(nativeType);
        }
    }

    @Override
    public int size() {
        return nativeResults.size();
    }

    @Override
    public boolean isEmpty() {
        return nativeResults.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return nativeResults.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return nativeResults.iterator();
    }

    @Override
    public Object[] toArray() {
        return nativeResults.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return nativeResults.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return nativeResults.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return nativeResults.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return nativeResults.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return nativeResults.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return nativeResults.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return nativeResults.retainAll(c);
    }

    @Override
    public void clear() {
        nativeResults.clear();
    }
}
