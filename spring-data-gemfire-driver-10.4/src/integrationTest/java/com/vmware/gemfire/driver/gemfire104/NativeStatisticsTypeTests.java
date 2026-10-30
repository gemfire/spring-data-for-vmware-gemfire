/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.geode.StatisticsTypeFactory;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

class NativeStatisticsTypeTests {
	@Test
	void xmlDelegationPreservesReaderAndWrapsRegisteredTypes() throws Exception {
		var nativeFactory = mock(StatisticsTypeFactory.class);
		var nativeType = mock(org.apache.geode.StatisticsType.class);
		try (var reader = new StringReader("<statistics/>")) {
			when(nativeFactory.createTypesFromXml(reader)).thenReturn(new org.apache.geode.StatisticsType[] { nativeType });
			var factory = new GemFireStatisticsTypes(nativeFactory);
			var result = factory.createTypesFromXml(reader);
			assertThat(result).hasSize(1);
			assertThat(factory.unwrap(result.get(0))).isSameAs(nativeType);
			assertThatThrownBy(result::clear).isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Test
	void xmlCheckedAndNativeConfigurationFailuresAreTranslatedWhileErrorsPropagate() throws Exception {
		var nativeFactory = mock(StatisticsTypeFactory.class);
		var factory = new GemFireStatisticsTypes(nativeFactory);
		try (var reader = new StringReader("<statistics/>")) {
			var io = new IOException("private-token");
			var config = new org.apache.geode.GemFireConfigException("private-token", io);
			var fatal = new LinkageError("fatal");
			when(nativeFactory.createTypesFromXml(reader)).thenThrow(io, config, fatal);
			for (var cause : List.of(io, config)) {
				assertThatThrownBy(() -> factory.createTypesFromXml(reader))
					.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
						assertThat(failure.kind()).isEqualTo(cause == io ? DriverFailureKind.UNKNOWN : DriverFailureKind.SYSTEM_FAILURE);
						assertThat(failure.getCause()).isSameAs(cause);
						assertThat(failure.getMessage()).doesNotContain("private-token");
					});
			}
			assertThatThrownBy(() -> factory.createTypesFromXml(reader)).isSameAs(fatal);
		}
	}

	@Test
	void typedDescriptorsMatchNativeFactoriesIncludingDeprecatedIntStorage() {
		try (var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of(
			"mcast-port", "0", "locators", "", "log-level", "error", "statistic-sampling-enabled", "false")))) {
			var system = ((GemFireConnection) connection).cacheForTransactionCallback().getDistributedSystem();
			var factory = connection.getRuntime().getStatisticsTypes();
			for (boolean counter : List.of(false, true)) {
				for (boolean largerBetter : List.of(false, true)) {
					var nativeInt = counter ? system.createIntCounter("int", null, null, largerBetter)
						: system.createIntGauge("int", null, null, largerBetter);
					var descriptor = factory.createDescriptor(new StatisticDescriptorConfiguration("int", null, null, ValueType.LONG, counter, largerBetter));
					assertThat(nativeInt.getType()).isEqualTo(long.class);
					assertThat(descriptor.getType()).isEqualTo(ValueType.LONG);
					assertThat(descriptor.getDescription()).isEqualTo(nativeInt.getDescription());
					assertThat(descriptor.getUnit()).isEqualTo(nativeInt.getUnit());
					assertThat(descriptor.isCounter()).isEqualTo(nativeInt.isCounter());
					assertThat(descriptor.isLargerBetter()).isEqualTo(nativeInt.isLargerBetter());
					assertThat(descriptor.getId()).isEqualTo(nativeInt.getId());
				}
			}
			assertThat(system.createIntCounter("default", "", "").isLargerBetter()).isTrue();
			assertThat(system.createIntGauge("default", "", "").isLargerBetter()).isFalse();
			String name = "native-types-" + UUID.randomUUID();
			var descriptors = List.of(factory.createDescriptor(new StatisticDescriptorConfiguration("ratio", "", "%", ValueType.DOUBLE, false)),
				factory.createDescriptor(new StatisticDescriptorConfiguration("count", "", "operations", ValueType.LONG, true)));
			var type = factory.createType(name, "schema", descriptors);
			var nativeType = system.findType(name);
			assertThat(nativeType).isNotNull();
			assertThat(nativeType.getStatistics()).extracting(org.apache.geode.StatisticDescriptor::getName).containsExactly("ratio", "count");
			assertThat(type.nameToId("ratio")).isEqualTo(nativeType.nameToId("ratio"));
			assertThat(type.nameToId("count")).isEqualTo(nativeType.nameToId("count"));
			assertThat(new GemFireStatisticsTypes(system).findType(name)).isEqualTo(type);
			assertThat(nativeType.getStatistics()).hasSize(type.getStatistics().size());
			assertThat(StatisticsTypeFactory.MAX_DESCRIPTORS_PER_TYPE).isEqualTo(254);
		}
	}

	@Test
	void factoryFailuresAreTranslatedAndErrorsPropagate() {
		var nativeFactory = mock(StatisticsTypeFactory.class);
		var factory = new GemFireStatisticsTypes(nativeFactory);
		var cause = new IllegalStateException("private-token");
		when(nativeFactory.findType("private-token")).thenThrow(cause);
		when(nativeFactory.createLongCounter("value", "", "", true)).thenThrow(cause);
		for (Runnable operation : List.<Runnable>of(() -> factory.findType("private-token"),
			() -> factory.createDescriptor(new StatisticDescriptorConfiguration("value", "", "", ValueType.LONG, true)))) {
			assertThatThrownBy(operation::run).isInstanceOfSatisfying(DriverOperationException.class, failure -> {
				assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
				assertThat(failure.getCause()).isSameAs(cause);
				assertThat(failure.getMessage()).doesNotContain("private-token");
			});
		}
		assertThatThrownBy(() -> factory.createType("foreign", "", List.of(mock(DriverStatisticDescriptor.class))))
			.isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		var fatal = new LinkageError("fatal");
		when(nativeFactory.findType("fatal")).thenThrow(fatal);
		assertThatThrownBy(() -> factory.findType("fatal")).isSameAs(fatal);
	}
}
