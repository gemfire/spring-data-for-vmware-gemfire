// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.geode.DataSerializer;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

class NativeDataCodecTests {
	enum Color { RED, GREEN }

	private static DriverDataCodec codec() { return new GemFireDataCodec(null, new GemFireExceptionMapper()); }
	private static DataInput input(ByteArrayOutputStream bytes) { return new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())); }

	@Test void connectionExposesTheCodec() {
		var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), null, null));
		try { assertThat(connection.getDataCodec()).isInstanceOf(GemFireDataCodec.class); }
		finally { connection.close(); }
	}

	@Test void bytesAreTheNativeWireFormat() throws Exception {
		var driver = new ByteArrayOutputStream();
		var nativeBytes = new ByteArrayOutputStream();
		var map = new LinkedHashMap<String, Integer>();
		map.put("b", 2); map.put("a", 1);
		codec().writeString("text", new DataOutputStream(driver));
		codec().writeLinkedHashMap(map, new DataOutputStream(driver));
		codec().writeIntArray(new int[] { 3, 4 }, new DataOutputStream(driver));
		DataSerializer.writeString("text", new DataOutputStream(nativeBytes));
		DataSerializer.writeLinkedHashMap(map, new DataOutputStream(nativeBytes));
		DataSerializer.writeIntArray(new int[] { 3, 4 }, new DataOutputStream(nativeBytes));
		assertThat(driver.toByteArray()).isEqualTo(nativeBytes.toByteArray());
	}

	@Test void nullsAndCollectionKindsRoundTrip() throws Exception {
		var codec = codec();
		var bytes = new ByteArrayOutputStream();
		var out = new DataOutputStream(bytes);
		codec.writeString(null, out);
		codec.writeByteArray(null, out);
		var linked = new LinkedList<>(List.of("x", "y"));
		codec.writeLinkedList(linked, out);
		var tree = new TreeMap<String, Integer>(Comparator.reverseOrder());
		tree.put("a", 1); tree.put("b", 2);
		codec.writeTreeMap(tree, out);
		codec.writeConcurrentHashMap(new ConcurrentHashMap<>(Map.of("k", "v")), out);
		codec.writeEnum(Color.GREEN, out);
		codec.writeOptionalLong(OptionalLong.of(9), out);
		codec.writeObject(new Date(5), out);
		var in = input(bytes);
		assertThat(codec.readString(in)).isNull();
		assertThat(codec.readByteArray(in)).isNull();
		assertThat(codec.<String>readLinkedList(in)).isInstanceOf(LinkedList.class).containsExactly("x", "y");
		var readTree = codec.<String, Integer>readTreeMap(in);
		assertThat(readTree.keySet()).containsExactly("b", "a");
		assertThat(codec.<String, String>readConcurrentHashMap(in)).containsEntry("k", "v");
		assertThat(codec.readEnum(Color.class, in)).isEqualTo(Color.GREEN);
		assertThat(codec.readOptionalLong(in)).hasValue(9);
		assertThat((Date) codec.readObject(in)).isEqualTo(new Date(5));
	}

	@Test void inputFilterAppliesToJavaSerializedValuesOnly() throws Exception {
		var codec = codec();
		var bytes = new ByteArrayOutputStream();
		var out = new DataOutputStream(bytes);
		codec.writeObject("plain", out);
		codec.writeObject(new java.math.BigDecimal("1.5"), out);
		ObjectInputFilter rejectAll = info -> ObjectInputFilter.Status.REJECTED;
		var in = input(bytes);
		// Built-in types never reach Java serialization, so the filter does not see them.
		assertThat((String) codec.readObject(in, rejectAll)).isEqualTo("plain");
		assertThat((Object) codec.readObject(in, rejectAll)).isEqualTo(new java.math.BigDecimal("1.5"));
	}

	@Test void disallowingJavaSerializationRejectsSerializableOnlyValues() throws Exception {
		var codec = codec();
		var bytes = new ByteArrayOutputStream();
		assertThatThrownBy(() -> codec.writeObject(new java.util.concurrent.atomic.AtomicInteger(1), new DataOutputStream(bytes), false))
			.isInstanceOf(NotSerializableException.class);
		codec.writeObject(new java.util.concurrent.atomic.AtomicInteger(1), new DataOutputStream(bytes), true);
		assertThat(bytes.size()).isPositive();
	}
}
