/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatistics;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/** In-memory values, preserving native validation order and long/double ID spaces. */
final class MockStatistics implements DriverStatistics {
	private final DriverStatisticsType type;
	private final MockStatisticsTypes types;
	private final String textId;
	private final long numericId;
	private final long uniqueId;
	private final boolean atomic;
	private final Consumer<DriverStatistics> onClose;
	private final int longCount;
	private final long[] longs;
	private final double[] doubles;
	private final DoubleAdder[] atomicDoubles;
	private final Map<Integer, IntSupplier> intSuppliers = new HashMap<>();
	private final Map<Integer, LongSupplier> longSuppliers = new HashMap<>();
	private final Map<Integer, DoubleSupplier> doubleSuppliers = new HashMap<>();
	private volatile boolean closed;

	MockStatistics(DriverStatisticsType type, MockStatisticsTypes types, String textId, long numericId,
			long uniqueId, boolean atomic, Consumer<DriverStatistics> onClose) {
		this.type = type;
		this.types = types;
		this.textId = textId;
		this.numericId = numericId;
		this.uniqueId = uniqueId;
		this.atomic = atomic;
		this.onClose = onClose;
		longCount = (int) type.getStatistics().stream().filter(d -> d.getType() == ValueType.LONG).count();
		int doubleCount = type.getStatistics().size() - longCount;
		// Native ordinary storage is null for an absent value kind; atomic arrays are empty.
		longs = !atomic && longCount == 0 ? null : new long[longCount];
		doubles = doubleCount == 0 ? null : new double[doubleCount];
		atomicDoubles = atomic ? new DoubleAdder[doubleCount] : null;
		if (atomic) for (int i = 0; i < atomicDoubles.length; i++) atomicDoubles[i] = new DoubleAdder();
	}
	@Override public synchronized void close() { onClose.accept(this); closed = true; }
	@Override public int nameToId(String name) { return type.nameToId(name); }
	@Override public DriverStatisticDescriptor nameToDescriptor(String name) { return type.nameToDescriptor(name); }
	@Override public long getUniqueId() { return uniqueId; }
	@Override public DriverStatisticsType getType() { return type; }
	@Override public String getTextId() { return textId; }
	@Override public long getNumericId() { return numericId; }
	@Override public boolean isAtomic() { return atomic; }
	@Override public boolean isClosed() { return closed; }

	@Override public synchronized long getLong(int id) {
		return invoke("Statistics.getLong", () -> {
			if (closed) return 0L;
			// Native long validation checks only the upper bound; negative IDs fail storage access.
			if (id >= longCount) throw new IllegalArgumentException("Not a long statistic");
			return longs[id];
		});
	}
	@Override public synchronized double getDouble(int id) {
		return invoke("Statistics.getDouble", () -> closed ? 0.0 : readDouble(id));
	}
	@Override public synchronized void setLong(int id, long value) {
		invoke("Statistics.setLong", () -> { if (!closed) longs[id] = value; return null; });
	}
	@Override public synchronized void incLong(int id, long delta) {
		invoke("Statistics.incLong", () -> { if (!closed) longs[id] += delta; return null; });
	}
	@Override public synchronized void setDouble(int id, double value) {
		invoke("Statistics.setDouble", () -> {
			if (!closed) {
				writeDouble(id, value);
			}
			return null;
		});
	}
	@Override public synchronized void incDouble(int id, double delta) {
		invoke("Statistics.incDouble", () -> {
			if (!closed) {
				int index = id - longCount;
				if (atomic) atomicDoubles[index].add(delta);
				else doubles[index] += delta;
			}
			return null;
		});
	}
	@Override public long getLong(String name) { return getLong(nameToDescriptor(name)); }
	@Override public long getLong(DriverStatisticDescriptor descriptor) { return invoke("Statistics.getLong", () -> getLong(typedId(descriptor, ValueType.LONG))); }
	@Override public void setLong(String name, long value) { setLong(nameToDescriptor(name), value); }
	@Override public void setLong(DriverStatisticDescriptor descriptor, long value) { invoke("Statistics.setLong", () -> { setLong(typedId(descriptor, ValueType.LONG), value); return null; }); }
	@Override public void incLong(String name, long delta) { incLong(nameToDescriptor(name), delta); }
	@Override public void incLong(DriverStatisticDescriptor descriptor, long delta) { invoke("Statistics.incLong", () -> { incLong(typedId(descriptor, ValueType.LONG), delta); return null; }); }
	@Override public double getDouble(String name) { return getDouble(nameToDescriptor(name)); }
	@Override public double getDouble(DriverStatisticDescriptor descriptor) { return invoke("Statistics.getDouble", () -> getDouble(typedId(descriptor, ValueType.DOUBLE))); }
	@Override public void setDouble(String name, double value) { setDouble(nameToDescriptor(name), value); }
	@Override public void setDouble(DriverStatisticDescriptor descriptor, double value) { invoke("Statistics.setDouble", () -> { setDouble(typedId(descriptor, ValueType.DOUBLE), value); return null; }); }
	@Override public void incDouble(String name, double delta) { incDouble(nameToDescriptor(name), delta); }
	@Override public void incDouble(DriverStatisticDescriptor descriptor, double delta) { invoke("Statistics.incDouble", () -> { incDouble(typedId(descriptor, ValueType.DOUBLE), delta); return null; }); }

