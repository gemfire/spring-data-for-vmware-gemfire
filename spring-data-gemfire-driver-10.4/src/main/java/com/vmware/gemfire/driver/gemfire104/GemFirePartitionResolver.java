// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.EntryOperation;
import org.apache.geode.cache.PartitionResolver;

import com.vmware.gemfire.driver.spi.DriverEntryOperation;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperation;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverPartitionResolver;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.GemFireDriverException;

/** The native routing boundary; no native operation or region escapes into the application resolver. */
final class GemFirePartitionResolver<K, V> implements PartitionResolver<K, V> {
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	private final DriverPartitionResolver<K, V> delegate;

	GemFirePartitionResolver(DriverPartitionResolver<K, V> delegate) { this.delegate = delegate; }

	DriverPartitionResolver<K, V> delegate() { return delegate; }

	@Override public Object getRoutingObject(EntryOperation<K, V> operation) {
		try { return delegate.getRoutingObject(view(operation)); }
		catch (GemFireDriverException failure) { throw failure; }
		catch (IllegalArgumentException failure) { throw failure; }
		catch (RuntimeException failure) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Partition resolver failed", failure);
		}
	}

	@Override public String getName() { return delegate.getName(); }

	@Override public void init(java.util.Properties properties) {
		try { delegate.init(properties); }
		catch (RuntimeException failure) { throw exceptions.translate("PartitionResolver.init", failure); }
	}

	@Override public void initialize(org.apache.geode.cache.Cache cache, java.util.Properties properties) {
		try { delegate.initialize(new GemFireConnection(cache), properties); }
		catch (RuntimeException failure) { throw exceptions.translate("PartitionResolver.initialize", failure); }
	}

	@Override public void close() {
		try { delegate.close(); }
		catch (GemFireDriverException failure) { throw failure; }
		catch (RuntimeException failure) {
			throw new DriverOperationException(DriverFailureKind.RESOURCE_FAILURE, "Partition resolver cleanup failed", failure);
		}
	}

	static <K, V> DriverEntryOperation<K, V> view(EntryOperation<K, V> operation) {
		return new DriverEntryOperation<>() {
			@Override public DriverRegion<K, V> getRegion() { return operation.getRegion() == null ? null : new GemFireRegion<>(operation.getRegion()); }
			@Override public DriverOperation getOperation() { return operation.getOperation() == null ? null : DriverOperation.fromOrdinal(operation.getOperation().ordinal); }
			@Override public K getKey() { return operation.getKey(); }
			@Override public Object getCallbackArgument() { return operation.getCallbackArgument(); }
			@Override public boolean isCallbackArgumentAvailable() { return operation.isCallbackArgumentAvailable(); }
			@Override public V getNewValue() { return operation.getNewValue(); }
		};
	}
}
