/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;

import java.util.function.Supplier;

import org.apache.geode.Statistics;

import com.vmware.gemfire.driver.spi.DriverStatistics;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;

/** Native state stays behind a typed handle; all operation failures cross the same boundary. */
final class GemFireStatistics implements DriverStatistics {
	private final Statistics delegate;
	private final GemFireStatisticsTypes types;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	GemFireStatistics(Statistics delegate, GemFireStatisticsTypes types) {
		this.delegate = delegate;
		this.types = types;
	}
	@Override public void close() { invoke("Statistics.close", () -> { delegate.close(); return null; }); }
	@Override public int nameToId(String name) { return invoke("Statistics.nameToId", () -> delegate.nameToId(name)); }
	@Override public DriverStatisticDescriptor nameToDescriptor(String name) { return invoke("Statistics.nameToDescriptor", () -> types.wrap(delegate.nameToDescriptor(name))); }
	@Override public long getUniqueId() { return invoke("Statistics.getUniqueId", () -> delegate.getUniqueId()); }
	@Override public DriverStatisticsType getType() { return invoke("Statistics.getType", () -> types.wrap(delegate.getType())); }
	@Override public String getTextId() { return invoke("Statistics.getTextId", () -> delegate.getTextId()); }
	@Override public long getNumericId() { return invoke("Statistics.getNumericId", () -> delegate.getNumericId()); }
	@Override public boolean isAtomic() { return invoke("Statistics.isAtomic", () -> delegate.isAtomic()); }
	@Override public boolean isClosed() { return invoke("Statistics.isClosed", () -> delegate.isClosed()); }
	@Override public long getLong(int id) { return invoke("Statistics.getLong", () -> delegate.getLong(id)); }
	@Override public void setLong(int id, long value) { invoke("Statistics.setLong", () -> { delegate.setLong(id, value); return null; }); }
	@Override public void incLong(int id, long delta) { invoke("Statistics.incLong", () -> { delegate.incLong(id, delta); return null; }); }
	@Override public long getLong(String name) { return invoke("Statistics.getLong", () -> delegate.getLong(name)); }
	@Override public void setLong(String name, long value) { invoke("Statistics.setLong", () -> { delegate.setLong(name, value); return null; }); }
	@Override public void incLong(String name, long delta) { invoke("Statistics.incLong", () -> { delegate.incLong(name, delta); return null; }); }
	@Override public long getLong(DriverStatisticDescriptor descriptor) { return invoke("Statistics.getLong", () -> delegate.getLong(types.unwrap(descriptor))); }
	@Override public void setLong(DriverStatisticDescriptor descriptor, long value) { invoke("Statistics.setLong", () -> { delegate.setLong(types.unwrap(descriptor), value); return null; }); }
	@Override public void incLong(DriverStatisticDescriptor descriptor, long delta) { invoke("Statistics.incLong", () -> { delegate.incLong(types.unwrap(descriptor), delta); return null; }); }
	@Override public double getDouble(int id) { return invoke("Statistics.getDouble", () -> delegate.getDouble(id)); }
	@Override public void setDouble(int id, double value) { invoke("Statistics.setDouble", () -> { delegate.setDouble(id, value); return null; }); }
	@Override public void incDouble(int id, double delta) { invoke("Statistics.incDouble", () -> { delegate.incDouble(id, delta); return null; }); }
	@Override public double getDouble(String name) { return invoke("Statistics.getDouble", () -> delegate.getDouble(name)); }
	@Override public void setDouble(String name, double value) { invoke("Statistics.setDouble", () -> { delegate.setDouble(name, value); return null; }); }
	@Override public void incDouble(String name, double delta) { invoke("Statistics.incDouble", () -> { delegate.incDouble(name, delta); return null; }); }
	@Override public double getDouble(DriverStatisticDescriptor descriptor) { return invoke("Statistics.getDouble", () -> delegate.getDouble(types.unwrap(descriptor))); }
	@Override public void setDouble(DriverStatisticDescriptor descriptor, double value) { invoke("Statistics.setDouble", () -> { delegate.setDouble(types.unwrap(descriptor), value); return null; }); }
	@Override public void incDouble(DriverStatisticDescriptor descriptor, double delta) { invoke("Statistics.incDouble", () -> { delegate.incDouble(types.unwrap(descriptor), delta); return null; }); }
	@Override public Number get(String name) { return invoke("Statistics.get", () -> delegate.get(name)); }
	@Override public long getRawBits(String name) { return invoke("Statistics.getRawBits", () -> delegate.getRawBits(name)); }
	@Override public Number get(DriverStatisticDescriptor descriptor) { return invoke("Statistics.get", () -> delegate.get(types.unwrap(descriptor))); }
	@Override public long getRawBits(DriverStatisticDescriptor descriptor) { return invoke("Statistics.getRawBits", () -> delegate.getRawBits(types.unwrap(descriptor))); }

	@Override public boolean equals(Object other) { return other instanceof GemFireStatistics that && delegate.equals(that.delegate); }
	@Override public int hashCode() { return delegate.hashCode(); }

	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException failure) { throw exceptions.translate(operation, failure); }
	}

	@Override public IntSupplier setIntSupplier(int id, IntSupplier supplier) { return invoke("Statistics.setIntSupplier", () -> delegate.setIntSupplier(id, supplier)); }
	@Override public IntSupplier setIntSupplier(String name, IntSupplier supplier) { return invoke("Statistics.setIntSupplier", () -> delegate.setIntSupplier(name, supplier)); }
	@Override public IntSupplier setIntSupplier(DriverStatisticDescriptor descriptor, IntSupplier supplier) { return invoke("Statistics.setIntSupplier", () -> delegate.setIntSupplier(types.unwrap(descriptor), supplier)); }
	@Override public LongSupplier setLongSupplier(int id, LongSupplier supplier) { return invoke("Statistics.setLongSupplier", () -> delegate.setLongSupplier(id, supplier)); }
	@Override public LongSupplier setLongSupplier(String name, LongSupplier supplier) { return invoke("Statistics.setLongSupplier", () -> delegate.setLongSupplier(name, supplier)); }
	@Override public LongSupplier setLongSupplier(DriverStatisticDescriptor descriptor, LongSupplier supplier) { return invoke("Statistics.setLongSupplier", () -> delegate.setLongSupplier(types.unwrap(descriptor), supplier)); }
	@Override public DoubleSupplier setDoubleSupplier(int id, DoubleSupplier supplier) { return invoke("Statistics.setDoubleSupplier", () -> delegate.setDoubleSupplier(id, supplier)); }
	@Override public DoubleSupplier setDoubleSupplier(String name, DoubleSupplier supplier) { return invoke("Statistics.setDoubleSupplier", () -> delegate.setDoubleSupplier(name, supplier)); }
	@Override public DoubleSupplier setDoubleSupplier(DriverStatisticDescriptor descriptor, DoubleSupplier supplier) { return invoke("Statistics.setDoubleSupplier", () -> delegate.setDoubleSupplier(types.unwrap(descriptor), supplier)); }
}
