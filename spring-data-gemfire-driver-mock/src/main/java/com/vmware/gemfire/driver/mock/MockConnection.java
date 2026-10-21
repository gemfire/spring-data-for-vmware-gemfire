/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.vmware.gemfire.driver.spi.RegionCallbacks;
import com.vmware.gemfire.driver.spi.RegionConfiguration;
import com.vmware.gemfire.driver.spi.DriverMember;
import java.util.Set;

import com.vmware.gemfire.driver.spi.CacheConfiguration.Mode;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.GemFireDriverException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;

final class MockConnection implements DriverConnection {

	private final Mode mode;
	private final MockMember member = new MockMember();
	DriverMember member() { return member; }
	void removeRegion(String name, MockRegion<?, ?> region) { regions.remove(region.getFullPath().substring(1), region); }
	private final Map<String, MockRegion<?, ?>> regions = new HashMap<>();
	private final Map<String, MockPool> pools = new HashMap<>();
	private volatile boolean closed;

	MockConnection(Mode mode) { this.mode = mode; }

	@Override public <K, V> DriverRegion<K, V> createRegion(String name, RegionShortcut shortcut, RegionConfiguration<K, V> configuration) {
		return createConfigured(name, shortcut, configuration, null);
	}
	<K, V> DriverRegion<K, V> createConfigured(String name, RegionShortcut shortcut, RegionConfiguration<K, V> configuration, MockRegion<?, ?> parent) {
		checkMode(Mode.PEER);
		if (shortcut == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null shortcut", null);
		if (shortcut != RegionShortcut.LOCAL) throw UnsupportedFeatureException.notIntroduced("RegionShortcut." + shortcut);
		return create(name, configuration, parent);
	}
	@Override public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionConfiguration<K, V> configuration) {
		return createConfiguredClient(name, shortcut, configuration, null);
	}
	<K, V> DriverRegion<K, V> createConfiguredClient(String name, ClientRegionShortcut shortcut, RegionConfiguration<K, V> configuration, MockRegion<?, ?> parent) {
		checkMode(Mode.CLIENT);
		if (shortcut == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null shortcut", null);
		if (shortcut != ClientRegionShortcut.LOCAL) throw UnsupportedFeatureException.notIntroduced("ClientRegionShortcut." + shortcut);
		return create(name, configuration, parent);
	}
	Set<DriverRegion<?, ?>> subregions(MockRegion<?, ?> parent, boolean recursive) {
		Set<DriverRegion<?, ?>> result = new java.util.HashSet<>();
		for (var region : regions.values()) {
			if (region.getParentRegion() == parent || recursive && region.getFullPath().startsWith(parent.getFullPath() + "/")) result.add(region);
		}
		return result;
	}

	@Override
	public DriverPool createPool(String name, PoolConfiguration configuration) {
		checkPoolAccess(name);
		if (configuration == null || pools.containsKey(name)) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null pool configuration or duplicate pool name", null);
		}
		MockPool pool = new MockPool(name, configuration, this);
		pools.put(name, pool);
		return pool;
	}

	@Override
	public DriverPool getPool(String name) {
		checkPoolAccess(name);
		return pools.get(name);
	}

	void removePool(String name, MockPool pool) { pools.remove(name, pool); }

	private void checkPoolAccess(String name) {
		if (closed) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Cache is closed", null);
		if (mode != Mode.CLIENT) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Pool access requires a CLIENT cache", null);
		if (name == null || name.isBlank()) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Pool name is required", null);
	}

	@Override
	public <K, V> DriverRegion<K, V> createRegion(String name, RegionShortcut shortcut) {
		return createRegion(name, shortcut, new RegionCallbacks<>(java.util.List.of(), null, null));
	}

	@Override
	public <K, V> DriverRegion<K, V> createRegion(String name, RegionShortcut shortcut, RegionCallbacks<K, V> callbacks) {
		checkMode(Mode.PEER);
		if (shortcut == null) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Region shortcut must not be null.", null);
		}
		if (shortcut != RegionShortcut.LOCAL) {
			throw UnsupportedFeatureException.notIntroduced("RegionShortcut." + shortcut);
		}
		return create(name, callbacks);
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut) {
		return createClientRegion(name, shortcut, new RegionCallbacks<>(java.util.List.of(), null, null));
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionCallbacks<K, V> callbacks) {
		checkMode(Mode.CLIENT);
		if (callbacks != null && (callbacks.loader() != null || callbacks.writer() != null)) {
			throw UnsupportedFeatureException.notIntroduced("ClientRegion.creationLoaderWriter");
		}
		if (shortcut == null) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Client region shortcut must not be null.", null);
		}
		if (shortcut != ClientRegionShortcut.LOCAL) {
			throw UnsupportedFeatureException.notIntroduced("ClientRegionShortcut." + shortcut);
		}
		return create(name, callbacks);
	}

	private <K, V> DriverRegion<K, V> create(String name, RegionCallbacks<K, V> callbacks) {
		if (callbacks == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null callbacks", null);
		return create(name, RegionConfiguration.<K, V>builder().callbacks(callbacks).build(), null);
	}
	private <K, V> DriverRegion<K, V> create(String name, RegionConfiguration<K, V> config, MockRegion<?, ?> parent) {
		if (name == null || name.isEmpty() || name.contains("/")) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Invalid mock region name", null);
		if (config == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null region configuration", null);
		if (parent != null) parent.checkOpen();
		String path = parent == null ? name : parent.getFullPath().substring(1) + "/" + name;
		if (regions.containsKey(path)) throw new com.vmware.gemfire.driver.spi.DriverRegionExistsException(regions.get(path), null);
		var callbacks = config.callbacks().isSet() ? config.callbacks().value() : new RegionCallbacks<K, V>(List.of(), null, null);
		if (mode == Mode.CLIENT && (callbacks.loader() != null || callbacks.writer() != null)) throw UnsupportedFeatureException.notIntroduced("ClientRegion.creationLoaderWriter");
		MockRegion<K, V> region = new MockRegion<>(name, this, parent, config, mode == Mode.CLIENT);
		regions.put(path, region);
		try { region.created(callbacks); }
		catch (RuntimeException failure) { regions.remove(path, region); throw failure; }
		return region;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> DriverRegion<K, V> getRegion(String name) {
		checkOpen();
		if (name == null || name.isEmpty()) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Region name must not be null or empty.", null);
		}
		return (DriverRegion<K, V>) regions.get(name.startsWith("/") ? name.substring(1) : name);
	}

	@Override
	public Set<DriverRegion<?, ?>> rootRegions() {
		checkOpen();
		return regions.values().stream().filter(region -> region.getParentRegion() == null)
			.collect(java.util.stream.Collectors.toUnmodifiableSet());
	}

	@Override
	public boolean isClosed() { return closed; }

	@Override
	public void close() {
		closed = true;
		GemFireDriverException failure = null;
		for (MockRegion<?, ?> region : List.copyOf(regions.values())) {
			try {
				region.releaseEntries();
			}
			catch (GemFireDriverException ex) {
				if (failure == null) { failure = ex; }
				else if (failure != ex) { failure.addSuppressed(ex); }
			}
		}
		regions.clear();
		for (MockPool pool : Set.copyOf(pools.values())) pool.destroy();
		if (failure != null) { throw failure; }
	}

	void checkOpen() {
		if (closed) {
			throw new DriverOperationException(DriverFailureKind.CANCELLED, "Mock cache is closed.", null);
		}
	}

	private void checkMode(Mode expected) {
		checkOpen();
		if (mode != expected) {
			throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Region creation requires a " + expected + " cache.", null);
		}
	}
}
