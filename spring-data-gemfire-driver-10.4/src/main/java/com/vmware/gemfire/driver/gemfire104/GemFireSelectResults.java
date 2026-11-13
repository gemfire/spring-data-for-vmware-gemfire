/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.geode.cache.query.SelectResults;

import com.vmware.gemfire.driver.spi.DriverCollectionType;
import com.vmware.gemfire.driver.spi.DriverSelectResults;

/** An unmodifiable snapshot taken when the query completes. */
final class GemFireSelectResults extends AbstractCollection<Object> implements DriverSelectResults<Object> {

	private final List<Object> elements;
	private final DriverCollectionType collectionType;

	GemFireSelectResults(SelectResults<?> results) {
		this.elements = Collections.unmodifiableList(GemFireQueryValues.convertAll(results));
		this.collectionType = results.getCollectionType() == null ? null : GemFireObjectTypes.collection(results.getCollectionType());
	}

	@Override public Iterator<Object> iterator() { return elements.iterator(); }
	@Override public int size() { return elements.size(); }
	@Override public boolean isModifiable() { return false; }
	@Override public int occurrences(Object element) {
		int count = 0;
		for (Object candidate : elements) if (Objects.equals(candidate, element)) count++;
		return count;
	}
	@Override public Set<Object> asSet() { return Collections.unmodifiableSet(new LinkedHashSet<>(elements)); }
	@Override public List<Object> asList() { return elements; }
	@Override public DriverCollectionType collectionType() { return collectionType; }
}
