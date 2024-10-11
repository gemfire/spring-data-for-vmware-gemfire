/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.NOT_SUPPORTED;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.CacheCallback;
import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.DiskStoreFactory;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.EvictionAttributesMutator;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.MembershipAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionExistsException;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.SubscriptionAttributes;
import org.apache.geode.cache.TransactionId;
import org.apache.geode.cache.TransactionListener;
import org.apache.geode.cache.TransactionWriter;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolFactory;
import org.apache.geode.cache.client.PoolManager;
import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.control.ResourceManager;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexStatistics;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.QueryStatistics;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.types.CollectionType;
import org.apache.geode.cache.query.types.ObjectType;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.cache.server.ClientSubscriptionConfig;
import org.apache.geode.cache.server.ServerLoadProbe;
import org.apache.geode.compression.Compressor;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.internal.cache.PoolManagerImpl;
import org.apache.geode.pdx.PdxSerializer;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.RegionShortcutWrapper;
import org.springframework.data.gemfire.client.ClientRegionShortcutWrapper;
import org.springframework.data.gemfire.server.SubscriptionEvictionPolicy;
import org.springframework.data.gemfire.tests.mock.support.MockObjectInvocationException;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.tests.util.ObjectUtils;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link GemFireMockObjectsSupport} class is an abstract base class encapsulating factory methods for creating
 * Apache Geode or VMware (Pivotal) GemFire Mock Objects, {@link ClientCache}, {@link Region},
 * and so on).
 *
 * @author John Blum
 * @see File
 * @see InputStream
 * @see InetAddress
 * @see InetSocketAddress
 * @see Objects
 * @see Optional
 * @see Properties
 * @see Random
 * @see UUID
 * @see AttributesMutator
 * @see ClientCacheFactory
 * @see CacheListener
 * @see CacheLoader
 * @see CacheWriter
 * @see CustomExpiry
 * @see DataPolicy
 * @see DiskStore
 * @see DiskStoreFactory
 * @see EvictionAttributes
 * @see EvictionAttributesMutator
 * @see ExpirationAttributes
 * @see ClientCache
 * @see MembershipAttributes
 * @see Region
 * @see RegionAttributes
 * @see RegionFactory
 * @see RegionService
 * @see Scope
 * @see SubscriptionAttributes
 * @see ClientCache
 * @see ClientCacheFactory
 * @see ClientRegionFactory
 * @see ClientRegionShortcut
 * @see Pool
 * @see PoolFactory
 * @see PoolManager
 * @see ResourceManager
 * @see RegionFunctionContext
 * @see CqAttributes
 * @see CqQuery
 * @see Index
 * @see IndexStatistics
 * @see Query
 * @see QueryService
 * @see QueryStatistics
 * @see CacheServer
 * @see ClientSubscriptionConfig
 * @see ServerLoadProbe
 * @see Compressor
 * @see DistributedMember
 * @see DistributedSystem
 * @see PdxSerializer
 * @see org.mockito.Mockito
 * @see DisposableBean
 * @see MockObjectsSupport
 * @since 0.0.1
 */
@SuppressWarnings("all")
public abstract class GemFireMockObjectsSupport extends MockObjectsSupport {

	private static final boolean DEFAULT_USE_SINGLETON_CACHE = false;

	private static final AtomicReference<ClientCache> cacheReference = new AtomicReference<>(null);
	private static final AtomicReference<ClientCache> singletonCache = new AtomicReference<>(null);
	private static final AtomicReference<Properties> gemfireProperties = new AtomicReference<>(new Properties());

	private static final List<Object> cachedGemFireObjects = Collections.synchronizedList(new ArrayList<>());

	private static final Map<String, DiskStore> diskStores = new ConcurrentHashMap<>();

	private static final Map<String, Region<Object, Object>> regions = new ConcurrentHashMap<>();

	private static final Map<String, RegionAttributes<Object, Object>> regionAttributes = new ConcurrentHashMap<>();

	private static final Set<String> registeredPoolNames = new ConcurrentSkipListSet<>();

	private static final String CACHE_FACTORY_DS_PROPS_FIELD_NAME = "dsProps";
	private static final String CACHE_FACTORY_INTERNAL_CACHE_BUILDER_FIELD_NAME = "internalCacheBuilder";
	private static final String CLIENT_CACHE_FACTORY_DS_PROPS_FIELD_NAME = "dsProps";
	private static final String INTERNAL_CACHE_BUILDER_CONFIG_PROPERTIES_FIELD_NAME = "configProperties";
	private static final String GEMFIRE_SYSTEM_PROPERTY_PREFIX = "gemfire.";
	private static final String FROM_KEYWORD = "FROM";
	private static final String REPEATING_REGION_SEPARATOR = Region.SEPARATOR + "{2,}";
	private static final String USE_SINGLETON_CACHE_PROPERTY = "spring.data.gemfire.test.cache.singleton";
	private static final String WHERE_KEYWORD = "WHERE";

	private static final String[] GEMFIRE_OBJECT_BASED_PROPERTIES = {
			"security-client-auth-init",
			"security-manager",
			"security-post-processor",
	};

	private static final String[] SPRING_DATA_GEODE_TEST_PROPERTIES = {
			USE_SINGLETON_CACHE_PROPERTY,
	};

	/**
	 * Destroys all mock object state.
	 */
	public static void destroy() {

		cacheReference.set(null);
		singletonCache.set(null);
		gemfireProperties.set(new Properties());
		diskStores.clear();
		regions.clear();
		regionAttributes.clear();

		unregisterFunctions();
		unregisterManagedPools();
		closePools();
		destroyGemFireObjects();
		clearSpringDataGeodeTestProperties();
	}

	/**
	 * Clears all {@literal spring.data.gemfire.test.*} {@link System#getProperties() System Properties}.
	 *
	 * @see System#getProperties()
	 */
	static void clearSpringDataGeodeTestProperties() {
		Arrays.stream(ArrayUtils.nullSafeArray(SPRING_DATA_GEODE_TEST_PROPERTIES, String.class))
				.forEach(System::clearProperty);
	}

	/**
	 * Closes all {@link Pool Pools}.
	 *
	 * @see Pool
	 * @see PoolManager
	 */
	static void closePools() {

		// TODO: add support for keepAlive (??)
		ObjectUtils.doOperationSafely(() -> {
			PoolManager.close();
			return null;
		}, null);
	}

	/**
	 * Destroys all {@link DisposableBean} based {@link Object GemFire objects}.
	 *
	 * @see DisposableBean
	 */
	static synchronized void destroyGemFireObjects() {

		cachedGemFireObjects.stream()
				.filter(gemfireObject -> gemfireObject instanceof DisposableBean)
				.map(gemfireObject -> (DisposableBean) gemfireObject)
				.forEach(disposableBean -> {
					ObjectUtils.doOperationSafely(() -> {
						disposableBean.destroy();
						return null;
					});
				});

		cachedGemFireObjects.clear();
	}

	/**
	 * Unregisters all {@link Function Functions} registered with the {@link FunctionService} by Spring.
	 *
	 * @see org.apache.geode.cache.execute.Function
	 * @see FunctionService
	 */
	static synchronized void unregisterFunctions() {

		CollectionUtils.nullSafeMap(FunctionService.getRegisteredFunctions())
				.forEach((functionId, function) -> FunctionService.unregisterFunction(functionId));
	}

	/**
	 * Unrigsters all {@link Pool Pools} registered with Apache Geode and managed by Spring.
	 *
	 * @see Pool
	 * @see PoolManager
	 */
	static synchronized void unregisterManagedPools() {

		CollectionUtils.nullSafeMap(PoolManager.getAll()).values().stream()
				.filter(Objects::nonNull)
				.filter(pool -> registeredPoolNames.contains(pool.getName()))
				.forEach(GemFireMockObjectsSupport::unregister);

		registeredPoolNames.clear();
	}

	/**
	 * Caches the given {@link Object GemFire object} in order to release resources on shutdown.
	 *
	 * @param gemfireObject {@link Object GemFire object} to cache.
	 */
	private static synchronized void cacheGemFireObject(Object gemfireObject) {

		Optional.ofNullable(gemfireObject).
				ifPresent(cachedGemFireObjects::add);
	}

	/**
	 * Instantiates all Apache Geode/VMware GemFire objects which have been declared
	 * via {@link System#getProperties() System properties}.
	 *
	 * @param <T> {@link Class type} of the {@link ClientCache}.
	 * @param gemfireCache reference to the {@link ClientCache} instance.
	 * @return the given {@link ClientCache} instance.
	 * @see ClientCache
	 */
	private static <T extends ClientCache> T constructGemFireObjects(T gemfireCache) {

		Properties localGemfireProperties = gemfireProperties.get();

		Arrays.stream(GEMFIRE_OBJECT_BASED_PROPERTIES)
				.map(localGemfireProperties::getProperty)
				.filter(StringUtils::hasText)
				.filter(className -> ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader()))
				.forEach(className ->
						cacheGemFireObject(ReflectionUtils.createInstanceIfPresent(className, null)));

