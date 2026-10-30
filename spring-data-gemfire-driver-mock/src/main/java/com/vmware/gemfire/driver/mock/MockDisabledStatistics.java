/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.vmware.gemfire.driver.spi.DriverStatistics;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

/** Inert statistics: metadata survives, values and callbacks are never stored. */
final class MockDisabledStatistics implements DriverStatistics {
	private final DriverStatisticsType type;
	private final MockStatisticsTypes types;
	private final String textId;
	private final long numericId;

	MockDisabledStatistics(DriverStatisticsType type, MockStatisticsTypes types, String textId, long numericId) {
		this.type = type;
		this.types = types;
		this.textId = textId;
		this.numericId = numericId;
	}
	@Override public void close() {}
	@Override public int nameToId(String name) { return type.nameToId(name); }
	@Override public DriverStatisticDescriptor nameToDescriptor(String name) { return type.nameToDescriptor(name); }
	@Override public long getUniqueId() { return 0; }
	@Override public DriverStatisticsType getType() { return type; }
	@Override public String getTextId() { return textId; }
	@Override public long getNumericId() { return numericId; }
	@Override public boolean isAtomic() { return true; }
	@Override public boolean isClosed() { return false; }
	@Override public long getLong(int id) { return 0L; }
	@Override public void setLong(int id, long value) { }
	@Override public void incLong(int id, long value) { }
	@Override public long getLong(String name) { return 0L; }
	@Override public void setLong(String name, long value) { }
	@Override public void incLong(String name, long value) { }
	@Override public long getLong(DriverStatisticDescriptor descriptor) { return descriptor("Statistics.getLong", descriptor, () -> 0L); }
	@Override public void setLong(DriverStatisticDescriptor descriptor, long value) { descriptor("Statistics.setLong", descriptor, () -> null); }
	@Override public void incLong(DriverStatisticDescriptor descriptor, long value) { descriptor("Statistics.incLong", descriptor, () -> null); }
	@Override public double getDouble(int id) { return 0.0; }
	@Override public void setDouble(int id, double value) { }
	@Override public void incDouble(int id, double value) { }
	@Override public double getDouble(String name) { return 0.0; }
	@Override public void setDouble(String name, double value) { }
	@Override public void incDouble(String name, double value) { }
	@Override public double getDouble(DriverStatisticDescriptor descriptor) { return descriptor("Statistics.getDouble", descriptor, () -> 0.0); }
	@Override public void setDouble(DriverStatisticDescriptor descriptor, double value) { descriptor("Statistics.setDouble", descriptor, () -> null); }
	@Override public void incDouble(DriverStatisticDescriptor descriptor, double value) { descriptor("Statistics.incDouble", descriptor, () -> null); }
	@Override public Number get(String name) { return Integer.valueOf(0); }
	@Override public Number get(DriverStatisticDescriptor descriptor) { return descriptor("Statistics.get", descriptor, () -> Integer.valueOf(0)); }
	@Override public long getRawBits(String name) { return 0L; }
	@Override public long getRawBits(DriverStatisticDescriptor descriptor) { return descriptor("Statistics.getRawBits", descriptor, () -> 0L); }
	@Override public IntSupplier setIntSupplier(int id, IntSupplier supplier) { return null; }
	@Override public IntSupplier setIntSupplier(String name, IntSupplier supplier) { return null; }
	@Override public IntSupplier setIntSupplier(DriverStatisticDescriptor descriptor, IntSupplier supplier) { return descriptor("Statistics.setIntSupplier", descriptor, () -> null); }
	@Override public LongSupplier setLongSupplier(int id, LongSupplier supplier) { return null; }
	@Override public LongSupplier setLongSupplier(String name, LongSupplier supplier) { return null; }
	@Override public LongSupplier setLongSupplier(DriverStatisticDescriptor descriptor, LongSupplier supplier) { return descriptor("Statistics.setLongSupplier", descriptor, () -> null); }
	@Override public DoubleSupplier setDoubleSupplier(int id, DoubleSupplier supplier) { return null; }
	@Override public DoubleSupplier setDoubleSupplier(String name, DoubleSupplier supplier) { return null; }
	@Override public DoubleSupplier setDoubleSupplier(DriverStatisticDescriptor descriptor, DoubleSupplier supplier) { return descriptor("Statistics.setDoubleSupplier", descriptor, () -> null); }

	// The adapter still checks descriptor ownership before invoking the inert native operation.
	private <T> T descriptor(String operation, DriverStatisticDescriptor descriptor, Supplier<T> action) {
		try { types.requireDescriptor(descriptor); return action.get(); }
		catch (IllegalArgumentException | NullPointerException failure) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, operation + " failed", failure);
		}
	}
}
