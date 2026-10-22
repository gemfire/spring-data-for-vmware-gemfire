/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** The region service associated with a handle, including an authenticated client view.
 * Borrowed from its region; closing it has the underlying service's ownership semantics.
 * Query and serialization services are supplied by their respective SPI contracts.
 */
public interface DriverRegionService extends AutoCloseable {
	default DriverPdxInstanceFactory createPdxInstanceFactory(String className) {
		throw UnsupportedFeatureException.notIntroduced("RegionService.createPdxInstanceFactory");
	}
	default DriverPdxInstance createPdxEnum(String className, String name, int ordinal) {
		throw UnsupportedFeatureException.notIntroduced("RegionService.createPdxEnum");
	}
	<K, V> DriverRegion<K, V> getRegion(String path);
	java.util.Set<DriverRegion<?, ?>> rootRegions();
	boolean isClosed();
	@Override void close();
}
