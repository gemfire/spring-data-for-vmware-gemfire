// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** Application type that writes and reads its own binary form using JDK streams only. */
public interface DriverDataSerializable {
	void toData(DataOutput out) throws IOException;
	void fromData(DataInput in) throws IOException, ClassNotFoundException;
}