	@Override public Number get(String name) { return get(nameToDescriptor(name)); }
	@Override public synchronized Number get(DriverStatisticDescriptor descriptor) {
		return invoke("Statistics.get", () -> {
			types.requireDescriptor(descriptor);
			if (closed) return Integer.valueOf(0);
			// Separate branches avoid Java's conditional numeric promotion from Long to Double.
			if (descriptor.getType() == ValueType.LONG) return Long.valueOf(longs[descriptor.getId()]);
			return Double.valueOf(readDouble(descriptor.getId()));
		});
	}
	@Override public long getRawBits(String name) { return getRawBits(nameToDescriptor(name)); }
	@Override public synchronized long getRawBits(DriverStatisticDescriptor descriptor) {
		return invoke("Statistics.getRawBits", () -> {
			types.requireDescriptor(descriptor);
			if (closed) return 0L;
			return descriptor.getType() == ValueType.LONG ? longs[descriptor.getId()]
				: Double.doubleToRawLongBits(readDouble(descriptor.getId()));
		});
	}
	private synchronized void writeLong(int id, long value) { longs[id] = value; }
	private synchronized void writeDouble(int id, double value) {
		int index = id - longCount;
		if (atomic) { atomicDoubles[index].reset(); atomicDoubles[index].add(value); }
		else doubles[index] = value;
	}

	/** Snapshot each supplier kind just before visiting it, as the native sampler does. */
	int updateSuppliedValues() {
		int errors = sample(intSuppliers, (id, supplier) -> writeLong(id, supplier.getAsInt()));
		errors += sample(longSuppliers, (id, supplier) -> writeLong(id, supplier.getAsLong()));
		return errors + sample(doubleSuppliers, (id, supplier) -> writeDouble(id, supplier.getAsDouble()));
	}
	private <T> int sample(Map<Integer, T> suppliers, java.util.function.BiConsumer<Integer, T> update) {
		Map<Integer, T> snapshot;
		synchronized (this) { snapshot = new HashMap<>(suppliers); }
		int errors = 0;
		for (var entry : snapshot.entrySet()) {
			try { update.accept(entry.getKey(), entry.getValue()); }
			catch (Throwable failure) { errors++; }
		}
		return errors;
	}
	private double readDouble(int id) {
		int index = id - longCount;
		return atomic ? atomicDoubles[index].sum() : doubles[index];
	}
	private int typedId(DriverStatisticDescriptor descriptor, ValueType expected) {
		types.requireDescriptor(descriptor);
		if (descriptor.getType() != expected) throw new IllegalArgumentException("Wrong statistic value type");
		return descriptor.getId();
	}
	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (DriverOperationException failure) { throw failure; }
		catch (RuntimeException failure) {
			var kind = failure instanceof IllegalArgumentException || failure instanceof NullPointerException
				? DriverFailureKind.INVALID_ARGUMENT : DriverFailureKind.UNKNOWN;
			throw new DriverOperationException(kind, operation + " failed", failure);
		}
	}

	@Override public IntSupplier setIntSupplier(int id, IntSupplier supplier) {
		return invoke("Statistics.setIntSupplier", () -> {
			if (id >= longCount) throw new IllegalArgumentException("Wrong statistic value type");
			long initial;
			try { initial = supplier.getAsInt(); }
			catch (Exception failure) { initial = 0; }
			writeLong(id, initial);
			synchronized (this) { return intSuppliers.put(id, supplier); }
		});
	}
	@Override public IntSupplier setIntSupplier(String name, IntSupplier supplier) { return invoke("Statistics.setIntSupplier", () -> setIntSupplier(nameToId(name), supplier)); }
	@Override public IntSupplier setIntSupplier(DriverStatisticDescriptor descriptor, IntSupplier supplier) { return invoke("Statistics.setIntSupplier", () -> setIntSupplier(typedId(descriptor, ValueType.LONG), supplier)); }
	@Override public LongSupplier setLongSupplier(int id, LongSupplier supplier) {
		return invoke("Statistics.setLongSupplier", () -> {
			if (id >= longCount) throw new IllegalArgumentException("Wrong statistic value type");
			long initial;
			try { initial = supplier.getAsLong(); }
			catch (Exception failure) { initial = 0; }
			writeLong(id, initial);
			synchronized (this) { return longSuppliers.put(id, supplier); }
		});
	}
	@Override public LongSupplier setLongSupplier(String name, LongSupplier supplier) { return invoke("Statistics.setLongSupplier", () -> setLongSupplier(nameToId(name), supplier)); }
	@Override public LongSupplier setLongSupplier(DriverStatisticDescriptor descriptor, LongSupplier supplier) { return invoke("Statistics.setLongSupplier", () -> setLongSupplier(typedId(descriptor, ValueType.LONG), supplier)); }
	@Override public DoubleSupplier setDoubleSupplier(int id, DoubleSupplier supplier) {
		return invoke("Statistics.setDoubleSupplier", () -> {
			if (id < longCount || id >= longCount + (atomic ? atomicDoubles.length : doubles == null ? 0 : doubles.length)) throw new IllegalArgumentException("Wrong statistic value type");
			double initial;
			try { initial = supplier.getAsDouble(); }
			catch (Exception failure) { initial = 0; }
			writeDouble(id, initial);
			synchronized (this) { return doubleSuppliers.put(id, supplier); }
		});
	}
	@Override public DoubleSupplier setDoubleSupplier(String name, DoubleSupplier supplier) { return invoke("Statistics.setDoubleSupplier", () -> setDoubleSupplier(nameToId(name), supplier)); }
	@Override public DoubleSupplier setDoubleSupplier(DriverStatisticDescriptor descriptor, DoubleSupplier supplier) { return invoke("Statistics.setDoubleSupplier", () -> setDoubleSupplier(typedId(descriptor, ValueType.DOUBLE), supplier)); }
}
