/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.pdx.PdxInstance;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

class GemFirePdxInstance implements DriverPdxInstance {
	final PdxInstance value;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFirePdxInstance(PdxInstance value) { this.value = value; }
	@Override public String getClassName() { return invoke(value::getClassName); }
	@Override public boolean isEnum() { return invoke(value::isEnum); }
	@Override public boolean isDeserializable() { return invoke(value::isDeserializable); }
	@Override public boolean isIdentityField(String name) { return invoke(() -> value.isIdentityField(name)); }
	@Override public boolean hasField(String name) { return invoke(() -> value.hasField(name)); }
	@Override public List<String> getFieldNames() { return invoke(value::getFieldNames); }
	@Override public Object getField(String name) { return invoke(() -> new GemFirePdxValues().fromNative(value.getField(name))); }
	@Override public Object getAndCheckField(String name) {
		return invoke(() -> {
			Object result = value.getAndCheckField(name);
			return result == org.apache.geode.cache.Document.FIELD_NOT_FOUND ? MissingField.INSTANCE : new GemFirePdxValues().fromNative(result);
		});
	}
	@Override public void foreach(BiConsumer<String, Object> consumer) {
		// Do not translate exceptions thrown by application consumers.
		for (String name : getFieldNames()) consumer.accept(name, getField(name));
	}
	@Override public Object getObject() { return invoke(() -> new GemFirePdxValues().fromNative(value.getObject())); }
	@Override public DriverWritablePdxInstance createWriter() { return invoke(() -> new GemFireWritablePdxInstance(value.createWriter())); }
	@Override public boolean equals(Object other) { return other instanceof GemFirePdxInstance instance && invoke(() -> value.equals(instance.value)); }
	@Override public int hashCode() { return invoke(value::hashCode); }
	@Override public String toString() { return invoke(value::toString); }
	final <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("pdxInstance", ex); }
	}
}
