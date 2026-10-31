// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

class MockJsonTests {
	private final MockConnection connection = new MockConnection(new Properties(), false, null, null);

	private static void assertKind(Throwable t, DriverFailureKind kind) {
		assertThat(t).isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(kind));
	}

	@Test void documentsExposeMissingNullNestedAndArrayFields() {
		var doc = connection.getJsonDocumentFactory().create("{\"a\":null,\"n\":{\"x\":1},\"l\":[1,\"s\",{\"y\":true}]}");
		assertThat(doc.getFieldNames()).containsExactly("a", "n", "l");
		assertThat(doc.hasField("a")).isTrue();
		assertThat(doc.getField("a")).isNull();
		assertThat(doc.getField("missing")).isNull();
		assertThat(doc.getAndCheckField("missing")).isSameAs(DriverDocument.MissingField.INSTANCE);
		assertThat(doc.getField("n")).isInstanceOf(DriverJsonDocument.class);
		assertThat((List<?>) doc.getField("l")).hasSize(3);
		assertThat(doc.toJson()).isEqualTo("{\"a\":null,\"n\":{\"x\":1},\"l\":[1,\"s\",{\"y\":true}]}");
	}

	@Test void integralNumbersNarrowToTheSmallestType() {
		var doc = connection.getJsonDocumentFactory().create("{\"b\":1,\"s\":300,\"i\":70000,\"l\":5000000000,\"big\":99999999999999999999,\"d\":1.5,\"e\":1e2}");
		assertThat(doc.getField("b")).isEqualTo((byte) 1);
		assertThat(doc.getField("s")).isEqualTo((short) 300);
		assertThat(doc.getField("i")).isEqualTo(70000);
		assertThat(doc.getField("l")).isEqualTo(5000000000L);
		assertThat(doc.getField("big")).isEqualTo(new BigInteger("99999999999999999999"));
		assertThat(doc.getField("d")).isEqualTo(1.5d);
		assertThat(doc.getField("e")).isEqualTo(100.0d);
	}

	@Test void formatterBuildsJsonClassPdxWithNestedValues() {
		var pdx = connection.getJsonFormatter().fromJSON("{\"name\":\"ann\",\"n\":{\"x\":1},\"l\":[1,2],\"z\":null}");
		assertThat(pdx.getClassName()).isEqualTo(DriverJsonFormatter.JSON_CLASSNAME).isEqualTo("__GEMFIRE_JSON");
		assertThat(pdx.getField("name")).isEqualTo("ann");
		assertThat(pdx.getField("n")).isInstanceOf(DriverPdxInstance.class);
		assertThat((List<Object>) pdx.getField("l")).containsExactly((byte) 1, (byte) 2);
		assertThat(pdx.hasField("z")).isTrue();
	}

	@Test void identityFieldsAreMarkedAfterTheWrites() {
		var pdx = connection.getJsonFormatter().fromJSON("{\"id\":1,\"name\":\"ann\"}", "id");
		assertThat(pdx.isIdentityField("id")).isTrue();
		assertThat(pdx.isIdentityField("name")).isFalse();
		assertThat(pdx.getFieldNames()).containsExactly("id", "name");
		assertThatThrownBy(() -> connection.getJsonFormatter().fromJSON("{\"id\":1}", "unknown"))
			.isInstanceOf(DriverOperationException.class);
	}

	@Test void pdxRoundTripsToJsonAndBytes() {
		var formatter = connection.getJsonFormatter();
		var pdx = formatter.fromJSON("{\"a\":[1,{\"b\":null}],\"c\":\"x\"}");
		assertThat(formatter.toJSON(pdx)).isEqualTo("{\"a\":[1,{\"b\":null}],\"c\":\"x\"}");
		assertThat(new String(formatter.toJSONByteArray(pdx), StandardCharsets.UTF_8)).isEqualTo(formatter.toJSON(pdx));
		assertThat(formatter.toJsonByteArrayFromPdxInstance(pdx)).isEqualTo(formatter.toJSONByteArray(pdx));
		assertThat(formatter.fromPdxInstance(pdx)).isEqualTo(formatter.toJSON(pdx));
	}

	@Test void textIsNormalizedToUtf8InBothDirections() {
		var formatter = connection.getJsonFormatter();
		var json = "{\"k\":\"héllo €\"}";
		var pdx = formatter.fromJSON(json.getBytes(StandardCharsets.UTF_8));
		assertThat(pdx.getField("k")).isEqualTo("héllo €");
		assertThat(formatter.toJsonByteArrayFromPdxInstance(pdx)).isEqualTo(json.getBytes(StandardCharsets.UTF_8));
		assertThat(formatter.fromPdxInstance(pdx)).isEqualTo(json);
	}

	@Test void parseFailuresKeepTheirCauseAndDoNotLeakTheDocument() {
		var secret = "{\"ssn\":\"123-45-6789\" oops}";
		assertThatThrownBy(() -> connection.getJsonDocumentFactory().create(secret)).isInstanceOf(DriverJsonParseException.class)
			.satisfies(t -> {
				assertKind(t, DriverFailureKind.INVALID_ARGUMENT);
				assertThat(t.getCause()).isNotNull();
				assertThat(t.getMessage()).doesNotContain("123-45-6789");
				assertThat(t.getCause().getMessage()).doesNotContain("123-45-6789").doesNotContain("ssn");
			});
		assertThatThrownBy(() -> connection.getJsonFormatter().fromJSON(secret)).isInstanceOf(DriverJsonParseException.class);
	}

	@Test void invalidInputsAreRejected() {
		assertThatThrownBy(() -> connection.getJsonDocumentFactory().create("[1,2]")).isInstanceOf(DriverJsonParseException.class);
		assertThatThrownBy(() -> connection.getJsonDocumentFactory().create("{\"a\":1} extra")).isInstanceOf(DriverJsonParseException.class);
		assertThatThrownBy(() -> connection.getJsonDocumentFactory().create(null)).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_ARGUMENT));
		assertThatThrownBy(() -> connection.getJsonFormatter().toPdxInstance(42)).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_ARGUMENT));
		assertThatThrownBy(() -> connection.getJsonFormatter().toJSON(null)).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_ARGUMENT));
	}

	@Test void bsonStorageIsNotIntroducedAndPdxStorageIsSupported() {
		assertThatThrownBy(() -> connection.getJsonDocumentFactory(DriverStorageFormat.BSON)).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("JsonDocument.bsonStorage");
		assertThat(connection.getJsonDocumentFactory(DriverStorageFormat.PDX).create("{\"a\":1}").getField("a")).isEqualTo((byte) 1);
		assertThatThrownBy(() -> connection.getJsonDocumentFactory(null)).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_ARGUMENT));
	}

	@Test void formatterIsBoundToTheIssuingService() {
		assertThat(connection.getJsonFormatter().getRegionService()).isSameAs(connection);
	}

	@Test void closedConnectionsRejectJsonOperations() {
		var formatter = connection.getJsonFormatter();
		var factory = connection.getJsonDocumentFactory();
		connection.close();
		assertThatThrownBy(() -> formatter.fromJSON("{}")).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_STATE));
		assertThatThrownBy(() -> factory.create("{}")).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_STATE));
		assertThatThrownBy(connection::getJsonFormatter).satisfies(t -> assertKind(t, DriverFailureKind.INVALID_STATE));
	}
}
