// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import org.apache.geode.DataSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

/** Region references, expiration attributes and PDX metadata registration against a native client cache. */
class NativeCodecReferencesTests {
	public static final class Person implements DriverPdxSerializable {
		String name;
		public Person() {}
		@Override public void toData(DriverPdxWriter writer) { writer.writeString("name", name).markIdentityField("name"); }
		@Override public void fromData(DriverPdxReader reader) { name = reader.readString("name"); }
	}

	/** The PDX bridge serializer is installed only when a PDX configuration is supplied. */
	private static DriverConnection connect(int port) {
		var pool = PoolConfiguration.builder().servers(List.of(new PoolConfiguration.Endpoint("localhost", port)))
			.minimumConnections(0).retryAttempts(0).build();
		var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), pool, PdxConfiguration.defaults()));
		connection.getDefaultPool(); // The native default pool is created lazily.
		return connection;
	}
	private static DriverConnection connect() {
		return new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), null, null));
	}

	@Test void regionReferencesRoundTripAndMatchTheNativeEncoding() throws Exception {
		var connection = connect();
		try {
			var region = connection.createClientRegion("codec-region", ClientRegionShortcut.LOCAL, RegionConfiguration.<String, String>builder().build());
			var codec = connection.getDataCodec();
			var bytes = new ByteArrayOutputStream();
			codec.writeRegion(region, new DataOutputStream(bytes));
			codec.writeRegion(null, new DataOutputStream(bytes));
			var nativeBytes = new ByteArrayOutputStream();
			DataSerializer.writeRegion(org.apache.geode.cache.client.ClientCacheFactory.getAnyInstance().getRegion("codec-region"), new DataOutputStream(nativeBytes));
			assertThat(bytes.toByteArray()).startsWith(nativeBytes.toByteArray());
			var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
			assertThat(codec.<String, String>readRegion(in).getFullPath()).isEqualTo("/codec-region");
			assertThat(codec.<String, String>readRegion(in)).isNull();
		}
		finally { connection.close(); }
	}

	@Test void unknownRegionPathFailsAsRegionUnavailable() throws Exception {
		var connection = connect();
		try {
			var bytes = new ByteArrayOutputStream();
			DataSerializer.writeString("/missing", new DataOutputStream(bytes));
			var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
			assertThatThrownBy(() -> connection.getDataCodec().readRegion(in)).isInstanceOf(DriverRegionUnavailableException.class);
		}
		finally { connection.close(); }
	}

	@Test void foreignRegionsAreRejected() {
		var connection = connect();
		try {
			var foreign = (DriverRegion<?, ?>) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { DriverRegion.class }, (p, m, a) -> null);
			assertThatThrownBy(() -> connection.getDataCodec().writeRegion(foreign, new DataOutputStream(new ByteArrayOutputStream())))
				.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		}
		finally { connection.close(); }
	}

	@Test void expirationAttributesRoundTripThroughTheNativeEncoding() throws Exception {
		var connection = connect();
		try {
			var codec = connection.getDataCodec();
			var value = new ExpirationAttributes(30, ExpirationAction.LOCAL_DESTROY);
			var bytes = new ByteArrayOutputStream();
			codec.writeExpirationAttributes(value, new DataOutputStream(bytes));
			var nativeBytes = new ByteArrayOutputStream();
			new org.apache.geode.cache.ExpirationAttributes(30, org.apache.geode.cache.ExpirationAction.LOCAL_DESTROY).toData(new DataOutputStream(nativeBytes));
			assertThat(bytes.toByteArray()).isEqualTo(nativeBytes.toByteArray());
			assertThat(codec.readExpirationAttributes(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())))).isEqualTo(value);
		}
		finally { connection.close(); }
	}

	@Test void pdxMetaDataRegistrationRequiresAPdxValue(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var connection = connect(server.port);
			try {
				var person = new Person();
				person.name = "ann";
				connection.registerPdxMetaData(person);
				assertThatThrownBy(() -> connection.registerPdxMetaData("plain")).isInstanceOf(GemFireDriverException.class);
			}
			finally { connection.close(); }
		}
	}
}
