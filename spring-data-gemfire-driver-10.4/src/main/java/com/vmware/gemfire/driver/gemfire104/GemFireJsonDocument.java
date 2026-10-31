// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.apache.geode.cache.Document;
import org.apache.geode.json.JsonDocument;
import com.vmware.gemfire.driver.spi.DriverJsonDocument;

/** Wraps a native JSON document. Nested documents and arrays are wrapped lazily so no native type reaches the caller. */
final class GemFireJsonDocument implements DriverJsonDocument {
	private final JsonDocument value;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireJsonDocument(JsonDocument value) { this.value = value; }
	@Override public boolean hasField(String name) { return invoke(() -> value.hasField(name)); }
	@Override public List<String> getFieldNames() { return invoke(value::getFieldNames); }
	@Override public Object getField(String name) { return invoke(() -> wrap(value.getField(name))); }
	@Override public Object getAndCheckField(String name) {
		return invoke(() -> {
			Object result = value.getAndCheckField(name);
			return result == Document.FIELD_NOT_FOUND ? MissingField.INSTANCE : wrap(result);
		});
	}
	@Override public void foreach(BiConsumer<String, Object> consumer) {
		// Do not translate exceptions thrown by application consumers.
		for (String name : getFieldNames()) consumer.accept(name, getField(name));
	}
	@Override public String toJson() { return invoke(value::toJson); }
	@Override public boolean equals(Object other) { return other instanceof GemFireJsonDocument document && invoke(() -> value.equals(document.value)); }
	@Override public int hashCode() { return invoke(value::hashCode); }
	@Override public String toString() { return invoke(value::toString); }

	private Object wrap(Object result) {
		if (result instanceof JsonDocument document) return new GemFireJsonDocument(document);
		if (result instanceof List<?> items) {
			var wrapped = new ArrayList<Object>(items.size());
			for (Object item : items) wrapped.add(wrap(item));
			return wrapped;
		}
		return new GemFirePdxValues().fromNative(result);
	}
	private <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("jsonDocument", ex); }
	}
}
