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

package org.springframework.data.gemfire;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.gemfire.gud.api.GudGemFireCheckedException;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudIndexInvalidException;
import org.springframework.data.gemfire.gud.api.GudQuery;
import org.springframework.data.gemfire.gud.api.GudQueryInvalidException;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSelectResults;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link GemfireTemplate} class simplifies Apache Geode data access operations, converting Apache Geode
 * {@link GudGemFireCheckedException GemFireCheckedExceptions} and {@link GudGemFireException GemFireExceptions} into
 * Spring {@link DataAccessException DataAccessExceptions}, following the <code>org.springframework.dao</code>
 * {@link Exception} hierarchy.
 *
 * The central method is <code>execute</code>, supporting Apache Geode data access code implementing the
 * {@link GemfireCallback} interface. It provides dedicated handling such that neither the {@link GemfireCallback}
 * implementation nor the {@literal calling code} needs to explicitly care about handling {@link GudRegion} life-cycle
 * {@link Exception Exceptions}.
 *
 * This template class is typically used to implement data access operations or business logic services using Apache
 * Geode within their implementation but are Geode-agnostic in their interface. The latter or code calling the latter
 * only have to deal with business objects, query objects, and <code>org.springframework.dao</code>
 * {@link Exception Exceptions}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Map
 * @see GudGemFireCheckedException
 * @see GudGemFireException
 * @see GudRegion
 * @see GudClientCache
 * @see GudQuery
 * @see GudQueryService
 * @see GudSelectResults
 * @see GemfireAccessor
 * @see GemfireOperations
 */
@SuppressWarnings("unused")
public class GemfireTemplate extends GemfireAccessor implements GemfireOperations {

	private boolean exposeNativeRegion = false;

	private GudRegion<?, ?> regionProxy;

	/**
	 * Constructs a new, uninitialized instance of {@link GemfireTemplate}.
	 *
	 * @see #GemfireTemplate(Region)
	 */
	public GemfireTemplate() { }

