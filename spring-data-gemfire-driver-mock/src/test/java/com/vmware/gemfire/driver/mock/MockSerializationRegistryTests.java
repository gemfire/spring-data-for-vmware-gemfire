// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

class MockSerializationRegistryTests {
	public static final class Money implements DriverDataSerializable {
		public void toData(DataOutput out) {}
		public void fromData(DataInput in) {}
	}
	private static DriverDataSerializer serializer(int id, Class<?>... types) {
		return new DriverDataSerializer() {
			public int id() { return id; }
			public Class<?>[] supportedClasses() { return types; }
			public boolean toData(Object o, DataOutput out) { return true; }
			public Object fromData(DataInput in) { return null; }
		};
	}
	private static DriverInstantiator instantiator(int id) {
		return new DriverInstantiator() {
			public int id() { return id; }
			public Class<? extends DriverDataSerializable> instantiatedClass() { return Money.class; }
			public DriverDataSerializable newInstance() { return new Money(); }
		};
	}
	private static DriverSerializationRegistry registry() {
		return new MockConnection(new Properties(), false, null, null).getSerializationRegistry();
	}

	@Test void registeredSerializersAreFoundAndRemoved() {
		var registry = registry();
		var serializer = serializer(1, String.class);
		registry.registerSerializer(serializer);
		assertThat(registry.findSerializer(1)).containsSame(serializer);
		registry.unregisterSerializer(1);
		registry.unregisterSerializer(1); // absent id is a no-op
		assertThat(registry.findSerializer(1)).isEmpty();
	}

	@Test void idAndClassCollisionsAreRejected() {
		var registry = registry();
		registry.registerSerializer(serializer(1, String.class));
		assertThatThrownBy(() -> registry.registerSerializer(serializer(1, Integer.class)))
			.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		assertThatThrownBy(() -> registry.registerSerializer(serializer(2, String.class))).isInstanceOf(DriverOperationException.class);
		assertThat(registry.findSerializer(2)).isEmpty();
	}

	@Test void instantiatorsShareTheSerializerIdSpace() {
		var registry = registry();
		registry.registerInstantiator(instantiator(5), false);
		assertThat(registry.findSerializer(5)).isPresent();
		assertThatThrownBy(() -> registry.registerSerializer(serializer(5, Long.class))).isInstanceOf(DriverOperationException.class);
		assertThatThrownBy(() -> registry.registerInstantiator(instantiator(6), true)).isInstanceOf(DriverOperationException.class);
	}

	@Test void registriesAreIsolatedPerConnection() {
		var first = registry();
		first.registerSerializer(serializer(1, String.class));
		assertThat(registry().findSerializer(1)).isEmpty();
	}

	@Test void mockDoesNotPromiseTheNativeWireFormat() {
		var connection = new MockConnection(new Properties(), false, null, null);
		assertThatThrownBy(connection::getDataCodec).isInstanceOf(UnsupportedFeatureException.class);
	}

	public static final class Pdx implements DriverPdxSerializable {
		public void toData(DriverPdxWriter writer) {}
		public void fromData(DriverPdxReader reader) {}
	}

	@Test void registerPdxMetaDataValidatesInsteadOfSucceedingSilently() {
		var connection = new MockConnection(new Properties(), false, null, null);
		connection.registerPdxMetaData(new Pdx());
		assertThatThrownBy(() -> connection.registerPdxMetaData("plain"))
			.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		assertThatThrownBy(() -> connection.registerPdxMetaData(null)).isInstanceOf(DriverOperationException.class);
		connection.close();
		assertThatThrownBy(() -> connection.registerPdxMetaData(new Pdx()))
			.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_STATE));
	}
}
