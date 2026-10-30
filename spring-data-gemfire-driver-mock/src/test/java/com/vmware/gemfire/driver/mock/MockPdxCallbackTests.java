// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.PdxConfiguration;

class MockPdxCallbackTests {
	static final class Person implements DriverPdxSerializable {
		String name; int age; DriverPdxUnreadFields unread;
		public Person() {}
		Person(String name, int age) { this.name = name; this.age = age; }
		@Override public void toData(DriverPdxWriter writer) {
			writer.writeString("name", name).writeInt("age", age).markIdentityField("name");
		}
		@Override public void fromData(DriverPdxReader reader) {
			name = reader.readString("name"); age = reader.readInt("age");
			unread = reader.readUnreadFields();
		}
	}
	static final class V1 implements DriverPdxSerializable {
		String name;
		public V1() {}
		@Override public void toData(DriverPdxWriter writer) { writer.writeString("name", name); }
		@Override public void fromData(DriverPdxReader reader) { name = reader.readString("name"); }
	}
	static final class Thing { String label = "x"; }

	private static MockConnection connection(PdxConfiguration pdx) { return new MockConnection(new Properties(), false, null, pdx); }

	@Test void configurationIsReadBackAndDefaultsApply() {
		var serializer = new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { return false; }
			public Object fromData(Class<?> c, DriverPdxReader r) { return null; }
		};
		var pdx = PdxConfiguration.defaults().withReadSerialized(true).withIgnoreUnreadFields(true).withPersistent(true).withDiskStoreName("ds").withSerializer(serializer);
		var read = connection(pdx).getPdxConfiguration();
		assertThat(read).isEqualTo(pdx);
		assertThat(read.serializer()).containsSame(serializer);
		assertThat(connection(null).getPdxConfiguration()).isEqualTo(PdxConfiguration.defaults());
	}

	@Test void serializableRoundTripKeepsIdentityAndValues() {
		var codec = connection(null).pdxCodec();
		var instance = codec.serialize(new Person("ann", 7));
		assertThat(instance.isIdentityField("name")).isTrue();
		assertThat(instance.isIdentityField("age")).isFalse();
		var back = (Person) codec.deserialize(Person.class, instance);
		assertThat(back.name).isEqualTo("ann");
		assertThat(back.age).isEqualTo(7);
	}

	@Test void missingFieldsReadAsDefaultsButTypeMismatchFails() {
		var codec = connection(null).pdxCodec();
		var v1 = codec.serialize(new V1());
		var person = (Person) codec.deserialize(Person.class, v1);
		assertThat(person.age).isZero();
		var reader = new MockPdxReader(codec.serialize(new Person("a", 1)), false);
		assertThatThrownBy(() -> reader.readString("age")).isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
	}

	@Test void unreadFieldsSurviveSchemaEvolutionUnlessIgnored() {
		var codec = connection(null).pdxCodec();
		var v2 = codec.serialize(new Person("ann", 7));
		var v1 = (V1) codec.deserialize(V1.class, v2);
		assertThat(v1.name).isEqualTo("ann");
		var reader = new MockPdxReader(v2, false);
		reader.readString("name");
		var carried = reader.readUnreadFields();
		var writer = new MockPdxWriter("p");
		writer.writeUnreadFields(carried).writeString("name", "bob");
		assertThat(writer.create().getField("age")).isEqualTo(7);
		var ignoring = new MockPdxReader(v2, true);
		ignoring.readString("name");
		assertThat(((MockPdxUnreadFields) ignoring.readUnreadFields()).fields()).isEmpty();
	}

	@Test void writeUnreadFieldsMustComeFirst() {
		var source = new MockPdxWriter("p");
		source.writeInt("age", 1);
		var carried = new MockPdxReader(source.create(), false).readUnreadFields();
		var writer = new MockPdxWriter("p").writeString("name", "x");
		assertThatThrownBy(() -> writer.writeUnreadFields(carried)).isInstanceOf(IllegalStateException.class);
	}

	@Test void serializerDeclineAndNestedFailures() {
		var decline = new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { return false; }
			public Object fromData(Class<?> c, DriverPdxReader r) { return null; }
		};
		var codec = connection(PdxConfiguration.defaults().withSerializer(decline)).pdxCodec();
		assertThatThrownBy(() -> codec.serialize(new Thing())).isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		var failing = new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { throw new IllegalStateException("boom"); }
			public Object fromData(Class<?> c, DriverPdxReader r) { throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "driver", null); }
		};
		var failingCodec = connection(PdxConfiguration.defaults().withSerializer(failing)).pdxCodec();
		assertThatThrownBy(() -> failingCodec.serialize(new Thing())).isInstanceOf(DriverOperationException.class).hasRootCauseMessage("boom");
		var instance = codec.serialize(new Person("a", 1));
		assertThatThrownBy(() -> failingCodec.deserialize(Thing.class, instance)).hasMessage("driver");
	}

	@Test void serializerHandlesNonSerializableTypes() {
		var serializer = new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { if (!(o instanceof Thing t)) return false; w.writeString("label", t.label); return true; }
			public Object fromData(Class<?> c, DriverPdxReader r) { var t = new Thing(); t.label = r.readString("label"); return t; }
		};
		var codec = connection(PdxConfiguration.defaults().withSerializer(serializer)).pdxCodec();
		assertThat(((Thing) codec.deserialize(Thing.class, codec.serialize(new Thing()))).label).isEqualTo("x");
	}
}
