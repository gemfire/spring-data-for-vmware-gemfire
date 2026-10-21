/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import com.vmware.gemfire.driver.spi.DriverRegion;
import org.apache.geode.cache.Region;

/** Live adapters. Delegate collection mutations instead of substituting region operations. */
final class GemFireRegionViews {
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	Set<DriverRegion<?, ?>> regions(Set<Region<?, ?>> regions) { return mappedSet(regions, r -> new GemFireRegion<>(r)); }
	<T, R> Set<R> set(Set<T> source, Function<T, R> mapper) {
		return set(source, mapper, Function.identity());
	}
	<T, R> Set<R> mappedSet(Set<T> source, Function<T, R> mapper) { return set(source, mapper, this::unwrap); }
	private <T, R> Set<R> set(Set<T> source, Function<T, R> mapper, Function<Object, Object> reverse) {
		Collection<R> view = collection(source, mapper, reverse);
		return new AbstractSet<>() {
			@Override public Iterator<R> iterator() { return view.iterator(); }
			@Override public int size() { return view.size(); }
			@Override public boolean contains(Object value) { return view.contains(value); }
			@Override public boolean add(R value) { return view.add(value); }
			@Override public boolean remove(Object value) { return view.remove(value); }
			@Override public boolean removeAll(Collection<?> values) { return view.removeAll(values); }
			@Override public boolean retainAll(Collection<?> values) { return view.retainAll(values); }
			@Override public void clear() { view.clear(); }
		};
	}
	<T, R> Collection<R> collection(Collection<T> source, Function<T, R> mapper) {
		return collection(source, mapper, Function.identity());
	}
	private <T, R> Collection<R> collection(Collection<T> source, Function<T, R> mapper, Function<Object, Object> reverse) {
		return new AbstractCollection<>() {
			@Override public int size() { return invoke(source::size); }
			@Override @SuppressWarnings("unchecked") public boolean add(R value) { return invoke(() -> source.add((T) reverse.apply(value))); }
			@Override public boolean contains(Object value) { return invoke(() -> source.contains(reverse.apply(value))); }
			@Override public boolean remove(Object value) { return invoke(() -> source.remove(reverse.apply(value))); }
			@Override public boolean removeAll(Collection<?> values) { return invoke(() -> source.removeAll(values.stream().map(reverse).toList())); }
			@Override public boolean retainAll(Collection<?> values) { return invoke(() -> source.retainAll(values.stream().map(reverse).toList())); }
			@Override public void clear() { invoke(() -> { source.clear(); return null; }); }
			@Override public Iterator<R> iterator() {
				Iterator<T> iterator = invoke(source::iterator);
				return new Iterator<>() {
					@Override public boolean hasNext() { return invoke(iterator::hasNext); }
					@Override public R next() { return invoke(() -> mapper.apply(iterator.next())); }
					@Override public void remove() { invoke(() -> { iterator.remove(); return null; }); }
				};
			}
		};
	}
	private Object unwrap(Object value) { return value instanceof GemFireMapEntry<?, ?> e ? e.entry : value instanceof GemFireRegion<?, ?> r ? r.region : value; }
	private <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("regionView", ex); }
	}
}
