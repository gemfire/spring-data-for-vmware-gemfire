/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.cache;

import java.util.concurrent.Callable;

import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheLoaderException;
import org.springframework.data.gemfire.gud.api.GudLoaderHelper;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudTimeoutException;

import org.springframework.util.Assert;

/**
 * The {@link CallableCacheLoaderAdapter} class is a {@link Callable} and GemFire {@link GudCacheLoader} implementation
 * that adapts the {@link Callable} interface into an instance of the {@link GudCacheLoader} interface.  This class is
 * useful in situations where GemFire developers have several {@link GudCacheLoader} implementations that they wish to
 * use with Spring's Cache Abstraction.
 *
 * @author John Blum
 * @see Callable
 * @see GudCacheLoader
 * @see GudLoaderHelper
 * @see GudRegion
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class CallableCacheLoaderAdapter<K, V> implements Callable<V>, GudCacheLoader<K, V> {

	private final K key;

	private final GudCacheLoader<K, V> cacheLoader;

	private final Object argument;

	private final GudRegion<K, V> region;

	/**
	 * Constructs an instance of the CallableCacheLoaderAdapter that delegates to the given {@link GudCacheLoader}.
	 *
	 * @param delegate the {@link GudCacheLoader} delegated to by this adapter.
	 * @see #CallableCacheLoaderAdapter(GudCacheLoader, Object, GudRegion, Object)
	 * @see GudCacheLoader
	 */
	public CallableCacheLoaderAdapter(GudCacheLoader<K, V> delegate) {
		this(delegate, null, null, null);
	}

	/**
	 * Constructs an instance of the CallableCacheLoaderAdapter that delegates to the given {@link GudCacheLoader}
	 * and is initialized with the given key for which the value will be loaded along with the {@link GudRegion}
	 * in which the entry (key/value) belongs.
	 *
	 * @param delegate the {@link GudCacheLoader} delegated to by this adapter.
	 * @param key the key for which the value will be loaded.
	 * @param region the {@link GudRegion} in which the entry (key/value) belongs.
	 * @see #CallableCacheLoaderAdapter(GudCacheLoader, Object, GudRegion, Object)
	 * @see GudCacheLoader
	 * @see GudRegion
	 */
	public CallableCacheLoaderAdapter(GudCacheLoader<K, V> delegate, K key, GudRegion<K, V> region) {
		this(delegate, key, region, null);
	}

	/**
	 * Constructs an instance of the CallableCacheLoaderAdapter that delegates to the given {@link GudCacheLoader}
	 * and is initialized with the given key for which the value will be loaded along with the {@link GudRegion}
	 * in which the entry (key/value) belongs.  Additionally, an argument may be specified for use with the
	 * {@link GudCacheLoader} delegate.
	 *
	 * @param delegate the {@link GudCacheLoader} delegated to by this adapter.
	 * @param key the key for which the value will be loaded.
	 * @param region the {@link GudRegion} in which the entry (key/value) belongs.
	 * @param argument the Object argument used with the {@link GudCacheLoader} delegate.
	 * @see #CallableCacheLoaderAdapter(GudCacheLoader, Object, GudRegion, Object)
	 * @see GudCacheLoader
	 * @see GudRegion
	 */
	public CallableCacheLoaderAdapter(GudCacheLoader<K, V> delegate, K key, GudRegion<K, V> region, Object argument) {

		Assert.notNull(delegate, "CacheLoader must not be null");

		this.cacheLoader = delegate;
		this.argument = argument;
		this.key = key;
		this.region = region;
	}

	/**
	 * Gets the argument used by this {@link GudCacheLoader} to load the value for the specified key.
	 *
	 * @return an Object argument used by this {@link GudCacheLoader} when loading the value for the specified key.
	 */
	protected Object getArgument() {
		return this.argument;
	}

	/**
	 * The {@link GudCacheLoader} delegate used to actually load the cache value for the specified key.
	 *
	 * @return a reference to the actual {@link GudCacheLoader} used when loading the cache value for the specified key.
	 * @see GudCacheLoader
	 */
	protected GudCacheLoader<K, V> getCacheLoader() {
		return this.cacheLoader;
	}

	/**
	 * The specified key for which a value will be loaded by this {@link GudCacheLoader}.
	 *
	 * @return the specified key for which the value will be loaded.
	 */
	protected K getKey() {
		return this.key;
	}

	/**
	 * Returns the Region to which the entry (key/value) belongs.
	 *
	 * @return the Region to which the entry belongs.
	 * @see GudRegion
	 */
	protected GudRegion<K, V> getRegion() {
		return this.region;
	}

	/**
	 * Invoked to load a cache value for the specified key.  Delegates to {@link #load(GudLoaderHelper)}.
	 *
	 * @return the loaded cache value for the specified key.
	 * @throws IllegalStateException if the {@link GudRegion} or key references are null.
	 * @throws Exception if the load operation fails.
	 * @see #load(GudLoaderHelper)
	 */
	public final V call() throws Exception {

		Assert.state(getKey() != null, "The key for which the value is loaded for cannot be null");
		Assert.state(getRegion() != null, "The Region to load cannot be null");

		return load(new GudLoaderHelper<K, V>() {

			public V netSearch(final boolean doNetLoad) throws GudCacheLoaderException, GudTimeoutException {
				throw new UnsupportedOperationException("not implemented");
			}

			public K getKey() {
				return CallableCacheLoaderAdapter.this.getKey();
			}

			public GudRegion<K, V> getRegion() {
				return CallableCacheLoaderAdapter.this.getRegion();
			}

			public Object getArgument() {
				return CallableCacheLoaderAdapter.this.getArgument();
			}
		});
	}

	/**
	 * Closes any resources used by this {@link GudCacheLoader}.  Delegates to the underlying {@link GudCacheLoader}.
	 *
	 * @see #getCacheLoader()
	 */
	public void close() {
		getCacheLoader().close();
	}

	/**
	 * Loads a value for the specified cache (i.e. {@link GudRegion}) and key with the help of the {@link GudLoaderHelper}.
	 * Delegates to the underlying {@link GudCacheLoader}.
	 *
	 * @param loaderHelper a {@link GudLoaderHelper} object passed in from cache service providing access to the key,
	 * {@link GudRegion}, argument, and <code>netSearch</code>.
	 * @return the value supplied for the specified key, or null if no value can be supplied.  A local loader will
	 * always be invoked if one exists. Otherwise one remote loader is invoked. Returning <code>null</code> causes
	 * {@link GudRegion#get(Object, Object)} to return <code>null</code>.
	 * @throws GudCacheLoaderException if an error occurs during the load operation. This exception, or any other
	 * Exception thrown by this method will be propagated back to the application from the
	 * {@link GudRegion#get(Object)} method.
	 * @see GudCacheLoader#load(GudLoaderHelper)
	 * @see GudLoaderHelper
	 * @see #getCacheLoader()
	 */
	public V load(GudLoaderHelper<K, V> loaderHelper) throws GudCacheLoaderException {
		return getCacheLoader().load(loaderHelper);
	}
}
