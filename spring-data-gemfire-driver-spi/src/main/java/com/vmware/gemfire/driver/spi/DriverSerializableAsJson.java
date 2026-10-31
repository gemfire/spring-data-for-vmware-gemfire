// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** A value that can render itself as JSON text. The rendering is the provider's. */
public interface DriverSerializableAsJson {
	String toJson();
}
