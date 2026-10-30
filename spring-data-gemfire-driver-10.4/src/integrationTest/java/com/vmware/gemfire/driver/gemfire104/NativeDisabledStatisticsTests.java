/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import com.vmware.gemfire.driver.contract.DisabledStatisticsContract;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;

/** Runs in its own JVM under the integration task's forkEvery=1 policy. */
@ResourceLock(Resources.SYSTEM_PROPERTIES)
class NativeDisabledStatisticsTests extends DisabledStatisticsContract {
	private String previous;

	@BeforeEach
	void disableStatistics() {
		previous = System.getProperty("gemfire.statsDisabled");
		System.setProperty("gemfire.statsDisabled", "true");
	}

	@AfterEach
	void restoreStatisticsSetting() {
		if (previous == null) System.clearProperty("gemfire.statsDisabled");
		else System.setProperty("gemfire.statsDisabled", previous);
	}

	@Override
	protected DriverConnection connectDisabled() {
		return new GemFire104Driver().connect(new CacheConfiguration(Map.of(
			"mcast-port", "0", "locators", "", "log-level", "error", "statistic-sampling-enabled", "false")));
	}

	@Test
	void modeIsCapturedByRuntimeAndSamplingDisabledStillAllowsEnabledStatistics() {
		try (var disabled = connectDisabled()) {
			var types = disabled.getRuntime().getStatisticsTypes();
			var type = types.createType("disabled-reconnect-" + UUID.randomUUID(), "", List.of());
			var factory = disabled.getRuntime().getStatisticsFactory();
			System.setProperty("gemfire.statsDisabled", "false");
			try (var reused = connectDisabled();
				var inert = reused.getRuntime().getStatisticsFactory().create(type, "inert", 1, false)) {
				assertThat(inert.getUniqueId()).isZero();
				assertThat(factory.findByType(type)).isEmpty();
			}
			// Closing the reused cache disconnects the original runtime.
			try (var enabled = connectDisabled();
				var active = enabled.getRuntime().getStatisticsFactory().create(type, "active", 1, false);
				var retained = factory.create(type, "still-inert", 1, false)) {
				assertThat(active.getUniqueId()).isPositive();
				assertThat(enabled.getRuntime().getStatisticsFactory().findByType(type)).containsExactly(active);
				assertThat(retained.getUniqueId()).isZero();
				assertThat(factory.findByType(type)).isEmpty();
				active.close();
				assertThat(active.isClosed()).isTrue();
			}
		}
	}
}
