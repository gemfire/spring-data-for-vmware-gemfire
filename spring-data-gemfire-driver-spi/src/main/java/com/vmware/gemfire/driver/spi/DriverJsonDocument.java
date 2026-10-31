// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** A JSON document exposed through the field-oriented document contract. Missing fields and present null fields are distinct.
 * Nested objects are {@link DriverJsonDocument}s and arrays are {@link java.util.List}s.
 */
public interface DriverJsonDocument extends DriverDocument, DriverSerializableAsJson {
}
