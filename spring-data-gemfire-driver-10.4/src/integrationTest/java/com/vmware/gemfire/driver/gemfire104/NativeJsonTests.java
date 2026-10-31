// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

/** JSON documents and JSON/PDX conversion delegated to the native provider. PDX storage registers its type with a server, so those tests use one. */
@SuppressWarnings("deprecation")
class NativeJsonTests {
	private static DriverConnection connect(int port) {
		var pool = PoolConfiguration.builder().servers(List.of(new PoolConfiguration.Endpoint("localhost", port)))
			.minimumConnections(0).retryAttempts(0).build();
		var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), pool, PdxConfiguration.defaults()));
		connection.getDefaultPool(); // The native default pool is created lazily.
		return connection;
	}

	@Test void connectionExposesTheJsonAccessors() {
		var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), null, null));
		try {
			assertThat(connection.getJsonFormatter()).isInstanceOf(GemFireJsonFormatter.class);
			assertThat(connection.getJsonFormatter().getRegionService()).isSameAs(connection);
			assertThat(connection.getJsonDocumentFactory()).isInstanceOf(GemFireJsonDocumentFactory.class);
			assertThat(connection.getJsonDocumentFactory(DriverStorageFormat.BSON)).isInstanceOf(GemFireJsonDocumentFactory.class);
			assertThat(DriverJsonFormatter.JSON_CLASSNAME).isEqualTo(org.apache.geode.pdx.JSONFormatter.JSON_CLASSNAME);
			assertThat(DriverJsonFormatter.SORT_JSON_FIELD_NAMES_PROPERTY).isEqualTo(org.apache.geode.pdx.JSONFormatter.SORT_JSON_FIELD_NAMES_PROPERTY);
		}
		finally { connection.close(); }
	}

	@Test void formatterConvertsAgainstAClientCache(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var connection = connect(server.port);
			try {
				var formatter = connection.getJsonFormatter();
				var pdx = formatter.fromJSON("{\"id\":1,\"name\":\"ann\",\"n\":{\"x\":2},\"l\":[1,2],\"z\":null}", "id");
				assertThat(pdx.getClassName()).isEqualTo(DriverJsonFormatter.JSON_CLASSNAME);
				assertThat(pdx.isIdentityField("id")).isTrue();
				assertThat(pdx.isIdentityField("name")).isFalse();
				assertThat(pdx.getField("id")).isEqualTo((byte) 1);
				assertThat(pdx.getField("n")).isInstanceOf(DriverPdxInstance.class);
				assertThat((List<?>) pdx.getField("l")).hasSize(2);
				assertThat(pdx.hasField("z")).isTrue();
				assertThat(formatter.toJSON(pdx)).contains("\"name\"").contains("\"ann\"");
				assertThat(formatter.toJSON(pdx)).isEqualTo(new String(formatter.toJSONByteArray(pdx), StandardCharsets.UTF_8));
				assertThat(formatter.toPdxInstance("{\"a\":1}".getBytes(StandardCharsets.UTF_8)).getField("a")).isEqualTo((byte) 1);
			}
			finally { connection.close(); }
		}
	}

	/** Pins the native behavior: fromPdxInstance decodes the bytes with the platform default charset. The native writer escapes non-ASCII characters, so the bytes are ASCII and both decodings agree for this input. */
	@Test void fromPdxInstanceDecodesWithTheDefaultCharset(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var connection = connect(server.port);
			try {
				var formatter = connection.getJsonFormatter();
				var pdx = formatter.fromJSON("{\"k\":\"héllo €\"}");
				var utf8 = formatter.toJsonByteArrayFromPdxInstance(pdx);
				assertThat(new String(utf8, StandardCharsets.UTF_8)).contains("h\\u00E9llo \\u20AC");
				assertThat(formatter.fromPdxInstance(pdx)).isEqualTo(new String(utf8));
			}
			finally { connection.close(); }
		}
	}

	@Test void parseFailuresKeepTheirCauseAndDoNotCopyTheDocument(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var connection = connect(server.port);
			try {
				var secret = "{\"ssn\":\"123-45-6789\" oops}";
				assertThatThrownBy(() -> connection.getJsonFormatter().fromJSON(secret)).isInstanceOf(DriverJsonParseException.class)
					.satisfies(t -> { assertThat(t.getMessage()).doesNotContain("123-45-6789"); assertThat(t.getCause()).isNotNull(); });
				assertThatThrownBy(() -> connection.getJsonDocumentFactory().create(secret)).isInstanceOf(DriverJsonParseException.class)
					.satisfies(t -> { assertThat(t.getMessage()).doesNotContain("123-45-6789"); assertThat(t.getCause()).isNotNull(); });
			}
			finally { connection.close(); }
		}
	}

	@Test void pdxDocumentsExposeMissingNullNestedAndArrayFields(@TempDir java.nio.file.Path directory) throws Exception {
		try (var server = new NativeServerFixture(directory)) {
			var connection = connect(server.port);
			try {
				var doc = connection.getJsonDocumentFactory(DriverStorageFormat.PDX).create("{\"a\":null,\"n\":{\"x\":1},\"l\":[1,2]}");
				assertThat(doc.getFieldNames()).contains("a", "n", "l");
				assertThat(doc.getAndCheckField("missing")).isSameAs(DriverDocument.MissingField.INSTANCE);
				assertThat(doc.getField("a")).isNull();
				assertThat(doc.getField("n")).isInstanceOf(DriverJsonDocument.class);
				assertThat((List<?>) doc.getField("l")).hasSize(2);
				assertThat(doc.toJson()).contains("\"x\"");
			}
			finally { connection.close(); }
		}
	}

	@Test void bsonDocumentsAreCreatedAndParseFailuresAreMapped() {
		var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of("log-level", "error"), null, null));
		try {
			var factory = connection.getJsonDocumentFactory(DriverStorageFormat.BSON);
			assertThat(factory.create("{\"a\":1}").hasField("a")).isTrue();
			assertThatThrownBy(() -> factory.create("{not json")).isInstanceOf(DriverJsonParseException.class).hasCauseInstanceOf(org.apache.geode.json.JsonParseException.class);
		}
		finally { connection.close(); }
	}
}
