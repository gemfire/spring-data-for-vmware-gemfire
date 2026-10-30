/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class MockStatisticsTests {
	@Test
	void instanceRegistriesAreIndependentEvenWhenLabelsAndLocalIdsMatch() {
		try (var first = new MockConnection(); var second = new MockConnection()) {
			var firstFactory = first.getRuntime().getStatisticsFactory();
			var secondFactory = second.getRuntime().getStatisticsFactory();
			var type = first.getRuntime().getStatisticsTypes().createType("isolated", "", List.of());
			try (var a = firstFactory.create(type, "same-label", 7, false);
				 var b = secondFactory.create(type, "same-label", 7, false)) {
				assertThat(firstFactory).isSameAs(first.getRuntime().getStatisticsFactory()).isNotSameAs(secondFactory);
				assertThat(a.getUniqueId()).isEqualTo(b.getUniqueId());
				assertThat(a).isNotEqualTo(b);
				assertThat(firstFactory.findByTextId("same-label")).containsExactly(a);
				assertThat(secondFactory.findByNumericId(7)).containsExactly(b);
				a.close();
				assertThat(firstFactory.findByType(type)).isEmpty();
				assertThat(secondFactory.findByType(type)).containsExactly(b);
				assertThat(b.isClosed()).isFalse();
			}
		}
	}
	@Test
	void explicitSamplingIsConfinedToItsRuntimeAndOmitsClosedInstances() {
		try (var first = new MockConnection(); var second = new MockConnection()) {
			var schemas = first.getRuntime().getStatisticsTypes();
			var descriptor = schemas.createDescriptor(new com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration(
				"count", "", "", com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType.LONG, false));
			var type = schemas.createType("sampled", "", List.of(descriptor));
			var firstFactory = (MockStatisticsFactory) first.getRuntime().getStatisticsFactory();
			var secondFactory = (MockStatisticsFactory) second.getRuntime().getStatisticsFactory();
			try (var a = firstFactory.create(type, "same", 1, false);
				 var b = secondFactory.create(type, "same", 1, false)) {
				var firstCalls = new java.util.concurrent.atomic.AtomicLong();
				var secondCalls = new java.util.concurrent.atomic.AtomicLong();
				a.setLongSupplier("count", firstCalls::incrementAndGet);
				b.setLongSupplier("count", secondCalls::incrementAndGet);
				assertThat(firstFactory.sampleSuppliers()).isZero();
				assertThat(a.getLong("count")).isEqualTo(2);
				assertThat(b.getLong("count")).isEqualTo(1);
				a.close();
				assertThat(firstFactory.sampleSuppliers()).isZero();
				assertThat(firstCalls.get()).isEqualTo(2);
				assertThat(secondFactory.sampleSuppliers()).isZero();
				assertThat(b.getLong("count")).isEqualTo(2);
			}
		}
	}

}