	/**
	 * Constructs a new instance of the {@link GemfireTemplate} initialized with the given {@link GudRegion} on which
	 * (cache) data access operations will be performed.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param region {@link GudRegion} on which data access operations will be performed by this template;
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link GudRegion} is {@literal null}.
	 * @see #setRegion(GudRegion)
	 * @see #afterPropertiesSet()
	 */
	public <K, V> GemfireTemplate(GudRegion<K, V> region) {
		setRegion(region);
		afterPropertiesSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() {

		super.afterPropertiesSet();

		this.regionProxy = createRegionProxy(getRegion());
	}

	/**
	 * Configure whether to expose the native {@link GudRegion} to {@link GemfireCallback} code.
	 *
	 * <p>Default is {@literal false}, therefore a {@link GudRegion} {@literal proxy} will be returned,
	 * suppressing <code>close</code> calls.
	 *
	 * <p>As there is often a need to cast to an interface, the exposed proxy implements all interfaces implemented by
	 * the original {@link GudRegion}. If this is not sufficient, turn this flag to {@literal true}.
	 *
	 * @param exposeNativeRegion a boolean value indicating whether the native {@link GudRegion} should be exposed to
	 * the {@link GemfireCallback}.
	 * @see GemfireCallback
	 */
	public void setExposeNativeRegion(boolean exposeNativeRegion) {
		this.exposeNativeRegion = exposeNativeRegion;
	}

	/**
	 * Determines whether to expose the native {@link GudRegion} or the {@link GudRegion} {@literal proxy}
	 * to {@link GemfireCallback} code.
	 *
	 * @return a boolean value indicating whether the native {@link GudRegion} or the {@link GudRegion} {@literal proxy}
	 * is exposed to {@link GemfireCallback} code.
	 * @see #setExposeNativeRegion(boolean)
	 */
	public boolean isExposeNativeRegion() {
		return this.exposeNativeRegion;
	}

	@Override
	public boolean containsKey(Object key) {
		return getRegion().containsKey(key);
	}

	@Override
	public boolean containsKeyOnServer(Object key) {
		return getRegion().containsKeyOnServer(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getRegion().containsValue(value);
	}

	@Override
	public boolean containsValueForKey(Object key) {
		return getRegion().containsValueForKey(key);
	}

	@Override
	public <K, V> void create(K key, V value) {

		try {
			getRegion().create(key, value);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> V get(K key) {

		try {
			return this.<K, V>getRegion().get(key);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> Map<K, V> getAll(Collection<?> keys) {

		try {
			return this.<K, V>getRegion().getAll(keys);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> V put(K key, V value) {

		try {
			return this.<K, V>getRegion().put(key, value);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> void putAll(Map<? extends K, ? extends V> map) {

		try {
			this.<K, V>getRegion().putAll(map);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> V putIfAbsent(K key, V value) {

		try {
			return this.<K, V>getRegion().putIfAbsent(key, value);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> V remove(K key) {

		try {
			return this.<K, V>getRegion().remove(key);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public void removeAll(Collection<?> keys) {

		try {
			getRegion().removeAll(keys);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> V replace(K key, V value) {

		try {
			return this.<K, V>getRegion().replace(key, value);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <K, V> boolean replace(K key, V oldValue, V newValue) {

		try {
			return this.<K, V>getRegion().replace(key, oldValue, newValue);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
	}

	@Override
	public <E> GudSelectResults<E> query(String query) {

		try {
			return getRegion().query(query);
		}
		catch (GudIndexInvalidException | GudQueryInvalidException cause) {
			throw convertGemFireQueryException(cause);
		}
		catch (GudGemFireCheckedException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (RuntimeException cause) {

			if (GemfireCacheUtils.isCqInvalidException(cause)) {
				throw GemfireCacheUtils.convertCqInvalidException(cause);
			}

			throw cause;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> GudSelectResults<E> find(String query, Object... arguments) throws InvalidDataAccessApiUsageException {

		try {

			GudQueryService queryService = resolveQueryService(getRegion());

			GudQuery compiledQuery = queryService.newQuery(query);

			Object result = compiledQuery.execute(arguments);

			if (result instanceof GudSelectResults) {
				return (GudSelectResults<E>) result;
			}
			else {

				String message =
					String.format("The result from executing query [%1$s] was not an instance of SelectResults [%2$s]",
						query, result);

				throw new InvalidDataAccessApiUsageException(message);
			}
		}
		catch (GudIndexInvalidException | GudQueryInvalidException cause) {
			throw convertGemFireQueryException(cause);
		}
		catch (GudGemFireCheckedException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (RuntimeException cause) {

			if (GemfireCacheUtils.isCqInvalidException(cause)) {
				throw GemfireCacheUtils.convertCqInvalidException(cause);
			}

			throw cause;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T findUnique(String query, Object... arguments) throws InvalidDataAccessApiUsageException {

		try {

			GudQueryService queryService = resolveQueryService(getRegion());

			GudQuery compiledQuery = queryService.newQuery(query);

			Object result = compiledQuery.execute(arguments);

			if (result instanceof GudSelectResults) {

				GudSelectResults<T> selectResults = (GudSelectResults<T>) result;

				List<T> results = selectResults.asList();

				if (results.size() == 1) {
					result = results.get(0);
				}
				else {

					String message = String.format("The result returned from query [%1$s]) was not unique [%2$s]",
						query, result);

					throw new InvalidDataAccessApiUsageException(message);
				}
			}

			return (T) result;
		}
		catch (GudIndexInvalidException | GudQueryInvalidException cause) {
			throw convertGemFireQueryException(cause);
		}
		catch (GudGemFireCheckedException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (RuntimeException cause) {

			if (GemfireCacheUtils.isCqInvalidException(cause)) {
				throw GemfireCacheUtils.convertCqInvalidException(cause);
			}

			throw cause;
		}
	}

	/**
	 * Returns the {@link GudQueryService} used by this template in its query/finder methods.
	 *
	 * @param region {@link GudRegion} used to acquire the {@link GudQueryService}.
	 * @return the {@link GudQueryService} that will perform the {@link GudQuery}.
	 * @see GudRegion
	 * @see GudRegion#getRegionService()
	 * @see GudClientCache#getLocalQueryService()
	 */
	protected GudQueryService resolveQueryService(GudRegion<?, ?> region) {

		return region.getRegionService() instanceof GudClientCache
			? resolveClientQueryService(region)
			: queryServiceFrom(region);
	}

	GudQueryService resolveClientQueryService(GudRegion<?, ?> region) {

		GudClientCache clientCache = (GudClientCache) region.getRegionService();

		return requiresLocalQueryService(region) ? clientCache.getLocalQueryService()
			: requiresPooledQueryService(region) ? clientCache.getQueryService(poolNameFrom(region))
			: queryServiceFrom(region);
	}

	boolean requiresLocalQueryService(GudRegion<?, ?> region) {
		return GudScope.LOCAL.equals(region.getAttributes().getScope()) && isLocalWithNoServerProxy(region);
	}

	boolean isLocalWithNoServerProxy(GudRegion<?, ?> region) {

		if (RegionUtils.isLocal(region)) {

			SpringExtensions.ValueReturningThrowableOperation<Boolean> hasServerProxyMethod = () ->
				Optional.ofNullable(ReflectionUtils.findMethod(region.getClass(), "hasServerProxy"))
					.map(method -> ReflectionUtils.invokeMethod(method, region))
					.map(Boolean.FALSE::equals)
					.orElse(false);

			return SpringExtensions.safeGetValue(hasServerProxyMethod, false);
		}

		return false;
	}

	boolean requiresPooledQueryService(GudRegion<?, ?> region) {
		return StringUtils.hasText(poolNameFrom(region));
	}

	String poolNameFrom(GudRegion<?, ?> region) {
		return region.getAttributes().getPoolName();
	}

	GudQueryService queryServiceFrom(GudRegion<?, ?> region) {
		return region.getRegionService().getQueryService();
	}

	/**
	 * Executes the given data access operation defined by the {@link GemfireCallback} in the context of Apache Geode.
	 *
	 * @param <T> {@link Class type} returned by the {@link GemfireCallback}.
	 * @param action {@link GemfireCallback} object defining the Apache Geode action to execute;
	 * must not be {@literal null}.
	 * @return the result of executing the {@link GemfireCallback}.
	 * @throws DataAccessException if an Apache Geode error is thrown by a data access operation.
	 * @throws IllegalArgumentException if {@link GemfireCallback} is {@literal null}.
	 * @see GemfireCallback
	 * @see #execute(GemfireCallback, boolean)
	 */
	@Override
	public <T> T execute(@NonNull GemfireCallback<T> action) throws DataAccessException {
		return execute(action, isExposeNativeRegion());
	}

	/**
	 * Executes the given data access operation defined by the {@link GemfireCallback} in the context of Apache Geode.
	 *
	 * @param <T> {@link Class type} returned by the {@link GemfireCallback}.
	 * @param action {@link GemfireCallback} object defining the Apache Geode action to execute;
	 * must not be {@literal null}.
	 * @param exposeNativeRegion boolean value indicating whether to pass the native {@link GudRegion}
	 * or the {@link GudRegion} {@literal proxy} to the {@link GemfireCallback}.
	 * @return the result of executing the {@link GemfireCallback}.
	 * @throws DataAccessException if an Apache Geode error is thrown by a data access operation.
	 * @throws IllegalArgumentException if {@link GemfireCallback} is {@literal null}.
	 * @see GemfireCallback
	 */
	@Override
	public <T> T execute(@NonNull GemfireCallback<T> action, boolean exposeNativeRegion) throws DataAccessException {

		Assert.notNull(action, "GemfireCallback must not be null");

		try {

			GudRegion<?, ?> regionArgument = exposeNativeRegion ? getRegion() : this.regionProxy;

			return action.doInGemfire(regionArgument);
		}
		catch (GudIndexInvalidException | GudQueryInvalidException cause) {
			throw convertGemFireQueryException(cause);
		}
		catch (GudGemFireCheckedException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (GudGemFireException cause) {
			throw convertGemFireAccessException(cause);
		}
		catch (RuntimeException cause) {

			if (GemfireCacheUtils.isCqInvalidException(cause)) {
				throw GemfireCacheUtils.convertCqInvalidException(cause);
			}

			throw cause;
		}
	}

	/**
	 * Create a close-suppressing proxy for the given Apache Geode cache {@link GudRegion}.
	 *
	 * Called by the <code>execute</code> method.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param region {@link GudRegion} for which a proxy will be created.
	 * @return the Region proxy implementing all interfaces implemented by the passed-in Region object.
	 * @see GudRegion#close()
	 * @see #execute(GemfireCallback, boolean)
	 */
	@SuppressWarnings("unchecked")
	@NonNull
	protected <K, V> GudRegion<K, V> createRegionProxy(@NonNull GudRegion<K, V> region) {

		Class<?> regionType = region.getClass();

		return (GudRegion<K, V>) Proxy.newProxyInstance(regionType.getClassLoader(),
			ClassUtils.getAllInterfacesForClass(regionType, getClass().getClassLoader()),
				new RegionCloseSuppressingInvocationHandler(region));
	}

	/**
	 * {@link InvocationHandler} that suppresses the {@link GudRegion#close()} call on a target {@link GudRegion}.
	 *
	 * @see InvocationHandler
	 * @see GudRegion#close()
	 */
	private static class RegionCloseSuppressingInvocationHandler implements InvocationHandler {

		private final GudRegion<?, ?> target;

		/**
		 * Constructs a new instance of the {@link RegionCloseSuppressingInvocationHandler} initialized with
		 * the given {@link GudRegion}.
		 *
		 * @param target {@link GudRegion} to proxy; must not be {@literal null}.
		 * @throws IllegalArgumentException if {@link GudRegion} is {@literal null}.
		 * @see GudRegion
		 */
		public RegionCloseSuppressingInvocationHandler(@NonNull GudRegion<?, ?> target) {

			Assert.notNull(target, "Target Region must not be null");

			this.target = target;
		}

		/**
		 * {@inheritDoc}
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if ("equals".equals(method.getName())) {
				// only consider equals when proxies are identical
				return proxy == args[0];
			}
			else if ("hashCode".equals(method.getName())) {
				// use hashCode of Region proxy
				return System.identityHashCode(proxy);
			}
			else if ("close".equals(method.getName())) {
				// suppress Region.close()
				return null;
			}
			else {
				try {
					return method.invoke(this.target, args);
				}
				catch (InvocationTargetException cause) {
					throw cause.getTargetException();
				}
			}
		}
	}
}
