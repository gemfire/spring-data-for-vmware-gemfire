// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/**
 * Computes the routing object used to place a key in a server partition. A null result is
 * rejected by the engine. Lifecycle hooks are inherited from {@link DriverCacheCallback};
 * the engine does not call init or initialize on resolvers it builds from server metadata.
 */
public interface DriverPartitionResolver<K, V> extends DriverCacheCallback {
	Object getRoutingObject(DriverEntryOperation<K, V> operation);
	String getName();
	@Override default void close() { }
}
