// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.util.Map;
import org.apache.geode.DataSerializer;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

/** Each test uses its own serializer ids because the native registry is process-wide. */
class NativeSerializationRegistryTests {
	public static final class Amount { final long cents; Amount(long cents) { this.cents = cents; } }
	public static final class Ticket implements DriverDataSerializable {
		String label;
		public Ticket() {}
		Ticket(String label) { this.label = label; }
		@Override public void toData(DataOutput out) throws IOException { out.writeUTF(label); }
		@Override public void fromData(DataInput in) throws IOException { label = in.readUTF(); }
	}

	private static DriverConnection connect() {
		return new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), null, null));
	}
	private static DriverDataSerializer amounts(int id) {
		return new DriverDataSerializer() {
			public int id() { return id; }
			public Class<?>[] supportedClasses() { return new Class<?>[] { Amount.class }; }
			public boolean toData(Object o, DataOutput out) throws IOException { out.writeLong(((Amount) o).cents); return true; }
			public Object fromData(DataInput in) throws IOException { return new Amount(in.readLong()); }
		};
	}
	private static byte[] encode(Object value) throws IOException {
		var bytes = new ByteArrayOutputStream();
		DataSerializer.writeObject(value, new DataOutputStream(bytes));
		return bytes.toByteArray();
	}
	private static Object decode(byte[] bytes) throws Exception {
		return DataSerializer.readObject(new DataInputStream(new ByteArrayInputStream(bytes)));
	}

	@Test void registeredSerializerRoundTripsThroughTheNativeWireFormat() throws Exception {
		var connection = connect();
		try {
			var registry = connection.getSerializationRegistry();
			var serializer = amounts(9101);
			registry.registerSerializer(serializer);
			try {
				assertThat(registry.findSerializer(9101)).containsSame(serializer);
				assertThat(((Amount) decode(encode(new Amount(250)))).cents).isEqualTo(250);
			}
			finally { registry.unregisterSerializer(9101); }
			assertThat(registry.findSerializer(9101)).isEmpty();
			registry.unregisterSerializer(9101); // absent id is a no-op
		}
		finally { connection.close(); }
	}

	@Test void duplicateIdIsRejected() {
		var connection = connect();
		try {
			var registry = connection.getSerializationRegistry();
			registry.registerSerializer(amounts(9102));
			try {
				assertThatThrownBy(() -> registry.registerSerializer(amounts(9102))).isInstanceOf(GemFireDriverException.class);
			}
			finally { registry.unregisterSerializer(9102); }
		}
		finally { connection.close(); }
	}

	/** The serializer-backed instantiator round-trips natively; its framed id makes it incompatible with native instantiators. */
	@Test void serializerBackedInstantiatorRoundTripsAndFramesItsId() throws Exception {
		var connection = connect();
		try {
			var registry = connection.getSerializationRegistry();
			registry.registerInstantiator(new DriverInstantiator() {
				public int id() { return 9103; }
				public Class<? extends DriverDataSerializable> instantiatedClass() { return Ticket.class; }
				public DriverDataSerializable newInstance() { return new Ticket(); }
			}, false);
			try {
				var bytes = encode(new Ticket("vip"));
				assertThat(((Ticket) decode(bytes)).label).isEqualTo("vip");
				assertThat(new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1)).contains("vip");
				assertThat(registry.findSerializer(9103)).isPresent();
			}
			finally { registry.unregisterSerializer(9103); }
		}
		finally { connection.close(); }
	}
}
