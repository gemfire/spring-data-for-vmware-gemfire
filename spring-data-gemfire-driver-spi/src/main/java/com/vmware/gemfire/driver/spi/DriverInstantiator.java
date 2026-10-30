// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Creates empty instances of a {@link DriverDataSerializable} class before {@code fromData} fills them.
 * Providers register it through a serializer-backed adapter; see {@link DriverSerializationRegistry#registerInstantiator}.
 */
public interface DriverInstantiator {
	int id();
	Class<? extends DriverDataSerializable> instantiatedClass();
	DriverDataSerializable newInstance();
}
