/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/** Disabled statistics are distinct from enabled statistics with sampling turned off. */
public abstract class DisabledStatisticsContract {
	protected abstract DriverConnection connectDisabled();

	@Test
	void disabledInstancesKeepExactMetadataButNeverRegisterOrClose() {
		try (var connection = connectDisabled()) {
			var type = type(connection);
			var factory = connection.getRuntime().getStatisticsFactory();
			assertThat(connection.getRuntime().getStatisticsTypes().findType(type.getName())).isEqualTo(type);
			for (boolean atomic : List.of(false, true)) {
				for (String text : new String[] { null, "", "disabled" }) {
					try (var instance = factory.create(type, text, 0, atomic)) {
						assertThat(instance.getType()).isEqualTo(type);
						assertThat(instance.getTextId()).isEqualTo(text);
						assertThat(instance.getNumericId()).isZero();
						assertThat(instance.getUniqueId()).isZero();
						assertThat(instance.isAtomic()).isTrue();
						assertThat(instance.isClosed()).isFalse();
						assertThat(instance.nameToDescriptor("count")).isEqualTo(type.nameToDescriptor("count"));
						assertThat(instance.nameToId("count")).isEqualTo(type.nameToId("count"));
						assertThat(factory.findByType(type)).isEmpty();
						assertThat(factory.findByTextId(text)).isEmpty();
						assertThat(factory.findByNumericId(0)).isEmpty();
						instance.close();
						assertThat(instance.isClosed()).isFalse();
					}
				}
			}
			connection.close();
			try (var retained = factory.create(type, "after-close", -7, false)) {
				assertThat(retained.getNumericId()).isEqualTo(-7);
				assertThat(retained.isClosed()).isFalse();
				assertThat(factory.findByType(type)).isEmpty();
			}
		}
	}

	@Test
	void disabledValuesIgnoreUpdatesAndValueSelectorsButExplicitNameLookupStillValidates() {
		try (var connection = connectDisabled()) {
			var type = type(connection);
			for (boolean atomic : List.of(false, true)) {
				try (var instance = connection.getRuntime().getStatisticsFactory().create(type, "values", 42, atomic)) {
					var count = type.nameToDescriptor("count");
					var ratio = type.nameToDescriptor("ratio");
					for (int id : new int[] { count.getId(), ratio.getId(), -1, Integer.MAX_VALUE }) {
						instance.setLong(id, 9); instance.incLong(id, 1);
						instance.setDouble(id, 9.5); instance.incDouble(id, 1.5);
						assertThat(instance.getLong(id)).isZero();
						assertThat(instance.getDouble(id)).isZero();
					}
					for (String name : new String[] { "count", "ratio", "missing", null }) {
						instance.setLong(name, 9); instance.incLong(name, 1);
						instance.setDouble(name, 9.5); instance.incDouble(name, 1.5);
						assertThat(instance.getLong(name)).isZero();
						assertThat(instance.getDouble(name)).isZero();
						assertThat(instance.get(name)).isEqualTo(Integer.valueOf(0));
						assertThat(instance.getRawBits(name)).isZero();
					}
					for (var descriptor : List.of(count, ratio)) {
						instance.setLong(descriptor, 9); instance.incLong(descriptor, 1);
						instance.setDouble(descriptor, 9.5); instance.incDouble(descriptor, 1.5);
						assertThat(instance.getLong(descriptor)).isZero();
						assertThat(instance.getDouble(descriptor)).isZero();
						assertThat(instance.get(descriptor)).isEqualTo(Integer.valueOf(0));
						assertThat(instance.getRawBits(descriptor)).isZero();
					}
					assertThatThrownBy(() -> instance.getLong((DriverStatisticDescriptor) null))
						.isInstanceOfSatisfying(DriverOperationException.class,
							failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
					assertThatThrownBy(() -> instance.nameToId("missing"))
						.isInstanceOfSatisfying(DriverOperationException.class,
							failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
					assertThatThrownBy(() -> instance.nameToDescriptor("missing"))
						.isInstanceOf(DriverOperationException.class);
				}
			}
		}
	}

	@Test
	void disabledSuppliersAreNeverEvaluatedOrRetainedEvenAfterClose() {
		try (var connection = connectDisabled()) {
			var type = type(connection);
			for (boolean atomic : List.of(false, true)) {
				try (var instance = connection.getRuntime().getStatisticsFactory().create(type, "suppliers", 1, atomic)) {
					var calls = new AtomicInteger();
					IntSupplier ints = calls::incrementAndGet;
					LongSupplier longs = calls::incrementAndGet;
					DoubleSupplier doubles = calls::incrementAndGet;
					for (int attempt = 0; attempt < 2; attempt++) {
						assertThat(instance.setIntSupplier(-1, ints)).isNull();
						assertThat(instance.setLongSupplier(-1, longs)).isNull();
						assertThat(instance.setDoubleSupplier(-1, doubles)).isNull();
						assertThat(instance.setIntSupplier("missing", ints)).isNull();
						assertThat(instance.setLongSupplier("missing", longs)).isNull();
						assertThat(instance.setDoubleSupplier("missing", doubles)).isNull();
						assertThat(instance.setIntSupplier(type.nameToDescriptor("ratio"), ints)).isNull();
						assertThat(instance.setLongSupplier(type.nameToDescriptor("ratio"), longs)).isNull();
						assertThat(instance.setDoubleSupplier(type.nameToDescriptor("count"), doubles)).isNull();
						assertThat(instance.setLongSupplier("count", longs)).isNull();
						assertThat(instance.setLongSupplier("count", null)).isNull();
						instance.close();
					}
					assertThat(calls.get()).isZero();
					assertThat(instance.getLong("count")).isZero();
				}
			}
		}
	}

	private DriverStatisticsType type(DriverConnection connection) {
		var types = connection.getRuntime().getStatisticsTypes();
		return types.createType("disabled-" + UUID.randomUUID(), "", List.of(
			types.createDescriptor(new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, true)),
			types.createDescriptor(new StatisticDescriptorConfiguration("ratio", "", "", ValueType.DOUBLE, false))));
	}
}
