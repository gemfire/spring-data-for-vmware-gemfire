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

import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.GemFireDriverException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

final class MockConnection implements DriverConnection {

	private final MockMember member;
	DriverMember member() { return member; }
	void removeRegion(String name, MockRegion<?, ?> region) { regions.remove(region.getFullPath().substring(1), region); }
	private final Map<String, MockRegion<?, ?>> regions = new HashMap<>();
	private final Map<String, MockPool> pools = new HashMap<>();
	private final Map<java.util.List<String>, Integer> pdxEnumOrdinals = new HashMap<>();
	private final MockTransactionManager transactions = new MockTransactionManager();
	private volatile boolean closed;
	private final MockClientRuntime runtime;
	private final MockCancellation cancellation = new MockCancellation(() -> closed);

	MockConnection() { this(new java.util.Properties()); }

	MockConnection(java.util.Properties properties) {
		this(properties, true);
	}

	MockConnection(java.util.Properties properties, boolean statisticsEnabled) {
		this.member = new MockMember(properties);
		this.runtime = new MockClientRuntime(member, () -> closed, properties, statisticsEnabled);
	}

	@Override public com.vmware.gemfire.driver.spi.DriverClientRuntime getRuntime() { return runtime; }
	@Override public com.vmware.gemfire.driver.spi.DriverCancellation getCancellation() { return cancellation; }
	@Override public Set<PoolConfiguration.Endpoint> getCurrentServers() { return Set.of(); }


	@Override public com.vmware.gemfire.driver.spi.DriverTransactionManager getTransactionManager() {
		if (closed) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Cache is closed", null);
		return transactions;
	}

	@Override public com.vmware.gemfire.driver.spi.DriverPdxInstanceFactory createPdxInstanceFactory(String className) {
		if (closed) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Cache is closed", null);
		return new MockPdxInstanceFactory(className);
	}
	@Override public com.vmware.gemfire.driver.spi.DriverPdxInstance createPdxEnum(String className, String name, int ordinal) {
		if (closed) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Cache is closed", null);
		if (className == null || name == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "PDX enum class and name are required", null);
		Integer previous = pdxEnumOrdinals.putIfAbsent(java.util.List.of(className, name), ordinal);
		if (previous != null && previous != ordinal) throw new DriverOperationException(DriverFailureKind.RESOURCE_FAILURE, "PDX enum ordinal cannot change", null);
		Map<String, MockPdxField> fields = new java.util.LinkedHashMap<>();
		fields.put("name", new MockPdxField(String.class, name, false));
		fields.put("ordinal", new MockPdxField(int.class, ordinal, false));
		return new MockPdxInstance(className, fields, true, true);
	}

	@Override public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionConfiguration<K, V> configuration) {
		return createConfiguredClient(name, shortcut, configuration, null);
	}
	<K, V> DriverRegion<K, V> createConfiguredClient(String name, ClientRegionShortcut shortcut, RegionConfiguration<K, V> configuration, MockRegion<?, ?> parent) {
		checkOpen();
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
		if (name == null || name.isBlank()) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Pool name is required", null);
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut) {
		return createClientRegion(name, shortcut, new RegionCallbacks<>(java.util.List.of(), null, null));
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionCallbacks<K, V> callbacks) {
		checkOpen();
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
		if ((callbacks.loader() != null || callbacks.writer() != null)) throw UnsupportedFeatureException.notIntroduced("ClientRegion.creationLoaderWriter");
		MockRegion<K, V> region = new MockRegion<>(name, this, parent, config);
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

}
