// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;
import java.util.function.BiConsumer;

/** Immutable in-memory JSON document. Nested objects are documents and arrays are lists; equality follows the parsed tree. */
final class MockJsonDocument implements DriverJsonDocument {
	private final Map<String, Object> fields = new LinkedHashMap<>();

	MockJsonDocument(Map<String, Object> tree) {
		tree.forEach((name, value) -> fields.put(name, wrap(value)));
	}

	@SuppressWarnings("unchecked")
	private static Object wrap(Object value) {
		if (value instanceof Map<?, ?> map) return new MockJsonDocument((Map<String, Object>) map);
		if (value instanceof List<?> list) { var result = new ArrayList<Object>(); for (Object item : list) result.add(wrap(item)); return Collections.unmodifiableList(result); }
		return value;
	}

	@Override public boolean hasField(String name) { return fields.containsKey(name); }
	@Override public List<String> getFieldNames() { return List.copyOf(fields.keySet()); }
	@Override public Object getField(String name) { return fields.get(name); }
	@Override public Object getAndCheckField(String name) { return fields.containsKey(name) ? fields.get(name) : MissingField.INSTANCE; }
	@Override public void foreach(BiConsumer<String, Object> consumer) { new LinkedHashMap<>(fields).forEach(consumer); }
	@Override public String toJson() { return MockJson.write(MockJson.toTree(this)); }
	@Override public boolean equals(Object other) { return other instanceof MockJsonDocument document && fields.equals(document.fields); }
	@Override public int hashCode() { return fields.hashCode(); }
	@Override public String toString() { return "MockJsonDocument" + getFieldNames(); }
}
