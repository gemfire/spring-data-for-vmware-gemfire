/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

final class MockRegion<K, V> implements DriverRegion<K, V> {

	private final String name;
	private final MockConnection connection;
	private final ConcurrentMap<K, V> entries = new ConcurrentHashMap<>();
	private DriverCacheLoader<K, V> loader;

	MockRegion(String name, MockConnection connection) {
		this.name = name;
		this.connection = connection;
	}

	@Override
	public boolean isDestroyed() {
		return connection.isClosed();
	}

	@Override
	public void clear() {
		connection.checkOpen();
		entries.clear();
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return invoke(key, () -> {
			if (value == null) {
				throw new DriverOperationException("Region values must not be null for putIfAbsent.", null);
			}
			return entries.putIfAbsent(key, value);
		});
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public V get(K key) {
		return get(key, null);
	}

	@Override
	public synchronized V get(K key, Object argument) {
		return invoke(key, () -> {
			V value = entries.get(key);
			if (value != null || loader == null) {
				return value;
			}
			try {
				V loaded = loader.load(new DriverLoaderHelper<>() {
					@Override
					public K getKey() {
						return key;
					}

					@Override
					public DriverRegion<K, V> getRegion() {
						return MockRegion.this;
					}

					@Override
					public Object getArgument() {
						return argument;
					}

					@Override
					public V netSearch(boolean doNetLoad) {
						throw UnsupportedFeatureException.notIntroduced("LoaderHelper.netSearch");
					}
				});
				if (loaded == null) {
					return null;
				}
				V previous = entries.putIfAbsent(key, loaded);
				return previous != null ? previous : loaded;
			} catch (RuntimeException ex) {
				throw new DriverOperationException("Mock region operation failed: load", ex);
			}
		});
	}

	@Override
	public synchronized void setCacheLoader(DriverCacheLoader<K, V> loader) {
		connection.checkOpen();
		if (this.loader != loader) {
			DriverCacheLoader<K, V> previous = this.loader;
			this.loader = loader;
			closeLoader(previous);
		}
	}

	@Override
	public V put(K key, V value) {
		return invoke(key, () -> {
			if (value == null) {
				throw new DriverOperationException("Region values must not be null for put.", null);
			}
			return entries.put(key, value);
		});
	}

	@Override
	public V remove(K key) {
		return invoke(key, () -> entries.remove(key));
	}

	@Override
	public boolean containsKey(K key) {
		return invoke(key, () -> entries.containsKey(key));
	}

	@Override
	public int size() {
		connection.checkOpen();
		return entries.size();
	}

	synchronized void releaseEntries() {
		entries.clear();
		DriverCacheLoader<K, V> previous = loader;
		loader = null;
		closeLoader(previous);
	}

	private void closeLoader(DriverCacheLoader<K, V> previous) {
		if (previous != null) {
			try {
				previous.close();
			} catch (RuntimeException ex) {
				throw new DriverOperationException("Mock region operation failed: closeLoader", ex);
			}
		}
	}

	private <T> T invoke(K key, Supplier<T> action) {
		connection.checkOpen();
		if (key == null) {
			throw new DriverOperationException("Region keys must not be null.", null);
		}
		return action.get();
	}
}
