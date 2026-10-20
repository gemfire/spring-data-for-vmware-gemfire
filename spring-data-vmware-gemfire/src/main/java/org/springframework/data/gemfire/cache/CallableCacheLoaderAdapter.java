/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import java.util.concurrent.Callable;

import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

import org.springframework.util.Assert;

/**
 * The {@link CallableCacheLoaderAdapter} class is a {@link Callable} and GemFire {@link DriverCacheLoader} implementation
 * that adapts the {@link Callable} interface into an instance of the {@link DriverCacheLoader} interface.  This class is
 * useful in situations where GemFire developers have several {@link DriverCacheLoader} implementations that they wish to
 * use with Spring's Cache Abstraction.
 *
 * @author John Blum
 * @see Callable
 * @see DriverCacheLoader
 * @see DriverLoaderHelper
 * @see DriverRegion
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class CallableCacheLoaderAdapter<K, V> implements Callable<V>, DriverCacheLoader<K, V> {

	private final K key;

	private final DriverCacheLoader<K, V> cacheLoader;

	private final Object argument;

	private final DriverRegion<K, V> region;

	/**
	 * Constructs an instance of the CallableCacheLoaderAdapter that delegates to the given {@link DriverCacheLoader}.
	 *
	 * @param delegate the {@link DriverCacheLoader} delegated to by this adapter.
	 * @see #CallableCacheLoaderAdapter(DriverCacheLoader, Object, DriverRegion, Object)
	 * @see DriverCacheLoader
	 */
	public CallableCacheLoaderAdapter(DriverCacheLoader<K, V> delegate) {
		this(delegate, null, null, null);
	}

	/**
	 * Constructs an instance of the CallableCacheLoaderAdapter that delegates to the given {@link DriverCacheLoader}
	 * and is initialized with the given key for which the value will be loaded along with the {@link DriverRegion}
	 * in which the entry (key/value) belongs.
	 *
	 * @param delegate the {@link DriverCacheLoader} delegated to by this adapter.
	 * @param key the key for which the value will be loaded.
	 * @param region the {@link DriverRegion} in which the entry (key/value) belongs.
	 * @see #CallableCacheLoaderAdapter(DriverCacheLoader, Object, DriverRegion, Object)
	 * @see DriverCacheLoader
	 * @see DriverRegion
	 */
	public CallableCacheLoaderAdapter(DriverCacheLoader<K, V> delegate, K key, DriverRegion<K, V> region) {
		this(delegate, key, region, null);
	}

	/**
	 * Constructs an instance of the CallableCacheLoaderAdapter that delegates to the given {@link DriverCacheLoader}
	 * and is initialized with the given key for which the value will be loaded along with the {@link DriverRegion}
	 * in which the entry (key/value) belongs.  Additionally, an argument may be specified for use with the
	 * {@link DriverCacheLoader} delegate.
	 *
	 * @param delegate the {@link DriverCacheLoader} delegated to by this adapter.
	 * @param key the key for which the value will be loaded.
	 * @param region the {@link DriverRegion} in which the entry (key/value) belongs.
	 * @param argument the Object argument used with the {@link DriverCacheLoader} delegate.
	 * @see #CallableCacheLoaderAdapter(DriverCacheLoader, Object, DriverRegion, Object)
	 * @see DriverCacheLoader
	 * @see DriverRegion
	 */
	public CallableCacheLoaderAdapter(DriverCacheLoader<K, V> delegate, K key, DriverRegion<K, V> region, Object argument) {

		Assert.notNull(delegate, "CacheLoader must not be null");

		this.cacheLoader = delegate;
		this.argument = argument;
		this.key = key;
		this.region = region;
	}

	/**
	 * Gets the argument used by this {@link DriverCacheLoader} to load the value for the specified key.
	 *
	 * @return an Object argument used by this {@link DriverCacheLoader} when loading the value for the specified key.
	 */
	protected Object getArgument() {
		return this.argument;
	}

	/**
	 * The {@link DriverCacheLoader} delegate used to actually load the cache value for the specified key.
	 *
	 * @return a reference to the actual {@link DriverCacheLoader} used when loading the cache value for the specified key.
	 * @see DriverCacheLoader
	 */
	protected DriverCacheLoader<K, V> getCacheLoader() {
		return this.cacheLoader;
	}

	/**
	 * The specified key for which a value will be loaded by this {@link DriverCacheLoader}.
	 *
	 * @return the specified key for which the value will be loaded.
	 */
	protected K getKey() {
		return this.key;
	}

	/**
	 * Returns the DriverRegion to which the entry (key/value) belongs.
	 *
	 * @return the DriverRegion to which the entry belongs.
	 * @see DriverRegion
	 */
	protected DriverRegion<K, V> getRegion() {
		return this.region;
	}

	/**
	 * Invoked to load a cache value for the specified key.  Delegates to {@link #load(DriverLoaderHelper)}.
	 *
	 * @return the loaded cache value for the specified key.
	 * @throws IllegalStateException if the {@link DriverRegion} or key references are null.
	 * @throws Exception if the load operation fails.
	 * @see #load(DriverLoaderHelper)
	 */
	public final V call() throws Exception {

		Assert.state(getKey() != null, "The key for which the value is loaded for cannot be null");
		Assert.state(getRegion() != null, "The Region to load cannot be null");

		return load(new DriverLoaderHelper<K, V>() {

			public V netSearch(final boolean doNetLoad) {
				throw UnsupportedFeatureException.notIntroduced("LoaderHelper.netSearch");
			}

			public K getKey() {
				return CallableCacheLoaderAdapter.this.getKey();
			}

			public DriverRegion<K, V> getRegion() {
				return CallableCacheLoaderAdapter.this.getRegion();
			}

			public Object getArgument() {
				return CallableCacheLoaderAdapter.this.getArgument();
			}
		});
	}

	/**
	 * Closes any resources used by this {@link DriverCacheLoader}.  Delegates to the underlying {@link DriverCacheLoader}.
	 *
	 * @see #getCacheLoader()
	 */
	public void close() {
		getCacheLoader().close();
	}

	/**
	 * Loads a value for the specified cache (i.e. {@link DriverRegion}) and key with the help of the {@link DriverLoaderHelper}.
	 * Delegates to the underlying {@link DriverCacheLoader}. Runtime exceptions propagate unchanged.
	 *
	 * @param loaderHelper a {@link DriverLoaderHelper} object passed in from cache service providing access to the key,
	 * {@link DriverRegion}, argument, and <code>netSearch</code>.
	 * @return the value supplied for the specified key, or null if no value can be supplied.
	 * The driver controls when its loader is invoked.
	 * @see DriverCacheLoader#load(DriverLoaderHelper)
	 * @see DriverLoaderHelper
	 * @see #getCacheLoader()
	 */
	public V load(DriverLoaderHelper<K, V> loaderHelper) {
		return getCacheLoader().load(loaderHelper);
	}
}
