// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.util.Map;
import org.apache.geode.DataSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.PdxConfiguration;

class NativePdxCallbackTests {
	/** Skips the age while {@code partial} is set in the data, carrying it as an unread field. */
	public static final class Person implements DriverPdxSerializable {
		String name; int age; boolean partial; DriverPdxUnreadFields unread;
		public Person() {}
		Person(String name, int age, boolean partial) { this.name = name; this.age = age; this.partial = partial; }
		@Override public void toData(DriverPdxWriter writer) {
			if (unread != null) writer.writeUnreadFields(unread);
			writer.writeString("name", name);
			if (unread == null) writer.writeInt("age", age);
			// A rewrite that carries the unread age is complete, so it no longer claims to be partial.
			writer.writeBoolean("partial", unread == null && partial).markIdentityField("name");
		}
		@Override public void fromData(DriverPdxReader reader) {
			name = reader.readString("name");
			partial = reader.readBoolean("partial");
			if (!partial) age = reader.readInt("age");
			unread = partial ? reader.readUnreadFields() : null;
		}
	}
	public static final class Thing { final String label = "x"; }

	private static DriverPdxSerializer declining() {
		return new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { return false; }
			public Object fromData(Class<?> c, DriverPdxReader r) { return null; }
		};
	}

	private static DriverConnection connect(PdxConfiguration pdx) {
		return new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), null, pdx));
	}
	/** A client needs a live pool to define PDX types, so round trips run against the private server fixture. */
	private static DriverConnection connect(PdxConfiguration pdx, int port) {
		var pool = PoolConfiguration.builder().servers(java.util.List.of(new PoolConfiguration.Endpoint("localhost", port)))
			.minimumConnections(0).retryAttempts(0).build();
		var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), pool, pdx));
		connection.getDefaultPool(); // The native default pool is created lazily; PDX type definition needs it live.
		return connection;
	}
	private static byte[] encode(Object value) throws IOException {
		var bytes = new ByteArrayOutputStream();
		DataSerializer.writeObject(value, new DataOutputStream(bytes));
		return bytes.toByteArray();
	}
	private static Object decode(byte[] bytes) throws Exception {
		return DataSerializer.readObject(new DataInputStream(new ByteArrayInputStream(bytes)));
	}

	@Test void effectiveClientCacheSettingsAreReadBack() {
		var serializer = declining();
		var pdx = PdxConfiguration.defaults().withReadSerialized(true).withIgnoreUnreadFields(true).withSerializer(serializer);
		var connection = connect(pdx);
		try {
			var read = connection.getPdxConfiguration();
			assertThat(read.readSerialized()).isTrue();
			assertThat(read.ignoreUnreadFields()).isTrue();
			assertThat(read.serializer()).containsSame(serializer);
			// persistence and disk store are deprecated and ignored by the client; they are not promised.
			assertThat(read.persistent()).isFalse();
		}
		finally { connection.close(); }
	}

	@Test void serializableRoundTripsThroughNativePdx(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
		var connection = connect(PdxConfiguration.defaults(), server.port);
		try {
			var person = (Person) decode(encode(new Person("ann", 7, false)));
			assertThat(person.name).isEqualTo("ann");
			assertThat(person.age).isEqualTo(7);
		}
		finally { connection.close(); }
		}
	}

	/** Native unread fields are those in the stored type that the class has not registered locally, so the data comes from an evolved type. */
	@Test void unreadFieldsArePreservedAcrossReadAndRewrite(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
		var connection = connect(PdxConfiguration.defaults(), server.port);
		try {
			var evolved = org.apache.geode.cache.client.ClientCacheFactory.getAnyInstance()
				.createPdxInstanceFactory(Person.class.getName())
				.writeString("name", "ann").writeInt("age", 7).writeBoolean("partial", true).markIdentityField("name").create();
			var partial = (Person) decode(encode(evolved));
			assertThat(partial.unread).isNotNull();
			assertThat(partial.age).isZero();
			// The rewrite carries the unread age and no longer marks the value partial.
			var restored = (Person) decode(encode(partial));
			assertThat(restored.age).isEqualTo(7);
			assertThat(restored.partial).isFalse();
		}
		finally { connection.close(); }
		}
	}

	@Test void declineAndNestedFailuresSurfaceAsExpected(@TempDir java.nio.file.Path directory) throws Exception {
		assertThat(new GemFirePdxSerializerBridge(null).toData(new Thing(), null)).isFalse();
		assertThat(new GemFirePdxSerializerBridge(null).fromData(Thing.class, null)).isNull();
		try (var server = new NativeServerFixture(directory)) {
			var declining = connect(PdxConfiguration.defaults().withSerializer(declining()), server.port);
			try {
				// A declined, non-serializable value falls through to native Java serialization, which rejects it.
				assertThatThrownBy(() -> encode(new Thing())).isInstanceOf(java.io.NotSerializableException.class);
			}
			finally { declining.close(); }
		}
	}

	@Test void driverExceptionsFromCallbacksStayInTheCauseChain(@TempDir java.nio.file.Path directory) throws Exception {
		var failing = new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "nested", null); }
			public Object fromData(Class<?> c, DriverPdxReader r) { return null; }
		};
		try (var server = new NativeServerFixture(directory)) {
			var connection = connect(PdxConfiguration.defaults().withSerializer(failing), server.port);
			try { assertThatThrownBy(() -> encode(new Thing())).hasRootCauseInstanceOf(DriverOperationException.class); }
			finally { connection.close(); }
		}
	}
}
