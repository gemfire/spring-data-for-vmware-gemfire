/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.cache;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.util.Assert;

/**
 * Spring Framework {@link Cache} implementation backed by a GemFire {@link GudRegion}.
 *
 * @author Costin Leau
 * @author John Blum
 * @author Oliver Gierke
 * @see GudRegion
 */
@SuppressWarnings("rawtypes")
public class GemfireCache implements Cache {

	private final GudRegion region;

	/**
	 * Wraps a GemFire {@link GudRegion} in an instance of {@link GemfireCache} to adapt the GemFire {@link GudRegion}
	 * to function as a Spring {@link Cache} in Spring's caching infrastructure.
	 *
	 * @param region GemFire {@link GudRegion} to wrap.
	 * @return an instance of {@link GemfireCache} backed by the provided GemFire {@link GudRegion}.
	 * @see GudRegion
	 * @see Cache
	 * @see #GemfireCache(GudRegion)
	 */
	public static GemfireCache wrap(GudRegion<?, ?> region) {
		return new GemfireCache(region);
	}

	/**
	 * Constructs an instance of {@link GudClientCache} initialized with the given GemFire {@link GudRegion}.
	 * The {@link GudRegion} will function as the backing store and implementation for
	 * the Spring {@link Cache} interface.
	 *
	 * @param region GemFire {@link GudRegion} backing the Spring {@link Cache}.
	 * @throws IllegalArgumentException if {@link GudRegion} is null.
	 */
	public GemfireCache(GudRegion<?, ?> region) {
		Assert.notNull(region, "GemFire Region must not be null");
		this.region = region;
	}

	/**
	 * Returns the GemFire {@link GudRegion} used as the implementation for this Spring {@link Cache}.
	 *
	 * @return the GemFire {@link GudRegion} used as the implementation for this Spring {@link Cache}.
	 * @see GudRegion
	 */
	public GudRegion getNativeCache() {
		return this.region;
	}

	/**
	 * Returns the name of this Spring {@link Cache}.
	 *
	 * @return the name of this Spring {@link Cache}.
	 * @see GudRegion#getName()
	 */
	public String getName() {
		return getNativeCache().getName();
	}

	/**
	 * Clears the entire contents of this Spring {@link Cache}.
	 *
	 * @see GudRegion#clear()
	 */
	public void clear() {
		getNativeCache().clear();
	}

	/**
	 * Evicts (destroys) the entry (key/value) mapped to the given key from this Spring {@link Cache}.
	 *
	 * @param key key used to identify the cache entry to evict.
	 * @see GudRegion#remove(Object)
	 */
	public void evict(Object key) {
		getNativeCache().remove(key);
	}

	/**
	 * Returns the cache value for the given key wrapped in an instance of
	 * {@link ValueWrapper}.
	 *
	 * @param key key identifying the the value to retrieve from the cache.
	 * @return the value cached with the given key.
	 * @see ValueWrapper
	 * @see GudRegion#get(Object)
	 */
	public ValueWrapper get(Object key) {

		Object value = getNativeCache().get(key);

		return value != null ? new SimpleValueWrapper(value) : null;
	}

	/**
	 * Returns the cache value for the given key cast to the specified {@link Class} type.
	 *
	 * @param <T> desired {@link Class} type of the cache value.
	 * @param key key identifying the the value to retrieve from the cache.
	 * @param type desired {@link Class} type of the value.
	 * @return the cache value for the given key cast to the specified {@link Class} type.
	 * @throws IllegalStateException if the value is not null and not an instance of the desired type.
	 * @see GudRegion#get(Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {

		Object value = getNativeCache().get(key);

		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException(String.format(
				"Cached value [%1$s] is not an instance of type [%2$s]",
					value, type.getName()));
		}

		return (T) value;
	}

	/**
	 * Returns the cache value for given key.  If the value is {@literal null}, then the provided
	 * {@link Callable} {@code valueLoader} will be called to obtain a value and add the entry
	 * to this cache.
	 *
	 * @param <T> {@link Class} type of the value.
	 * @param key key identifying the the value to retrieve from the cache.
	 * @param valueLoader {@link Callable} object used to load a value if the entry identified by the key
	 * does not already have value.
	 * @return the cache value of the given key or a value obtained by calling the {@link Callable} object
	 * if the value for key is {@literal null}.
	 * @throws ValueRetrievalException if an error occurs while trying to
	 * load a value for given key using the {@link Callable}.
	 * @see #get(Object, Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Callable<T> valueLoader) {

		T value = (T) get(key, Object.class);

		if (value == null) {
			synchronized (getNativeCache()) {
				value = (T) get(key, Object.class);

				if (value == null) {
					try {
						value = valueLoader.call();
						put(key, value);
					}
					catch (Exception e) {
						throw new ValueRetrievalException(key, valueLoader, e);
					}
				}
			}
		}

		return value;
	}

	/**
	 * Stores the given value in the cache referenced by the given key.  This operation will only store the value
	 * if the value is not {@literal null}.
	 *
	 * @param key key used to reference the value in the cache.
	 * @param value value to store in the cache referenced by the key.
	 * @see GudRegion#put(Object, Object)
	 */
	@SuppressWarnings("unchecked")
	public void put(Object key, Object value) {

		if (value != null) {
			getNativeCache().put(key, value);
		}
	}

	/**
	 * Implementation of {@link Cache#putIfAbsent(Object, Object)} satisfying the extension of
	 * the {@link Cache} interface in Spring 4.1. Don't add the {@link Override} annotation
	 * otherwise this will break the compilation on 4.0.
	 *
	 * @return the existing value if the given key is already mapped to a value.
	 * @see Cache#putIfAbsent(Object, Object)
	 * @see GudRegion#putIfAbsent(Object, Object)
	 */
	@SuppressWarnings("unchecked")
	public ValueWrapper putIfAbsent(Object key, Object value) {

		Object existingValue = getNativeCache().putIfAbsent(key, value);

		return (existingValue != null ? new SimpleValueWrapper(existingValue) : null);
	}
}
