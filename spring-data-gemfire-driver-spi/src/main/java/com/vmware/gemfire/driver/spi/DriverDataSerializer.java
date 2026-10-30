// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** Application-supplied binary serializer for one or more classes, identified by a user-chosen id.
 * Declining is expressed by returning {@code false} from {@link #toData}.
 */
public interface DriverDataSerializer {
	/** @return the id under which this serializer is registered; it must be stable across every member that reads its data. */
	int id();
	/** @return the classes this serializer handles. */
	Class<?>[] supportedClasses();
	/** @return true when the object was written; false to decline. */
	boolean toData(Object object, DataOutput out) throws IOException;
	/** @return the rebuilt object. */
	Object fromData(DataInput in) throws IOException, ClassNotFoundException;
}
