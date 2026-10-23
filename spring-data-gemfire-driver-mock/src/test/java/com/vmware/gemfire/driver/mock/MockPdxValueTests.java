/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import com.vmware.gemfire.driver.spi.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class MockPdxValueTests {
	@Test void mockRejectsNativeDeserializationAndUnknownObjectEncodingWithoutInvokingApplicationCode() {
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(Map.of()))) {
			var value = connection.createPdxInstanceFactory("ApplicationType").writeInt("id", 1).create();
			assertThatThrownBy(value::getObject).isInstanceOfSatisfying(UnsupportedFeatureException.class,
				ex -> assertThat(ex.feature()).isEqualTo("Pdx.applicationObjectDeserialization"));
			for (boolean portable : List.of(false, true)) {
				assertThatThrownBy(() -> connection.createPdxInstanceFactory("Type").writeObject("opaque", new Object(), portable))
					.isInstanceOfSatisfying(UnsupportedFeatureException.class, ex -> assertThat(ex.feature()).isEqualTo("Pdx.applicationObjectSerialization"));
			}
			List<Object> cyclic = new ArrayList<>(); cyclic.add(cyclic);
			assertThatThrownBy(() -> connection.createPdxInstanceFactory("Type").writeObject("cycle", cyclic)).isInstanceOf(UnsupportedFeatureException.class);
		}
	}
	@Test void mockSnapshotsNestedWritableValuesAndDateArraysWithoutGlobalSchemaState() {
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(Map.of()))) {
			var child = connection.createPdxInstanceFactory("Child").writeInt("id", 1).create().createWriter();
			Date date = new Date(42); var factory = connection.createPdxInstanceFactory("Parent").writeObject("child", child).writeDate("date", date);
			date.setTime(100); child.setField("id", 2);
			var value = factory.create(); assertThat(((Date) value.getField("date")).getTime()).isEqualTo(42);
			assertThat(((DriverPdxInstance) value.getField("child")).getField("id")).isEqualTo(1);
			assertThat(value.toString()).contains("Parent").doesNotContain("42");
		}
	}
}
