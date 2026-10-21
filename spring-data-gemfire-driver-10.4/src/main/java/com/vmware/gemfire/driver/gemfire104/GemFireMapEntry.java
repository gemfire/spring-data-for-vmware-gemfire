/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Map;
import java.util.function.Supplier;

class GemFireMapEntry<K, V> implements Map.Entry<K, V> {
	protected final Map.Entry<K, V> entry;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireMapEntry(Map.Entry<K, V> entry) { this.entry = entry; }
	@Override public K getKey() { return invoke(entry::getKey); }
	@Override public V getValue() { return invoke(entry::getValue); }
	@Override public V setValue(V value) { return invoke(() -> entry.setValue(value)); }
	@Override public boolean equals(Object other) { return invoke(() -> entry.equals(other instanceof GemFireMapEntry<?, ?> e ? e.entry : other)); }
	@Override public int hashCode() { return invoke(entry::hashCode); }
	protected <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("regionEntry", ex); }
	}
}
