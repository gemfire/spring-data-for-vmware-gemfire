// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Application-supplied serializer for classes that do not implement {@link DriverPdxSerializable}.
 * Declining is expressed by returning {@code false} from {@link #toData} or {@code null} from {@link #fromData}.
 */
public interface DriverPdxSerializer {
	/** @return true when the object was written; false to decline so the provider falls back to its default handling. */
	boolean toData(Object object, DriverPdxWriter writer);
	/** @return the rebuilt object, or null to decline. */
	Object fromData(Class<?> type, DriverPdxReader reader);
}
