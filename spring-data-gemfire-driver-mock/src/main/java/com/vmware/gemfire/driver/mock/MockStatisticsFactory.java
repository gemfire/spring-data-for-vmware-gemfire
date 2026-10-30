/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatistics;
import com.vmware.gemfire.driver.spi.DriverStatisticsFactory;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;

/** Per-runtime registry with explicit sampling; no background timer or process-global state. */
public final class MockStatisticsFactory implements DriverStatisticsFactory {
	private final MockStatisticsTypes types;
	private final String runtimeName;
	private final boolean enabled;
	private final AtomicLong nextId = new AtomicLong(1);
	private final List<DriverStatistics> instances = new CopyOnWriteArrayList<>();

	MockStatisticsFactory(MockStatisticsTypes types, String runtimeName, boolean enabled) {
		this.types = types;
		this.runtimeName = runtimeName;
		this.enabled = enabled;
	}
	@Override public DriverStatistics create(DriverStatisticsType type, String textId, long numericId, boolean atomic) {
		return invoke("StatisticsFactory.create", () -> {
			if (!enabled) return new MockDisabledStatistics(types.requireType(type), types, textId, numericId);
			var instance = new MockStatistics(types.requireType(type), types,
				textId == null || textId.isEmpty() ? runtimeName : textId,
				numericId == 0 ? ProcessHandle.current().pid() : numericId,
				nextId.getAndIncrement(), atomic, instances::remove);
			instances.add(instance);
			return instance;
		});
	}
	@Override public List<DriverStatistics> findByType(DriverStatisticsType type) {
		return invoke("StatisticsFactory.findByType", () -> {
			types.requireType(type);
			return instances.stream().filter(instance -> instance.getType() == type).toList();
		});
	}
	@Override public List<DriverStatistics> findByTextId(String textId) {
		return invoke("StatisticsFactory.findByTextId", () ->
			instances.stream().filter(instance -> textId.equals(instance.getTextId())).toList());
	}
	@Override public List<DriverStatistics> findByNumericId(long numericId) {
		return instances.stream().filter(instance -> instance.getNumericId() == numericId).toList();
	}
	/**
	 * Evaluate registered suppliers once and return the number of failed callbacks.
	 * This explicit test control operates regardless of automatic sampling settings.
	 * Instances closed before the registry snapshot are omitted; callbacks already
	 * in flight may finish. Supplier failures preserve values and do not stop sampling.
	 */
	public int sampleSuppliers() {
		int errors = 0;
		for (var instance : instances) errors += ((MockStatistics) instance).updateSuppliedValues();
		return errors;
	}
	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (IllegalArgumentException | NullPointerException failure) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, operation + " failed", failure);
		}
	}
}
