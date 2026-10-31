// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Converts between JSON text and PDX instances for one region service; an authenticated service gets a formatter bound to that service.
 * Parsed objects are PDX instances of class {@link #JSON_CLASSNAME} whose nested objects are PDX instances and whose arrays are lists.
 * Field encoding, numeric narrowing and charset handling belong to the provider. Failures are {@link DriverJsonParseException}s.
 * {@link #fromPdxInstance} decodes the UTF-8 bytes of {@link #toJsonByteArrayFromPdxInstance} with the provider's own charset rules.
 */
public interface DriverJsonFormatter {
	String JSON_CLASSNAME = "__GEMFIRE_JSON";
	String SORT_JSON_FIELD_NAMES_PROPERTY = "gemfire.pdx.mapper.sort-json-field-names";

	DriverPdxInstance fromJSON(String json);
	DriverPdxInstance fromJSON(String json, String... identityFields);
	DriverPdxInstance fromJSON(byte[] json);
	DriverPdxInstance fromJSON(byte[] json, String... identityFields);
	/** The json is a {@link String} or a {@code byte[]}. */
	DriverPdxInstance toPdxInstance(Object json, String... identityFields);
	String fromPdxInstance(DriverPdxInstance instance);
	String toJSON(DriverPdxInstance instance);
	byte[] toJSONByteArray(DriverPdxInstance instance);
	byte[] toJsonByteArrayFromPdxInstance(DriverPdxInstance instance);
	/** The service this formatter is bound to, or null for a formatter that is not bound to one. */
	DriverRegionService getRegionService();
}
