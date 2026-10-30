/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.geode.Statistics;
import org.apache.geode.StatisticsFactory;

import com.vmware.gemfire.driver.spi.DriverStatistics;
import com.vmware.gemfire.driver.spi.DriverStatisticsFactory;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;

final class GemFireStatisticsFactory implements DriverStatisticsFactory {
	private final StatisticsFactory factory;
	private final GemFireStatisticsTypes types;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	GemFireStatisticsFactory(StatisticsFactory factory, GemFireStatisticsTypes types) {
		this.factory = factory;
		this.types = types;
	}
	@Override public DriverStatistics create(DriverStatisticsType type, String textId, long numericId, boolean atomic) {
		return invoke("StatisticsFactory.create", () -> new GemFireStatistics(atomic
			? factory.createAtomicStatistics(types.unwrap(type), textId, numericId)
			: factory.createStatistics(types.unwrap(type), textId, numericId), types));
	}
	@Override public List<DriverStatistics> findByType(DriverStatisticsType type) {
		return invoke("StatisticsFactory.findByType", () -> wrap(factory.findStatisticsByType(types.unwrap(type))));
	}
	@Override public List<DriverStatistics> findByTextId(String textId) {
		return invoke("StatisticsFactory.findByTextId", () -> wrap(factory.findStatisticsByTextId(textId)));
	}
	@Override public List<DriverStatistics> findByNumericId(long numericId) {
		return invoke("StatisticsFactory.findByNumericId", () -> wrap(factory.findStatisticsByNumericId(numericId)));
	}
	private List<DriverStatistics> wrap(Statistics[] instances) {
		return Arrays.stream(instances).<DriverStatistics>map(instance -> new GemFireStatistics(instance, types)).toList();
	}
	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException failure) { throw exceptions.translate(operation, failure); }
	}
}
