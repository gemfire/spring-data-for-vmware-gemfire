/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolFactory;
import org.apache.geode.cache.client.PoolManager;

import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.RegionCallbacks;
import com.vmware.gemfire.driver.spi.RegionConfiguration;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

final class GemFireConnection implements DriverConnection {

	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	private final GemFireCache cache;
	private final GemFireClientRuntime runtime;
	private final GemFireCancellation cancellation;

	GemFireConnection(GemFireCache cache) {
		this.cache = cache;
		this.runtime = new GemFireClientRuntime(cache.getDistributedSystem());
		this.cancellation = new GemFireCancellation(cache.getCancelCriterion());
	}

	@Override public com.vmware.gemfire.driver.spi.DriverClientRuntime getRuntime() { return runtime; }
	@Override public com.vmware.gemfire.driver.spi.DriverCancellation getCancellation() { return cancellation; }
	@Override public Set<PoolConfiguration.Endpoint> getCurrentServers() {
		return invoke("getCurrentServers", () -> ((ClientCache) cache).getCurrentServers().stream()
			.map(address -> new PoolConfiguration.Endpoint(address.getHostString(), address.getPort()))
			.collect(java.util.stream.Collectors.toUnmodifiableSet()));
	}

	org.apache.geode.cache.GemFireCache cacheForTransactionCallback() { return cache; }
	@Override public com.vmware.gemfire.driver.spi.DriverTransactionManager getTransactionManager() {
		return invoke("getTransactionManager", () -> new GemFireTransactionManager(cache.getCacheTransactionManager()));
	}

	@Override public com.vmware.gemfire.driver.spi.DriverPdxInstanceFactory createPdxInstanceFactory(String className) {
		return invoke("createPdxInstanceFactory", () -> new GemFirePdxInstanceFactory(cache.createPdxInstanceFactory(className)));
	}
	@Override public com.vmware.gemfire.driver.spi.DriverPdxInstance createPdxEnum(String className, String name, int ordinal) {
		return invoke("createPdxEnum", () -> new GemFirePdxInstance(cache.createPdxEnum(className, name, ordinal)));
	}

	@Override public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionConfiguration<K, V> configuration) {
		return invoke("createConfiguredClientRegion", () -> {
			if (!((org.apache.geode.internal.cache.InternalCache) cache).isClient() || !(cache instanceof ClientCache client)) throw new IllegalStateException("Client region creation requires a client cache");
			var factory = client.<K, V>createClientRegionFactory(org.apache.geode.cache.client.ClientRegionShortcut.valueOf(shortcut.name()));
			new GemFireRegionConfiguration().apply(configuration, factory);
			return new GemFireRegion<>(factory.create(name));
		});
	}

	@Override
	public DriverPool createPool(String name, PoolConfiguration configuration) {
		return invoke("createPool", () -> {
			checkPoolAccess(name);
			Objects.requireNonNull(configuration, "configuration");
			PoolFactory factory = PoolManager.createFactory();
			new GemFirePoolConfiguration().apply(configuration, factory);
			return new GemFirePool(factory.create(name));
		});
	}

	@Override
	public DriverPool getPool(String name) {
		return invoke("getPool", () -> {
			checkPoolAccess(name);
			Pool pool = PoolManager.find(name);
			return pool == null ? null : new GemFirePool(pool);
		});
	}

	@Override
	public DriverPool getDefaultPool() {
		return invoke("getDefaultPool", () -> {
			checkPoolAccess();
			Pool pool = ((ClientCache) cache).getDefaultPool();
			return pool == null ? null : new GemFirePool(pool);
		});
	}

	private void checkPoolAccess() {
		if (cache.isClosed()) throw new IllegalStateException("Cache is closed");
		if (!((org.apache.geode.internal.cache.InternalCache) cache).isClient()) throw new IllegalStateException("Pool access requires a CLIENT cache");
	}

	private void checkPoolAccess(String name) {
		if (cache.isClosed()) throw new IllegalStateException("Cache is closed");
		if (!((org.apache.geode.internal.cache.InternalCache) cache).isClient()) throw new IllegalStateException("Pool access requires a CLIENT cache");
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Pool name is required");
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut) {
		return createClientRegion(name, shortcut, new RegionCallbacks<>(java.util.List.of(), null, null));
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionCallbacks<K, V> callbacks) {
		return invoke("createClientRegion", () -> {
			if (!((org.apache.geode.internal.cache.InternalCache) cache).isClient() || !(cache instanceof ClientCache client)) {
				throw new IllegalStateException("Client region creation requires a client cache.");
			}
			Objects.requireNonNull(callbacks);
			var factory = client.<K, V>createClientRegionFactory(org.apache.geode.cache.client.ClientRegionShortcut.valueOf(shortcut.name()));
			callbacks.listeners().forEach(listener -> factory.addCacheListener(new GemFireCacheListenerFactory().adapt(listener)));
			if (callbacks.loader() != null || callbacks.writer() != null) {
				throw com.vmware.gemfire.driver.spi.UnsupportedFeatureException.notIntroduced("ClientRegion.creationLoaderWriter");
			}
			return new GemFireRegion<>(factory.create(name));
		});
	}

	@Override
	public <K, V> DriverRegion<K, V> getRegion(String name) {
		return invoke("getRegion", () -> {
			Region<K, V> region = cache.getRegion(name);
			return region == null ? null : new GemFireRegion<>(region);
		});
	}

	@Override
	public Set<DriverRegion<?, ?>> rootRegions() {
		return invoke("rootRegions", () -> {
			Set<DriverRegion<?, ?>> regions = new HashSet<>();
			cache.rootRegions().forEach(region -> regions.add(new GemFireRegion<>(region)));
			return regions;
		});
	}

	@Override
	public boolean isClosed() {
		return invoke("isClosed", cache::isClosed);
	}

	@Override
	public void close() {
		invoke("close", () -> { cache.close(); return null; });
	}

	private <T> T invoke(String operation, Supplier<T> action) {
		try {
			return action.get();
		}
		catch (RuntimeException ex) {
			throw exceptions.translate(operation, ex);
		}
	}
}
