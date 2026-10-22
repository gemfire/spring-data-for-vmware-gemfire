/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;
import java.util.function.BiConsumer;

class MockPdxInstance implements DriverPdxInstance {
	private final String className;
	final Map<String, MockPdxField> fields;
	private final boolean deserializable;
	private final boolean enumValue;
	MockPdxInstance(String className, Map<String, MockPdxField> fields, boolean deserializable, boolean enumValue) {
		this.className = className;
		this.fields = new LinkedHashMap<>(fields);
		this.deserializable = deserializable;
		this.enumValue = enumValue;
	}
	MockPdxInstance snapshot() { return new MockPdxInstance(className, fields, deserializable, enumValue); }
	@Override public String getClassName() { return className; }
	@Override public boolean isEnum() { return enumValue; }
	@Override public boolean isDeserializable() { return deserializable; }
	@Override public boolean isIdentityField(String name) { return fields.containsKey(name) && fields.get(name).identity(); }
	@Override public boolean hasField(String name) { return fields.containsKey(name); }
	@Override public List<String> getFieldNames() { return List.copyOf(fields.keySet()); }
	@Override public Object getField(String name) { return fields.containsKey(name) ? new MockPdxValues().copy(fields.get(name).value()) : null; }
	@Override public Object getAndCheckField(String name) { return hasField(name) ? getField(name) : MissingField.INSTANCE; }
	@Override public void foreach(BiConsumer<String, Object> consumer) { for (String name : getFieldNames()) consumer.accept(name, getField(name)); }
	@Override public Object getObject() {
		if (!deserializable) return this;
		throw UnsupportedFeatureException.notIntroduced("Pdx.applicationObjectDeserialization");
	}
	@Override public DriverWritablePdxInstance createWriter() {
		if (enumValue) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "PDX enum cannot be modified", null);
		return new MockWritablePdxInstance(className, fields, deserializable);
	}
	private Map<String, MockPdxField> identityFields() {
		boolean explicit = fields.values().stream().anyMatch(MockPdxField::identity);
		Map<String, MockPdxField> result = new TreeMap<>();
		fields.forEach((name, field) -> { if (!explicit || field.identity()) result.put(name, field); });
		return result;
	}
	@Override public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof MockPdxInstance that) || !className.equals(that.className) || enumValue != that.enumValue) return false;
		if (enumValue) return Objects.equals(fields.get("name").value(), that.fields.get("name").value());
		Map<String, MockPdxField> left = identityFields(), right = that.identityFields();
		Set<String> names = new HashSet<>(left.keySet()); names.addAll(right.keySet());
		for (String name : names) {
			MockPdxField a = left.get(name), b = right.get(name);
			if (a == null) { if (!b.hasDefaultValue()) return false; }
			else if (b == null) { if (!a.hasDefaultValue()) return false; }
			else if (a.type() != b.type() || !Objects.deepEquals(a.value(), b.value())) return false;
		}
		return true;
	}
	@Override public int hashCode() {
		if (enumValue) return Objects.hash(className, fields.get("name").value());
		int result = className.hashCode();
		for (var entry : identityFields().entrySet()) if (!entry.getValue().hasDefaultValue()) result = 31 * result + Objects.hash(entry.getKey(), entry.getValue().type(), new MockPdxValues().hash(entry.getValue().value()));
		return result;
	}
	@Override public String toString() { return "MockPdxInstance[className=" + className + ", fields=" + fields.keySet() + "]"; }
}
