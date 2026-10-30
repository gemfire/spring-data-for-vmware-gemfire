/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

class NativeStatisticsSupplierTests {
	@Test
	void enabledRuntimeSamplerPublishesSupplierValuesWithoutExplicitSampling() throws Exception {
		try (var connection = new GemFire104Driver().connect(new CacheConfiguration(Map.of(
			"mcast-port", "0", "locators", "", "log-level", "error",
			"statistic-sampling-enabled", "true", "statistic-sample-rate", "1000")))) {
			var schemas = connection.getRuntime().getStatisticsTypes();
			var type = schemas.createType("scheduled-" + UUID.randomUUID(), "", List.of(schemas.createDescriptor(
				new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, false))));
			try (var statistics = connection.getRuntime().getStatisticsFactory().create(type, "scheduled", 1, true)) {
				var next = new AtomicLong(7);
				var published = new CountDownLatch(1);
				statistics.setLongSupplier("count", () -> {
					// Observe an earlier sample's completed write, not just callback entry.
					if (statistics.getLong("count") == 43) published.countDown();
					return next.get();
				});
				assertThat(statistics.getLong("count")).isEqualTo(7);
				next.set(43);
				assertThat(published.await(30, TimeUnit.SECONDS)).as("scheduled value publication").isTrue();
				assertThat(statistics.getLong("count")).isEqualTo(43);
			}
		}
	}
}
