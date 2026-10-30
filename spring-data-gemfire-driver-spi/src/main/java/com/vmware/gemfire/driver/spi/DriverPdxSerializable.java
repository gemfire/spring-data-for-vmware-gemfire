// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Application class that writes and reads its own PDX form. Implementations need a public no-argument constructor. */
public interface DriverPdxSerializable {
	void toData(DriverPdxWriter writer);
	void fromData(DriverPdxReader reader);
}