		return gemfireCache;
	}

	/**
	 * Converts the given {@link ClientRegionShortcut} into a corresponding {@link DataPolicy}.
	 *
	 * @param clientRegionShortcut {@link ClientRegionShortcut} to convert.
	 * @return a {@link DataPolicy} from the {@link ClientRegionShortcut}.
	 * @see ClientRegionShortcut
	 * @see DataPolicy
	 */
	@SuppressWarnings("unchecked")
	private static DataPolicy convert(ClientRegionShortcut clientRegionShortcut) {

		return Optional.ofNullable(clientRegionShortcut)
				.map(shortcut -> {

					switch(shortcut) {
						case CACHING_PROXY:
						case CACHING_PROXY_HEAP_LRU:
						case CACHING_PROXY_OVERFLOW:
						case LOCAL:
						case LOCAL_HEAP_LRU:
						case LOCAL_OVERFLOW:
							return DataPolicy.NORMAL;
						case LOCAL_PERSISTENT:
						case LOCAL_PERSISTENT_OVERFLOW:
							return DataPolicy.PERSISTENT_REPLICATE;
						case PROXY:
							return DataPolicy.EMPTY;
						default:
							return null;
					}

				}).orElse(DataPolicy.DEFAULT);
	}

	/**
	 * Converts the given {@link RegionShortcut} into a corresponding {@link DataPolicy}.
	 *
	 * @param regionShortcut {@link RegionShortcut} to convert.
	 * @return a {@link DataPolicy} from the {@link RegionShortcut}.
	 * @see RegionShortcut
	 * @see DataPolicy
	 */
	@SuppressWarnings("unchecked")
	private static DataPolicy convert(RegionShortcut regionShortcut) {

		return Optional.ofNullable(regionShortcut)
				.map(shortcut -> {

					switch (shortcut) {
						case LOCAL:
						case LOCAL_HEAP_LRU:
						case LOCAL_OVERFLOW:
							return DataPolicy.NORMAL;
						case REPLICATE:
						case REPLICATE_HEAP_LRU:
						case REPLICATE_OVERFLOW:
							return DataPolicy.REPLICATE;
						case LOCAL_PERSISTENT:
						case LOCAL_PERSISTENT_OVERFLOW:
						case REPLICATE_PERSISTENT:
						case REPLICATE_PERSISTENT_OVERFLOW:
							return DataPolicy.PERSISTENT_REPLICATE;
						case REPLICATE_PROXY:
							return DataPolicy.EMPTY;
						default:
							return null;
					}

				}).orElse(DataPolicy.DEFAULT);
	}

	/**
	 * Determines whether the given {@link Region} is a root {@link Region}.
	 *
	 * @param region {@link Region} to evaluate.
	 * @return a boolean value indicating whether the {@link Region} is a root {@link Region}.
	 * @see Region
	 * @see #isRootRegion(String)
	 */
	private static boolean isRootRegion(Region<?, ?> region) {
		return isRootRegion(region.getFullPath());
	}

	/**
	 * Determines whether the {@link Region} identified by the given {@link String path} is a root {@link Region}.
	 *
	 * @param regionPath {@link String path} identifying the {@link Region} to evaluate.
	 * @return a boolean value indicating whether the {@link Region} identified by the given {@link String path}
	 * is a root {@link Region}.
	 */
	private static boolean isRootRegion(String regionPath) {
		return regionPath.lastIndexOf(Region.SEPARATOR) <= 0;
	}

	/**
	 * Normalizes the {@link String name} of the Apache Geode/VMware GemFire System property by stripping off
	 * the {@literal gemfire.} prefix.
	 *
	 * @param propertyName {@link String name} of the property to normalize.
	 * @return the {@link String normalized form} of the Apache Geode/VMware GemFire System property.
	 * @see <a href="https://geode.apache.org/docs/guide/16/reference/topics/gemfire_properties.html">GemFire Properties</a>
	 */
	private static String normalizeGemFirePropertyName(String propertyName) {

		return Optional.ofNullable(propertyName)
				.filter(StringUtils::hasText)
				.filter(it -> it.startsWith(GEMFIRE_SYSTEM_PROPERTY_PREFIX))
				.map(it -> it.substring(GEMFIRE_SYSTEM_PROPERTY_PREFIX.length()))
				.orElse(propertyName);
	}

	/**
	 * Normalizes the given {@link Region#getFullPath() Regon path} by removing all duplicate, repeating
	 * {@link Region#SEPARATOR} characters between path segments as well as removing the trailing
	 * {@link Region#SEPARATOR}.
	 *
	 * @param regionPath {@link Region#getFullPath()} to normalize.
	 * @return a normalized version of the given {@link Region#getFullPath()}.
	 */
	private static String normalizeRegionPath(String regionPath) {

		regionPath = regionPath.replaceAll(REPEATING_REGION_SEPARATOR, Region.SEPARATOR);

		regionPath = regionPath.endsWith(Region.SEPARATOR)
				? regionPath.substring(0, regionPath.length() - 1)
				: regionPath;

		return regionPath;
	}

	/**
	 * Stores a reference to the given {@link ClientCache} object.
	 *
	 * @param <T> {@link Class type} of {@link ClientCache} (e.g. client or peer).
	 * @param gemfireCache reference to the {@link ClientCache} object to store; maybe {@literal null}.
	 * @return the given {@link ClientCache} object.
	 * @see ClientCache
	 */
	@SuppressWarnings("unchecked")
	private static @Nullable  <T extends ClientCache> T referTo(@Nullable T gemfireCache) {
		return (T) cacheReference.updateAndGet(currentCacheReference -> gemfireCache);
	}

	/**
	 * Remembers the given mock {@link ClientCache} object, which may be a {@link ClientCache}.
	 *
	 * @param <T> {@link Class sub-type} of the {@link ClientCache} instance.
	 * @param mockedGemFireCache {@link ClientCache} to remember.
	 * @param useSingletonCache boolean value indicating whether the {@link ClientCache} is a Singleton.
	 * @return the given {@link ClientCache}.
	 * @throws IllegalArgumentException if {@link ClientCache} is {@literal null}.
	 * @see ClientCache
	 */
	private static <T extends ClientCache> T rememberMockedGemFireCache(T mockedGemFireCache,
																																			boolean useSingletonCache) {

		return Optional.ofNullable(mockedGemFireCache)
				.map(it -> {

					if (useSingletonCache) {
						singletonCache.compareAndSet(null, it);
					}

					return it;
				})
				.orElseThrow(() -> newIllegalArgumentException("ClientCache is required"));
	}

	/**
	 * Remembers the given mock {@link Region}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value.
	 * @param mockRegion {@link Region} to remember.
	 * @throws IllegalArgumentException if the given {@link Region} is {@literal null}.
	 * @throws RegionExistsException if the given {@link Region} already exists.
	 * @return the given {@link Region}.
	 * @see Region
	 */
	@SuppressWarnings("unchecked")
	private static <K, V> Region<K, V> rememberMockedRegion(Region<K, V> mockRegion) {

		String mockRegionPath = Optional.ofNullable(mockRegion)
				.map(Region::getFullPath)
				.orElseThrow(() -> newIllegalArgumentException("Region is required"));

		if (regions.putIfAbsent(mockRegionPath, (Region) mockRegion) != null) {
			throw new RegionExistsException(mockRegion);
		}

		assertThat(regions).containsValue((Region) mockRegion);

		return mockRegion;
	}

	/**
	 * Resolves any {@link ClientCache} object created by the Spring Test for Apache Geode mock objects test framework.
	 *
	 * If {@literal Singleton} caches are not used (default is {@literal false}), then the reference will store the last
	 * mock {@link ClientCache} object created by the Apache Geode mock objects test framework.
	 *
	 * @param <T> {@link Class type} of {@link ClientCache} (e.g. client or peer).
	 * @return a reference to any (and the last) {@ink ClientCache} object created by this test framework.
	 * @see ClientCache
	 * @see Optional
	 */
	private static <T extends ClientCache> Optional<T> resolveAnyGemFireCache() {
		return Optional.ofNullable((T) cacheReference.get());
	}

	/**
	 * Resolves the single, remembered {@link ClientCache} if using GemFire in Singleton-mode.
	 *
	 * @param <T> {@link Class sub-type} of the {@link ClientCache} instance.
	 * @param useSingletonCache boolean value indicating if mock infrastructure is using GemFire Singletons.
	 * @return an {@link Optional}, single remembered instance of the {@link ClientCache}.
	 * @see ClientCache
	 */
	@SuppressWarnings("unchecked")
	private static <T extends ClientCache> Optional<T> resolveMockedGemFireCache(boolean useSingletonCache) {

		return Optional.ofNullable((T) singletonCache.get())
				.filter(it -> useSingletonCache);
	}

	/**
	 * Resolves the {@link RegionAttributes} identified by the given {@link String id}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value.
	 * @param regionAttributesId {@link String id} identifying the {@link RegionAttributes} to resolve.
	 * @return the resolved {@link RegionAttributes} identified by the given {@link String id}.
	 * @throws IllegalStateException if {@link RegionAttributes} could not be resolved from the given {@link String id}.
	 * @see RegionAttributes
	 */
	@SuppressWarnings("unchecked")
	@NonNull
	private static <K, V> RegionAttributes<K, V> resolveRegionAttributes(String regionAttributesId) {

		return (RegionAttributes<K, V>) Optional.ofNullable(regionAttributes.get(regionAttributesId))
				.orElseThrow(() -> newIllegalStateException("RegionAttributes with ID [%s] cannot be found",
						regionAttributesId));
	}

	/**
	 * Constructs, configures and initializes {@link RegionAttributes} from a given {@link ClientRegionShortcut}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value.
	 * @param clientRegionShortcut {@link ClientRegionShortcut} used to construct, configure and initialize
	 * {@link RegionAttributes}.
	 * @return a {@link RegionAttributes} object created from the given {@link ClientRegionShortcut}
	 * or {@literal null} if the {@link ClientRegionShortcut} is {@literal null}.
	 * @see ClientRegionShortcut
	 * @see RegionAttributes
	 */
	@SuppressWarnings("unchecked")
	private static <K, V> RegionAttributes<K, V> resolveRegionAttributesFromClientRegionShortcut(
			@Nullable ClientRegionShortcut clientRegionShortcut) {

		RegionAttributes<K, V> mockRegionAttributes = null;

		if (clientRegionShortcut != null) {

			ClientRegionShortcutWrapper clientRegionShortcutWrapper =
					ClientRegionShortcutWrapper.valueOf(clientRegionShortcut);

			mockRegionAttributes = mock(RegionAttributes.class, withSettings().lenient());

			doReturn(convert(clientRegionShortcut)).when(mockRegionAttributes).getDataPolicy();

			if (clientRegionShortcutWrapper.isHeapLru()) {
				doReturn(EvictionAttributes.createLRUHeapAttributes())
						.when(mockRegionAttributes).getEvictionAttributes();
			}
			else if (clientRegionShortcutWrapper.isOverflow()) {
				doReturn(EvictionAttributes.createLRUHeapAttributes(null, EvictionAction.OVERFLOW_TO_DISK))
						.when(mockRegionAttributes).getEvictionAttributes();
			}
		}

		return mockRegionAttributes;
	}

	/**
	 * Constructs, configures and initializes {@link RegionAttributes} from a given {@link RegionShortcut}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value.
	 * @param regionShortcut {@link RegionShortcut} used to construct, configure and initialize {@link RegionAttributes}.
	 * @return a {@link RegionAttributes} object created from the given {@link RegionShortcut} or {@literal null}
	 * if the {@link RegionShortcut} is {@literal null}.
	 * @see RegionAttributes
	 * @see RegionShortcut
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	private static <K, V> RegionAttributes<K, V> resolveRegionAttributesFromRegionShortcut(
			@Nullable RegionShortcut regionShortcut) {

		RegionAttributes<K, V> mockRegionAttributes = null;

		if (regionShortcut != null) {

			RegionShortcutWrapper regionShortcutWrapper = RegionShortcutWrapper.valueOf(regionShortcut);

			mockRegionAttributes = mock(RegionAttributes.class, withSettings().lenient());

			doReturn(convert(regionShortcut)).when(mockRegionAttributes).getDataPolicy();

			if (regionShortcutWrapper.isHeapLru()) {
				doReturn(EvictionAttributes.createLRUHeapAttributes())
						.when(mockRegionAttributes).getEvictionAttributes();
			}
			else if (regionShortcutWrapper.isOverflow()) {
				doReturn(EvictionAttributes.createLRUHeapAttributes(null, EvictionAction.OVERFLOW_TO_DISK))
						.when(mockRegionAttributes).getEvictionAttributes();
			}

			if (regionShortcutWrapper.isLocal()) {
				doReturn(Scope.LOCAL).when(mockRegionAttributes).getScope();
			}
		}

		return mockRegionAttributes;
	}

	/**
	 * Converts the given {@link String Region name} into a proper {@link Region#getName() Region name}.
	 *
	 * @param regionName {@link String Region name} to evaluate.
	 * @return a proper {@link Region#getName() Region name} from the given {@link String Region name}.
	 * @throws IllegalArgumentException if {@link String Region name} is {@literal null}
	 * or {@link String#isEmpty() empty}.
	 * @see String
	 */
	private static String toRegionName(String regionName) {

		return Optional.ofNullable(regionName)
				.map(String::trim)
				.map(it -> {

					int lastIndexOfRegionSeparator = it.lastIndexOf(Region.SEPARATOR);

					return lastIndexOfRegionSeparator < 0 ? it : it.substring(lastIndexOfRegionSeparator + 1);
				})
				.filter(it -> !it.isEmpty())
				.orElseThrow(() -> newIllegalArgumentException("Region name [%s] is required", regionName));
	}

	/**
	 * Converts the given {@link String Region path} into a proper {@link Region#getFullPath() Region path}.
	 *
	 * @param regionPath {@link String Region path} to evaluate.
	 * @return a proper {@link Region#getFullPath() Region path} from the given {@link String Region path}.
	 * @throws IllegalArgumentException if {@link String Region path} is {@literal null}
	 * or {@link String#isEmpty() empty}.
	 * @see String
	 */
	private static String toRegionPath(String regionPath) {

		return Optional.ofNullable(regionPath)
				.map(String::trim)
				.map(it -> it.startsWith(Region.SEPARATOR) ? it : String.format("%1$s%2$s", Region.SEPARATOR, it))
				.map(GemFireMockObjectsSupport::normalizeRegionPath)
				.filter(it -> !it.isEmpty())
				.orElseThrow(() -> newIllegalArgumentException("Region path [%s] is required", regionPath));
	}

	@SuppressWarnings("unchecked")
	private static <T extends ClientCache> T mockCacheApi(T mockGemFireCache) {

		AtomicBoolean copyOnRead = new AtomicBoolean(false);

		CacheTransactionManager mockCacheTransactionManager = mockCacheTransactionManager();

		DistributedSystem mockDistributedSystem = mockDistributedSystem();

		ResourceManager mockResourceManager = mockResourceManager();

		doAnswer(newSetter(copyOnRead, null)).when(mockGemFireCache).setCopyOnRead(anyBoolean());

		doAnswer(newSetter(regionAttributes, null))
				.when(mockGemFireCache).setRegionAttributes(anyString(), any(RegionAttributes.class));

		when(mockGemFireCache.getCacheTransactionManager()).thenReturn(mockCacheTransactionManager);

		when(mockGemFireCache.getCopyOnRead()).thenAnswer(newGetter(copyOnRead));

		when(mockGemFireCache.getDistributedSystem()).thenReturn(mockDistributedSystem);

		when(mockGemFireCache.getName()).thenAnswer(invocation -> Optional.ofNullable(gemfireProperties)
				.map(AtomicReference::get)
				.map(properties -> properties.getProperty(DistributionConfig.NAME_NAME))
				.filter(StringUtils::hasText)
				.orElse(null));

		when(mockGemFireCache.getRegionAttributes(anyString()))
				.thenAnswer(invocation -> regionAttributes.get(invocation.<String>getArgument(0)));

		when(mockGemFireCache.getResourceManager()).thenReturn(mockResourceManager);

		when(mockGemFireCache.createDiskStoreFactory()).thenAnswer(invocation -> mockDiskStoreFactory());

		when(mockGemFireCache.findDiskStore(anyString()))
				.thenAnswer(invocation -> diskStores.get(invocation.<String>getArgument(0)));

		when(mockGemFireCache.listRegionAttributes()).thenReturn(Collections.unmodifiableMap(regionAttributes));

		doThrow(newUnsupportedOperationException(NOT_SUPPORTED))
				.when(mockGemFireCache).loadCacheXml(any(InputStream.class));

		return mockRegionServiceApi(mockGemFireCache);
	}

	private static <T extends RegionService> T mockRegionServiceApi(T mockRegionService) {

		AtomicBoolean closed = new AtomicBoolean(false);

		doAnswer(newSetter(closed, true, null)).when(mockRegionService).close();

		when(mockRegionService.isClosed()).thenAnswer(newGetter(closed));

		when(mockRegionService.getCancelCriterion()).thenThrow(newUnsupportedOperationException(NOT_SUPPORTED));

		when(mockRegionService.getRegion(anyString())).thenAnswer(invocation -> {

			String regionPath = invocation.getArgument(0);

			String resolvedRegionPath = Optional.ofNullable(regionPath)
					.map(String::trim)
					.filter(it -> !it.isEmpty())
					.map(GemFireMockObjectsSupport::toRegionPath)
					.orElseThrow(() -> newIllegalArgumentException("Region path [%s] is not valid", regionPath));

			return regions.get(resolvedRegionPath);
		});

		when(mockRegionService.createPdxEnum(anyString(), anyString(), anyInt()))
				.thenThrow(newUnsupportedOperationException(NOT_SUPPORTED));

		when(mockRegionService.createPdxInstanceFactory(anyString()))
				.thenThrow(newUnsupportedOperationException(NOT_SUPPORTED));

		when(mockRegionService.rootRegions()).thenAnswer(invocation ->
				regions.values().stream().filter(GemFireMockObjectsSupport::isRootRegion).collect(Collectors.toSet()));

		return mockRegionService;
	}

	public static ClientCache mockClientCache() {

		ClientCache mockClientCache = mock(ClientCache.class);

		doAnswer(newVoidAnswer(invocation -> mockClientCache.close())).when(mockClientCache).close(anyBoolean());

		when(mockClientCache.createClientRegionFactory(any(ClientRegionShortcut.class))).thenAnswer(invocation ->
				mockClientRegionFactory(mockClientCache, invocation.<ClientRegionShortcut>getArgument(0)));

		when(mockClientCache.createClientRegionFactory(anyString())).thenAnswer(invocation ->
				mockClientRegionFactory(mockClientCache, invocation.<String>getArgument(0)));

		return referTo(mockQueryService(mockCacheApi(mockClientCache)));
	}

	public static ClientCache mockGemFireCache() {

		ClientCache mockGemFireCache = mock(ClientCache.class);

		return referTo(mockQueryService(mockCacheApi(mockGemFireCache)));
	}

	public static CacheTransactionManager mockCacheTransactionManager() {

		CacheTransactionManager mockCacheTransactionManager = mock(CacheTransactionManager.class);

		AtomicBoolean distributed = new AtomicBoolean(false);

		AtomicReference<TransactionWriter> transactionWriter = new AtomicReference<>(null);

		List<TransactionListener> transactionListeners = new CopyOnWriteArrayList<>();

		doReturn(false).when(mockCacheTransactionManager).exists();
		doReturn(false).when(mockCacheTransactionManager).exists(any(TransactionId.class));
		doAnswer(newGetter(distributed)).when(mockCacheTransactionManager).isDistributed();
		doReturn(false).when(mockCacheTransactionManager).isSuspended(any(TransactionId.class));

		doAnswer(invocation -> transactionListeners.add(invocation.getArgument(0)))
				.when(mockCacheTransactionManager).addListener(any(TransactionListener.class));

		doAnswer(invocation -> transactionListeners.toArray(new TransactionListener[0]))
				.when(mockCacheTransactionManager).getListeners();

		doAnswer(newGetter(transactionWriter)).when(mockCacheTransactionManager).getWriter();

		doAnswer(invocation -> {

			TransactionListener[] newTransactionListeners = invocation.getArgument(0);

			transactionListeners.forEach(CacheCallback::close);
			transactionListeners.clear();

			Collections.addAll(transactionListeners, newTransactionListeners);

			return null;

		}).when(mockCacheTransactionManager).initListeners(any(TransactionListener[].class));

		doAnswer(invocation -> transactionListeners.remove(invocation.getArgument(0)))
				.when(mockCacheTransactionManager).removeListener(any(TransactionListener.class));

		doAnswer(newSetter(distributed, null)).when(mockCacheTransactionManager).setDistributed(anyBoolean());

		doAnswer(newSetter(transactionWriter)).when(mockCacheTransactionManager).setWriter(any(TransactionWriter.class));

		return mockCacheTransactionManager;
	}

	public static <K, V> ClientRegionFactory<K, V> mockClientRegionFactory(ClientCache mockClientCache,
																																				 ClientRegionShortcut clientRegionShortcut) {

		return mockClientRegionFactory(mockClientCache,
				resolveRegionAttributesFromClientRegionShortcut(clientRegionShortcut),
				clientRegionShortcut);
	}

	public static <K, V> ClientRegionFactory<K, V> mockClientRegionFactory(ClientCache mockClientCache,
																																				 String regionAttributesId) {

		return mockClientRegionFactory(mockClientCache, resolveRegionAttributes(regionAttributesId),
				null);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ClientRegionFactory<K, V> mockClientRegionFactory(ClientCache mockClientCache,
																																				 RegionAttributes<K, V> regionAttributes, ClientRegionShortcut clientRegionShortcut) {

		ClientRegionFactory<K, V> mockClientRegionFactory =
				mock(ClientRegionFactory.class, mockObjectIdentifier("MockClientRegionFactory"));

		ExpirationAttributes DEFAULT_EXPIRATION_ATTRIBUTES =
				new ExpirationAttributes(0, ExpirationAction.INVALIDATE);

		Optional<RegionAttributes<K, V>> optionalRegionAttributes = Optional.ofNullable(regionAttributes);

		AtomicBoolean cloningEnabled = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getCloningEnabled).orElse(false));

		AtomicBoolean concurrencyChecksEnabled = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getConcurrencyChecksEnabled).orElse(false));

		AtomicBoolean diskSynchronous = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::isDiskSynchronous).orElse(true));

		AtomicBoolean statisticsEnabled = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getStatisticsEnabled).orElse(false));

		AtomicInteger concurrencyLevel = new AtomicInteger(optionalRegionAttributes
				.map(RegionAttributes::getConcurrencyLevel).orElse(16));

		AtomicInteger initialCapacity = new AtomicInteger(optionalRegionAttributes
				.map(RegionAttributes::getInitialCapacity).orElse(16));

		AtomicReference<Compressor> compressor = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCompressor).orElse(null));

		AtomicReference<CustomExpiry<K, V>> customEntryIdleTimeout = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCustomEntryIdleTimeout).orElse(null));

		AtomicReference<CustomExpiry<K, V>> customEntryTimeToLive = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCustomEntryTimeToLive).orElse(null));

		AtomicReference<DataPolicy> dataPolicy = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getDataPolicy).orElseGet(() -> convert(clientRegionShortcut)));

		AtomicReference<String> diskStoreName = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getDiskStoreName).orElse(null));

		AtomicReference<ExpirationAttributes> entryIdleTimeout = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getEntryIdleTimeout).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<ExpirationAttributes> entryTimeToLive = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getEntryTimeToLive).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<EvictionAttributes> evictionAttributes = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getEvictionAttributes).orElseGet(EvictionAttributes::createLRUEntryAttributes));

		AtomicReference<Class<K>> keyConstraint = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getKeyConstraint).orElse(null));

		AtomicReference<Float> loadFactor = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getLoadFactor).orElse(0.75f));

		AtomicReference<String> poolName = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getPoolName).orElse(null));

		AtomicReference<ExpirationAttributes> regionIdleTimeout = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getRegionIdleTimeout).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<ExpirationAttributes> regionTimeToLive = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getRegionTimeToLive).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<Class<V>> valueConstraint = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getValueConstraint).orElse(null));

		List<CacheListener> cacheListeners = new ArrayList<>(Arrays.asList(nullSafeArray(optionalRegionAttributes
				.map(RegionAttributes::getCacheListeners).orElse(null), CacheListener.class)));

		when(mockClientRegionFactory.addCacheListener(any(CacheListener.class)))
				.thenAnswer(newAdder(cacheListeners, mockClientRegionFactory));

		when(mockClientRegionFactory.initCacheListeners(any(CacheListener[].class))).thenAnswer(invocation -> {
			cacheListeners.clear();
			Collections.addAll(cacheListeners, invocation.getArgument(0));
			return mockClientRegionFactory;
		});

		when(mockClientRegionFactory.setCloningEnabled(anyBoolean()))
				.thenAnswer(newSetter(cloningEnabled, mockClientRegionFactory));

		when(mockClientRegionFactory.setCompressor(any(Compressor.class)))
				.thenAnswer(newSetter(compressor, () -> mockClientRegionFactory));

		doAnswer(newSetter(concurrencyChecksEnabled, mockClientRegionFactory))
				.when(mockClientRegionFactory).setConcurrencyChecksEnabled(anyBoolean());

		when(mockClientRegionFactory.setConcurrencyLevel(anyInt()))
				.thenAnswer(newSetter(concurrencyLevel, mockClientRegionFactory));

		when(mockClientRegionFactory.setCustomEntryIdleTimeout(any(CustomExpiry.class)))
				.thenAnswer(newSetter(customEntryIdleTimeout, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setCustomEntryTimeToLive(any(CustomExpiry.class)))
				.thenAnswer(newSetter(customEntryTimeToLive, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setDiskStoreName(anyString()))
				.thenAnswer(newSetter(diskStoreName, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setDiskSynchronous(anyBoolean()))
				.thenAnswer(newSetter(diskSynchronous, mockClientRegionFactory));

		when(mockClientRegionFactory.setEntryIdleTimeout(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(entryIdleTimeout, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setEntryTimeToLive(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(entryTimeToLive, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setEvictionAttributes(any(EvictionAttributes.class)))
				.thenAnswer(newSetter(evictionAttributes, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setInitialCapacity(anyInt()))
				.thenAnswer(newSetter(initialCapacity, mockClientRegionFactory));

		when(mockClientRegionFactory.setKeyConstraint(any(Class.class)))
				.thenAnswer(newSetter(keyConstraint, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setLoadFactor(anyFloat()))
				.thenAnswer(newSetter(loadFactor, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setPoolName(anyString()))
				.thenAnswer(newSetter(poolName, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setRegionIdleTimeout(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(regionIdleTimeout, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setRegionTimeToLive(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(regionTimeToLive, () -> mockClientRegionFactory));

		when(mockClientRegionFactory.setStatisticsEnabled(anyBoolean()))
				.thenAnswer(newSetter(statisticsEnabled, mockClientRegionFactory));

		when(mockClientRegionFactory.setValueConstraint(any(Class.class)))
				.thenAnswer(newSetter(valueConstraint, () -> mockClientRegionFactory));

		RegionAttributes<K, V> mockRegionAttributes =
				mock(RegionAttributes.class, mockObjectIdentifier("MockRegionAttributes"));

		when(mockRegionAttributes.getCacheListeners())
				.thenAnswer(newGetter(() -> cacheListeners.toArray(new CacheListener[cacheListeners.size()])));

		when(mockRegionAttributes.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled));
		when(mockRegionAttributes.getCompressor()).thenAnswer(newGetter(compressor));
		when(mockRegionAttributes.getConcurrencyChecksEnabled()).thenAnswer(newGetter(concurrencyChecksEnabled));
		when(mockRegionAttributes.getConcurrencyLevel()).thenAnswer(newGetter(concurrencyLevel));
		when(mockRegionAttributes.getCustomEntryIdleTimeout()).thenAnswer(newGetter(customEntryIdleTimeout));
		when(mockRegionAttributes.getCustomEntryTimeToLive()).thenAnswer(newGetter(customEntryTimeToLive));
		when(mockRegionAttributes.getDataPolicy()).thenAnswer(newGetter(dataPolicy));
		when(mockRegionAttributes.getDiskStoreName()).thenAnswer(newGetter(diskStoreName));
		when(mockRegionAttributes.isDiskSynchronous()).thenAnswer(newGetter(diskSynchronous));
		when(mockRegionAttributes.getEntryIdleTimeout()).thenAnswer(newGetter(entryIdleTimeout));
		when(mockRegionAttributes.getEntryTimeToLive()).thenAnswer(newGetter(entryTimeToLive));
		when(mockRegionAttributes.getEvictionAttributes()).thenAnswer(newGetter(evictionAttributes));
		when(mockRegionAttributes.getInitialCapacity()).thenAnswer(newGetter(initialCapacity));
		when(mockRegionAttributes.getKeyConstraint()).thenAnswer(newGetter(keyConstraint));
		when(mockRegionAttributes.getLoadFactor()).thenAnswer(newGetter(loadFactor));
		when(mockRegionAttributes.getPoolName()).thenAnswer(newGetter(poolName));
		when(mockRegionAttributes.getRegionIdleTimeout()).thenAnswer(newGetter(regionIdleTimeout));
		when(mockRegionAttributes.getRegionTimeToLive()).thenAnswer(newGetter(regionTimeToLive));
		when(mockRegionAttributes.getStatisticsEnabled()).thenAnswer(newGetter(statisticsEnabled));
		when(mockRegionAttributes.getValueConstraint()).thenAnswer(newGetter(valueConstraint));

		when(mockClientRegionFactory.create(anyString())).thenAnswer(invocation ->
				mockRegion(mockClientCache, invocation.getArgument(0), mockRegionAttributes));

		when(mockClientRegionFactory.createSubregion(any(Region.class), anyString())).thenAnswer(invocation ->
				mockSubRegion(invocation.getArgument(0), invocation.getArgument(1), mockRegionAttributes));

		return mockClientRegionFactory;
	}

	public static ClientSubscriptionConfig mockClientSubscriptionConfig() {

		ClientSubscriptionConfig mockClientSubscriptionConfig = mock(ClientSubscriptionConfig.class);

		AtomicInteger subscriptionCapacity = new AtomicInteger(ClientSubscriptionConfig.DEFAULT_CAPACITY);

		AtomicReference<String> subscriptionDiskStoreName = new AtomicReference<>("");

		AtomicReference<SubscriptionEvictionPolicy> subscriptionEvictionPolicy =
				new AtomicReference<>(SubscriptionEvictionPolicy.DEFAULT);

		Function<String, SubscriptionEvictionPolicy> stringToSubscriptionEvictionPolicyConverter =
				arg -> SubscriptionEvictionPolicy.valueOfIgnoreCase(String.valueOf(arg));

		Function<SubscriptionEvictionPolicy, String> subscriptionEvictionPolicyToStringConverter =
				arg -> Optional.ofNullable(arg).map(Object::toString).map(String::toLowerCase).orElse(null);

		doAnswer(newSetter(subscriptionCapacity, null))
				.when(mockClientSubscriptionConfig).setCapacity(anyInt());

		doAnswer(newSetter(subscriptionDiskStoreName, () -> null))
				.when(mockClientSubscriptionConfig).setDiskStoreName(anyString());

		doAnswer(newSetter(subscriptionEvictionPolicy, stringToSubscriptionEvictionPolicyConverter, () -> null))
				.when(mockClientSubscriptionConfig).setEvictionPolicy(anyString());

		when(mockClientSubscriptionConfig.getCapacity()).thenAnswer(newGetter(subscriptionCapacity));
		when(mockClientSubscriptionConfig.getDiskStoreName()).thenAnswer(newGetter(subscriptionDiskStoreName));
		when(mockClientSubscriptionConfig.getEvictionPolicy()).thenAnswer(newGetter(subscriptionEvictionPolicy,
				subscriptionEvictionPolicyToStringConverter));

		return mockClientSubscriptionConfig;
	}

	public static DiskStoreFactory mockDiskStoreFactory() {

		DiskStoreFactory mockDiskStoreFactory = mock(DiskStoreFactory.class);

		AtomicBoolean allowForceCompaction = new AtomicBoolean(DiskStoreFactory.DEFAULT_ALLOW_FORCE_COMPACTION);
		AtomicBoolean autoCompact = new AtomicBoolean(DiskStoreFactory.DEFAULT_AUTO_COMPACT);

		AtomicInteger compactionThreshold = new AtomicInteger(DiskStoreFactory.DEFAULT_COMPACTION_THRESHOLD);
		AtomicInteger queueSize = new AtomicInteger(DiskStoreFactory.DEFAULT_QUEUE_SIZE);
		AtomicInteger writeBufferSize = new AtomicInteger(DiskStoreFactory.DEFAULT_WRITE_BUFFER_SIZE);
		AtomicInteger segments = new AtomicInteger(DiskStoreFactory.DEFAULT_SEGMENTS);

		AtomicLong maxOplogSize = new AtomicLong(DiskStoreFactory.DEFAULT_MAX_OPLOG_SIZE);
		AtomicLong timeInterval = new AtomicLong(DiskStoreFactory.DEFAULT_TIME_INTERVAL);

		AtomicReference<File[]> diskDirectories =
				new AtomicReference<>(new File[] { FileSystemUtils.WORKING_DIRECTORY });

		AtomicReference<int[]> diskDiretorySizes = new AtomicReference<>(new int[0]);

		AtomicReference<Float> diskUsageCriticalPercentage =
				new AtomicReference<>(DiskStoreFactory.DEFAULT_DISK_USAGE_CRITICAL_PERCENTAGE);

		AtomicReference<Float> diskUsageWarningPercentage =
				new AtomicReference<>(DiskStoreFactory.DEFAULT_DISK_USAGE_WARNING_PERCENTAGE);

		when(mockDiskStoreFactory.setAllowForceCompaction(anyBoolean()))
				.thenAnswer(newSetter(allowForceCompaction, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setAutoCompact(anyBoolean()))
				.thenAnswer(newSetter(autoCompact, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setCompactionThreshold(anyInt()))
				.thenAnswer(newSetter(compactionThreshold, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setDiskDirs(any(File[].class))).thenAnswer(invocation -> {

			File[] resolveDiskDirectories = nullSafeArray(invocation.getArgument(0), File.class);

			int[] resolvedDiskDirectorySizes = new int[resolveDiskDirectories.length];

			Arrays.fill(resolvedDiskDirectorySizes, DiskStoreFactory.DEFAULT_DISK_DIR_SIZE);

			diskDirectories.set(resolveDiskDirectories);
			diskDiretorySizes.set(resolvedDiskDirectorySizes);

			return mockDiskStoreFactory;
		});

		when(mockDiskStoreFactory.setDiskDirsAndSizes(any(File[].class), any(int[].class))).thenAnswer(invocation -> {

			diskDirectories.set(invocation.getArgument(0));
			diskDiretorySizes.set(invocation.getArgument(1));

			return mockDiskStoreFactory;
		});

		when(mockDiskStoreFactory.setDiskUsageCriticalPercentage(anyFloat()))
				.thenAnswer(newSetter(diskUsageCriticalPercentage, () -> mockDiskStoreFactory));

		when(mockDiskStoreFactory.setDiskUsageWarningPercentage(anyFloat()))
				.thenAnswer(newSetter(diskUsageWarningPercentage, () -> mockDiskStoreFactory));

		when(mockDiskStoreFactory.setMaxOplogSize(anyLong()))
				.thenAnswer(newSetter(maxOplogSize, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setQueueSize(anyInt()))
				.thenAnswer(newSetter(queueSize, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setTimeInterval(anyLong()))
				.thenAnswer(newSetter(timeInterval, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setWriteBufferSize(anyInt()))
				.thenAnswer(newSetter(writeBufferSize, mockDiskStoreFactory));

		when(mockDiskStoreFactory.setSegments(anyInt()))
				.thenAnswer(newSetter(segments, mockDiskStoreFactory));

		when(mockDiskStoreFactory.create(anyString())).thenAnswer(invocation -> {

			String name = invocation.getArgument(0);

			DiskStore mockDiskStore = mock(DiskStore.class, name);

			when(mockDiskStore.getAllowForceCompaction()).thenReturn(allowForceCompaction.get());
			when(mockDiskStore.getAutoCompact()).thenReturn(autoCompact.get());
			when(mockDiskStore.getCompactionThreshold()).thenReturn(compactionThreshold.get());
			when(mockDiskStore.getDiskDirs()).thenReturn(diskDirectories.get());
			when(mockDiskStore.getDiskDirSizes()).thenReturn(diskDiretorySizes.get());
			when(mockDiskStore.getDiskUsageCriticalPercentage()).thenReturn(diskUsageCriticalPercentage.get());
			when(mockDiskStore.getDiskUsageWarningPercentage()).thenReturn(diskUsageWarningPercentage.get());
			when(mockDiskStore.getDiskStoreUUID()).thenReturn(UUID.randomUUID());
			when(mockDiskStore.getMaxOplogSize()).thenReturn(maxOplogSize.get());
			when(mockDiskStore.getName()).thenReturn(name);
			when(mockDiskStore.getQueueSize()).thenReturn(queueSize.get());
			when(mockDiskStore.getTimeInterval()).thenReturn(timeInterval.get());
			when(mockDiskStore.getWriteBufferSize()).thenReturn(writeBufferSize.get());
			when(mockDiskStore.getSegments()).thenReturn(segments.get());

			diskStores.put(name, mockDiskStore);

			return mockDiskStore;
		});

		return mockDiskStoreFactory;
	}

	public static DistributedMember mockDistributedMember() {

		DistributedMember mockDistributedMember = mock(DistributedMember.class);

		when(mockDistributedMember.getGroups()).thenAnswer(invocation ->
				new ArrayList<>(StringUtils.commaDelimitedListToSet(gemfireProperties.get()
						.getProperty(DistributionConfig.GROUPS_NAME))));

		when(mockDistributedMember.getHost())
				.thenReturn(ObjectUtils.doOperationSafely(() -> InetAddress.getLocalHost().getHostName(), null));

		when(mockDistributedMember.getName()).thenAnswer(invocation -> gemfireProperties.get()
				.getProperty(DistributionConfig.NAME_NAME));

		return mockDistributedMember;
	}

	public static DistributedSystem mockDistributedSystem() {

		DistributedMember mockDistributedMember = mockDistributedMember();

		DistributedSystem mockDistributedSystem = mock(DistributedSystem.class);

		doAnswer(invocation -> gemfireProperties.get().getProperty(DistributionConfig.NAME_NAME))
				.when(mockDistributedSystem).getName();

		when(mockDistributedSystem.getDistributedMember()).thenReturn(mockDistributedMember);
		when(mockDistributedSystem.getProperties()).thenAnswer(invocation -> gemfireProperties.get());
		when(mockDistributedSystem.getReconnectedSystem()).thenAnswer(invocation -> mockDistributedSystem());

		return mockDistributedSystem;
	}

	public static PoolFactory mockPoolFactory() {

		PoolFactory mockPoolFactory = mock(PoolFactory.class);

		AtomicBoolean multiuserAuthentication = new AtomicBoolean(PoolFactory.DEFAULT_MULTIUSER_AUTHENTICATION);
		AtomicBoolean prSingleHopEnabled = new AtomicBoolean(PoolFactory.DEFAULT_PR_SINGLE_HOP_ENABLED);
		AtomicBoolean subscriptionEnabled = new AtomicBoolean(PoolFactory.DEFAULT_SUBSCRIPTION_ENABLED);
//		AtomicBoolean threadLocalConnections = new AtomicBoolean(PoolFactory.DEFAULT_THREAD_LOCAL_CONNECTIONS);

		AtomicInteger freeConnectionTimeout = new AtomicInteger(PoolFactory.DEFAULT_FREE_CONNECTION_TIMEOUT);
		AtomicInteger loadConditioningInterval = new AtomicInteger(PoolFactory.DEFAULT_LOAD_CONDITIONING_INTERVAL);
		AtomicInteger maxConnections = new AtomicInteger(PoolFactory.DEFAULT_MAX_CONNECTIONS);
		AtomicInteger minConnections = new AtomicInteger(PoolFactory.DEFAULT_MIN_CONNECTIONS);
		AtomicInteger maxConnectionsPerServer = new AtomicInteger(PoolFactory.DEFAULT_MAX_CONNECTIONS_PER_SERVER);
		AtomicInteger minConnectionsPerServer = new AtomicInteger(PoolFactory.DEFAULT_MIN_CONNECTIONS_PER_SERVER);
		AtomicInteger readTimeout = new AtomicInteger(PoolFactory.DEFAULT_READ_TIMEOUT);
		AtomicInteger retryAttempts = new AtomicInteger(PoolFactory.DEFAULT_RETRY_ATTEMPTS);
		AtomicInteger serverConnectionTimeout = new AtomicInteger(PoolFactory.DEFAULT_SERVER_CONNECTION_TIMEOUT);
		AtomicInteger socketBufferSize = new AtomicInteger(PoolFactory.DEFAULT_SOCKET_BUFFER_SIZE);
		AtomicInteger socketConnectTimeout = new AtomicInteger(PoolFactory.DEFAULT_SOCKET_CONNECT_TIMEOUT);
		AtomicInteger statisticInterval = new AtomicInteger(PoolFactory.DEFAULT_STATISTIC_INTERVAL);
		AtomicInteger subscriptionAckInterval = new AtomicInteger(PoolFactory.DEFAULT_SUBSCRIPTION_ACK_INTERVAL);
		AtomicInteger subscriptionMessageTrackingTimeout = new AtomicInteger(PoolFactory.DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT);
		AtomicInteger subscriptionRedundancy = new AtomicInteger(PoolFactory.DEFAULT_SUBSCRIPTION_REDUNDANCY);

		AtomicLong idleTimeout = new AtomicLong(PoolFactory.DEFAULT_IDLE_TIMEOUT);
		AtomicLong pingInterval = new AtomicLong(PoolFactory.DEFAULT_PING_INTERVAL);

		AtomicReference<SocketFactory> socketFactory = new AtomicReference<>(PoolFactory.DEFAULT_SOCKET_FACTORY);
		AtomicReference<String> serverGroup = new AtomicReference<>(PoolFactory.DEFAULT_SERVER_GROUP);

		List<InetSocketAddress> locators = new ArrayList<>();
		List<InetSocketAddress> servers = new ArrayList<>();

		when(mockPoolFactory.addLocator(anyString(), anyInt())).thenAnswer(invocation -> {
			locators.add(new InetSocketAddress(invocation.<String>getArgument(0), invocation.getArgument(1)));
			return mockPoolFactory;
		});

		when(mockPoolFactory.addServer(anyString(), anyInt())).thenAnswer(invocation -> {
			servers.add(new InetSocketAddress(invocation.<String>getArgument(0), invocation.getArgument(1)));
			return mockPoolFactory;
		});

		when(mockPoolFactory.setFreeConnectionTimeout(anyInt()))
				.thenAnswer(newSetter(freeConnectionTimeout, mockPoolFactory));

		when(mockPoolFactory.setIdleTimeout(anyLong()))
				.thenAnswer(newSetter(idleTimeout, mockPoolFactory));

		when(mockPoolFactory.setLoadConditioningInterval(anyInt()))
				.thenAnswer(newSetter(loadConditioningInterval, mockPoolFactory));

		when(mockPoolFactory.setMaxConnections(anyInt()))
				.thenAnswer(newSetter(maxConnections, mockPoolFactory));

		when(mockPoolFactory.setMinConnections(anyInt()))
				.thenAnswer(newSetter(minConnections, mockPoolFactory));

		when(mockPoolFactory.setMaxConnectionsPerServer(anyInt()))
				.thenAnswer(newSetter(maxConnectionsPerServer, mockPoolFactory));

		when(mockPoolFactory.setMinConnectionsPerServer(anyInt()))
				.thenAnswer(newSetter(minConnectionsPerServer, mockPoolFactory));

		when(mockPoolFactory.setMultiuserAuthentication(anyBoolean()))
				.thenAnswer(newSetter(multiuserAuthentication, mockPoolFactory));

		when(mockPoolFactory.setPingInterval(anyLong()))
				.thenAnswer(newSetter(pingInterval, mockPoolFactory));

		when(mockPoolFactory.setPRSingleHopEnabled(anyBoolean()))
				.thenAnswer(newSetter(prSingleHopEnabled, mockPoolFactory));

		when(mockPoolFactory.setReadTimeout(anyInt()))
				.thenAnswer(newSetter(readTimeout, mockPoolFactory));

		when(mockPoolFactory.setRetryAttempts(anyInt()))
				.thenAnswer(newSetter(retryAttempts, mockPoolFactory));

		when(mockPoolFactory.setServerConnectionTimeout(anyInt()))
				.thenAnswer(newSetter(serverConnectionTimeout, mockPoolFactory));

		when(mockPoolFactory.setServerGroup(anyString()))
				.thenAnswer(newSetter(serverGroup, () -> mockPoolFactory));

		when(mockPoolFactory.setSocketBufferSize(anyInt()))
				.thenAnswer(newSetter(socketBufferSize, mockPoolFactory));

		when(mockPoolFactory.setSocketConnectTimeout(anyInt()))
				.thenAnswer(newSetter(socketConnectTimeout, mockPoolFactory));

		when(mockPoolFactory.setSocketFactory(any(SocketFactory.class)))
				.thenAnswer(newSetter(socketFactory, () -> mockPoolFactory));

		when(mockPoolFactory.setStatisticInterval(anyInt()))
				.thenAnswer(newSetter(statisticInterval, mockPoolFactory));

		when(mockPoolFactory.setSubscriptionAckInterval(anyInt()))
				.thenAnswer(newSetter(subscriptionAckInterval, mockPoolFactory));

		when(mockPoolFactory.setSubscriptionEnabled(anyBoolean()))
				.thenAnswer(newSetter(subscriptionEnabled, mockPoolFactory));

		when(mockPoolFactory.setSubscriptionMessageTrackingTimeout(anyInt()))
				.thenAnswer(newSetter(subscriptionMessageTrackingTimeout, mockPoolFactory));

		when(mockPoolFactory.setSubscriptionRedundancy(anyInt()))
				.thenAnswer(newSetter(subscriptionRedundancy, mockPoolFactory));

//		when(mockPoolFactory.setThreadLocalConnections(anyBoolean()))
//			.thenAnswer(newSetter(threadLocalConnections, mockPoolFactory));

		when(mockPoolFactory.create(anyString())).thenAnswer(invocation -> {

			String name = invocation.getArgument(0);

			Pool mockPool = mock(Pool.class, name);

			AtomicReference<QueryService> queryService = new AtomicReference<>(null);

			AtomicBoolean destroyed = new AtomicBoolean(false);

			doAnswer(invocationOnMock -> {
				destroyed.set(true);
				return null;
			}).when(mockPool).destroy();

			doAnswer(invocationOnMock -> {
				destroyed.set(true);
				return null;
			}).when(mockPool).destroy(anyBoolean());

			when(mockPool.isDestroyed()).thenAnswer(newGetter(destroyed));
			when(mockPool.getFreeConnectionTimeout()).thenReturn(freeConnectionTimeout.get());
			when(mockPool.getIdleTimeout()).thenReturn(idleTimeout.get());
			when(mockPool.getLoadConditioningInterval()).thenReturn(loadConditioningInterval.get());
			when(mockPool.getLocators()).thenReturn(locators);
			when(mockPool.getMaxConnections()).thenReturn(maxConnections.get());
			when(mockPool.getMinConnections()).thenReturn(minConnections.get());
			when(mockPool.getMaxConnectionsPerServer()).thenReturn(maxConnectionsPerServer.get());
			when(mockPool.getMinConnectionsPerServer()).thenReturn(minConnectionsPerServer.get());
			when(mockPool.getMultiuserAuthentication()).thenReturn(multiuserAuthentication.get());
			when(mockPool.getName()).thenReturn(name);
			when(mockPool.getPingInterval()).thenReturn(pingInterval.get());
			when(mockPool.getPRSingleHopEnabled()).thenReturn(prSingleHopEnabled.get());
			when(mockPool.getReadTimeout()).thenReturn(readTimeout.get());
			when(mockPool.getRetryAttempts()).thenReturn(retryAttempts.get());
			when(mockPool.getServerConnectionTimeout()).thenReturn(serverConnectionTimeout.get());
			when(mockPool.getServerGroup()).thenReturn(serverGroup.get());
			when(mockPool.getServers()).thenReturn(servers);
			when(mockPool.getSocketBufferSize()).thenReturn(socketBufferSize.get());
			when(mockPool.getSocketConnectTimeout()).thenReturn(socketConnectTimeout.get());
			when(mockPool.getSocketFactory()).thenReturn(socketFactory.get());
			when(mockPool.getStatisticInterval()).thenReturn(statisticInterval.get());
			when(mockPool.getSubscriptionAckInterval()).thenReturn(subscriptionAckInterval.get());
			when(mockPool.getSubscriptionEnabled()).thenReturn(subscriptionEnabled.get());
			when(mockPool.getSubscriptionMessageTrackingTimeout()).thenReturn(subscriptionMessageTrackingTimeout.get());
			when(mockPool.getSubscriptionRedundancy()).thenReturn(subscriptionRedundancy.get());
//			when(mockPool.getThreadLocalConnections()).thenReturn(threadLocalConnections.get());

			doAnswer(getQueryServiceInvocation ->
					resolveAnyGemFireCache()
							.map(ClientCache::getQueryService)
							.orElseGet(() -> queryService.updateAndGet(it -> it != null ? it : mockQueryService()))
			).when(mockPool).getQueryService();

			register(mockPool);

			return mockPool;
		});

		return mockPoolFactory;
	}

	private static Pool register(Pool pool) {

		if (registeredPoolNames.add(pool.getName())) {
			PoolManagerImpl.getPMI().register(pool);
		}

		return pool;
	}

	private static Pool unregister(Pool pool) {

		PoolManagerImpl.getPMI().unregister(pool);

		return pool;
	}

	public static Pool mockQueryService(Pool pool) {

		QueryService mockQueryService = mockQueryService();

		when(pool.getQueryService()).thenReturn(mockQueryService);

		return pool;
	}

	public static <T extends RegionService> T mockQueryService(T regionService) {

		QueryService mockQueryService = mockQueryService();

		doReturn(mockQueryService).when(regionService).getQueryService();

		if (regionService instanceof ClientCache) {
			doReturn(mockQueryService).when((ClientCache) regionService).getLocalQueryService();
			doReturn(mockQueryService).when((ClientCache) regionService).getQueryService(anyString());
		}

		return regionService;
	}

	// TODO: write additional mocking logic for the QueryService interface
	public static QueryService mockQueryService() {

		QueryService mockQueryService = mock(QueryService.class);

		Set<CqQuery> cqQueries = Collections.synchronizedSet(new HashSet<>());
		Set<Index> indexes = Collections.synchronizedSet(new HashSet<>());

		try {

			when(mockQueryService.getCq(anyString())).thenAnswer(invocation ->
					cqQueries.stream().filter(cqQuery -> invocation.getArgument(0).equals(cqQuery.getName()))
							.findFirst().orElse(null));

			when(mockQueryService.getCqs()).thenAnswer(invocation -> cqQueries.toArray(new CqQuery[cqQueries.size()]));

			when(mockQueryService.getCqs(anyString())).thenAnswer(invocation -> {

				List<CqQuery> cqQueriesByRegion = cqQueries.stream().filter(cqQuery -> {

					String queryString = cqQuery.getQueryString();

					int indexOfFromClause = queryString.indexOf(FROM_KEYWORD);
					int indexOfWhereClause = queryString.indexOf(WHERE_KEYWORD);

					queryString = (indexOfFromClause > -1
							? queryString.substring(indexOfFromClause + FROM_KEYWORD.length()) : queryString);

					queryString = (indexOfWhereClause > 0 ? queryString.substring(0, indexOfWhereClause) : queryString);

					queryString = (queryString.startsWith(Region.SEPARATOR) ? queryString.substring(1) : queryString);

					return invocation.getArgument(0).equals(queryString.trim());

				}).collect(Collectors.toList());

				return cqQueriesByRegion.toArray(new CqQuery[cqQueriesByRegion.size()]);
			});

			when(mockQueryService.getIndex(any(Region.class), anyString())).thenAnswer(invocation -> {

				Region<?, ?> region = invocation.getArgument(0);

				String indexName = invocation.getArgument(1);

				Collection<Index> indexesForRegion = mockQueryService.getIndexes(region);

				return indexesForRegion.stream()
						.filter(index -> index.getName().equals(indexName))
						.findFirst().orElse(null);

			});

			when(mockQueryService.getIndexes()).thenReturn(indexes);

			when(mockQueryService.getIndexes(any(Region.class))).thenAnswer(invocation -> {

				Region<?, ?> region = invocation.getArgument(0);

				return indexes.stream()
						.filter(index -> index.getRegion().equals(region))
						.collect(Collectors.toList());

			});

			when(mockQueryService.newCq(anyString(), any(CqAttributes.class))).thenAnswer(invocation ->
					add(cqQueries, mockCqQuery(null, invocation.getArgument(0), invocation.getArgument(1),
							false)));

			when(mockQueryService.newCq(anyString(), any(CqAttributes.class), anyBoolean())).thenAnswer(invocation ->
					add(cqQueries, mockCqQuery(null, invocation.getArgument(0), invocation.getArgument(1),
							invocation.getArgument(2))));

			when(mockQueryService.newCq(anyString(), anyString(), any(CqAttributes.class))).thenAnswer(invocation ->
					add(cqQueries, mockCqQuery(invocation.getArgument(0), invocation.getArgument(1),
							invocation.getArgument(2), false)));

			when(mockQueryService.newCq(anyString(), anyString(), any(CqAttributes.class), anyBoolean()))
					.thenAnswer(invocation -> add(cqQueries, mockCqQuery(invocation.getArgument(0),
							invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3))));

			when(mockQueryService.newQuery(anyString()))
					.thenAnswer(invocation -> mockQuery(invocation.getArgument(0)));

		}
		catch (Exception cause) {
			throw new MockObjectInvocationException(cause);
		}

		return mockQueryService;
	}

	private static CqQuery add(Collection<CqQuery> cqQueries, CqQuery cqQuery) {

		cqQueries.add(cqQuery);

		return cqQuery;
	}

	private static Index add(Collection<Index> indexes, Index index) {

		indexes.add(index);

		return index;
	}

	private static CqQuery mockCqQuery(String name, String queryString, CqAttributes cqAttributes, boolean durable) {

		CqQuery mockCqQuery = mock(CqQuery.class);

		Query mockQuery = mockQuery(queryString);

		AtomicBoolean closed = new AtomicBoolean(false);
		AtomicBoolean running = new AtomicBoolean(false);
		AtomicBoolean stopped = new AtomicBoolean(true);

		when(mockCqQuery.getCqAttributes()).thenReturn(cqAttributes);
		when(mockCqQuery.getName()).thenReturn(name);
		when(mockCqQuery.getQuery()).thenReturn(mockQuery);
		when(mockCqQuery.getQueryString()).thenReturn(queryString);

		try {
			doAnswer(newSetter(closed, true, null)).when(mockCqQuery).close();

			doAnswer(invocation -> {

				running.set(true);
				stopped.set(false);

				return null;

			}).when(mockCqQuery).execute();

			doAnswer(invocation -> {

				running.set(false);
				stopped.set(true);

				return null;

			}).when(mockCqQuery).stop();
		}
		catch (Exception cause) {
			throw new MockObjectInvocationException(cause);
		}

		when(mockCqQuery.isClosed()).thenAnswer(newGetter(closed));
		when(mockCqQuery.isDurable()).thenReturn(durable);
		when(mockCqQuery.isRunning()).thenAnswer(newGetter(running));
		when(mockCqQuery.isStopped()).thenAnswer(newGetter(stopped));

		return mockCqQuery;
	}

	private static Query mockQuery(String queryString) {

		Query mockQuery = mock(Query.class);

		QueryStatistics mockQueryStatistics = mockQueryStatistics(mockQuery);

		SelectResults<?> mockSelectResults = mockSelectResults();

		doReturn(queryString).when(mockQuery).getQueryString();
		doReturn(mockQueryStatistics).when(mockQuery).getStatistics();

		try {
			doReturn(mockSelectResults).when(mockQuery).execute();
			doReturn(mockSelectResults).when(mockQuery).execute(any(Object.class));
			doReturn(mockSelectResults).when(mockQuery).execute(any(RegionFunctionContext.class));
			doReturn(mockSelectResults).when(mockQuery).execute(any(RegionFunctionContext.class), any());
		}
		catch (Throwable cause) {
			throw new MockObjectInvocationException(cause);
		}

		return mockQuery;
	}

	private static QueryStatistics mockQueryStatistics(Query query) {

		QueryStatistics mockQueryStatistics = mock(QueryStatistics.class);

		AtomicLong numberOfExecutions = new AtomicLong(0L);

		Answer<Object> executeAnswer = invocation -> {
			numberOfExecutions.incrementAndGet();
			return null;
		};

		try {
			when(query.execute()).thenAnswer(executeAnswer);
			when(query.execute(any(Object[].class))).thenAnswer(executeAnswer);
			when(query.execute(any(RegionFunctionContext.class))).thenAnswer(executeAnswer);
			when(query.execute(any(RegionFunctionContext.class), any(Object[].class))).thenAnswer(executeAnswer);
		}
		catch (Exception cause) {
			throw new MockObjectInvocationException(cause);
		}

		when(mockQueryStatistics.getNumExecutions()).thenAnswer(newGetter(numberOfExecutions));
		when(mockQueryStatistics.getTotalExecutionTime()).thenReturn(0L);

		return mockQueryStatistics;
	}

	@SuppressWarnings("unchecked")
	private static <T> SelectResults<T> mockSelectResults() {

		ObjectType mockObjectType = mock(ObjectType.class, withSettings().lenient());

		doReturn(Object.class.getSimpleName()).when(mockObjectType).getSimpleClassName();
		doReturn(false).when(mockObjectType).isCollectionType();
		doReturn(false).when(mockObjectType).isMapType();
		doReturn(false).when(mockObjectType).isStructType();
		doReturn(Object.class).when(mockObjectType).resolveClass();

		CollectionType mockCollectionType = mock(CollectionType.class, withSettings().lenient());

		doReturn(false).when(mockCollectionType).allowsDuplicates();
		doReturn(mockObjectType).when(mockCollectionType).getElementType();
		doReturn(false).when(mockCollectionType).isOrdered();

		SelectResults<T> mockSelectResults = mock(SelectResults.class, withSettings().lenient());

		doReturn(Collections.emptyList()).when(mockSelectResults).asList();
		doReturn(Collections.emptySet()).when(mockSelectResults).asSet();
		doReturn(mockCollectionType).when(mockSelectResults).getCollectionType();
		doReturn(false).when(mockSelectResults).isModifiable();
		doReturn(Collections.emptyIterator()).when(mockSelectResults).iterator();
		doReturn(0).when(mockSelectResults).occurrences(any());
		doNothing().when(mockSelectResults).setElementType(any(ObjectType.class));

		return mockSelectResults;
	}

	private static String fromClauseToRegionPath(String fromClause) {

		String regionName = String.valueOf(fromClause);

		int indexOfDot = regionName.indexOf(".");
		int indexOfSpace = regionName.indexOf(" ");

		regionName = regionName.startsWith(Region.SEPARATOR) ? regionName : GemfireUtils.toRegionPath(regionName);
		regionName = indexOfSpace > -1 ? regionName.substring(0, indexOfSpace) : regionName;
		regionName = indexOfDot > -1 ? regionName.substring(0, indexOfDot) : regionName;

		return regionName;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Region<K, V> mockRegion(RegionService regionService, String name,
																							 RegionAttributes<K, V> regionAttributes) {

		Region<K, V> mockRegion = mock(Region.class, withSettings().name(name).lenient());

		RegionAttributes<K, V> mockRegionAttributes = mockRegionAttributes(mockRegion, regionAttributes);

		Set<Region<?, ?>> subRegions = new CopyOnWriteArraySet<>();

		when(mockRegion.getFullPath()).thenReturn(toRegionPath(name));
		when(mockRegion.getName()).thenReturn(toRegionName(name));
		when(mockRegion.getRegionService()).thenReturn(regionService);

		mockRegionDataAccessOperations(mockRegion, mockRegionAttributes);

		doAnswer(invocation -> {

			String subRegionPath = toRegionPath(invocation.getArgument(0));
			String subRegionFullPath = String.format("%1$s%2$s", mockRegion.getFullPath(), subRegionPath);

			return regions.get(subRegionFullPath);

		}).when(mockRegion).getSubregion(anyString());

		doAnswer(invocation -> {

			boolean recursive = invocation.getArgument(0);

			return recursive
					? subRegions.stream()
					.flatMap(subRegion -> subRegion.subregions(true).stream())
					.collect(Collectors.toSet())
					: subRegions;

		}).when(mockRegion).subregions(anyBoolean());

		return rememberMockedRegion(mockRegion);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> RegionAttributes<K, V> mockRegionAttributes(Region<K, V> mockRegion,
																																		RegionAttributes<K, V> baseRegionAttributes) {

		AttributesMutator<K, V> mockAttributesMutator = mock(AttributesMutator.class, withSettings().lenient());

		EvictionAttributesMutator mockEvictionAttributesMutator =
				mock(EvictionAttributesMutator.class, withSettings().lenient());

		RegionAttributes<K, V> mockRegionAttributes = mock(RegionAttributes.class, withSettings().lenient());

		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegion.getAttributesMutator()).thenReturn(mockAttributesMutator);
		when(mockAttributesMutator.getEvictionAttributesMutator()).thenReturn(mockEvictionAttributesMutator);
		when(mockAttributesMutator.getRegion()).thenReturn(mockRegion);

		AtomicBoolean cloningEnabled = new AtomicBoolean(baseRegionAttributes.getCloningEnabled());

		AtomicInteger evictionMaximum =
				new AtomicInteger(Optional.ofNullable(baseRegionAttributes.getEvictionAttributes())
						.map(EvictionAttributes::getMaximum)
						.orElse(EvictionAttributes.DEFAULT_ENTRIES_MAXIMUM));

		AtomicReference<CacheLoader<K, V>> cacheLoader = new AtomicReference<>(baseRegionAttributes.getCacheLoader());

		AtomicReference<CacheWriter<K, V>> cacheWriter = new AtomicReference<>(baseRegionAttributes.getCacheWriter());

		AtomicReference<CustomExpiry<K, V>> customEntryIdleTimeout =
				new AtomicReference<>(baseRegionAttributes.getCustomEntryIdleTimeout());

		AtomicReference<CustomExpiry<K, V>> customEntryTimeToLive =
				new AtomicReference<>(baseRegionAttributes.getCustomEntryTimeToLive());

		AtomicReference<ExpirationAttributes> entryIdleTimeout =
				new AtomicReference<>(baseRegionAttributes.getEntryIdleTimeout());

		AtomicReference<ExpirationAttributes> entryTimeToLive =
				new AtomicReference<>(baseRegionAttributes.getEntryTimeToLive());

		AtomicReference<ExpirationAttributes> regionIdleTimeout =
				new AtomicReference<>(baseRegionAttributes.getRegionIdleTimeout());

		AtomicReference<ExpirationAttributes> regionTimeToLive =
				new AtomicReference<>(baseRegionAttributes.getRegionTimeToLive());

		List<CacheListener<K, V>> cacheListeners =
				new CopyOnWriteArrayList<>(nullSafeArray(baseRegionAttributes.getCacheListeners(), CacheListener.class));

		doAnswer(newAdder(cacheListeners, null))
				.when(mockAttributesMutator).addCacheListener(any(CacheListener.class));

		when(mockAttributesMutator.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled::get));

		doAnswer(invocation -> {

			CacheListener<K, V>[] cacheListenersArgument =
					nullSafeArray(invocation.getArgument(0), CacheListener.class);

			Arrays.stream(cacheListenersArgument).forEach(it ->
					Assert.notNull(it, "The CacheListener[] must not contain null elements"));

			cacheListeners.forEach(CacheListener::close);
			cacheListeners.addAll(Arrays.asList(cacheListenersArgument));

			return null;

		}).when(mockAttributesMutator).initCacheListeners(any(CacheListener[].class));

		doAnswer(invocation -> cacheListeners.remove(invocation.getArgument(0)))
				.when(mockAttributesMutator).removeCacheListener(any(CacheListener.class));

		doAnswer(newSetter(cacheLoader))
				.when(mockAttributesMutator).setCacheLoader(any(CacheLoader.class));

		doAnswer(newSetter(cacheWriter))
				.when(mockAttributesMutator).setCacheWriter(any(CacheWriter.class));

		doAnswer(newSetter(cloningEnabled, null))
				.when(mockAttributesMutator).setCloningEnabled(anyBoolean());

		doAnswer(newSetter(customEntryIdleTimeout))
				.when(mockAttributesMutator).setCustomEntryIdleTimeout(any(CustomExpiry.class));

		doAnswer(newSetter(customEntryTimeToLive))
				.when(mockAttributesMutator).setCustomEntryTimeToLive(any(CustomExpiry.class));

		doAnswer(newSetter(entryIdleTimeout))
				.when(mockAttributesMutator).setEntryIdleTimeout(any(ExpirationAttributes.class));

		doAnswer(newSetter(entryTimeToLive))
				.when(mockAttributesMutator).setEntryTimeToLive(any(ExpirationAttributes.class));

		doAnswer(newSetter(regionIdleTimeout))
				.when(mockAttributesMutator).setRegionIdleTimeout(any(ExpirationAttributes.class));

		doAnswer(newSetter(regionTimeToLive))
				.when(mockAttributesMutator).setRegionTimeToLive(any(ExpirationAttributes.class));

		// Mock EvictionAttributesMutator
		doAnswer(newSetter(evictionMaximum, null))
				.when(mockEvictionAttributesMutator).setMaximum(anyInt());

		// Mock RegionAttributes
		when(mockRegionAttributes.getCacheListeners())
				.thenAnswer(invocation -> cacheListeners.toArray(new CacheListener[cacheListeners.size()]));

		when(mockRegionAttributes.getCacheLoader()).thenAnswer(newGetter(cacheLoader::get));
		when(mockRegionAttributes.getCacheWriter()).thenAnswer(newGetter(cacheWriter::get));
		when(mockRegionAttributes.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled::get));
		when(mockRegionAttributes.getCompressor()).thenAnswer(newGetter(baseRegionAttributes::getCompressor));
		when(mockRegionAttributes.getConcurrencyChecksEnabled()).thenAnswer(newGetter(baseRegionAttributes::getConcurrencyChecksEnabled));
		when(mockRegionAttributes.getConcurrencyLevel()).thenAnswer(newGetter(baseRegionAttributes::getConcurrencyLevel));
		when(mockRegionAttributes.getCustomEntryIdleTimeout()).thenAnswer(newGetter(customEntryIdleTimeout::get));
		when(mockRegionAttributes.getCustomEntryTimeToLive()).thenAnswer(newGetter(customEntryTimeToLive::get));
		when(mockRegionAttributes.getDataPolicy()).thenAnswer(newGetter(baseRegionAttributes::getDataPolicy));
		when(mockRegionAttributes.getDiskStoreName()).thenAnswer(newGetter(baseRegionAttributes::getDiskStoreName));
		when(mockRegionAttributes.getEnableSubscriptionConflation()).thenAnswer(newGetter(baseRegionAttributes::getEnableSubscriptionConflation));
		when(mockRegionAttributes.getEntryIdleTimeout()).thenAnswer(newGetter(entryIdleTimeout::get));
		when(mockRegionAttributes.getEntryTimeToLive()).thenAnswer(newGetter(entryTimeToLive::get));

		when(mockRegionAttributes.getEvictionAttributes()).thenAnswer(invocation -> {

			EvictionAttributes mockEvictionAttibutes = mock(EvictionAttributes.class);
			EvictionAttributes regionEvictionAttributes = baseRegionAttributes.getEvictionAttributes();

			when(mockEvictionAttibutes.getAction()).thenAnswer(newGetter(regionEvictionAttributes::getAction));
			when(mockEvictionAttibutes.getAlgorithm()).thenAnswer(newGetter(regionEvictionAttributes::getAlgorithm));
			when(mockEvictionAttibutes.getMaximum()).thenAnswer(newGetter(evictionMaximum));
			when(mockEvictionAttibutes.getObjectSizer()).thenAnswer(newGetter(regionEvictionAttributes::getObjectSizer));

			return mockEvictionAttibutes;
		});

		when(mockRegionAttributes.getInitialCapacity()).thenAnswer(newGetter(baseRegionAttributes::getInitialCapacity));
		when(mockRegionAttributes.getKeyConstraint()).thenAnswer(newGetter(baseRegionAttributes::getKeyConstraint));
		when(mockRegionAttributes.getLoadFactor()).thenAnswer(newGetter(baseRegionAttributes::getLoadFactor));
		when(mockRegionAttributes.getMembershipAttributes()).thenAnswer(newGetter(baseRegionAttributes::getMembershipAttributes));
		when(mockRegionAttributes.getPoolName()).thenAnswer(newGetter(baseRegionAttributes::getPoolName));
		when(mockRegionAttributes.getRegionIdleTimeout()).thenAnswer(newGetter(regionIdleTimeout::get));
		when(mockRegionAttributes.getRegionTimeToLive()).thenAnswer(newGetter(regionTimeToLive::get));
		when(mockRegionAttributes.getScope()).thenAnswer(newGetter(baseRegionAttributes::getScope));
		when(mockRegionAttributes.getStatisticsEnabled()).thenAnswer(newGetter(baseRegionAttributes::getStatisticsEnabled));
		when(mockRegionAttributes.getSubscriptionAttributes()).thenAnswer(newGetter(baseRegionAttributes::getSubscriptionAttributes));
		when(mockRegionAttributes.getValueConstraint()).thenAnswer(newGetter(baseRegionAttributes::getValueConstraint));
		when(mockRegionAttributes.isDiskSynchronous()).thenAnswer(newGetter(baseRegionAttributes::isDiskSynchronous));
		when(mockRegionAttributes.isLockGrantor()).thenAnswer(newGetter(baseRegionAttributes::isLockGrantor));

		return mockRegionAttributes;
	}

	@SuppressWarnings("unchecked")
	private static <K, V> void mockRegionDataAccessOperations(Region<K, V> mockRegion,
																														RegionAttributes<K, V> mockRegionAttributes) {

		Map<K, V> data = new ConcurrentHashMap<>();

		Set<K> invalidatedKeys = new HashSet<>();

		// Map.clear() / Region.clear()
		doAnswer(invocation -> {
			data.clear();
			return null;
		}).when(mockRegion).clear();

		// Map.containsKey(key) / Region.containsKey(key)
		doAnswer(invocation -> data.containsKey(invocation.getArgument(0)))
				.when(mockRegion).containsKey(any());

		// Map.containsValue(value) / Region.containsValue(value)
		doAnswer(invocation -> data.containsValue(invocation.getArgument(0)))
				.when(mockRegion).containsValue(any());

		// Region.containsValueForKey(key)
		// NOTE: This containsValueForKey(..) operation is not atomic
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);

			return !invalidatedKeys.contains(key)
					&& data.containsKey(key)
					&& Objects.nonNull(data.get(key));

		}).when(mockRegion).containsValueForKey(any());

		// Map.forEach(:BiConsumer<K, V>)
		doAnswer(invocation -> {

			BiConsumer<K, V> consumer = invocation.getArgument(0);

			data.forEach(consumer);

			return null;

		}).when(mockRegion).forEach(any(BiConsumer.class));

		// Map.get(key) / Region.get(key)
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);
			V value = invalidatedKeys.contains(key) ? null : data.get(key);

			if (value == null) {

				value = Optional.ofNullable(mockRegionAttributes.getCacheLoader())
						.map(cacheLoader -> {

							LoaderHelper<K, V> mockLoaderHelper = mock(LoaderHelper.class, withSettings().lenient());

							when(mockLoaderHelper.getArgument()).thenReturn(null);
							when(mockLoaderHelper.getKey()).thenReturn(key);
							when(mockLoaderHelper.getRegion()).thenReturn(mockRegion);

							return cacheLoader.load(mockLoaderHelper);

						})
						.map(loadedValue -> {

							data.put(key, loadedValue);
							invalidatedKeys.remove(key);

							return loadedValue;

						})
						.orElse(value);
			}

			return value;

		}).when(mockRegion).get(ArgumentMatchers.<K>any());

		// Region.getAll(:Collection<K>)
		// NOTE: This getAll(..) operation is not atomic
		doAnswer(invocation -> {

			Collection<K> keys = invocation.getArgument(0);

			Map<K, V> result = new HashMap<>(keys.size());

			for (K key : keys) {
				if (key != null) {
					result.put(key, mockRegion.get(key));
				}
			}

			return result;

		}).when(mockRegion).getAll(any(Collection.class));

		// Region.getEntry(key)
		when(mockRegion.getEntry(ArgumentMatchers.<K>any())).thenAnswer(regionGetEntryInvocation ->
				data.entrySet().stream()
						.filter(entry -> entry.getKey().equals(regionGetEntryInvocation.getArgument(0)))
						.findFirst()
						.map(entry -> {

							Map.Entry<K, V> entrySpy = spy(entry);

							doAnswer(entryGetValueInvocation ->
									invalidatedKeys.contains(entry.getKey()) ? null : entry.getValue())
									.when(entrySpy).getValue();

							return entrySpy;

						})
						.orElse(null));

		// Map.getOrDefault(key, defaultValue)
		doAnswer(invocation -> {

			Object key = invocation.getArgument(0);
			Object value = data.get(key);
			Object defaultValue = invocation.getArgument(1);

			return value != null ? value : defaultValue;

		}).when(mockRegion).getOrDefault(any(), any());

		// Region.invalidate(key)
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);

			if (!data.containsKey(key)) {
				throw new EntryNotFoundException(String.format("Entry with key [%s] not found", key));
			}

			if (invalidatedKeys.add(key)) {

				EntryEvent<K, V> mockEntryEvent = mock(EntryEvent.class, withSettings().lenient());

				when(mockEntryEvent.getKey()).thenReturn(key);
				when(mockEntryEvent.getNewValue()).thenReturn(null);
				when(mockEntryEvent.getOldValue()).thenReturn(data.get(key));
				when(mockEntryEvent.getRegion()).thenReturn(mockRegion);

				Arrays.stream(ArrayUtils.nullSafeArray(mockRegionAttributes.getCacheListeners(), CacheListener.class))
						.filter(Objects::nonNull)
						.forEach(cacheListener -> cacheListener.afterInvalidate(mockEntryEvent));
			}

			return null;

		}).when(mockRegion).invalidate(any());

		// Map.isEmpty() / Region.isEmpty()
		doAnswer(invocation -> data.isEmpty()).when(mockRegion).isEmpty();

		// Map.keySet() / Region.keySet()
		doAnswer(invocation -> Collections.unmodifiableSet(data.keySet())).when(mockRegion).keySet();

		// Region.localClear()
		doAnswer(invocation -> {
			mockRegion.clear();
			return null;
		}).when(mockRegion).localClear();

		// Region.localInvalidate(key)
		doAnswer(invocation -> {
			mockRegion.invalidate(invocation.getArgument(0));
			return null;
		}).when(mockRegion).localInvalidate(any());

		// Region.put(key, value)
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);
			V newValue = invocation.getArgument(1);

			Assert.notNull(newValue, "Value is required");

			boolean entryExists = data.containsKey(key);

			EntryEvent<K, V> mockEntryEvent = mock(EntryEvent.class, withSettings().lenient());

			V entryEventValue = invalidatedKeys.contains(key) ? null : data.get(key);

			when(mockEntryEvent.getKey()).thenReturn(key);
			when(mockEntryEvent.getNewValue()).thenReturn(newValue);
			when(mockEntryEvent.getOldValue()).thenReturn(entryEventValue);
			when(mockEntryEvent.getRegion()).thenReturn(mockRegion);

			CacheWriter<K, V> cacheWriter = mockRegionAttributes.getCacheWriter();

			if (cacheWriter != null) {
				try {
					if (entryExists) {
						cacheWriter.beforeUpdate(mockEntryEvent);
					}
					else {
						cacheWriter.beforeCreate(mockEntryEvent);
					}
				}
				catch (Throwable cause) {
					throw new CacheWriterException("Create/Update Error", cause);
				}
			}

			V existingValue = data.put(key, newValue);

			Arrays.stream(ArrayUtils.nullSafeArray(mockRegionAttributes.getCacheListeners(), CacheListener.class))
					.filter(Objects::nonNull)
					.forEach(cacheListener -> {

						if (entryExists) {
							cacheListener.afterUpdate(mockEntryEvent);
						}
						else {
							cacheListener.afterCreate(mockEntryEvent);
						}
					});

			return invalidatedKeys.remove(key) ? null : existingValue;

		}).when(mockRegion).put(any(), any());

		// Map.putAll(:Map<K, V>) / Region.putAll(:Map<K, V>)
		// NOTE: This putAll(..) operation is not atomic
		doAnswer(invocation -> {

			Map<K, V> map = invocation.getArgument(0);

			CollectionUtils.nullSafeMap(map).entrySet()
					.forEach(entry -> mockRegion.put(entry.getKey(), entry.getValue()));

			return null;

		}).when(mockRegion).putAll(any(Map.class));

		// TODO Map.putIfAbsent(key, value) / Region.putIfAbsent(key, value) ???

		// Map.remove(key) / Region.remove(key)
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);

			EntryEvent<K, V> mockEntryEvent = mock(EntryEvent.class, withSettings().lenient());

			V entryEventValue = invalidatedKeys.contains(key) ? null : data.get(key);

			when(mockEntryEvent.getKey()).thenReturn(key);
			when(mockEntryEvent.getNewValue()).thenReturn(null);
			when(mockEntryEvent.getOldValue()).thenReturn(entryEventValue);
			when(mockEntryEvent.getRegion()).thenReturn(mockRegion);

			CacheWriter<K, V> cacheWriter = mockRegionAttributes.getCacheWriter();

			if (cacheWriter != null) {
				try {
					cacheWriter.beforeDestroy(mockEntryEvent);
				}
				catch (Throwable cause) {
					throw new CacheWriterException("Destroy Error", cause);
				}
			}

			V value  = data.remove(key);

			Arrays.stream(ArrayUtils.nullSafeArray(mockRegionAttributes.getCacheListeners(), CacheListener.class))
					.filter(Objects::nonNull)
					.forEach(cacheListener -> cacheListener.afterDestroy(mockEntryEvent));

			return invalidatedKeys.remove(key) ? null : value;

		}).when(mockRegion).remove(any());

		// TODO Map.remove(key, value) / Region.remove(key, value) ???

		// Region.removeAll(:Collection<K>)
		// NOTE: This removeAll(..) implementation is not atomic
		doAnswer(invocation -> {

			Collection<K> keys = invocation.getArgument(0);

			CollectionUtils.nullSafeCollection(keys).stream()
					.filter(Objects::nonNull)
					.forEach(mockRegion::remove);

			return null;

		}).when(mockRegion).removeAll(any(Collection.class));

		// TODO Map.replace(key, value) / Region.replace(key, value) ???
		// TODO Map.replace(key, oldValue, newValue) / Region.replace(key, oldValue, newValue) ???
		// TODO Map.replaceAll(:BiFunction<K, V) ???

		// Region.size()
		doAnswer(invocation -> data.size()).when(mockRegion).size();

		// Map.values() / Region.values()
		doAnswer(invocation -> Collections.unmodifiableCollection(data.values())).when(mockRegion).values();
	}

	public static <K, V> Region<K, V> mockSubRegion(Region<K, V> parent, String name,
																									RegionAttributes<K, V> regionAttributes) {

		String subRegionName = String.format("%1$s%2$s", parent.getFullPath(), toRegionPath(name));

		Region<K, V> mockSubRegion = mockRegion(parent.getRegionService(), subRegionName, regionAttributes);

		doReturn(parent).when(mockSubRegion).getParentRegion();

		parent.subregions(false).add(mockSubRegion);

		return mockSubRegion;
	}

	public static <K, V> RegionFactory<K, V> mockRegionFactory(ClientCache mockCache) {
		return mockRegionFactory(mockCache, null, null);
	}

	public static <K, V> RegionFactory<K, V> mockRegionFactory(ClientCache mockCache,
																														 RegionAttributes<K, V> regionAttributes) {

		return mockRegionFactory(mockCache, regionAttributes, null);
	}

	public static <K, V> RegionFactory<K, V> mockRegionFactory(ClientCache mockCache, RegionShortcut regionShortcut) {
		return mockRegionFactory(mockCache, resolveRegionAttributesFromRegionShortcut(regionShortcut), regionShortcut);
	}

	public static <K, V> RegionFactory<K, V> mockRegionFactory(ClientCache mockCache, String regionAttributesId) {
		return mockRegionFactory(mockCache, resolveRegionAttributes(regionAttributesId), null);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> RegionFactory<K, V> mockRegionFactory(ClientCache mockCache, RegionAttributes<K, V> regionAttributes,
																														 RegionShortcut regionShortcut) {

		RegionFactory<K, V> mockRegionFactory = mock(RegionFactory.class,
				mockObjectIdentifier("MockRegionFactory"));

		Optional<RegionAttributes<K, V>> optionalRegionAttributes = Optional.ofNullable(regionAttributes);

		ExpirationAttributes DEFAULT_EXPIRATION_ATTRIBUTES =
				new ExpirationAttributes(0, ExpirationAction.INVALIDATE);

		AtomicBoolean cloningEnabled = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getCloningEnabled)
				.orElse(false));

		AtomicBoolean concurrencyChecksEnabled = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getConcurrencyChecksEnabled)
				.orElse(true));

		AtomicBoolean diskSynchronous = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::isDiskSynchronous)
				.orElse(true));

		AtomicBoolean enableSubscriptionConflation = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getEnableSubscriptionConflation)
				.orElse(false));

		AtomicBoolean ignoreJta = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getIgnoreJTA)
				.orElse(false));

		AtomicBoolean lockGrantor = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::isLockGrantor)
				.orElse(false));

		AtomicBoolean statisticsEnabled = new AtomicBoolean(optionalRegionAttributes
				.map(RegionAttributes::getStatisticsEnabled)
				.orElse(false));

		AtomicInteger concurrencyLevel = new AtomicInteger(optionalRegionAttributes
				.map(RegionAttributes::getConcurrencyLevel)
				.orElse(16));

		AtomicInteger initialCapacity = new AtomicInteger(optionalRegionAttributes
				.map(RegionAttributes::getInitialCapacity)
				.orElse(16));

		AtomicReference<CacheLoader> cacheLoader = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCacheLoader)
				.orElse(null));

		AtomicReference<CacheWriter> cacheWriter = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCacheWriter)
				.orElse(null));

		AtomicReference<Compressor> compressor = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCompressor)
				.orElse(null));

		AtomicReference<CustomExpiry<K, V>> customEntryIdleTimeout = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCustomEntryIdleTimeout)
				.orElse(null));

		AtomicReference<CustomExpiry<K, V>> customEntryTimeToLive = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getCustomEntryTimeToLive)
				.orElse(null));

		AtomicReference<DataPolicy> dataPolicy = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getDataPolicy)
				.orElseGet(() -> convert(regionShortcut)));

		AtomicReference<String> diskStoreName = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getDiskStoreName)
				.orElse(null));

		AtomicReference<ExpirationAttributes> entryIdleTimeout = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getEntryIdleTimeout)
				.orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<ExpirationAttributes> entryTimeToLive = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getEntryTimeToLive)
				.orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<EvictionAttributes> evictionAttributes = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getEvictionAttributes)
				.orElseGet(EvictionAttributes::createLRUEntryAttributes));

		AtomicReference<Class<K>> keyConstraint = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getKeyConstraint)
				.orElse(null));

		AtomicReference<Float> loadFactor = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getLoadFactor)
				.orElse(0.75f));

		AtomicReference<MembershipAttributes> membershipAttributes = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getMembershipAttributes)
				.orElseGet(MembershipAttributes::new));

		AtomicReference<String> poolName = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getPoolName)
				.orElse(null));

		AtomicReference<ExpirationAttributes> regionIdleTimeout = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getRegionIdleTimeout)
				.orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<ExpirationAttributes> regionTimeToLive = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getRegionTimeToLive)
				.orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<Scope> scope = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getScope)
				.orElse(Scope.DISTRIBUTED_NO_ACK));

		AtomicReference<SubscriptionAttributes> subscriptionAttributes = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getSubscriptionAttributes)
				.orElseGet(SubscriptionAttributes::new));

		AtomicReference<Class<V>> valueConstraint = new AtomicReference<>(optionalRegionAttributes
				.map(RegionAttributes::getValueConstraint)
				.orElse(null));

		List<CacheListener> cacheListeners = new ArrayList<>(Arrays.asList(nullSafeArray(optionalRegionAttributes
				.map(RegionAttributes::getCacheListeners)
				.orElse(null), CacheListener.class)));

		when(mockRegionFactory.addCacheListener(any(CacheListener.class)))
				.thenAnswer(newAdder(cacheListeners, mockRegionFactory));

		when(mockRegionFactory.initCacheListeners(any(CacheListener[].class))).thenAnswer(invocation -> {
			cacheListeners.clear();
			Collections.addAll(cacheListeners, invocation.getArgument(0));
			return mockRegionFactory;
		});

		when(mockRegionFactory.setCacheLoader(any(CacheLoader.class)))
				.thenAnswer(newSetter(cacheLoader, () -> mockRegionFactory));

		when(mockRegionFactory.setCacheWriter(any(CacheWriter.class)))
				.thenAnswer(newSetter(cacheWriter, () -> mockRegionFactory));

		when(mockRegionFactory.setCloningEnabled(anyBoolean()))
				.thenAnswer(newSetter(cloningEnabled, mockRegionFactory));

		when(mockRegionFactory.setCompressor(any(Compressor.class)))
				.thenAnswer(newSetter(compressor, () -> mockRegionFactory));

		when(mockRegionFactory.setConcurrencyChecksEnabled(anyBoolean()))
				.then(newSetter(concurrencyChecksEnabled, mockRegionFactory));

		when(mockRegionFactory.setConcurrencyLevel(anyInt()))
				.thenAnswer(newSetter(concurrencyLevel, mockRegionFactory));

		when(mockRegionFactory.setCustomEntryIdleTimeout(any(CustomExpiry.class)))
				.thenAnswer(newSetter(customEntryIdleTimeout, () -> mockRegionFactory));

		when(mockRegionFactory.setCustomEntryTimeToLive(any(CustomExpiry.class)))
				.thenAnswer(newSetter(customEntryTimeToLive, () -> mockRegionFactory));

		when(mockRegionFactory.setDataPolicy(any(DataPolicy.class)))
				.thenAnswer(newSetter(dataPolicy, () -> mockRegionFactory));

		when(mockRegionFactory.setDiskStoreName(anyString()))
				.thenAnswer(newSetter(diskStoreName, () -> mockRegionFactory));

		when(mockRegionFactory.setDiskSynchronous(anyBoolean()))
				.thenAnswer(newSetter(diskSynchronous, mockRegionFactory));

		when(mockRegionFactory.setEnableSubscriptionConflation(anyBoolean()))
				.thenAnswer(newSetter(enableSubscriptionConflation, mockRegionFactory));

		when(mockRegionFactory.setEntryIdleTimeout(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(entryIdleTimeout, () -> mockRegionFactory));

		when(mockRegionFactory.setEntryTimeToLive(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(entryTimeToLive, () -> mockRegionFactory));

		when(mockRegionFactory.setEvictionAttributes(any(EvictionAttributes.class)))
				.thenAnswer(newSetter(evictionAttributes, () -> mockRegionFactory));

		when(mockRegionFactory.setIgnoreJTA(anyBoolean())).thenAnswer(newSetter(ignoreJta, mockRegionFactory));

		when(mockRegionFactory.setInitialCapacity(anyInt())).thenAnswer(newSetter(initialCapacity, mockRegionFactory));

		when(mockRegionFactory.setKeyConstraint(any(Class.class)))
				.thenAnswer(newSetter(keyConstraint, () -> mockRegionFactory));

		when(mockRegionFactory.setLoadFactor(anyFloat())).thenAnswer(newSetter(loadFactor, () -> mockRegionFactory));

		when(mockRegionFactory.setLockGrantor(anyBoolean())).thenAnswer(newSetter(lockGrantor, mockRegionFactory));

		when(mockRegionFactory.setMembershipAttributes(any(MembershipAttributes.class)))
				.thenAnswer(newSetter(membershipAttributes, () -> mockRegionFactory));

		when(mockRegionFactory.setPoolName(anyString())).thenAnswer(newSetter(poolName, () -> mockRegionFactory));

		when(mockRegionFactory.setRegionIdleTimeout(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(regionIdleTimeout, () -> mockRegionFactory));

		when(mockRegionFactory.setRegionTimeToLive(any(ExpirationAttributes.class)))
				.thenAnswer(newSetter(regionTimeToLive, () -> mockRegionFactory));

		when(mockRegionFactory.setScope(any(Scope.class))).thenAnswer(newSetter(scope, () -> mockRegionFactory));

		when(mockRegionFactory.setStatisticsEnabled(anyBoolean()))
				.thenAnswer(newSetter(statisticsEnabled, mockRegionFactory));

		when(mockRegionFactory.setSubscriptionAttributes(any(SubscriptionAttributes.class)))
				.thenAnswer(newSetter(subscriptionAttributes, () -> mockRegionFactory));

		when(mockRegionFactory.setValueConstraint(any(Class.class)))
				.thenAnswer(newSetter(valueConstraint, () -> mockRegionFactory));

		RegionAttributes<K, V> mockRegionAttributes =
				mock(RegionAttributes.class, mockObjectIdentifier("MockRegionAttributes"));

		when(mockRegionAttributes.getCacheListeners())
				.thenAnswer(newGetter(() -> cacheListeners.toArray(new CacheListener[cacheListeners.size()])));

		when(mockRegionAttributes.getCacheLoader()).thenAnswer(newGetter(cacheLoader));
		when(mockRegionAttributes.getCacheWriter()).thenAnswer(newGetter(cacheWriter));
		when(mockRegionAttributes.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled));
		when(mockRegionAttributes.getCompressor()).thenAnswer(newGetter(compressor));
		when(mockRegionAttributes.getConcurrencyChecksEnabled()).thenAnswer(newGetter(concurrencyChecksEnabled));
		when(mockRegionAttributes.getConcurrencyLevel()).thenAnswer(newGetter(concurrencyLevel));
		when(mockRegionAttributes.getCustomEntryIdleTimeout()).thenAnswer(newGetter(customEntryIdleTimeout));
		when(mockRegionAttributes.getCustomEntryTimeToLive()).thenAnswer(newGetter(customEntryTimeToLive));
		when(mockRegionAttributes.getDataPolicy()).thenAnswer(newGetter(dataPolicy));
		when(mockRegionAttributes.getDiskStoreName()).thenAnswer(newGetter(diskStoreName));
		when(mockRegionAttributes.isDiskSynchronous()).thenAnswer(newGetter(diskSynchronous));
		when(mockRegionAttributes.getEnableSubscriptionConflation()).thenAnswer(newGetter(enableSubscriptionConflation));
		when(mockRegionAttributes.getEntryIdleTimeout()).thenAnswer(newGetter(entryIdleTimeout));
		when(mockRegionAttributes.getEntryTimeToLive()).thenAnswer(newGetter(entryTimeToLive));
		when(mockRegionAttributes.getEvictionAttributes()).thenAnswer(newGetter(evictionAttributes));
		when(mockRegionAttributes.getInitialCapacity()).thenAnswer(newGetter(initialCapacity));
		when(mockRegionAttributes.getKeyConstraint()).thenAnswer(newGetter(keyConstraint));
		when(mockRegionAttributes.getLoadFactor()).thenAnswer(newGetter(loadFactor));
		when(mockRegionAttributes.isLockGrantor()).thenAnswer(newGetter(lockGrantor));
		when(mockRegionAttributes.getMembershipAttributes()).thenAnswer(newGetter(membershipAttributes));
		when(mockRegionAttributes.getPoolName()).thenAnswer(newGetter(poolName));
		when(mockRegionAttributes.getRegionIdleTimeout()).thenAnswer(newGetter(regionIdleTimeout));
		when(mockRegionAttributes.getRegionTimeToLive()).thenAnswer(newGetter(regionTimeToLive));
		when(mockRegionAttributes.getScope()).thenAnswer(newGetter(scope));
		when(mockRegionAttributes.getStatisticsEnabled()).thenAnswer(newGetter(statisticsEnabled));
		when(mockRegionAttributes.getSubscriptionAttributes()).thenAnswer(newGetter(subscriptionAttributes));
		when(mockRegionAttributes.getValueConstraint()).thenAnswer(newGetter(valueConstraint));

		when(mockRegionFactory.create(anyString())).thenAnswer(invocation ->
				mockRegion(mockCache, invocation.getArgument(0), mockRegionAttributes));

		when(mockRegionFactory.createSubregion(any(Region.class), anyString())).thenAnswer(invocation ->
				mockSubRegion(invocation.getArgument(0), invocation.getArgument(1), mockRegionAttributes));

		return mockRegionFactory;
	}

	public static ResourceManager mockResourceManager() {

		ResourceManager mockResourceManager = mock(ResourceManager.class);

		AtomicReference<Float> criticalHeapPercentage =
				new AtomicReference<>(ResourceManager.DEFAULT_CRITICAL_PERCENTAGE);

		AtomicReference<Float> evictionHeapPercentage =
				new AtomicReference<>(ResourceManager.DEFAULT_EVICTION_PERCENTAGE);

		doAnswer(newSetter(criticalHeapPercentage, () -> null))
				.when(mockResourceManager).setCriticalHeapPercentage(anyFloat());

		doAnswer(newSetter(evictionHeapPercentage, () -> null))
				.when(mockResourceManager).setEvictionHeapPercentage(anyFloat());

		when(mockResourceManager.getCriticalHeapPercentage()).thenAnswer(newGetter(criticalHeapPercentage));
		when(mockResourceManager.getEvictionHeapPercentage()).thenAnswer(newGetter(evictionHeapPercentage));
		when(mockResourceManager.getRebalanceOperations()).thenReturn(Collections.emptySet());

		return mockResourceManager;
	}

	public static boolean resolveUseSingletonCache() {
		return Boolean.parseBoolean(System.getProperty(USE_SINGLETON_CACHE_PROPERTY,
				String.valueOf(DEFAULT_USE_SINGLETON_CACHE)));
	}

	public static ClientCacheFactory spyOn(ClientCacheFactory clientCacheFactory) {
		return spyOn(clientCacheFactory, resolveUseSingletonCache());
	}

	public static ClientCacheFactory spyOn(ClientCacheFactory clientCacheFactory, boolean useSingletonCache) {

		AtomicBoolean pdxIgnoreUnreadFields = new AtomicBoolean(false);
		AtomicBoolean pdxPersistent = new AtomicBoolean(false);
		AtomicBoolean pdxReadSerialized = new AtomicBoolean(false);

		AtomicReference<String> pdxDiskStoreName = new AtomicReference<>(null);
		AtomicReference<PdxSerializer> pdxSerializer = new AtomicReference<>(null);

		ClientCacheFactory clientCacheFactorySpy = spy(clientCacheFactory);

		doAnswer(newSetter(pdxDiskStoreName, () -> clientCacheFactorySpy))
				.when(clientCacheFactorySpy).setPdxDiskStore(anyString());

		doAnswer(newSetter(pdxIgnoreUnreadFields, clientCacheFactorySpy))
				.when(clientCacheFactorySpy).setPdxIgnoreUnreadFields(anyBoolean());

		doAnswer(newSetter(pdxPersistent, clientCacheFactorySpy))
				.when(clientCacheFactorySpy).setPdxPersistent(anyBoolean());

		doAnswer(newSetter(pdxReadSerialized, clientCacheFactorySpy))
				.when(clientCacheFactorySpy).setPdxReadSerialized(anyBoolean());

		doAnswer(newSetter(pdxSerializer, () -> clientCacheFactorySpy))
				.when(clientCacheFactorySpy).setPdxSerializer(any(PdxSerializer.class));

		PoolFactory mockPoolFactory = mockPoolFactory();

		doAnswer(invocation -> {
			mockPoolFactory.addLocator(invocation.getArgument(0), invocation.getArgument(1));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).addPoolLocator(anyString(), anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.addServer(invocation.getArgument(0), invocation.getArgument(1));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).addPoolServer(anyString(), anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setFreeConnectionTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolFreeConnectionTimeout(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setIdleTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolIdleTimeout(anyLong());

		doAnswer(invocation -> {
			mockPoolFactory.setLoadConditioningInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolLoadConditioningInterval(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setMaxConnections(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMaxConnections(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setMinConnections(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMinConnections(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setMaxConnectionsPerServer(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMaxConnectionsPerServer(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setMinConnectionsPerServer(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMinConnectionsPerServer(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setMultiuserAuthentication(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMultiuserAuthentication(anyBoolean());

		doAnswer(invocation -> {
			mockPoolFactory.setPingInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolPingInterval(anyLong());

		doAnswer(invocation -> {
			mockPoolFactory.setPRSingleHopEnabled(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolPRSingleHopEnabled(anyBoolean());

		doAnswer(invocation -> {
			mockPoolFactory.setReadTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolReadTimeout(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setRetryAttempts(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolRetryAttempts(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setServerConnectionTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolServerConnectionTimeout(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setServerGroup(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolServerGroup(anyString());

		doAnswer(invocation -> {
			mockPoolFactory.setSocketBufferSize(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSocketBufferSize(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setSocketConnectTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSocketConnectTimeout(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setSocketFactory(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSocketFactory(any(SocketFactory.class));

		doAnswer(invocation -> {
			mockPoolFactory.setStatisticInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolStatisticInterval(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setSubscriptionAckInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionAckInterval(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setSubscriptionEnabled(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionEnabled(anyBoolean());

		doAnswer(invocation -> {
			mockPoolFactory.setSubscriptionMessageTrackingTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionMessageTrackingTimeout(anyInt());

		doAnswer(invocation -> {
			mockPoolFactory.setSubscriptionRedundancy(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionRedundancy(anyInt());

//		doAnswer(invocation -> {
//			mockPoolFactory.setThreadLocalConnections(invocation.getArgument(0));
//			return clientCacheFactorySpy;
//		}).when(clientCacheFactorySpy).setPoolThreadLocalConnections(anyBoolean());

		Supplier<ClientCache> resolvedMockedClientCache = () ->
				GemFireMockObjectsSupport.<ClientCache>resolveMockedGemFireCache(useSingletonCache).orElseGet(() -> {

					ClientCache mockClientCache = mockClientCache();

					Pool mockDefaultPool = mockPoolFactory.create("DEFAULT");

					doAnswer(invocation -> mockClientCache.getQueryService()).when(mockDefaultPool).getQueryService();

					when(mockClientCache.getCurrentServers()).thenAnswer(invocation ->
							Collections.unmodifiableSet(new HashSet<>(mockClientCache.getDefaultPool().getServers())));

					when(mockClientCache.getDefaultPool()).thenReturn(mockDefaultPool);

					when(mockClientCache.getPdxDiskStore()).thenAnswer(newGetter(pdxDiskStoreName));
					when(mockClientCache.getPdxIgnoreUnreadFields()).thenAnswer(newGetter(pdxIgnoreUnreadFields));
					when(mockClientCache.getPdxPersistent()).thenAnswer(newGetter(pdxPersistent));
					when(mockClientCache.getPdxReadSerialized()).thenAnswer(newGetter(pdxReadSerialized));
					when(mockClientCache.getPdxSerializer()).thenAnswer(newGetter(pdxSerializer));

					return mockClientCache;
				});

		doAnswer(invocation -> {
			storeConfiguration(clientCacheFactory);
			return rememberMockedGemFireCache(constructGemFireObjects(resolvedMockedClientCache.get()), useSingletonCache);
		}).when(clientCacheFactorySpy).create();

		return clientCacheFactorySpy;
	}

	private static void storeConfiguration(ClientCacheFactory clientCacheFactory) {
		storeConfiguration(clientCacheFactory, CLIENT_CACHE_FACTORY_DS_PROPS_FIELD_NAME);
	}

	private static void storeConfiguration(Object clientCacheFactory, String gemfirePropertiesFieldName) {

		Properties localGemFireProperties = gemfireProperties.get();

		localGemFireProperties.putAll(withGemFireApiProperties(clientCacheFactory, gemfirePropertiesFieldName));
		localGemFireProperties.putAll(withGemFireSystemProperties());
	}

	@SuppressWarnings("unchecked")
	private static Properties withGemFireApiProperties(Object clientCacheFactory, String gemfirePropertiesFieldName) {

		Class<?> cacheFactoryType = Optional.ofNullable(clientCacheFactory)
				.map(Object::getClass)
				.orElse((Class) Object.class);

		try {

			Field dsPropsField = cacheFactoryType.getDeclaredField(gemfirePropertiesFieldName);

			dsPropsField.setAccessible(true);

			Properties gemfireApiProperties = (Properties) dsPropsField.get(clientCacheFactory);

			return gemfireApiProperties;
		}
		catch (Throwable cause) {

			if (cause instanceof NoSuchFieldException
					&& !CACHE_FACTORY_INTERNAL_CACHE_BUILDER_FIELD_NAME.equals(gemfirePropertiesFieldName)) {

				return Arrays.stream(ArrayUtils.nullSafeArray(cacheFactoryType.getDeclaredFields(), Field.class))
						.filter(field -> CACHE_FACTORY_INTERNAL_CACHE_BUILDER_FIELD_NAME.equals(field.getName()))
						.findFirst()
						.map(field -> {

							field.setAccessible(true);

							Object internalCacheBuilder =
									ObjectUtils.doOperationSafely(() -> field.get(clientCacheFactory), null);

							if (internalCacheBuilder != null) {
								return withGemFireApiProperties(internalCacheBuilder,
										INTERNAL_CACHE_BUILDER_CONFIG_PROPERTIES_FIELD_NAME);
							}

							return null;

						})
						.orElseGet(Properties::new);
			}

			return new Properties();
		}
	}

	private static Properties withGemFireSystemProperties() {

		Properties gemfireSystemProperties = new Properties();

		List<String> gemfireSystemPropertyNames = System.getProperties().stringPropertyNames().stream()
				.filter(StringUtils::hasText)
				.filter(it -> it.startsWith(GEMFIRE_SYSTEM_PROPERTY_PREFIX))
				.collect(Collectors.toList());

		gemfireSystemPropertyNames.stream().forEach(propertyName ->
				gemfireSystemProperties.setProperty(normalizeGemFirePropertyName(propertyName),
						System.getProperty(propertyName)));

		return gemfireSystemProperties;
	}
}
