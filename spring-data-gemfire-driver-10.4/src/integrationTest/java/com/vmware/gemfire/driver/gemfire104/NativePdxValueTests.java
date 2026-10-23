/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.DataSerializer;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.PoolManager;
import org.apache.geode.pdx.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;

class NativePdxValueTests {
	@org.junit.jupiter.api.io.TempDir java.nio.file.Path directory;
	@Test void realNativeWireRoundTripRetainsNestedTypedFieldsIdentityAndWritableChanges() throws Exception {
		try (var server = new NativeServerFixture(directory);
			var cache = clientCache(server.port, true)) {
			var connection = new GemFireConnection(cache);
			var nested = connection.createPdxInstanceFactory("NestedWire").neverDeserialize().writeString("value", "nested").create();
			var value = connection.createPdxInstanceFactory("Wire").neverDeserialize().writeLong("id", 7).markIdentityField("id")
				.writeObject("nested", nested).writeObjectArray("items", new Object[] {nested, "text"}).create();
			var changed = value.createWriter(); changed.setField("id", 9L);
			// The native driver owns wire encoding; no native type appears in the shared contract fixture.
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataSerializer.writeObject(new GemFirePdxValues().toNative(changed), new DataOutputStream(bytes));
			Object decoded = DataSerializer.readObject(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
			assertThat(decoded).isInstanceOf(PdxInstance.class);
			var read = (DriverPdxInstance) new GemFirePdxValues().fromNative(decoded);
			assertThat(read).isEqualTo(changed); assertThat(read.hashCode()).isEqualTo(changed.hashCode());
			assertThat(read.getField("id")).isEqualTo(9L); assertThat(read.isIdentityField("id")).isTrue();
			assertThat(read.getField("nested")).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
			assertThat(((Object[]) read.getField("items"))[0]).isInstanceOf(DriverPdxInstance.class).isEqualTo(nested);
			assertThat(value.getField("id")).isEqualTo(7L);
			var raw = cache.createPdxInstanceFactory("Wire").neverDeserialize().writeLong("id", 9L).markIdentityField("id").writeString("ignored", "different").create();
			assertThat(decoded).isEqualTo(raw); // independent engine-created control
		}
	}
	@Test void applicationDeserializationAndEnumMaterializationDelegateToTheNativeEngine() throws Exception {
		try (var server = new NativeServerFixture(directory);
			var cache = clientCache(server.port, false)) {
			var connection = new GemFireConnection(cache);
			var value = connection.createPdxInstanceFactory(Person.class.getName()).writeInt("id", 17).writeString("name", "Ada").create();
			assertThat(value.isDeserializable()).isTrue();
			Person person = (Person) value.getObject(); assertThat(person.id).isEqualTo(17); assertThat(person.name).isEqualTo("Ada");
			assertThat(connection.createPdxEnum(Thread.State.class.getName(), "NEW", 0).getObject()).isSameAs(Thread.State.NEW);
			assertThatThrownBy(() -> connection.createPdxInstanceFactory("missing.Type").writeInt("id", 1).create().getObject())
				.isInstanceOfSatisfying(DriverOperationException.class, ex -> { assertThat(ex.kind()).isEqualTo(DriverFailureKind.RESOURCE_FAILURE); assertThat(ex.getCause()).isInstanceOf(PdxSerializationException.class); });
			assertThatThrownBy(() -> connection.createPdxInstanceFactory("Portability").writeObject("java", new java.io.File("example"), true))
				.isInstanceOf(DriverOperationException.class);
		}
	}
	@Test void borrowedRegionServiceAndClientConnectionExposePdxFactories() throws Exception {
		try (var server = new NativeServerFixture(directory);
			var cache = clientCache(server.port, false)) {
			var service = new GemFireRegionService(cache);
			assertThat(service.createPdxInstanceFactory("Borrowed").writeInt("id", 2).create().getField("id")).isEqualTo(2);
			assertThat(service.createPdxEnum("Color", "RED", 0).getField("name")).isEqualTo("RED");
		}
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			// Factory allocation is local, but enum/schema registration requires a server pool.
			assertThat(connection.createPdxInstanceFactory("Client")).isNotNull();
			assertThatThrownBy(() -> connection.createPdxEnum("Color", "RED", 0)).isInstanceOfSatisfying(DriverOperationException.class,
				ex -> assertThat(ex.kind()).isEqualTo(DriverFailureKind.CANCELLED));
		}
	}
	@Test void valueConversionRetainsAliasesAndCyclesInStandardCarriers() {
		var codec = new GemFirePdxValues();
		List<Object> cycle = new ArrayList<>(); cycle.add(cycle);
		List<?> mapped = (List<?>) codec.fromNative(cycle);
		assertThat(mapped.get(0)).isSameAs(mapped);
		var nativeEnum = new org.apache.geode.pdx.internal.PdxInstanceEnum("Enum", "ONE", 0);
		var values = (Object[]) codec.fromNative(new Object[] {nativeEnum, nativeEnum});
		assertThat(values[0]).isSameAs(values[1]).isInstanceOf(DriverPdxInstance.class);
		// Application implementations of the native interface must not escape in a nested carrier either.
		PdxInstance custom = (PdxInstance) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
			new Class<?>[] {PdxInstance.class}, (proxy, method, arguments) -> method.invoke(nativeEnum, arguments));
		assertThat(((List<?>) codec.fromNative(List.of(custom))).get(0)).isInstanceOf(DriverPdxInstance.class);
		assertThat(codec.toNative(new ArrayList<>(List.of("plain")))).isInstanceOf(ArrayList.class);
	}

	private ClientCache clientCache(int port, boolean readSerialized) {
		var cache = new ClientCacheFactory().set("log-level", "error").setPdxReadSerialized(readSerialized).create();
		try {
			// A PDX-only client has no region to initialize the lazy default pool.
			PoolManager.createFactory().addServer("localhost", port).setMinConnections(0).create("pdx-values");
			return cache;
		}
		catch (RuntimeException | Error failure) { cache.close(); throw failure; }
	}

	public static class Person implements PdxSerializable {
		int id; String name;
		public Person() { }
		@Override public void toData(PdxWriter writer) { writer.writeInt("id", id).writeString("name", name); }
		@Override public void fromData(PdxReader reader) { id = reader.readInt("id"); name = reader.readString("name"); }
	}
}
