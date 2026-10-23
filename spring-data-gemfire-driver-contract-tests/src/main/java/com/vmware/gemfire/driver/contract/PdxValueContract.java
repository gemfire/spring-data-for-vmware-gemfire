/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.*;
import com.vmware.gemfire.driver.spi.*;
import java.util.*;
import org.junit.jupiter.api.Test;

/** Shared local value behavior; native codec compatibility is tested by the native driver separately. */
public abstract class PdxValueContract extends PoolConfigurationContract {
	@Test void factoryWritesEveryFieldKindWithStableSnapshots() throws Exception {
		try (var connection = connect()) {
			var factory = connection.createPdxInstanceFactory("example.AllFields").neverDeserialize();
			Map<String, Object> values = new LinkedHashMap<>();
			values.put("Char", 'x');
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeChar", String.class, char.class).invoke(factory, "Char", values.get("Char"))).isSameAs(factory);
			values.put("Boolean", true);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeBoolean", String.class, boolean.class).invoke(factory, "Boolean", values.get("Boolean"))).isSameAs(factory);
			values.put("Byte", (byte) 7);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeByte", String.class, byte.class).invoke(factory, "Byte", values.get("Byte"))).isSameAs(factory);
			values.put("Short", (short) 8);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeShort", String.class, short.class).invoke(factory, "Short", values.get("Short"))).isSameAs(factory);
			values.put("Int", 9);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeInt", String.class, int.class).invoke(factory, "Int", values.get("Int"))).isSameAs(factory);
			values.put("Long", 10L);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeLong", String.class, long.class).invoke(factory, "Long", values.get("Long"))).isSameAs(factory);
			values.put("Float", 1.25F);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeFloat", String.class, float.class).invoke(factory, "Float", values.get("Float"))).isSameAs(factory);
			values.put("Double", 2.5D);
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeDouble", String.class, double.class).invoke(factory, "Double", values.get("Double"))).isSameAs(factory);
			values.put("Date", new Date(1234567));
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeDate", String.class, java.util.Date.class).invoke(factory, "Date", values.get("Date"))).isSameAs(factory);
			values.put("String", "text");
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeString", String.class, String.class).invoke(factory, "String", values.get("String"))).isSameAs(factory);
			values.put("Object", new ArrayList<>(List.of("a", 2)));
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeObject", String.class, Object.class).invoke(factory, "Object", values.get("Object"))).isSameAs(factory);
			values.put("BooleanArray", new boolean[] {true, false});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeBooleanArray", String.class, boolean[].class).invoke(factory, "BooleanArray", values.get("BooleanArray"))).isSameAs(factory);
			values.put("CharArray", new char[] {'a', 'b'});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeCharArray", String.class, char[].class).invoke(factory, "CharArray", values.get("CharArray"))).isSameAs(factory);
			values.put("ByteArray", new byte[] {1, 2});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeByteArray", String.class, byte[].class).invoke(factory, "ByteArray", values.get("ByteArray"))).isSameAs(factory);
			values.put("ShortArray", new short[] {3, 4});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeShortArray", String.class, short[].class).invoke(factory, "ShortArray", values.get("ShortArray"))).isSameAs(factory);
			values.put("IntArray", new int[] {5, 6});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeIntArray", String.class, int[].class).invoke(factory, "IntArray", values.get("IntArray"))).isSameAs(factory);
			values.put("LongArray", new long[] {7, 8});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeLongArray", String.class, long[].class).invoke(factory, "LongArray", values.get("LongArray"))).isSameAs(factory);
			values.put("FloatArray", new float[] {1.5F, 2.5F});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeFloatArray", String.class, float[].class).invoke(factory, "FloatArray", values.get("FloatArray"))).isSameAs(factory);
			values.put("DoubleArray", new double[] {3.5, 4.5});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeDoubleArray", String.class, double[].class).invoke(factory, "DoubleArray", values.get("DoubleArray"))).isSameAs(factory);
			values.put("StringArray", new String[] {"a", null});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeStringArray", String.class, String[].class).invoke(factory, "StringArray", values.get("StringArray"))).isSameAs(factory);
			values.put("ObjectArray", new Object[] {"a", 2});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeObjectArray", String.class, Object[].class).invoke(factory, "ObjectArray", values.get("ObjectArray"))).isSameAs(factory);
			values.put("ArrayOfByteArrays", new byte[][] {{1, 2}, null, {3}});
			assertThat(DriverPdxInstanceFactory.class.getMethod("writeArrayOfByteArrays", String.class, byte[][].class).invoke(factory, "ArrayOfByteArrays", values.get("ArrayOfByteArrays"))).isSameAs(factory);
			var value = factory.create();
			assertThat(value.getClassName()).isEqualTo("example.AllFields");
			assertThat(value.isEnum()).isFalse();
			assertThat(value.isDeserializable()).isFalse();
			assertThat(value.getObject()).isEqualTo(value);
			assertThat(value.getFieldNames()).containsExactlyElementsOf(values.keySet());
			for (var entry : values.entrySet()) {
				assertThat(Objects.deepEquals(value.getField(entry.getKey()), entry.getValue())).as(entry.getKey()).isTrue();
				assertThat(value.hasField(entry.getKey())).isTrue();
				assertThat(value.isIdentityField(entry.getKey())).isFalse();
			}
			((int[]) values.get("IntArray"))[0] = 999;
			((int[]) value.getField("IntArray"))[0] = 888;
			assertThat((int[]) value.getField("IntArray")).containsExactly(5, 6);
			assertThatThrownBy(factory::create).isInstanceOf(DriverOperationException.class);
		}
	}
	@Test void documentDistinguishesNullAndAbsentFieldsAndPreservesConsumerFailure() {
		try (var connection = connect()) {
			var value = connection.createPdxInstanceFactory("").writeString("nullField", null).writeInt("id", 4).create();
			assertThat(value.hasField("nullField")).isTrue();
			assertThat(value.getField("absent")).isNull();
			assertThat(value.getAndCheckField("nullField")).isNull();
			assertThat(value.getAndCheckField("absent")).isSameAs(DriverDocument.MissingField.INSTANCE);
			Map<String, Object> visited = new LinkedHashMap<>(); value.foreach(visited::put);
			assertThat(visited).containsEntry("nullField", null).containsEntry("id", 4);
			RuntimeException expected = new IllegalArgumentException("application consumer");
			assertThatThrownBy(() -> value.foreach((name, field) -> { throw expected; })).isSameAs(expected);
		}
	}
	@Test void identityIgnoresUnmarkedFieldsAndSchemaDefaultsButChecksNamesAndTypes() {
		try (var connection = connect()) {
			var one = connection.createPdxInstanceFactory("Identity").writeInt("id", 7).markIdentityField("id").writeString("value", "first").create();
			var two = connection.createPdxInstanceFactory("Identity").writeString("value", "second").writeInt("id", 7).markIdentityField("id").create();
			assertThat(one).isEqualTo(two); assertThat(one.hashCode()).isEqualTo(two.hashCode());
			assertThat(one.isIdentityField("id")).isTrue(); assertThat(one.isIdentityField("value")).isFalse();
			var older = connection.createPdxInstanceFactory("Schema").writeInt("id", 7).create();
			var newer = connection.createPdxInstanceFactory("Schema").writeInt("id", 7).writeInt("extra", 0).writeString("optional", null).create();
			assertThat(older).isEqualTo(newer); assertThat(older.hashCode()).isEqualTo(newer.hashCode());
			assertThat(connection.createPdxInstanceFactory("ZeroType").writeInt("id", 0).create())
				.isNotEqualTo(connection.createPdxInstanceFactory("ZeroType").writeLong("id", 0L).create());
			assertThat(older).isNotEqualTo(connection.createPdxInstanceFactory("Other").writeInt("id", 7).create());
			assertThat(older).isNotEqualTo(connection.createPdxInstanceFactory("Schema").writeLong("id", 7L).create());
		}
	}
	@Test void writableCopyChangesExistingFieldsWithoutMutatingSourceOrSibling() {
		try (var connection = connect()) {
			var value = connection.createPdxInstanceFactory("Writable").neverDeserialize().writeInt("id", 1).markIdentityField("id").writeString("name", "before").create();
			var changed = value.createWriter(); var sibling = changed.createWriter();
			changed.setField("id", 2); changed.setField("name", "after");
			assertThat(value.getField("id")).isEqualTo(1); assertThat(sibling.getField("id")).isEqualTo(1);
			assertThat(changed.getField("id")).isEqualTo(2); assertThat(changed.getField("name")).isEqualTo("after");
			assertThat(changed).isNotEqualTo(value);
			assertThat(changed.isIdentityField("id")).isTrue();
			assertThatThrownBy(() -> changed.setField("absent", 1)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> changed.setField("id", "wrong")).isInstanceOf(DriverOperationException.class);
			changed.setField("id", null); assertThat(changed.getField("id")).isEqualTo(0);
		}
	}
	@Test void nestedPdxValuesUseTypedHandlesThroughObjectsArraysContainersAndGenericFields() {
		try (var connection = connect()) {
			var nested = connection.createPdxInstanceFactory("Nested").neverDeserialize().writeString("name", "inside").create();
			var value = connection.createPdxInstanceFactory("Outer").neverDeserialize()
				.writeObject("direct", nested).writeObject("portable", nested, true)
				.writeObject("map", new LinkedHashMap<>(Map.of("nested", new ArrayList<>(List.of(nested)))))
				.writeObjectArray("array", new DriverPdxInstance[] {nested}).writeObjectArray("portableArray", new Object[] {nested}, true)
				.writeObjectArray("matrix", new DriverWritablePdxInstance[][] {{nested.createWriter()}})
				.writeField("generic", nested, DriverPdxInstance.class).writeField("number", 42, int.class, true).create();
			assertThat(value.getField("direct")).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
			assertThat(value.getField("portable")).isEqualTo(nested); assertThat(value.getField("generic")).isEqualTo(nested);
			assertThat(value.getField("number")).isEqualTo(42);
			assertThat(((Object[][]) value.getField("matrix"))[0][0]).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
			assertThat(((Object[]) value.getField("array"))[0]).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
			assertThat(((Object[]) value.getField("portableArray"))[0]).isEqualTo(nested);
			assertThat(((List<?>) ((Map<?, ?>) value.getField("map")).get("nested")).get(0)).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
			var writer = value.createWriter(); writer.setField("direct", nested.createWriter());
			assertThat(writer.getField("direct")).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
		}
	}
	@Test void pdxEnumsExposeFieldsAndIdentityWithoutRequiringTheApplicationClass() {
		try (var connection = connect()) {
			var value = connection.createPdxEnum("missing.ApplicationEnum", "READY", 2);
			assertThat(value.isEnum()).isTrue(); assertThat(value.getClassName()).isEqualTo("missing.ApplicationEnum");
			assertThat(value.getFieldNames()).containsExactly("name", "ordinal");
			assertThat(value.getField("name")).isEqualTo("READY"); assertThat(value.getField("ordinal")).isEqualTo(2);
			assertThat(value.isIdentityField("name")).isFalse();
			var otherOrdinal = connection.createPdxEnum("missing.ApplicationEnum", "READY", 2);
			assertThat(value).isEqualTo(otherOrdinal); assertThat(value.hashCode()).isEqualTo(otherOrdinal.hashCode());
			assertThatThrownBy(() -> connection.createPdxEnum("missing.ApplicationEnum", "READY", 9)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(value::createWriter).isInstanceOf(DriverOperationException.class);
		}
	}
	@Test void invalidSchemaAndFactoryInputFailAtTheTypedBoundary() {
		try (var connection = connect()) {
			assertThatThrownBy(() -> connection.createPdxInstanceFactory(null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> connection.createPdxEnum(null, "X", 0)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> connection.createPdxInstanceFactory("BadIdentity").markIdentityField("absent")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> connection.createPdxInstanceFactory("Duplicate").writeInt("id", 1).writeInt("id", 2)).isInstanceOf(DriverOperationException.class);
		}
	}
}
