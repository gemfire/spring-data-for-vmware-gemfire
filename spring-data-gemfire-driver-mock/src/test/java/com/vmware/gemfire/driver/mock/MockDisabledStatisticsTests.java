/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.contract.DisabledStatisticsContract;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

class MockDisabledStatisticsTests extends DisabledStatisticsContract {
	@Override
	protected DriverConnection connectDisabled() {
		return new MockGemFireDriver(false).connect(new CacheConfiguration(Map.of()));
	}

	@Test
	void explicitModeIsIsolatedAndDisabledInstancesNeverParticipateInSampling() {
		try (var disabled = connectDisabled();
			var enabled = new MockGemFireDriver().connect(new CacheConfiguration(
				Map.of("statistic-sampling-enabled", "false")))) {
			var types = disabled.getRuntime().getStatisticsTypes();
			var type = types.createType("isolated-disabled", "", List.of(types.createDescriptor(
				new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, true))));
			var disabledFactory = (MockStatisticsFactory) disabled.getRuntime().getStatisticsFactory();
			var enabledFactory = (MockStatisticsFactory) enabled.getRuntime().getStatisticsFactory();
			try (var inert = disabledFactory.create(type, "same", 1, false);
				var active = enabledFactory.create(type, "same", 1, false)) {
				var calls = new AtomicInteger();
				inert.setLongSupplier("count", calls::incrementAndGet);
				active.setLong("count", 7);
				assertThat(disabledFactory.sampleSuppliers()).isZero();
				disabled.close();
				assertThat(disabledFactory.sampleSuppliers()).isZero();
				assertThat(calls.get()).isZero();
				assertThat(inert.getLong("count")).isZero();
				assertThat(active.getLong("count")).isEqualTo(7);
				assertThat(enabledFactory.findByTextId("same")).containsExactly(active);
				assertThat(disabledFactory.findByTextId("same")).isEmpty();
			}
		}
	}
}
