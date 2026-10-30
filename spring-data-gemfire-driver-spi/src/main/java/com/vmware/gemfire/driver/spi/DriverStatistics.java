/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;

/**
 * A local statistics handle, active or inert according to runtime configuration. IDs are local to its type;
 * descriptor overloads use the descriptor's current ID, not a lookup by its name.
 * Use fresh descriptors per type. Atomic instances support concurrent increments.
 *
 * <p>With statistics enabled, after close values read as zero and updates are ignored; name lookup and
 * descriptor type validation still occur. Metadata remains readable. Generic
 * {@code get} returns Long/Double while open and Integer zero when closed.
 * Raw bits preserve the stored double representation; atomic accumulation may
 * normalize floating-point values. Deprecated native int operations use long
 * storage: write/increment with long methods and narrow reads with an int cast.
 *
 * <p>With statistics enabled, supplier registration evaluates immediately and returns the previous supplier
 * by identity. Int and long suppliers have separate registrations sharing long
 * storage. Registration catches supplier Exceptions and writes zero; Errors escape.
 * Null is a failing registered supplier, not removal. Later samples catch supplier
 * Throwables and retain the last value; reads alone do not evaluate suppliers.
 * Sampling visits int, long, then double suppliers. Close removes the instance
 * from future registry samples, without waiting for callbacks already in flight.
 * Registration still evaluates after close, although reads remain zero.
 * Native scheduling follows runtime configuration; mocks may offer explicit sampling.
 *
 * <p>With statistics enabled, connection shutdown stops native scheduled sampling
 * but does not close caller-created instances. Captured handles retain values and
 * permit updates until explicitly closed. Mocks have no background sampler and may
 * provide explicit sampling even after shutdown. No identity across separate
 * runtimes is promised.
 *
 * <p>When statistics are disabled, handles are unregistered and always read zero
 * (Integer zero for generic reads). Updates and supplier registration do nothing;
 * suppliers are never evaluated or retained, and registration returns null.
 * Text/numeric IDs are preserved exactly, unique ID is zero, and isAtomic is true.
 * Close does nothing and isClosed stays false, including after runtime shutdown.
 * Value operations ignore invalid names/IDs and descriptor value kinds, but driver
 * descriptor provenance and explicit nameToId/nameToDescriptor still validate.
 * Disabling sampling alone does not disable statistics.
 */
public interface DriverStatistics extends AutoCloseable {
	@Override void close();
	int nameToId(String name);
	DriverStatisticDescriptor nameToDescriptor(String name);
	long getUniqueId();
	DriverStatisticsType getType();
	String getTextId();
	long getNumericId();
	boolean isAtomic();
	boolean isClosed();
	long getLong(int id);
	void setLong(int id, long value);
	void incLong(int id, long delta);
	long getLong(String name);
	void setLong(String name, long value);
	void incLong(String name, long delta);
	long getLong(DriverStatisticDescriptor descriptor);
	void setLong(DriverStatisticDescriptor descriptor, long value);
	void incLong(DriverStatisticDescriptor descriptor, long delta);
	double getDouble(int id);
	void setDouble(int id, double value);
	void incDouble(int id, double delta);
	double getDouble(String name);
	void setDouble(String name, double value);
	void incDouble(String name, double delta);
	double getDouble(DriverStatisticDescriptor descriptor);
	void setDouble(DriverStatisticDescriptor descriptor, double value);
	void incDouble(DriverStatisticDescriptor descriptor, double delta);
	Number get(String name);
	long getRawBits(String name);
	Number get(DriverStatisticDescriptor descriptor);
	long getRawBits(DriverStatisticDescriptor descriptor);

	default IntSupplier setIntSupplier(int id, IntSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setIntSupplier"); }
	default IntSupplier setIntSupplier(String name, IntSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setIntSupplier"); }
	default IntSupplier setIntSupplier(DriverStatisticDescriptor descriptor, IntSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setIntSupplier"); }
	default LongSupplier setLongSupplier(int id, LongSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setLongSupplier"); }
	default LongSupplier setLongSupplier(String name, LongSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setLongSupplier"); }
	default LongSupplier setLongSupplier(DriverStatisticDescriptor descriptor, LongSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setLongSupplier"); }
	default DoubleSupplier setDoubleSupplier(int id, DoubleSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setDoubleSupplier"); }
	default DoubleSupplier setDoubleSupplier(String name, DoubleSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setDoubleSupplier"); }
	default DoubleSupplier setDoubleSupplier(DriverStatisticDescriptor descriptor, DoubleSupplier supplier) { throw UnsupportedFeatureException.notIntroduced("Statistics.setDoubleSupplier"); }
}
