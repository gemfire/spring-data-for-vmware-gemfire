/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.geode.Statistics;
import org.apache.geode.StatisticsFactory;
import org.apache.geode.StatisticsTypeFactory;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

class NativeStatisticsTests {
	@Test
	void instanceFactoryUsesActiveRuntimeRegistryAndDeprecatedIntReadsNarrowLongStorage() {
		try (var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of(
			"mcast-port", "0", "locators", "", "log-level", "error", "statistic-sampling-enabled", "false")))) {
			var system = ((GemFireConnection) connection).cacheForTransactionCallback().getDistributedSystem();
			var schemas = connection.getRuntime().getStatisticsTypes();
			var factory = connection.getRuntime().getStatisticsFactory();
			String name = "native-instances-" + UUID.randomUUID();
			var type = schemas.createType(name, "", List.of(schemas.createDescriptor(
				new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, true))));
			var nativeType = system.findType(name);
			for (boolean atomic : List.of(false, true)) {
				var control = atomic ? system.createAtomicStatistics(nativeType, "control", -987654321L)
					: system.createStatistics(nativeType, "control", -987654321L);
				try (var wrapped = factory.create(type, "wrapped", -987654321L, atomic)) {
					var raw = java.util.Arrays.stream(system.findStatisticsByType(nativeType))
						.filter(s -> s.getUniqueId() == wrapped.getUniqueId()).findFirst().orElseThrow();
					assertThat(wrapped.getNumericId()).isEqualTo(control.getNumericId()).isEqualTo(-987654321L);
					assertThat(wrapped.isAtomic()).isEqualTo(control.isAtomic());
					assertThat(wrapped.getTextId()).isEqualTo(raw.getTextId());
					assertThat(factory.findByType(type)).extracting(s -> s.getUniqueId())
						.contains(control.getUniqueId(), raw.getUniqueId());
					wrapped.setLong("count", 0x1_0000_0001L);
					assertThat(raw.getLong("count")).isEqualTo(wrapped.getLong("count"));
					assertThat(raw.getInt("count")).isEqualTo((int) wrapped.getLong("count")).isEqualTo(1);
					raw.setInt("count", Integer.MAX_VALUE);
					raw.incInt("count", 1);
					assertThat(wrapped.getLong("count")).isEqualTo(2147483648L);
					assertThat(raw.getInt("count")).isEqualTo(Integer.MIN_VALUE);
					var snapshot = factory.findByTextId("wrapped");
					raw.close();
					assertThat(wrapped.isClosed()).isTrue();
					assertThat(wrapped.get("count")).isEqualTo(Integer.valueOf(0));
					assertThat(snapshot).contains(wrapped);
					assertThat(factory.findByType(type)).doesNotContain(wrapped);
				}
				finally { control.close(); }
			}
		}
	}

	@Test
	void compatibleConnectionsShareStatisticsRegistryAndTypesSurviveReconnect() {
		var driver = new GemFire104Driver();
		var configuration = new CacheConfiguration(Map.of(
			"mcast-port", "0", "locators", "", "log-level", "error", "statistic-sampling-enabled", "false"));
		try (var first = driver.connect(configuration); var second = driver.connect(configuration)) {
			var schemas = first.getRuntime().getStatisticsTypes();
			String name = "reuse-statistics-" + UUID.randomUUID();
			var type = schemas.createType(name, "", List.of(schemas.createDescriptor(
				new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, true))));
			var firstFactory = first.getRuntime().getStatisticsFactory();
			var secondFactory = second.getRuntime().getStatisticsFactory();
			try (var instance = firstFactory.create(type, name, 123, true)) {
				var alias = secondFactory.findByTextId(name).get(0);
				assertThat(alias).isEqualTo(instance);
				alias.setLong("count", 7);
				assertThat(instance.getLong("count")).isEqualTo(7);
				second.close();
				assertThat(first.getRuntime().isConnected()).isFalse();
				assertThat(alias.isClosed()).isFalse();
				assertThat(firstFactory.findByType(type)).containsExactly(instance);
				assertThat(secondFactory.findByType(type)).containsExactly(instance);
				try (var fresh = driver.connect(configuration)) {
					// Native type registration is process-wide; instance registration is runtime-local.
					var reusedType = fresh.getRuntime().getStatisticsTypes().findType(name);
					assertThat(reusedType).isEqualTo(type);
					var freshFactory = fresh.getRuntime().getStatisticsFactory();
					assertThat(freshFactory.findByType(reusedType)).isEmpty();
					try (var replacement = freshFactory.create(reusedType, name, 123, true)) {
						alias.close();
						assertThat(instance.isClosed()).isTrue();
						assertThat(firstFactory.findByType(type)).isEmpty();
						assertThat(secondFactory.findByType(type)).isEmpty();
						assertThat(freshFactory.findByType(reusedType)).containsExactly(replacement);
					}
				}
			}
		}
	}

	@Test
	void instanceAndRegistryFailuresAreTranslatedWithoutCatchingFatalErrors() {
		var nativeFactory = mock(StatisticsFactory.class);
		var types = new GemFireStatisticsTypes(mock(StatisticsTypeFactory.class));
		var factory = new GemFireStatisticsFactory(nativeFactory, types);
		var cause = new IllegalStateException("private-token");
		when(nativeFactory.findStatisticsByTextId("private-token")).thenThrow(cause);
		assertThatThrownBy(() -> factory.findByTextId("private-token"))
			.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
				assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
				assertThat(failure.getCause()).isSameAs(cause);
				assertThat(failure.getMessage()).doesNotContain("private-token");
			});
		assertThatThrownBy(() -> factory.create(mock(DriverStatisticsType.class), "foreign", 1, false))
			.isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		var nativeInstance = mock(Statistics.class);
		var instance = new GemFireStatistics(nativeInstance, types);
		when(nativeInstance.getLong("private-token")).thenThrow(cause);
		assertThatThrownBy(() -> instance.getLong("private-token"))
			.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
				assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
				assertThat(failure.getCause()).isSameAs(cause);
				assertThat(failure.getMessage()).doesNotContain("private-token");
			});
		var fatal = new LinkageError("fatal");
		when(nativeInstance.getDouble(1)).thenThrow(fatal);
		when(nativeFactory.findStatisticsByNumericId(1)).thenThrow(fatal);
		assertThatThrownBy(() -> instance.getDouble(1)).isSameAs(fatal);
		assertThatThrownBy(() -> factory.findByNumericId(1)).isSameAs(fatal);
	}
}
