/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

class MockStatisticsTypeTests {
	@Test
	void xmlUsesTheConnectionRegistryAndRemainsAvailableThroughCapturedHandles() {
		try (var first = new MockConnection(); var second = new MockConnection()) {
			var types = first.getRuntime().getStatisticsTypes();
			var loaded = types.createTypesFromXml(new StringReader("<statistics><type name=\"xml-local\"/></statistics>"));
			assertThat(types.findType("xml-local")).isSameAs(loaded.get(0));
			assertThat(second.getRuntime().getStatisticsTypes().findType("xml-local")).isNull();
			first.close();
			assertThat(types.createTypesFromXml(new StringReader("<statistics><type name=\"xml-local\"/></statistics>")))
				.containsExactly(loaded.get(0));
		}
	}

	@Test
	void xmlRejectsUnknownExternalDefinitionsAndEntitiesBeforeRegistration() {
		try (var connection = new MockConnection()) {
			var types = connection.getRuntime().getStatisticsTypes();
			for (String xml : List.of(
				"<!DOCTYPE statistics SYSTEM 'file:///private-missing.dtd'><statistics><type name='external'/></statistics>",
				"<!DOCTYPE statistics [<!ENTITY secret SYSTEM 'file:///private-missing'>]>"
					+ "<statistics><type name='external'><description>&secret;</description></type></statistics>")) {
				assertThatThrownBy(() -> types.createTypesFromXml(new StringReader(xml)))
					.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
						assertThat(failure.kind()).isEqualTo(DriverFailureKind.SYSTEM_FAILURE);
						assertThat(failure.getMessage()).doesNotContain("private-missing");
						assertThat(failure.getCause()).isInstanceOf(org.xml.sax.SAXException.class);
					});
				assertThat(types.findType("external")).isNull();
			}
		}
	}

	@Test
	void statisticsRegistriesAreConnectionOwnedAndSurviveClosureThroughCapturedHandles() {
		try (var first = new MockConnection(); var second = new MockConnection()) {
			var firstTypes = first.getRuntime().getStatisticsTypes();
			var secondTypes = second.getRuntime().getStatisticsTypes();
			assertThat(firstTypes).isSameAs(first.getRuntime().getStatisticsTypes()).isNotSameAs(secondTypes);
			var firstType = firstTypes.createType("shared-name", "first", List.of(firstTypes.createDescriptor(
				new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, true))));
			assertThat(secondTypes.findType("shared-name")).isNull();
			var secondType = secondTypes.createType("shared-name", "second", List.of());
			assertThat(firstTypes.findType("shared-name")).isSameAs(firstType);
			assertThat(secondTypes.findType("shared-name")).isSameAs(secondType);
			first.close();
			assertThat(firstTypes.findType("shared-name")).isSameAs(firstType);
			assertThat(firstType.nameToId("count")).isZero();
			assertThat(secondTypes.findType("shared-name").getDescription()).isEqualTo("second");
		}
	}
}
