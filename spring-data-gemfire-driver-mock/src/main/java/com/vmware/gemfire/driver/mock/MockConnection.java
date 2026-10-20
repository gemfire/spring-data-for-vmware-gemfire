/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.vmware.gemfire.driver.spi.CacheConfiguration.Mode;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;

final class MockConnection implements DriverConnection {

	private final Mode mode;
	private final Map<String, MockRegion<?, ?>> regions = new HashMap<>();
	private volatile boolean closed;

	MockConnection(Mode mode) { this.mode = mode; }

	@Override
	public <K, V> DriverRegion<K, V> createRegion(String name, RegionShortcut shortcut) {
		checkMode(Mode.PEER);
		if (shortcut == null) {
			throw new DriverOperationException("Region shortcut must not be null.", null);
		}
		if (shortcut != RegionShortcut.LOCAL) {
			throw UnsupportedFeatureException.notIntroduced("RegionShortcut." + shortcut);
		}
		return create(name);
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut) {
		checkMode(Mode.CLIENT);
		if (shortcut == null) {
			throw new DriverOperationException("Client region shortcut must not be null.", null);
		}
		if (shortcut != ClientRegionShortcut.LOCAL) {
			throw UnsupportedFeatureException.notIntroduced("ClientRegionShortcut." + shortcut);
		}
		return create(name);
	}

	private <K, V> DriverRegion<K, V> create(String name) {
		if (name == null || name.isEmpty() || name.contains("/") || regions.containsKey(name)) {
			throw new DriverOperationException("Invalid or duplicate mock region name.", null);
		}
		MockRegion<K, V> region = new MockRegion<>(name, this);
		regions.put(name, region);
		return region;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> DriverRegion<K, V> getRegion(String name) {
		checkOpen();
		if (name == null || name.isEmpty()) {
			throw new DriverOperationException("Region name must not be null or empty.", null);
		}
		return (DriverRegion<K, V>) regions.get(name.startsWith("/") ? name.substring(1) : name);
	}

	@Override
	public Set<DriverRegion<?, ?>> rootRegions() {
		checkOpen();
		return Set.copyOf(regions.values());
	}

	@Override
	public boolean isClosed() { return closed; }

	@Override
	public void close() {
		closed = true;
		DriverOperationException failure = null;
		for (MockRegion<?, ?> region : regions.values()) {
			try {
				region.releaseEntries();
			}
			catch (DriverOperationException ex) {
				if (failure == null) { failure = ex; }
				else { failure.addSuppressed(ex); }
			}
		}
		regions.clear();
		if (failure != null) { throw failure; }
	}

	void checkOpen() {
		if (closed) {
			throw new DriverOperationException("Mock cache is closed.", null);
		}
	}

	private void checkMode(Mode expected) {
		checkOpen();
		if (mode != expected) {
			throw new DriverOperationException("Region creation requires a " + expected + " cache.", null);
		}
	}
}
