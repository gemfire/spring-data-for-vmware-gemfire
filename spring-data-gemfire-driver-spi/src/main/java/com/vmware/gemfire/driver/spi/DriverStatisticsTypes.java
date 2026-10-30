/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.io.Reader;
import java.util.List;

/**
 * Injected local statistics schema factory. Native drivers retain their native type registry's
 * lifetime/visibility; the mock owns a registry per connection. Neither creates sampling services.
 */
public interface DriverStatisticsTypes {
	/** Null descriptions/units become empty strings; IDs start at -1. */
	default DriverStatisticDescriptor createDescriptor(StatisticDescriptorConfiguration configuration) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsTypes.createDescriptor");
	}

	/**
	 * Equal definitions reuse the registered type; conflicting names and duplicate descriptors fail
	 * with INVALID_ARGUMENT. Descriptors must come from the same driver implementation. Declaration
	 * order is retained; LONG IDs start at zero and DOUBLE IDs follow them. Assignment mutates the
	 * descriptor handles and can occur before a conflict fails. The input list is copied. Native
	 * descriptor limits apply (254 for 10.4 and the mock). Types remain readable after cache closure.
	 */
	default DriverStatisticsType createType(String name, String description, List<DriverStatisticDescriptor> descriptors) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsTypes.createType");
	}

	/** Returns null if this registry has no matching type. */
	default DriverStatisticsType findType(String name) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsTypes.findType");
	}

	/**
	 * Reads statistics/type/stat XML into this registry and returns an immutable list in document
	 * order. Omitted storage and int storage use LONG; counters default to true and largerBetter
	 * defaults to the counter flag. Equal definitions reuse types. Registration is not transactional:
	 * a later definition failure leaves earlier types registered. XML syntax/read failures in 10.4
	 * are SYSTEM_FAILURE; conflicting definitions are INVALID_ARGUMENT. Native assertion errors
	 * for invalid schema content propagate. The mock uses AssertionError for these schema checks.
	 * The parser may close the reader; callers must ensure closure on all paths. Supply trusted XML.
	 */
	default List<DriverStatisticsType> createTypesFromXml(Reader reader) {
		throw UnsupportedFeatureException.notIntroduced("StatisticsTypes.createTypesFromXml");
	}
}
