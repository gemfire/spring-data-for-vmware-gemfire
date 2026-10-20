/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import com.vmware.gemfire.driver.spi.CacheConfiguration.Mode;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;

final class GemFireConnection implements DriverConnection {

	private final GemFireCache cache;
	private final Mode mode;

	GemFireConnection(GemFireCache cache, Mode mode) {
		this.cache = cache;
		this.mode = mode;
	}

	@Override
	public <K, V> DriverRegion<K, V> createRegion(String name, RegionShortcut shortcut) {
		return invoke("createRegion", () -> {
			// Native cache implementations can implement both Cache and ClientCache interfaces.
			if (mode != Mode.PEER || !(cache instanceof Cache peer)) {
				throw new IllegalStateException("Peer region creation requires a peer cache.");
			}
			return new GemFireRegion<>(peer.<K, V>createRegionFactory(
				org.apache.geode.cache.RegionShortcut.valueOf(shortcut.name())).create(name));
		});
	}

	@Override
	public <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut) {
		return invoke("createClientRegion", () -> {
			if (mode != Mode.CLIENT || !(cache instanceof ClientCache client)) {
				throw new IllegalStateException("Client region creation requires a client cache.");
			}
			return new GemFireRegion<>(client.<K, V>createClientRegionFactory(
				org.apache.geode.cache.client.ClientRegionShortcut.valueOf(shortcut.name())).create(name));
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
			throw new DriverOperationException("GemFire cache operation failed: " + operation, ex);
		}
	}
}
