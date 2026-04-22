/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 * 2026-04-02: Added doAnswer for set(String,String) in spyOn so mock factories capture GemFire properties
 */
package org.springframework.data.gemfire.tests.mock;

import org.springframework.data.gemfire.gud.api.GudAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudCacheCallback;
import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheTransactionManager;
import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudCacheWriterException;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudDiskStoreFactory;
import org.springframework.data.gemfire.gud.api.GudEntryEvent;
import org.springframework.data.gemfire.gud.api.GudEntryNotFoundException;
import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudLoaderHelper;
import org.springframework.data.gemfire.gud.api.GudMembershipAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionExistsException;
import org.springframework.data.gemfire.gud.api.GudRegionFactory;
import org.springframework.data.gemfire.gud.api.GudRegionService;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.gud.api.GudSubscriptionAttributes;
import org.springframework.data.gemfire.gud.api.GudTransactionId;
import org.springframework.data.gemfire.gud.api.GudTransactionListener;
import org.springframework.data.gemfire.gud.api.GudTransactionWriter;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudPoolManager;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;
import org.springframework.data.gemfire.gud.api.GudResourceManager;
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.data.gemfire.gud.api.GudRegionFunctionContext;
import org.springframework.data.gemfire.gud.api.GudCqAttributes;
import org.springframework.data.gemfire.gud.api.GudCqQuery;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudIndexStatistics;
import org.springframework.data.gemfire.gud.api.GudQuery;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudQueryStatistics;
import org.springframework.data.gemfire.gud.api.GudSelectResults;
import org.springframework.data.gemfire.gud.api.GudCollectionType;
import org.springframework.data.gemfire.gud.api.GudObjectType;
import org.springframework.data.gemfire.gud.api.GudCacheServer;
import org.springframework.data.gemfire.gud.api.GudClientSubscriptionConfig;
import org.springframework.data.gemfire.gud.api.GudServerLoadProbe;
import org.springframework.data.gemfire.gud.api.GudCompressor;
import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.springframework.data.gemfire.gud.api.GudConfigurationProperties;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudInterestPolicy;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

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

/**
 * The {@link GemFireMockObjectsSupport} class is an abstract base class encapsulating factory methods for creating
 * Apache Geode or VMware (Pivotal) GemFire Mock Objects, {@link GudClientCache}, {@link GudRegion}, and so on).
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
 * @see GudAttributesMutator
 * @see GudClientCacheFactory
 * @see GudCacheListener
 * @see GudCacheLoader
 * @see GudCacheWriter
 * @see GudCustomExpiry
 * @see GudDataPolicy
 * @see GudDiskStore
 * @see GudDiskStoreFactory
 * @see GudEvictionAttributes
 * @see GudEvictionAttributesMutator
 * @see GudExpirationAttributes
 * @see GudClientCache
 * @see GudMembershipAttributes
 * @see GudRegion
 * @see GudRegionAttributes
 * @see GudRegionFactory
 * @see GudRegionService
 * @see GudScope
 * @see GudSubscriptionAttributes
 * @see GudClientCache
 * @see GudClientCacheFactory
 * @see GudClientRegionFactory
 * @see GudClientRegionShortcut
 * @see GudPool
 * @see GudPoolFactory
 * @see GudPoolManager
 * @see GudResourceManager
 * @see GudRegionFunctionContext
 * @see GudCqAttributes
 * @see GudCqQuery
 * @see GudIndex
 * @see GudIndexStatistics
 * @see GudQuery
 * @see GudQueryService
 * @see GudQueryStatistics
 * @see GudCacheServer
 * @see GudClientSubscriptionConfig
 * @see GudServerLoadProbe
 * @see GudCompressor
 * @see GudDistributedMember
 * @see GudDistributedSystem
 * @see GudPdxSerializer
 * @see org.mockito.Mockito
 * @see DisposableBean
 * @see MockObjectsSupport
 * @since 0.0.1
 */
@SuppressWarnings("all")
public abstract class GemFireMockObjectsSupport extends MockObjectsSupport {

	private static final boolean DEFAULT_USE_SINGLETON_CACHE = false;

	private static final AtomicReference<GudClientCache> cacheReference = new AtomicReference<>(null);
	private static final AtomicReference<GudClientCache> singletonCache = new AtomicReference<>(null);
	private static final AtomicReference<Properties> gemfireProperties = new AtomicReference<>(new Properties());

	private static final List<Object> cachedGemFireObjects = Collections.synchronizedList(new ArrayList<>());

	private static final Map<String, GudDiskStore> diskStores = new ConcurrentHashMap<>();

	private static final Map<String, GudRegion<Object, Object>> regions = new ConcurrentHashMap<>();

	private static final Map<String, GudRegionAttributes<Object, Object>> regionAttributes = new ConcurrentHashMap<>();

	private static final Set<String> registeredGudPoolNames = new ConcurrentSkipListSet<>();

	private static final String CACHE_FACTORY_DS_PROPS_FIELD_NAME = "dsProps";
	private static final String CACHE_FACTORY_INTERNAL_CACHE_BUILDER_FIELD_NAME = "internalCacheBuilder";
	private static final String CLIENT_CACHE_FACTORY_DS_PROPS_FIELD_NAME = "dsProps";
	private static final String INTERNAL_CACHE_BUILDER_CONFIG_PROPERTIES_FIELD_NAME = "configProperties";
	private static final String GEMFIRE_SYSTEM_PROPERTY_PREFIX = "gemfire.";
	private static final String FROM_KEYWORD = "FROM";
	private static final String REPEATING_REGION_SEPARATOR = GudRegion.SEPARATOR + "{2,}";
	private static final String USE_SINGLETON_CACHE_PROPERTY = "spring.data.gemfire.test.cache.singleton";
	private static final String WHERE_KEYWORD = "WHERE";

	private static final String[] GEMFIRE_OBJECT_BASED_PROPERTIES = { "security-client-auth-init", "security-manager",
			"security-post-processor", };

	private static final String[] SPRING_DATA_GEODE_TEST_PROPERTIES = { USE_SINGLETON_CACHE_PROPERTY, };
	private static final Logger log = LoggerFactory.getLogger(GemFireMockObjectsSupport.class);

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
		unregisterManagedGudPools();
		closeGudPools();
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
	 * Closes all {@link GudPool GudPools}.
	 *
	 * @see GudPool
	 * @see GudPoolManager
	 */
	static void closeGudPools() {

		// TODO: add support for keepAlive (??) Cursor still to clean up
//		ObjectUtils.doOperationSafely(() -> {
//			GudPoolManager.close();
//			return null;
//		}, null);
	}

	/**
	 * Destroys all {@link DisposableBean} based {@link Object GemFire objects}.
	 *
	 * @see DisposableBean
	 */
	static synchronized void destroyGemFireObjects() {

		cachedGemFireObjects.stream().filter(gemfireObject -> gemfireObject instanceof DisposableBean)
				.map(gemfireObject -> (DisposableBean) gemfireObject).forEach(disposableBean -> {
					ObjectUtils.doOperationSafely(() -> {
						disposableBean.destroy();
						return null;
					});
				});

		cachedGemFireObjects.clear();
	}

	/**
	 * Unregisters all {@link Function Functions} registered with the {@link GudFunctionService} by Spring.
	 *
	 * @see GudFunction
	 * @see GudFunctionService
	 */
	static synchronized void unregisterFunctions() {

		CollectionUtils.nullSafeMap(GudFunctionService.getRegisteredFunctions())
				.forEach((functionId, function) -> GudFunctionService.unregisterFunction(functionId));
	}

	/**
	 * Unrigsters all {@link GudPool GudPools} registered with Apache Geode and managed by Spring.
	 *
	 * @see GudPool
	 * @see GudPoolManager
	 */
	static synchronized void unregisterManagedGudPools() {

//		CollectionUtils.nullSafeMap(GudPoolManager.getAll()).values().stream().filter(Objects::nonNull)
//				.filter(pool -> registeredGudPoolNames.contains(pool.getName())).forEach(GemFireMockObjectsSupport::unregister);
//
//		registeredGudPoolNames.clear();

		//TODO this still needs to be clean up cursor
	}

	/**
	 * Caches the given {@link Object GemFire object} in order to release resources on shutdown.
	 *
	 * @param gemfireObject {@link Object GemFire object} to cache.
	 */
	private static synchronized void cacheGemFireObject(Object gemfireObject) {

		Optional.ofNullable(gemfireObject).ifPresent(cachedGemFireObjects::add);
	}

	/**
	 * Instantiates all Apache Geode/VMware GemFire objects which have been declared
	 * via {@link System#getProperties() System properties}.
	 *
	 * @param <T> {@link Class type} of the {@link GudClientCache}.
	 * @param gemfireCache reference to the {@link GudClientCache} instance.
	 * @return the given {@link GudClientCache} instance.
	 * @see GudClientCache
	 */
	private static <T extends GudClientCache> T constructGemFireObjects(T gemfireCache) {

		Properties localGemfireProperties = gemfireProperties.get();

    
      Arrays.stream(GEMFIRE_OBJECT_BASED_PROPERTIES)
          .map(localGemfireProperties::getProperty)
          .filter(StringUtils::hasText)
          .filter(className -> ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader()))
          .forEach(className ->{
            try {
              cacheGemFireObject(Class.forName(className).getDeclaredConstructor().newInstance());
            } catch (Exception e) {
              log.info("Could not instantiate class: {}",className);
            }
          });

    return gemfireCache;
	}

	/**
	 * Converts the given {@link GudClientRegionShortcut} into a corresponding {@link GudDataPolicy}.
	 *
	 * @param clientGudRegionShortcut {@link GudClientRegionShortcut} to convert.
	 * @return a {@link GudDataPolicy} from the {@link GudClientRegionShortcut}.
	 * @see GudClientRegionShortcut
	 * @see GudDataPolicy
	 */
	@SuppressWarnings("unchecked")
	private static GudDataPolicy convert(GudClientRegionShortcut clientGudRegionShortcut) {

		return Optional.ofNullable(clientGudRegionShortcut).map(shortcut -> {

			switch (shortcut) {
				case CACHING_PROXY:
				case CACHING_PROXY_HEAP_LRU:
				case CACHING_PROXY_OVERFLOW:
				case LOCAL:
				case LOCAL_HEAP_LRU:
				case LOCAL_OVERFLOW:
					return GudDataPolicy.NORMAL;
				case LOCAL_PERSISTENT:
				case LOCAL_PERSISTENT_OVERFLOW:
					return GudDataPolicy.PERSISTENT_REPLICATE;
				case PROXY:
					return GudDataPolicy.EMPTY;
				default:
					return null;
			}

		}).orElse(GudDataPolicy.DEFAULT);
	}

	/**
	 * Converts the given {@link GudRegionShortcut} into a corresponding {@link GudDataPolicy}.
	 *
	 * @param regionShortcut {@link GudRegionShortcut} to convert.
	 * @return a {@link GudDataPolicy} from the {@link GudRegionShortcut}.
	 * @see GudRegionShortcut
	 * @see GudDataPolicy
	 */
	@SuppressWarnings("unchecked")
	private static GudDataPolicy convert(GudRegionShortcut regionShortcut) {

		return Optional.ofNullable(regionShortcut).map(shortcut -> {

			switch (shortcut) {
				case LOCAL:
				case LOCAL_HEAP_LRU:
				case LOCAL_OVERFLOW:
					return GudDataPolicy.NORMAL;
				case LOCAL_PERSISTENT:
				case LOCAL_PERSISTENT_OVERFLOW:
					return GudDataPolicy.PERSISTENT_REPLICATE;
				case REPLICATE_PROXY:
					return GudDataPolicy.EMPTY;
				default:
					return null;
			}

		}).orElse(GudDataPolicy.DEFAULT);
	}

	/**
	 * Determines whether the given {@link GudRegion} is a root {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} to evaluate.
	 * @return a boolean value indicating whether the {@link GudRegion} is a root {@link GudRegion}.
	 * @see GudRegion
	 * @see #isRootRegion(String)
	 */
	private static boolean isRootRegion(GudRegion<?, ?> region) {
		return isRootRegion(region.getFullPath());
	}

	/**
	 * Determines whether the {@link GudRegion} identified by the given {@link String path} is a root {@link GudRegion}.
	 *
	 * @param regionPath {@link String path} identifying the {@link GudRegion} to evaluate.
	 * @return a boolean value indicating whether the {@link GudRegion} identified by the given {@link String path} is a root
	 *         {@link GudRegion}.
	 */
	private static boolean isRootRegion(String regionPath) {
		return regionPath.lastIndexOf(GudRegion.SEPARATOR) <= 0;
	}

	/**
	 * Normalizes the {@link String name} of the Apache Geode/VMware GemFire System property by stripping off the
	 * {@literal gemfire.} prefix.
	 *
	 * @param propertyName {@link String name} of the property to normalize.
	 * @return the {@link String normalized form} of the Apache Geode/VMware GemFire System property.
	 * @see <a href="https://geode.apache.org/docs/guide/16/reference/topics/gemfire_properties.html">GemFire
	 *      Properties</a>
	 */
	private static String normalizeGemFirePropertyName(String propertyName) {

		return Optional.ofNullable(propertyName).filter(StringUtils::hasText)
				.filter(it -> it.startsWith(GEMFIRE_SYSTEM_PROPERTY_PREFIX))
				.map(it -> it.substring(GEMFIRE_SYSTEM_PROPERTY_PREFIX.length())).orElse(propertyName);
	}

	/**
	 * Normalizes the given {@link GudRegion#getFullPath() Regon path} by removing all duplicate, repeating
	 * {@link GudRegion#SEPARATOR} characters between path segments as well as removing the trailing
	 * {@link GudRegion#SEPARATOR}.
	 *
	 * @param regionPath {@link GudRegion#getFullPath()} to normalize.
	 * @return a normalized version of the given {@link GudRegion#getFullPath()}.
	 */
	private static String normalizeRegionPath(String regionPath) {

		regionPath = regionPath.replaceAll(REPEATING_REGION_SEPARATOR, GudRegion.SEPARATOR);

		regionPath = regionPath.endsWith(GudRegion.SEPARATOR) ? regionPath.substring(0, regionPath.length() - 1) : regionPath;

		return regionPath;
	}

	/**
	 * Stores a reference to the given {@link GudClientCache} object.
	 *
	 * @param <T> {@link Class type} of {@link GudClientCache} (e.g. client or peer).
	 * @param gemfireCache reference to the {@link GudClientCache} object to store; maybe {@literal null}.
	 * @return the given {@link GudClientCache} object.
	 * @see GudClientCache
	 */
	@SuppressWarnings("unchecked")
	private static @Nullable <T extends GudClientCache> T referTo(@Nullable T gemfireCache) {
		return (T) cacheReference.updateAndGet(currentCacheReference -> gemfireCache);
	}

	/**
	 * Remembers the given mock {@link GudClientCache} object, which may be a {@link GudClientCache}.
	 *
	 * @param <T> {@link Class sub-type} of the {@link GudClientCache} instance.
	 * @param mockedGemFireCache {@link GudClientCache} to remember.
	 * @param useSingletonCache boolean value indicating whether the {@link GudClientCache} is a Singleton.
	 * @return the given {@link GudClientCache}.
	 * @throws IllegalArgumentException if {@link GudClientCache} is {@literal null}.
	 * @see GudClientCache
	 */
	private static <T extends GudClientCache> T rememberMockedGemFireCache(T mockedGemFireCache, boolean useSingletonCache) {

		return Optional.ofNullable(mockedGemFireCache).map(it -> {

			if (useSingletonCache) {
				singletonCache.compareAndSet(null, it);
			}

			return it;
		}).orElseThrow(() -> newIllegalArgumentException("GudClientCache is required"));
	}

	/**
	 * Remembers the given mock {@link GudRegion}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param mockRegion {@link GudRegion} to remember.
	 * @throws IllegalArgumentException if the given {@link GudRegion} is {@literal null}.
	 * @throws GudRegionExistsException if the given {@link GudRegion} already exists.
	 * @return the given {@link GudRegion}.
	 * @see GudRegion
	 */
	@SuppressWarnings("unchecked")
	private static <K, V> GudRegion<K, V> rememberMockedRegion(GudRegion<K, V> mockRegion) {

		String mockRegionPath = Optional.ofNullable(mockRegion).map(GudRegion::getFullPath)
				.orElseThrow(() -> newIllegalArgumentException("Region is required"));

		if (regions.putIfAbsent(mockRegionPath, (GudRegion) mockRegion) != null) {
			throw new GudRegionExistsException(mockRegion);
		}

		assertThat(regions).containsValue((GudRegion) mockRegion);

		return mockRegion;
	}

	/**
	 * Resolves any {@link GudClientCache} object created by the Spring Test for Apache Geode mock objects test framework. If
	 * {@literal Singleton} caches are not used (default is {@literal false}), then the reference will store the last mock
	 * {@link GudClientCache} object created by the Apache Geode mock objects test framework.
	 *
	 * @param <T> {@link Class type} of {@link GudClientCache} (e.g. client or peer).
	 * @return a reference to any (and the last) {@ink GudClientCache} object created by this test framework.
	 * @see GudClientCache
	 * @see Optional
	 */
	private static <T extends GudClientCache> Optional<T> resolveAnyGemFireCache() {
		return Optional.ofNullable((T) cacheReference.get());
	}

	/**
	 * Resolves the single, remembered {@link GudClientCache} if using GemFire in Singleton-mode.
	 *
	 * @param <T> {@link Class sub-type} of the {@link GudClientCache} instance.
	 * @param useSingletonCache boolean value indicating if mock infrastructure is using GemFire Singletons.
	 * @return an {@link Optional}, single remembered instance of the {@link GudClientCache}.
	 * @see GudClientCache
	 */
	@SuppressWarnings("unchecked")
	private static <T extends GudClientCache> Optional<T> resolveMockedGemFireCache(boolean useSingletonCache) {

		return Optional.ofNullable((T) singletonCache.get()).filter(it -> useSingletonCache);
	}

	/**
	 * Resolves the {@link GudRegionAttributes} identified by the given {@link String id}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param regionAttributesId {@link String id} identifying the {@link GudRegionAttributes} to resolve.
	 * @return the resolved {@link GudRegionAttributes} identified by the given {@link String id}.
	 * @throws IllegalStateException if {@link GudRegionAttributes} could not be resolved from the given {@link String id}.
	 * @see GudRegionAttributes
	 */
	@SuppressWarnings("unchecked")
	@NonNull
	private static <K, V> GudRegionAttributes<K, V> resolveGudRegionAttributes(String regionAttributesId) {

		return (GudRegionAttributes<K, V>) Optional.ofNullable(regionAttributes.get(regionAttributesId)).orElseThrow(
				() -> newIllegalStateException("GudRegionAttributes with ID [%s] cannot be found", regionAttributesId));
	}

	/**
	 * Constructs, configures and initializes {@link GudRegionAttributes} from a given {@link GudClientRegionShortcut}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param clientGudRegionShortcut {@link GudClientRegionShortcut} used to construct, configure and initialize
	 *          {@link GudRegionAttributes}.
	 * @return a {@link GudRegionAttributes} object created from the given {@link GudClientRegionShortcut} or {@literal null} if
	 *         the {@link GudClientRegionShortcut} is {@literal null}.
	 * @see GudClientRegionShortcut
	 * @see GudRegionAttributes
	 */
	@SuppressWarnings("unchecked")
	private static <K, V> GudRegionAttributes<K, V> resolveGudRegionAttributesFromGudClientRegionShortcut(
			@Nullable GudClientRegionShortcut clientGudRegionShortcut) {

		GudRegionAttributes<K, V> mockGudRegionAttributes = null;

		if (clientGudRegionShortcut != null) {

			ClientRegionShortcutWrapper clientRegionShortcutWrapper = ClientRegionShortcutWrapper
					.valueOf(clientGudRegionShortcut);

			mockGudRegionAttributes = mock(GudRegionAttributes.class, withSettings().lenient());

			doReturn(convert(clientGudRegionShortcut)).when(mockGudRegionAttributes).getDataPolicy();

			if (clientRegionShortcutWrapper.isHeapLru()) {
				doReturn(GudEvictionAttributes.createLRUHeapAttributes()).when(mockGudRegionAttributes).getEvictionAttributes();
			} else if (clientRegionShortcutWrapper.isOverflow()) {
				doReturn(GudEvictionAttributes.createLRUHeapAttributes(null, GudEvictionAction.OVERFLOW_TO_DISK))
						.when(mockGudRegionAttributes).getEvictionAttributes();
			}
		}

		return mockGudRegionAttributes;
	}

	/**
	 * Constructs, configures and initializes {@link GudRegionAttributes} from a given {@link GudRegionShortcut}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param regionShortcut {@link GudRegionShortcut} used to construct, configure and initialize {@link GudRegionAttributes}.
	 * @return a {@link GudRegionAttributes} object created from the given {@link GudRegionShortcut} or {@literal null} if the
	 *         {@link GudRegionShortcut} is {@literal null}.
	 * @see GudRegionAttributes
	 * @see GudRegionShortcut
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	private static <K, V> GudRegionAttributes<K, V> resolveGudRegionAttributesFromGudRegionShortcut(
			@Nullable GudRegionShortcut regionShortcut) {

		GudRegionAttributes<K, V> mockGudRegionAttributes = null;

		if (regionShortcut != null) {

			RegionShortcutWrapper regionShortcutWrapper = RegionShortcutWrapper.valueOf(regionShortcut);

			mockGudRegionAttributes = mock(GudRegionAttributes.class, withSettings().lenient());

			doReturn(convert(regionShortcut)).when(mockGudRegionAttributes).getDataPolicy();

			if (regionShortcutWrapper.isHeapLru()) {
				doReturn(GudEvictionAttributes.createLRUHeapAttributes()).when(mockGudRegionAttributes).getEvictionAttributes();
			} else if (regionShortcutWrapper.isOverflow()) {
				doReturn(GudEvictionAttributes.createLRUHeapAttributes(null, GudEvictionAction.OVERFLOW_TO_DISK))
						.when(mockGudRegionAttributes).getEvictionAttributes();
			}

			if (regionShortcutWrapper.isLocal()) {
				doReturn(GudScope.LOCAL).when(mockGudRegionAttributes).getScope();
			}
		}

		return mockGudRegionAttributes;
	}

	/**
	 * Converts the given {@link String Region name} into a proper {@link GudRegion#getName() Region name}.
	 *
	 * @param regionName {@link String Region name} to evaluate.
	 * @return a proper {@link GudRegion#getName() Region name} from the given {@link String Region name}.
	 * @throws IllegalArgumentException if {@link String Region name} is {@literal null} or {@link String#isEmpty()
	 *           empty}.
	 * @see String
	 */
	private static String toRegionName(String regionName) {

		return Optional.ofNullable(regionName).map(String::trim).map(it -> {

			int lastIndexOfRegionSeparator = it.lastIndexOf(GudRegion.SEPARATOR);

			return lastIndexOfRegionSeparator < 0 ? it : it.substring(lastIndexOfRegionSeparator + 1);
		}).filter(it -> !it.isEmpty())
				.orElseThrow(() -> newIllegalArgumentException("Region name [%s] is required", regionName));
	}

	/**
	 * Converts the given {@link String Region path} into a proper {@link GudRegion#getFullPath() Region path}.
	 *
	 * @param regionPath {@link String Region path} to evaluate.
	 * @return a proper {@link GudRegion#getFullPath() Region path} from the given {@link String Region path}.
	 * @throws IllegalArgumentException if {@link String Region path} is {@literal null} or {@link String#isEmpty()
	 *           empty}.
	 * @see String
	 */
	private static String toRegionPath(String regionPath) {

		return Optional.ofNullable(regionPath).map(String::trim)
				.map(it -> it.startsWith(GudRegion.SEPARATOR) ? it : String.format("%1$s%2$s", GudRegion.SEPARATOR, it))
				.map(GemFireMockObjectsSupport::normalizeRegionPath).filter(it -> !it.isEmpty())
				.orElseThrow(() -> newIllegalArgumentException("Region path [%s] is required", regionPath));
	}

	@SuppressWarnings("unchecked")
	private static <T extends GudClientCache> T mockCacheApi(T mockGemFireCache) {

		AtomicBoolean copyOnRead = new AtomicBoolean(false);

		GudCacheTransactionManager mockGudCacheTransactionManager = mockGudCacheTransactionManager();

		GudDistributedSystem mockGudDistributedSystem = mockGudDistributedSystem();

		GudResourceManager mockGudResourceManager = mockGudResourceManager();

		doAnswer(newSetter(copyOnRead, null)).when(mockGemFireCache).setCopyOnRead(anyBoolean());

		// TODO: Server-side Cache methods not available in GUD API (client-focused)
		// doAnswer(newSetter(regionAttributes, null)).when(mockGemFireCache).setRegionAttributes(anyString(),
		//		any(GudRegionAttributes.class));

		when(mockGemFireCache.getCacheTransactionManager()).thenReturn(mockGudCacheTransactionManager);

		when(mockGemFireCache.getCopyOnRead()).thenAnswer(newGetter(copyOnRead));

		when(mockGemFireCache.getDistributedSystem()).thenReturn(mockGudDistributedSystem);

		when(mockGemFireCache.getName()).thenAnswer(invocation -> Optional.ofNullable(gemfireProperties)
				.map(AtomicReference::get).map(properties -> properties.getProperty(GudConfigurationProperties.NAME_NAME))
				.filter(StringUtils::hasText).orElse(null));

		// TODO: Server-side Cache methods not available in GUD API (client-focused)
		// when(mockGemFireCache.getRegionAttributes(anyString()))
		//		.thenAnswer(invocation -> regionAttributes.get(invocation.<String> getArgument(0)));

		when(mockGemFireCache.getResourceManager()).thenReturn(mockGudResourceManager);

		when(mockGemFireCache.createDiskStoreFactory()).thenAnswer(invocation -> mockGudDiskStoreFactory());

		when(mockGemFireCache.findDiskStore(anyString()))
				.thenAnswer(invocation -> diskStores.get(invocation.<String> getArgument(0)));

		// TODO: Server-side Cache methods not available in GUD API (client-focused)
		// when(mockGemFireCache.listRegionAttributes()).thenReturn(Collections.unmodifiableMap(regionAttributes));

		doThrow(newUnsupportedOperationException(NOT_SUPPORTED)).when(mockGemFireCache)
				.loadCacheXml(any(InputStream.class));

		return mockGudRegionServiceApi(mockGemFireCache);
	}

	private static <T extends GudRegionService> T mockGudRegionServiceApi(T mockGudRegionService) {

		AtomicBoolean closed = new AtomicBoolean(false);

		doAnswer(newSetter(closed, true, null)).when(mockGudRegionService).close();

		when(mockGudRegionService.isClosed()).thenAnswer(newGetter(closed));

		// TODO: getCancelCriterion is server-side, not in GUD API
		// when(mockGudRegionService.getCancelCriterion()).thenThrow(newUnsupportedOperationException(NOT_SUPPORTED));

		when(mockGudRegionService.getRegion(anyString())).thenAnswer(invocation -> {

			String regionPath = invocation.getArgument(0);

			String resolvedRegionPath = Optional.ofNullable(regionPath).map(String::trim).filter(it -> !it.isEmpty())
					.map(GemFireMockObjectsSupport::toRegionPath)
					.orElseThrow(() -> newIllegalArgumentException("Region path [%s] is not valid", regionPath));

			return regions.get(resolvedRegionPath);
		});

		// TODO: createPdxEnum is server-side, not in GUD API
		// when(mockGudRegionService.createPdxEnum(anyString(), anyString(), anyInt()))
		//		.thenThrow(newUnsupportedOperationException(NOT_SUPPORTED));

		when(mockGudRegionService.createPdxInstanceFactory(anyString()))
				.thenThrow(newUnsupportedOperationException(NOT_SUPPORTED));

		when(mockGudRegionService.rootRegions()).thenAnswer(invocation -> regions.values().stream()
				.filter(GemFireMockObjectsSupport::isRootRegion).collect(Collectors.toSet()));

		return mockGudRegionService;
	}

	public static GudClientCache mockGudClientCache() {

		GudClientCache mockGudClientCache = mock(GudClientCache.class);

		doAnswer(newVoidAnswer(invocation -> mockGudClientCache.close())).when(mockGudClientCache).close(anyBoolean());

		when(mockGudClientCache.createClientRegionFactory(any(GudClientRegionShortcut.class))).thenAnswer(
				invocation -> mockGudClientRegionFactory(mockGudClientCache, invocation.<GudClientRegionShortcut> getArgument(0)));

		// TODO: GudClientCache doesn't have createClientRegionFactory(String) - GUD API uses enum shortcuts
		// when(mockGudClientCache.createClientRegionFactory(anyString()))
		//		.thenAnswer(invocation -> mockGudClientRegionFactory(mockGudClientCache, invocation.<String> getArgument(0)));

		return referTo(mockGudQueryService(mockCacheApi(mockGudClientCache)));
	}

	public static GudClientCache mockGemFireCache() {

		GudClientCache mockGemFireCache = mock(GudClientCache.class);

		return referTo(mockGudQueryService(mockCacheApi(mockGemFireCache)));
	}

	public static GudCacheTransactionManager mockGudCacheTransactionManager() {

		GudCacheTransactionManager mockGudCacheTransactionManager = mock(GudCacheTransactionManager.class);

		AtomicBoolean distributed = new AtomicBoolean(false);

		AtomicReference<GudTransactionWriter> transactionWriter = new AtomicReference<>(null);

		List<GudTransactionListener> transactionListeners = new CopyOnWriteArrayList<>();

		doReturn(false).when(mockGudCacheTransactionManager).exists();
		doReturn(false).when(mockGudCacheTransactionManager).exists(any(GudTransactionId.class));
		doAnswer(newGetter(distributed)).when(mockGudCacheTransactionManager).isDistributed();
		doReturn(false).when(mockGudCacheTransactionManager).isSuspended(any(GudTransactionId.class));

		doAnswer(invocation -> transactionListeners.add(invocation.getArgument(0))).when(mockGudCacheTransactionManager)
				.addListener(any(GudTransactionListener.class));

		doAnswer(invocation -> transactionListeners.toArray(new GudTransactionListener[0])).when(mockGudCacheTransactionManager)
				.getListeners();

		doAnswer(newGetter(transactionWriter)).when(mockGudCacheTransactionManager).getWriter();

		doAnswer(invocation -> {

			GudTransactionListener[] newGudTransactionListeners = invocation.getArgument(0);

			transactionListeners.forEach(GudCacheCallback::close);
			transactionListeners.clear();

			Collections.addAll(transactionListeners, newGudTransactionListeners);

			return null;

		}).when(mockGudCacheTransactionManager).initListeners(any(GudTransactionListener[].class));

		doAnswer(invocation -> transactionListeners.remove(invocation.getArgument(0))).when(mockGudCacheTransactionManager)
				.removeListener(any(GudTransactionListener.class));

		doAnswer(newSetter(distributed, null)).when(mockGudCacheTransactionManager).setDistributed(anyBoolean());

		doAnswer(newSetter(transactionWriter)).when(mockGudCacheTransactionManager).setWriter(any(GudTransactionWriter.class));

		return mockGudCacheTransactionManager;
	}

	public static <K, V> GudClientRegionFactory<K, V> mockGudClientRegionFactory(GudClientCache mockGudClientCache,
			GudClientRegionShortcut clientGudRegionShortcut) {

		return mockGudClientRegionFactory(mockGudClientCache,
				resolveGudRegionAttributesFromGudClientRegionShortcut(clientGudRegionShortcut), clientGudRegionShortcut);
	}

	public static <K, V> GudClientRegionFactory<K, V> mockGudClientRegionFactory(GudClientCache mockGudClientCache,
			String regionAttributesId) {

		return mockGudClientRegionFactory(mockGudClientCache, resolveGudRegionAttributes(regionAttributesId), null);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> GudClientRegionFactory<K, V> mockGudClientRegionFactory(GudClientCache mockGudClientCache,
			GudRegionAttributes<K, V> regionAttributes, GudClientRegionShortcut clientGudRegionShortcut) {

		GudClientRegionFactory<K, V> mockGudClientRegionFactory = mock(GudClientRegionFactory.class,
				mockObjectIdentifier("MockGudClientRegionFactory"));

		GudExpirationAttributes DEFAULT_EXPIRATION_ATTRIBUTES = GudExpirationAttributes.of(0, GudExpirationAction.INVALIDATE);

		Optional<GudRegionAttributes<K, V>> optionalGudRegionAttributes = Optional.ofNullable(regionAttributes);

		AtomicBoolean cloningEnabled = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCloningEnabled).orElse(false));

		AtomicBoolean concurrencyChecksEnabled = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getConcurrencyChecksEnabled).orElse(false));

		AtomicBoolean diskSynchronous = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::isDiskSynchronous).orElse(true));

		AtomicBoolean statisticsEnabled = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getStatisticsEnabled).orElse(false));

		AtomicInteger concurrencyLevel = new AtomicInteger(
				optionalGudRegionAttributes.map(GudRegionAttributes::getConcurrencyLevel).orElse(16));

		AtomicInteger initialCapacity = new AtomicInteger(
				optionalGudRegionAttributes.map(GudRegionAttributes::getInitialCapacity).orElse(16));

		AtomicReference<GudCompressor> compressor = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCompressor).orElse(null));

		AtomicReference<GudCustomExpiry<K, V>> customEntryIdleTimeout = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCustomEntryIdleTimeout).orElse(null));

		AtomicReference<GudCustomExpiry<K, V>> customEntryTimeToLive = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCustomEntryTimeToLive).orElse(null));

		AtomicReference<GudDataPolicy> dataPolicy = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getDataPolicy).orElseGet(() -> convert(clientGudRegionShortcut)));

		AtomicReference<String> diskStoreName = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getDiskStoreName).orElse(null));

		AtomicReference<GudExpirationAttributes> entryIdleTimeout = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getEntryIdleTimeout).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudExpirationAttributes> entryTimeToLive = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getEntryTimeToLive).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudEvictionAttributes> evictionAttributes = new AtomicReference<>(optionalGudRegionAttributes
				.map(GudRegionAttributes::getEvictionAttributes).orElseGet(GudEvictionAttributes::createLRUEntryAttributes));

		AtomicReference<Class<K>> keyConstraint = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getKeyConstraint).orElse(null));

		AtomicReference<Float> loadFactor = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getLoadFactor).orElse(0.75f));

		AtomicReference<String> poolName = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getPoolName).orElse(null));

		AtomicReference<GudExpirationAttributes> regionIdleTimeout = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getRegionIdleTimeout).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudExpirationAttributes> regionTimeToLive = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getRegionTimeToLive).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<Class<V>> valueConstraint = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getValueConstraint).orElse(null));

		List<GudCacheListener> cacheListeners = new ArrayList<>(Arrays.asList(nullSafeArray(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCacheListeners).orElse(null), GudCacheListener.class)));

		when(mockGudClientRegionFactory.addCacheListener(any(GudCacheListener.class)))
				.thenAnswer(newAdder(cacheListeners, mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.initCacheListeners(any(GudCacheListener[].class))).thenAnswer(invocation -> {
			cacheListeners.clear();
			Collections.addAll(cacheListeners, invocation.getArgument(0));
			return mockGudClientRegionFactory;
		});

		when(mockGudClientRegionFactory.setCloningEnabled(anyBoolean()))
				.thenAnswer(newSetter(cloningEnabled, mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setCompressor(any(GudCompressor.class)))
				.thenAnswer(newSetter(compressor, () -> mockGudClientRegionFactory));

		doAnswer(newSetter(concurrencyChecksEnabled, mockGudClientRegionFactory)).when(mockGudClientRegionFactory)
				.setConcurrencyChecksEnabled(anyBoolean());

		when(mockGudClientRegionFactory.setConcurrencyLevel(anyInt()))
				.thenAnswer(newSetter(concurrencyLevel, mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setCustomEntryIdleTimeout(any(GudCustomExpiry.class)))
				.thenAnswer(newSetter(customEntryIdleTimeout, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setCustomEntryTimeToLive(any(GudCustomExpiry.class)))
				.thenAnswer(newSetter(customEntryTimeToLive, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setDiskStoreName(anyString()))
				.thenAnswer(newSetter(diskStoreName, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setDiskSynchronous(anyBoolean()))
				.thenAnswer(newSetter(diskSynchronous, mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setEntryIdleTimeout(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(entryIdleTimeout, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setEntryTimeToLive(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(entryTimeToLive, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setEvictionAttributes(any(GudEvictionAttributes.class)))
				.thenAnswer(newSetter(evictionAttributes, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setInitialCapacity(anyInt()))
				.thenAnswer(newSetter(initialCapacity, mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setKeyConstraint(any(Class.class)))
				.thenAnswer(newSetter(keyConstraint, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setLoadFactor(anyFloat()))
				.thenAnswer(newSetter(loadFactor, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setPoolName(anyString()))
				.thenAnswer(newSetter(poolName, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setRegionIdleTimeout(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(regionIdleTimeout, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setRegionTimeToLive(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(regionTimeToLive, () -> mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setStatisticsEnabled(anyBoolean()))
				.thenAnswer(newSetter(statisticsEnabled, mockGudClientRegionFactory));

		when(mockGudClientRegionFactory.setValueConstraint(any(Class.class)))
				.thenAnswer(newSetter(valueConstraint, () -> mockGudClientRegionFactory));

		GudRegionAttributes<K, V> mockGudRegionAttributes = mock(GudRegionAttributes.class,
				mockObjectIdentifier("MockGudRegionAttributes"));

		when(mockGudRegionAttributes.getCacheListeners())
				.thenAnswer(newGetter(() -> cacheListeners.toArray(new GudCacheListener[cacheListeners.size()])));

		when(mockGudRegionAttributes.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled));
		when(mockGudRegionAttributes.getCompressor()).thenAnswer(newGetter(compressor));
		when(mockGudRegionAttributes.getConcurrencyChecksEnabled()).thenAnswer(newGetter(concurrencyChecksEnabled));
		when(mockGudRegionAttributes.getConcurrencyLevel()).thenAnswer(newGetter(concurrencyLevel));
		when(mockGudRegionAttributes.getCustomEntryIdleTimeout()).thenAnswer(newGetter(customEntryIdleTimeout));
		when(mockGudRegionAttributes.getCustomEntryTimeToLive()).thenAnswer(newGetter(customEntryTimeToLive));
		when(mockGudRegionAttributes.getDataPolicy()).thenAnswer(newGetter(dataPolicy));
		when(mockGudRegionAttributes.getDiskStoreName()).thenAnswer(newGetter(diskStoreName));
		when(mockGudRegionAttributes.isDiskSynchronous()).thenAnswer(newGetter(diskSynchronous));
		when(mockGudRegionAttributes.getEntryIdleTimeout()).thenAnswer(newGetter(entryIdleTimeout));
		when(mockGudRegionAttributes.getEntryTimeToLive()).thenAnswer(newGetter(entryTimeToLive));
		when(mockGudRegionAttributes.getEvictionAttributes()).thenAnswer(newGetter(evictionAttributes));
		when(mockGudRegionAttributes.getInitialCapacity()).thenAnswer(newGetter(initialCapacity));
		when(mockGudRegionAttributes.getKeyConstraint()).thenAnswer(newGetter(keyConstraint));
		when(mockGudRegionAttributes.getLoadFactor()).thenAnswer(newGetter(loadFactor));
		when(mockGudRegionAttributes.getPoolName()).thenAnswer(newGetter(poolName));
		when(mockGudRegionAttributes.getRegionIdleTimeout()).thenAnswer(newGetter(regionIdleTimeout));
		when(mockGudRegionAttributes.getRegionTimeToLive()).thenAnswer(newGetter(regionTimeToLive));
		when(mockGudRegionAttributes.getStatisticsEnabled()).thenAnswer(newGetter(statisticsEnabled));
		when(mockGudRegionAttributes.getValueConstraint()).thenAnswer(newGetter(valueConstraint));

		when(mockGudClientRegionFactory.create(anyString()))
				.thenAnswer(invocation -> mockRegion(mockGudClientCache, invocation.getArgument(0), mockGudRegionAttributes));

		when(mockGudClientRegionFactory.createSubregion(any(GudRegion.class), anyString())).thenAnswer(
				invocation -> mockSubRegion(invocation.getArgument(0), invocation.getArgument(1), mockGudRegionAttributes));

		return mockGudClientRegionFactory;
	}

	public static GudClientSubscriptionConfig mockGudClientSubscriptionConfig() {

		GudClientSubscriptionConfig mockGudClientSubscriptionConfig = mock(GudClientSubscriptionConfig.class);

		AtomicInteger subscriptionCapacity = new AtomicInteger(GudClientSubscriptionConfig.DEFAULT_CAPACITY);

		AtomicReference<String> subscriptionGudDiskStoreName = new AtomicReference<>("");

		AtomicReference<SubscriptionEvictionPolicy> subscriptionEvictionPolicy = new AtomicReference<>(
				SubscriptionEvictionPolicy.DEFAULT);

		Function<String, SubscriptionEvictionPolicy> stringToSubscriptionEvictionPolicyConverter = arg -> SubscriptionEvictionPolicy
				.valueOfIgnoreCase(String.valueOf(arg));

		Function<SubscriptionEvictionPolicy, String> subscriptionEvictionPolicyToStringConverter = arg -> Optional
				.ofNullable(arg).map(Object::toString).map(String::toLowerCase).orElse(null);

		doAnswer(newSetter(subscriptionCapacity, null)).when(mockGudClientSubscriptionConfig).setCapacity(anyInt());

		doAnswer(newSetter(subscriptionGudDiskStoreName, () -> null)).when(mockGudClientSubscriptionConfig)
				.setDiskStoreName(anyString());

		doAnswer(newSetter(subscriptionEvictionPolicy, stringToSubscriptionEvictionPolicyConverter, () -> null))
				.when(mockGudClientSubscriptionConfig).setEvictionPolicy(anyString());

		when(mockGudClientSubscriptionConfig.getCapacity()).thenAnswer(newGetter(subscriptionCapacity));
		when(mockGudClientSubscriptionConfig.getDiskStoreName()).thenAnswer(newGetter(subscriptionGudDiskStoreName));
		when(mockGudClientSubscriptionConfig.getEvictionPolicy())
				.thenAnswer(newGetter(subscriptionEvictionPolicy, subscriptionEvictionPolicyToStringConverter));

		return mockGudClientSubscriptionConfig;
	}

	public static GudDiskStoreFactory mockGudDiskStoreFactory() {

		GudDiskStoreFactory mockGudDiskStoreFactory = mock(GudDiskStoreFactory.class);

		AtomicBoolean allowForceCompaction = new AtomicBoolean(GudDiskStoreFactory.DEFAULT_ALLOW_FORCE_COMPACTION);
		AtomicBoolean autoCompact = new AtomicBoolean(GudDiskStoreFactory.DEFAULT_AUTO_COMPACT);

		AtomicInteger compactionThreshold = new AtomicInteger(GudDiskStoreFactory.DEFAULT_COMPACTION_THRESHOLD);
		AtomicInteger queueSize = new AtomicInteger(GudDiskStoreFactory.DEFAULT_QUEUE_SIZE);
		AtomicInteger writeBufferSize = new AtomicInteger(GudDiskStoreFactory.DEFAULT_WRITE_BUFFER_SIZE);
		AtomicInteger segments = new AtomicInteger(GudDiskStoreFactory.DEFAULT_SEGMENTS);

		AtomicLong maxOplogSize = new AtomicLong(GudDiskStoreFactory.DEFAULT_MAX_OPLOG_SIZE);
		AtomicLong timeInterval = new AtomicLong(GudDiskStoreFactory.DEFAULT_TIME_INTERVAL);

		AtomicReference<File[]> diskDirectories = new AtomicReference<>(new File[] { FileSystemUtils.WORKING_DIRECTORY });

		AtomicReference<int[]> diskDiretorySizes = new AtomicReference<>(new int[0]);

		AtomicReference<Float> diskUsageCriticalPercentage = new AtomicReference<>(
				GudDiskStoreFactory.DEFAULT_DISK_USAGE_CRITICAL_PERCENTAGE);

		AtomicReference<Float> diskUsageWarningPercentage = new AtomicReference<>(
				GudDiskStoreFactory.DEFAULT_DISK_USAGE_WARNING_PERCENTAGE);

		when(mockGudDiskStoreFactory.setAllowForceCompaction(anyBoolean()))
				.thenAnswer(newSetter(allowForceCompaction, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setAutoCompact(anyBoolean())).thenAnswer(newSetter(autoCompact, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setCompactionThreshold(anyInt()))
				.thenAnswer(newSetter(compactionThreshold, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setDiskDirs(any(File[].class))).thenAnswer(invocation -> {

			File[] resolveDiskDirectories = nullSafeArray(invocation.getArgument(0), File.class);

			int[] resolvedDiskDirectorySizes = new int[resolveDiskDirectories.length];

			Arrays.fill(resolvedDiskDirectorySizes, GudDiskStoreFactory.DEFAULT_DISK_DIR_SIZE);

			diskDirectories.set(resolveDiskDirectories);
			diskDiretorySizes.set(resolvedDiskDirectorySizes);

			return mockGudDiskStoreFactory;
		});

		when(mockGudDiskStoreFactory.setDiskDirsAndSizes(any(File[].class), any(int[].class))).thenAnswer(invocation -> {

			diskDirectories.set(invocation.getArgument(0));
			diskDiretorySizes.set(invocation.getArgument(1));

			return mockGudDiskStoreFactory;
		});

		when(mockGudDiskStoreFactory.setDiskUsageCriticalPercentage(anyFloat()))
				.thenAnswer(newSetter(diskUsageCriticalPercentage, () -> mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setDiskUsageWarningPercentage(anyFloat()))
				.thenAnswer(newSetter(diskUsageWarningPercentage, () -> mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setMaxOplogSize(anyLong())).thenAnswer(newSetter(maxOplogSize, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setQueueSize(anyInt())).thenAnswer(newSetter(queueSize, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setTimeInterval(anyLong())).thenAnswer(newSetter(timeInterval, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setWriteBufferSize(anyInt()))
				.thenAnswer(newSetter(writeBufferSize, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.setSegments(anyInt())).thenAnswer(newSetter(segments, mockGudDiskStoreFactory));

		when(mockGudDiskStoreFactory.create(anyString())).thenAnswer(invocation -> {

			String name = invocation.getArgument(0);

			GudDiskStore mockGudDiskStore = mock(GudDiskStore.class, name);

			when(mockGudDiskStore.getAllowForceCompaction()).thenReturn(allowForceCompaction.get());
			when(mockGudDiskStore.getAutoCompact()).thenReturn(autoCompact.get());
			when(mockGudDiskStore.getCompactionThreshold()).thenReturn(compactionThreshold.get());
			when(mockGudDiskStore.getDiskDirs()).thenReturn(diskDirectories.get());
			when(mockGudDiskStore.getDiskDirSizes()).thenReturn(diskDiretorySizes.get());
			when(mockGudDiskStore.getDiskUsageCriticalPercentage()).thenReturn(diskUsageCriticalPercentage.get());
			when(mockGudDiskStore.getDiskUsageWarningPercentage()).thenReturn(diskUsageWarningPercentage.get());
			when(mockGudDiskStore.getDiskStoreUUID()).thenReturn(UUID.randomUUID());
			when(mockGudDiskStore.getMaxOplogSize()).thenReturn(maxOplogSize.get());
			when(mockGudDiskStore.getName()).thenReturn(name);
			when(mockGudDiskStore.getQueueSize()).thenReturn(queueSize.get());
			when(mockGudDiskStore.getTimeInterval()).thenReturn(timeInterval.get());
			when(mockGudDiskStore.getWriteBufferSize()).thenReturn(writeBufferSize.get());
			when(mockGudDiskStore.getSegments()).thenReturn(new int[] { segments.get() });

			diskStores.put(name, mockGudDiskStore);

			return mockGudDiskStore;
		});

		return mockGudDiskStoreFactory;
	}

	public static GudDistributedMember mockGudDistributedMember() {

		GudDistributedMember mockGudDistributedMember = mock(GudDistributedMember.class);

		when(mockGudDistributedMember.getGroups()).thenAnswer(invocation -> new ArrayList<>(
				StringUtils.commaDelimitedListToSet(gemfireProperties.get().getProperty(GudConfigurationProperties.GROUPS))));

		when(mockGudDistributedMember.getHost())
				.thenReturn(ObjectUtils.doOperationSafely(() -> InetAddress.getLocalHost().getHostName(), null));

		when(mockGudDistributedMember.getName())
				.thenAnswer(invocation -> gemfireProperties.get().getProperty(GudConfigurationProperties.NAME_NAME));

		return mockGudDistributedMember;
	}

	public static GudDistributedSystem mockGudDistributedSystem() {

		GudDistributedMember mockGudDistributedMember = mockGudDistributedMember();

		GudDistributedSystem mockGudDistributedSystem = mock(GudDistributedSystem.class);

		doAnswer(invocation -> gemfireProperties.get().getProperty(GudConfigurationProperties.NAME_NAME))
				.when(mockGudDistributedSystem).getName();

		when(mockGudDistributedSystem.getDistributedMember()).thenReturn(mockGudDistributedMember);
		when(mockGudDistributedSystem.getProperties()).thenAnswer(invocation -> gemfireProperties.get());
		when(mockGudDistributedSystem.getReconnectedSystem()).thenAnswer(invocation -> mockGudDistributedSystem());

		return mockGudDistributedSystem;
	}

	public static GudPoolFactory mockGudPoolFactory() {

		GudPoolFactory mockGudPoolFactory = mock(GudPoolFactory.class);

		AtomicBoolean multiuserAuthentication = new AtomicBoolean(GudPoolFactory.DEFAULT_MULTIUSER_AUTHENTICATION);
		AtomicBoolean prSingleHopEnabled = new AtomicBoolean(GudPoolFactory.DEFAULT_PR_SINGLE_HOP_ENABLED);
		AtomicBoolean subscriptionEnabled = new AtomicBoolean(GudPoolFactory.DEFAULT_SUBSCRIPTION_ENABLED);
		// AtomicBoolean threadLocalConnections = new AtomicBoolean(GudPoolFactory.DEFAULT_THREAD_LOCAL_CONNECTIONS);

		AtomicInteger freeConnectionTimeout = new AtomicInteger(GudPoolFactory.DEFAULT_FREE_CONNECTION_TIMEOUT);
		AtomicInteger loadConditioningInterval = new AtomicInteger(GudPoolFactory.DEFAULT_LOAD_CONDITIONING_INTERVAL);
		AtomicInteger maxConnections = new AtomicInteger(GudPoolFactory.DEFAULT_MAX_CONNECTIONS);
		AtomicInteger minConnections = new AtomicInteger(GudPoolFactory.DEFAULT_MIN_CONNECTIONS);
		AtomicInteger maxConnectionsPerServer = new AtomicInteger(GudPoolFactory.DEFAULT_MAX_CONNECTIONS_PER_SERVER);
		AtomicInteger minConnectionsPerServer = new AtomicInteger(GudPoolFactory.DEFAULT_MIN_CONNECTIONS_PER_SERVER);
		AtomicInteger readTimeout = new AtomicInteger(GudPoolFactory.DEFAULT_READ_TIMEOUT);
		AtomicInteger retryAttempts = new AtomicInteger(GudPoolFactory.DEFAULT_RETRY_ATTEMPTS);
		AtomicInteger serverConnectionTimeout = new AtomicInteger(GudPoolFactory.DEFAULT_SERVER_CONNECTION_TIMEOUT);
		AtomicInteger socketBufferSize = new AtomicInteger(GudPoolFactory.DEFAULT_SOCKET_BUFFER_SIZE);
		AtomicInteger socketConnectTimeout = new AtomicInteger(GudPoolFactory.DEFAULT_SOCKET_CONNECT_TIMEOUT);
		AtomicInteger statisticInterval = new AtomicInteger(GudPoolFactory.DEFAULT_STATISTIC_INTERVAL);
		AtomicInteger subscriptionAckInterval = new AtomicInteger(GudPoolFactory.DEFAULT_SUBSCRIPTION_ACK_INTERVAL);
		AtomicInteger subscriptionMessageTrackingTimeout = new AtomicInteger(
				GudPoolFactory.DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT);
		AtomicInteger subscriptionRedundancy = new AtomicInteger(GudPoolFactory.DEFAULT_SUBSCRIPTION_REDUNDANCY);

		AtomicLong idleTimeout = new AtomicLong(GudPoolFactory.DEFAULT_IDLE_TIMEOUT);
		AtomicLong pingInterval = new AtomicLong(GudPoolFactory.DEFAULT_PING_INTERVAL);

		AtomicReference<GudSocketFactory> socketFactory = new AtomicReference<>(GudPoolFactory.DEFAULT_SOCKET_FACTORY);
		AtomicReference<String> serverGroup = new AtomicReference<>(GudPoolFactory.DEFAULT_SERVER_GROUP);

		List<InetSocketAddress> locators = new ArrayList<>();
		List<InetSocketAddress> servers = new ArrayList<>();

		when(mockGudPoolFactory.addLocator(anyString(), anyInt())).thenAnswer(invocation -> {
			locators.add(new InetSocketAddress(invocation.<String> getArgument(0), invocation.getArgument(1)));
			return mockGudPoolFactory;
		});

		when(mockGudPoolFactory.addServer(anyString(), anyInt())).thenAnswer(invocation -> {
			servers.add(new InetSocketAddress(invocation.<String> getArgument(0), invocation.getArgument(1)));
			return mockGudPoolFactory;
		});

		when(mockGudPoolFactory.setFreeConnectionTimeout(anyInt()))
				.thenAnswer(newSetter(freeConnectionTimeout, mockGudPoolFactory));

		when(mockGudPoolFactory.setIdleTimeout(anyLong())).thenAnswer(newSetter(idleTimeout, mockGudPoolFactory));

		when(mockGudPoolFactory.setLoadConditioningInterval(anyInt()))
				.thenAnswer(newSetter(loadConditioningInterval, mockGudPoolFactory));

		when(mockGudPoolFactory.setMaxConnections(anyInt())).thenAnswer(newSetter(maxConnections, mockGudPoolFactory));

		when(mockGudPoolFactory.setMinConnections(anyInt())).thenAnswer(newSetter(minConnections, mockGudPoolFactory));

		when(mockGudPoolFactory.setMaxConnectionsPerServer(anyInt()))
				.thenAnswer(newSetter(maxConnectionsPerServer, mockGudPoolFactory));

		when(mockGudPoolFactory.setMinConnectionsPerServer(anyInt()))
				.thenAnswer(newSetter(minConnectionsPerServer, mockGudPoolFactory));

		when(mockGudPoolFactory.setMultiuserAuthentication(anyBoolean()))
				.thenAnswer(newSetter(multiuserAuthentication, mockGudPoolFactory));

		when(mockGudPoolFactory.setPingInterval(anyLong())).thenAnswer(newSetter(pingInterval, mockGudPoolFactory));

		when(mockGudPoolFactory.setPRSingleHopEnabled(anyBoolean()))
				.thenAnswer(newSetter(prSingleHopEnabled, mockGudPoolFactory));

		when(mockGudPoolFactory.setReadTimeout(anyInt())).thenAnswer(newSetter(readTimeout, mockGudPoolFactory));

		when(mockGudPoolFactory.setRetryAttempts(anyInt())).thenAnswer(newSetter(retryAttempts, mockGudPoolFactory));

		when(mockGudPoolFactory.setServerConnectionTimeout(anyInt()))
				.thenAnswer(newSetter(serverConnectionTimeout, mockGudPoolFactory));

		when(mockGudPoolFactory.setServerGroup(anyString())).thenAnswer(newSetter(serverGroup, () -> mockGudPoolFactory));

		when(mockGudPoolFactory.setSocketBufferSize(anyInt())).thenAnswer(newSetter(socketBufferSize, mockGudPoolFactory));

		when(mockGudPoolFactory.setSocketConnectTimeout(anyInt()))
				.thenAnswer(newSetter(socketConnectTimeout, mockGudPoolFactory));

		when(mockGudPoolFactory.setSocketFactory(any(GudSocketFactory.class)))
				.thenAnswer(newSetter(socketFactory, () -> mockGudPoolFactory));

		when(mockGudPoolFactory.setStatisticInterval(anyInt())).thenAnswer(newSetter(statisticInterval, mockGudPoolFactory));

		when(mockGudPoolFactory.setSubscriptionAckInterval(anyInt()))
				.thenAnswer(newSetter(subscriptionAckInterval, mockGudPoolFactory));

		when(mockGudPoolFactory.setSubscriptionEnabled(anyBoolean()))
				.thenAnswer(newSetter(subscriptionEnabled, mockGudPoolFactory));

		when(mockGudPoolFactory.setSubscriptionMessageTrackingTimeout(anyInt()))
				.thenAnswer(newSetter(subscriptionMessageTrackingTimeout, mockGudPoolFactory));

		when(mockGudPoolFactory.setSubscriptionRedundancy(anyInt()))
				.thenAnswer(newSetter(subscriptionRedundancy, mockGudPoolFactory));

		// when(mockGudPoolFactory.setThreadLocalConnections(anyBoolean()))
		// .thenAnswer(newSetter(threadLocalConnections, mockGudPoolFactory));

		when(mockGudPoolFactory.create(anyString())).thenAnswer(invocation -> {

			String name = invocation.getArgument(0);

			GudPool mockGudPool = mock(GudPool.class, name);

			AtomicReference<GudQueryService> queryService = new AtomicReference<>(null);

			AtomicBoolean destroyed = new AtomicBoolean(false);

			doAnswer(invocationOnMock -> {
				destroyed.set(true);
				return null;
			}).when(mockGudPool).destroy();

			doAnswer(invocationOnMock -> {
				destroyed.set(true);
				return null;
			}).when(mockGudPool).destroy(anyBoolean());

			when(mockGudPool.isDestroyed()).thenAnswer(newGetter(destroyed));
			when(mockGudPool.getFreeConnectionTimeout()).thenReturn(freeConnectionTimeout.get());
			when(mockGudPool.getIdleTimeout()).thenReturn(idleTimeout.get());
			when(mockGudPool.getLoadConditioningInterval()).thenReturn(loadConditioningInterval.get());
			when(mockGudPool.getLocators()).thenReturn(locators);
			when(mockGudPool.getMaxConnections()).thenReturn(maxConnections.get());
			when(mockGudPool.getMinConnections()).thenReturn(minConnections.get());
			when(mockGudPool.getMaxConnectionsPerServer()).thenReturn(maxConnectionsPerServer.get());
			when(mockGudPool.getMinConnectionsPerServer()).thenReturn(minConnectionsPerServer.get());
			when(mockGudPool.getMultiuserAuthentication()).thenReturn(multiuserAuthentication.get());
			when(mockGudPool.getName()).thenReturn(name);
			when(mockGudPool.getPingInterval()).thenReturn(pingInterval.get());
			when(mockGudPool.getPRSingleHopEnabled()).thenReturn(prSingleHopEnabled.get());
			when(mockGudPool.getReadTimeout()).thenReturn(readTimeout.get());
			when(mockGudPool.getRetryAttempts()).thenReturn(retryAttempts.get());
			when(mockGudPool.getServerConnectionTimeout()).thenReturn(serverConnectionTimeout.get());
			when(mockGudPool.getServerGroup()).thenReturn(serverGroup.get());
			when(mockGudPool.getServers()).thenReturn(servers);
			when(mockGudPool.getSocketBufferSize()).thenReturn(socketBufferSize.get());
			when(mockGudPool.getSocketConnectTimeout()).thenReturn(socketConnectTimeout.get());
			when(mockGudPool.getSocketFactory()).thenReturn(socketFactory.get());
			when(mockGudPool.getStatisticInterval()).thenReturn(statisticInterval.get());
			when(mockGudPool.getSubscriptionAckInterval()).thenReturn(subscriptionAckInterval.get());
			when(mockGudPool.getSubscriptionEnabled()).thenReturn(subscriptionEnabled.get());
			when(mockGudPool.getSubscriptionMessageTrackingTimeout()).thenReturn(subscriptionMessageTrackingTimeout.get());
			when(mockGudPool.getSubscriptionRedundancy()).thenReturn(subscriptionRedundancy.get());
			// when(mockGudPool.getThreadLocalConnections()).thenReturn(threadLocalConnections.get());

			doAnswer(getGudQueryServiceInvocation -> resolveAnyGemFireCache().map(GudClientCache::getQueryService)
					.orElseGet(() -> queryService.updateAndGet(it -> it != null ? it : mockGudQueryService()))).when(mockGudPool)
					.getQueryService();

			register(mockGudPool);

			return mockGudPool;
		});

		return mockGudPoolFactory;
	}

	private static GudPool register(GudPool pool) {
		// TODO: GUD API doesn't expose internal PoolManagerImpl for registration
		// In test environment, pools are managed through mocking
		registeredGudPoolNames.add(pool.getName());
		return pool;
	}

	private static GudPool unregister(GudPool pool) {
		// TODO: GUD API doesn't expose internal PoolManagerImpl for unregistration
		// In test environment, pools are managed through mocking
		registeredGudPoolNames.remove(pool.getName());
		return pool;
	}

	public static GudPool mockGudQueryService(GudPool pool) {

		GudQueryService mockGudQueryService = mockGudQueryService();

		when(pool.getQueryService()).thenReturn(mockGudQueryService);

		return pool;
	}

	public static <T extends GudRegionService> T mockGudQueryService(T regionService) {

		GudQueryService mockGudQueryService = mockGudQueryService();

		doReturn(mockGudQueryService).when(regionService).getQueryService();

		if (regionService instanceof GudClientCache) {
			doReturn(mockGudQueryService).when((GudClientCache) regionService).getLocalQueryService();
			doReturn(mockGudQueryService).when((GudClientCache) regionService).getQueryService(anyString());
		}

		return regionService;
	}

	// TODO: write additional mocking logic for the GudQueryService interface
	public static GudQueryService mockGudQueryService() {

		GudQueryService mockGudQueryService = mock(GudQueryService.class);

		Set<GudCqQuery> cqQueries = Collections.synchronizedSet(new HashSet<>());
		Set<GudIndex> indexes = Collections.synchronizedSet(new HashSet<>());

		try {

			when(mockGudQueryService.getCq(anyString())).thenAnswer(invocation -> cqQueries.stream()
					.filter(cqGudQuery -> invocation.getArgument(0).equals(cqGudQuery.getName())).findFirst().orElse(null));

			when(mockGudQueryService.getCqs()).thenAnswer(invocation -> cqQueries.toArray(new GudCqQuery[cqQueries.size()]));

			when(mockGudQueryService.getCqs(anyString())).thenAnswer(invocation -> {

				List<GudCqQuery> cqQueriesByRegion = cqQueries.stream().filter(cqGudQuery -> {

					String queryString = cqGudQuery.getQueryString();

					int indexOfFromClause = queryString.indexOf(FROM_KEYWORD);
					int indexOfWhereClause = queryString.indexOf(WHERE_KEYWORD);

					queryString = (indexOfFromClause > -1 ? queryString.substring(indexOfFromClause + FROM_KEYWORD.length())
							: queryString);

					queryString = (indexOfWhereClause > 0 ? queryString.substring(0, indexOfWhereClause) : queryString);

					queryString = (queryString.startsWith(GudRegion.SEPARATOR) ? queryString.substring(1) : queryString);

					return invocation.getArgument(0).equals(queryString.trim());

				}).collect(Collectors.toList());

				return cqQueriesByRegion.toArray(new GudCqQuery[cqQueriesByRegion.size()]);
			});

			when(mockGudQueryService.getIndex(any(GudRegion.class), anyString())).thenAnswer(invocation -> {

				GudRegion<?, ?> region = invocation.getArgument(0);

				String indexName = invocation.getArgument(1);

				Collection<GudIndex> indexesForRegion = mockGudQueryService.getIndexes(region);

				return indexesForRegion.stream().filter(index -> index.getName().equals(indexName)).findFirst().orElse(null);

			});

			when(mockGudQueryService.getIndexes()).thenReturn(indexes);

			when(mockGudQueryService.getIndexes(any(GudRegion.class))).thenAnswer(invocation -> {

				GudRegion<?, ?> region = invocation.getArgument(0);

				return indexes.stream().filter(index -> index.getRegion().equals(region)).collect(Collectors.toList());

			});

			when(mockGudQueryService.newCq(anyString(), any(GudCqAttributes.class))).thenAnswer(
					invocation -> add(cqQueries, mockGudCqQuery(null, invocation.getArgument(0), invocation.getArgument(1), false)));

			when(mockGudQueryService.newCq(anyString(), any(GudCqAttributes.class), anyBoolean()))
					.thenAnswer(invocation -> add(cqQueries,
							mockGudCqQuery(null, invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

			when(mockGudQueryService.newCq(anyString(), anyString(), any(GudCqAttributes.class)))
					.thenAnswer(invocation -> add(cqQueries,
							mockGudCqQuery(invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2), false)));

			when(mockGudQueryService.newCq(anyString(), anyString(), any(GudCqAttributes.class), anyBoolean()))
					.thenAnswer(invocation -> add(cqQueries, mockGudCqQuery(invocation.getArgument(0), invocation.getArgument(1),
							invocation.getArgument(2), invocation.getArgument(3))));

			when(mockGudQueryService.newQuery(anyString())).thenAnswer(invocation -> mockGudQuery(invocation.getArgument(0)));

		} catch (Exception cause) {
			throw new MockObjectInvocationException(cause);
		}

		return mockGudQueryService;
	}

	private static GudCqQuery add(Collection<GudCqQuery> cqQueries, GudCqQuery cqGudQuery) {

		cqQueries.add(cqGudQuery);

		return cqGudQuery;
	}

	private static GudIndex add(Collection<GudIndex> indexes, GudIndex index) {

		indexes.add(index);

		return index;
	}

	private static GudCqQuery mockGudCqQuery(String name, String queryString, GudCqAttributes cqAttributes, boolean durable) {

		GudCqQuery mockGudCqQuery = mock(GudCqQuery.class);

		GudQuery mockGudQuery = mockGudQuery(queryString);

		AtomicBoolean closed = new AtomicBoolean(false);
		AtomicBoolean running = new AtomicBoolean(false);
		AtomicBoolean stopped = new AtomicBoolean(true);

		when(mockGudCqQuery.getCqAttributes()).thenReturn(cqAttributes);
		when(mockGudCqQuery.getName()).thenReturn(name);
		when(mockGudCqQuery.getQuery()).thenReturn(mockGudQuery);
		when(mockGudCqQuery.getQueryString()).thenReturn(queryString);

		try {
			doAnswer(newSetter(closed, true, null)).when(mockGudCqQuery).close();

			doAnswer(invocation -> {

				running.set(true);
				stopped.set(false);

				return null;

			}).when(mockGudCqQuery).execute();

			doAnswer(invocation -> {

				running.set(false);
				stopped.set(true);

				return null;

			}).when(mockGudCqQuery).stop();
		} catch (Exception cause) {
			throw new MockObjectInvocationException(cause);
		}

		when(mockGudCqQuery.isClosed()).thenAnswer(newGetter(closed));
		when(mockGudCqQuery.isDurable()).thenReturn(durable);
		when(mockGudCqQuery.isRunning()).thenAnswer(newGetter(running));
		when(mockGudCqQuery.isStopped()).thenAnswer(newGetter(stopped));

		return mockGudCqQuery;
	}

	private static GudQuery mockGudQuery(String queryString) {

		GudQuery mockGudQuery = mock(GudQuery.class);

		GudQueryStatistics mockGudQueryStatistics = mockGudQueryStatistics(mockGudQuery);

		GudSelectResults<?> mockGudSelectResults = mockGudSelectResults();

		doReturn(queryString).when(mockGudQuery).getQueryString();
		doReturn(mockGudQueryStatistics).when(mockGudQuery).getStatistics();

		try {
			doReturn(mockGudSelectResults).when(mockGudQuery).execute();
			doReturn(mockGudSelectResults).when(mockGudQuery).execute(any(Object[].class));
			// TODO: GudQuery doesn't have execute(GudRegionFunctionContext) - server-side function execution
			// doReturn(mockGudSelectResults).when(mockGudQuery).execute(any(GudRegionFunctionContext.class));
			// doReturn(mockGudSelectResults).when(mockGudQuery).execute(any(GudRegionFunctionContext.class), any());
		} catch (Throwable cause) {
			throw new MockObjectInvocationException(cause);
		}

		return mockGudQuery;
	}

	private static GudQueryStatistics mockGudQueryStatistics(GudQuery query) {

		GudQueryStatistics mockGudQueryStatistics = mock(GudQueryStatistics.class);

		AtomicLong numberOfExecutions = new AtomicLong(0L);

		Answer<Object> executeAnswer = invocation -> {
			numberOfExecutions.incrementAndGet();
			return null;
		};

		try {
			when(query.execute()).thenAnswer(executeAnswer);
			when(query.execute(any(Object[].class))).thenAnswer(executeAnswer);
			// TODO: GudQuery doesn't have execute(GudRegionFunctionContext) - server-side function execution
			// when(query.execute(any(GudRegionFunctionContext.class))).thenAnswer(executeAnswer);
			// when(query.execute(any(GudRegionFunctionContext.class), any(Object[].class))).thenAnswer(executeAnswer);
		} catch (Exception cause) {
			throw new MockObjectInvocationException(cause);
		}

		when(mockGudQueryStatistics.getNumExecutions()).thenAnswer(newGetter(numberOfExecutions));
		when(mockGudQueryStatistics.getTotalExecutionTime()).thenReturn(0L);

		return mockGudQueryStatistics;
	}

	@SuppressWarnings("unchecked")
	private static <T> GudSelectResults<T> mockGudSelectResults() {

		GudObjectType mockGudObjectType = mock(GudObjectType.class, withSettings().lenient());

		doReturn(Object.class.getSimpleName()).when(mockGudObjectType).getSimpleClassName();
		doReturn(false).when(mockGudObjectType).isCollectionType();
		doReturn(false).when(mockGudObjectType).isMapType();
		doReturn(false).when(mockGudObjectType).isStructType();
		doReturn(Object.class).when(mockGudObjectType).resolveClass();

		GudCollectionType mockGudCollectionType = mock(GudCollectionType.class, withSettings().lenient());

		doReturn(false).when(mockGudCollectionType).allowsDuplicates();
		doReturn(mockGudObjectType).when(mockGudCollectionType).getElementType();
		doReturn(false).when(mockGudCollectionType).isOrdered();

		GudSelectResults<T> mockGudSelectResults = mock(GudSelectResults.class, withSettings().lenient());

		doReturn(Collections.emptyList()).when(mockGudSelectResults).asList();
		doReturn(Collections.emptySet()).when(mockGudSelectResults).asSet();
		doReturn(mockGudCollectionType).when(mockGudSelectResults).getCollectionType();
		doReturn(false).when(mockGudSelectResults).isModifiable();
		doReturn(Collections.emptyIterator()).when(mockGudSelectResults).iterator();
		doReturn(0).when(mockGudSelectResults).occurrences(any());
		doNothing().when(mockGudSelectResults).setElementType(any(GudObjectType.class));

		return mockGudSelectResults;
	}

	private static String fromClauseToRegionPath(String fromClause) {

		String regionName = String.valueOf(fromClause);

		int indexOfDot = regionName.indexOf(".");
		int indexOfSpace = regionName.indexOf(" ");

		regionName = regionName.startsWith(GudRegion.SEPARATOR) ? regionName : GemfireUtils.toRegionPath(regionName);
		regionName = indexOfSpace > -1 ? regionName.substring(0, indexOfSpace) : regionName;
		regionName = indexOfDot > -1 ? regionName.substring(0, indexOfDot) : regionName;

		return regionName;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> GudRegion<K, V> mockRegion(GudRegionService regionService, String name,
			GudRegionAttributes<K, V> regionAttributes) {

		GudRegion<K, V> mockRegion = mock(GudRegion.class, withSettings().name(name).lenient());

		GudRegionAttributes<K, V> mockGudRegionAttributes = mockGudRegionAttributes(mockRegion, regionAttributes);

		Set<GudRegion<?, ?>> subRegions = new CopyOnWriteArraySet<>();

		when(mockRegion.getFullPath()).thenReturn(toRegionPath(name));
		when(mockRegion.getName()).thenReturn(toRegionName(name));
		when(mockRegion.getRegionService()).thenReturn(regionService);

		mockRegionDataAccessOperations(mockRegion, mockGudRegionAttributes);

		doAnswer(invocation -> {

			String subRegionPath = toRegionPath(invocation.getArgument(0));
			String subRegionFullPath = String.format("%1$s%2$s", mockRegion.getFullPath(), subRegionPath);

			return regions.get(subRegionFullPath);

		}).when(mockRegion).getSubregion(anyString());

		doAnswer(invocation -> {

			boolean recursive = invocation.getArgument(0);

			return recursive
					? subRegions.stream().flatMap(subRegion -> subRegion.subregions(true).stream()).collect(Collectors.toSet())
					: subRegions;

		}).when(mockRegion).subregions(anyBoolean());

		return rememberMockedRegion(mockRegion);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> GudRegionAttributes<K, V> mockGudRegionAttributes(GudRegion<K, V> mockRegion,
			GudRegionAttributes<K, V> baseGudRegionAttributes) {

		GudAttributesMutator<K, V> mockGudAttributesMutator = mock(GudAttributesMutator.class, withSettings().lenient());

		GudEvictionAttributesMutator mockGudEvictionAttributesMutator = mock(GudEvictionAttributesMutator.class,
				withSettings().lenient());

		GudRegionAttributes<K, V> mockGudRegionAttributes = mock(GudRegionAttributes.class, withSettings().lenient());

		when(mockRegion.getAttributes()).thenReturn(mockGudRegionAttributes);
		when(mockRegion.getAttributesMutator()).thenReturn(mockGudAttributesMutator);
		when(mockGudAttributesMutator.getEvictionAttributesMutator()).thenReturn(mockGudEvictionAttributesMutator);
		when(mockGudAttributesMutator.getRegion()).thenReturn(mockRegion);

		AtomicBoolean cloningEnabled = new AtomicBoolean(baseGudRegionAttributes.getCloningEnabled());

		AtomicInteger evictionMaximum = new AtomicInteger(Optional.ofNullable(baseGudRegionAttributes.getEvictionAttributes())
				.map(GudEvictionAttributes::getMaximum).orElse(GudEvictionAttributes.DEFAULT_ENTRIES_MAXIMUM));

		AtomicReference<GudCacheLoader<K, V>> cacheLoader = new AtomicReference<>(baseGudRegionAttributes.getCacheLoader());

		AtomicReference<GudCacheWriter<K, V>> cacheWriter = new AtomicReference<>(baseGudRegionAttributes.getCacheWriter());

		AtomicReference<GudCustomExpiry<K, V>> customEntryIdleTimeout = new AtomicReference<>(
				baseGudRegionAttributes.getCustomEntryIdleTimeout());

		AtomicReference<GudCustomExpiry<K, V>> customEntryTimeToLive = new AtomicReference<>(
				baseGudRegionAttributes.getCustomEntryTimeToLive());

		AtomicReference<GudExpirationAttributes> entryIdleTimeout = new AtomicReference<>(
				baseGudRegionAttributes.getEntryIdleTimeout());

		AtomicReference<GudExpirationAttributes> entryTimeToLive = new AtomicReference<>(
				baseGudRegionAttributes.getEntryTimeToLive());

		AtomicReference<GudExpirationAttributes> regionIdleTimeout = new AtomicReference<>(
				baseGudRegionAttributes.getRegionIdleTimeout());

		AtomicReference<GudExpirationAttributes> regionTimeToLive = new AtomicReference<>(
				baseGudRegionAttributes.getRegionTimeToLive());

		List<GudCacheListener<K, V>> cacheListeners = new CopyOnWriteArrayList<>(
				nullSafeArray(baseGudRegionAttributes.getCacheListeners(), GudCacheListener.class));

		doAnswer(newAdder(cacheListeners, null)).when(mockGudAttributesMutator).addCacheListener(any(GudCacheListener.class));

		when(mockGudAttributesMutator.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled::get));

		doAnswer(invocation -> {

			GudCacheListener<K, V>[] cacheListenersArgument = nullSafeArray(invocation.getArgument(0), GudCacheListener.class);

			Arrays.stream(cacheListenersArgument)
					.forEach(it -> Assert.notNull(it, "The GudCacheListener[] must not contain null elements"));

			cacheListeners.forEach(GudCacheListener::close);
			cacheListeners.addAll(Arrays.asList(cacheListenersArgument));

			return null;

		}).when(mockGudAttributesMutator).initCacheListeners(any(GudCacheListener[].class));

		doAnswer(invocation -> cacheListeners.remove(invocation.getArgument(0))).when(mockGudAttributesMutator)
				.removeCacheListener(any(GudCacheListener.class));

		doAnswer(newSetter(cacheLoader)).when(mockGudAttributesMutator).setCacheLoader(any(GudCacheLoader.class));

		doAnswer(newSetter(cacheWriter)).when(mockGudAttributesMutator).setCacheWriter(any(GudCacheWriter.class));

		doAnswer(newSetter(cloningEnabled, null)).when(mockGudAttributesMutator).setCloningEnabled(anyBoolean());

		doAnswer(newSetter(customEntryIdleTimeout)).when(mockGudAttributesMutator)
				.setCustomEntryIdleTimeout(any(GudCustomExpiry.class));

		doAnswer(newSetter(customEntryTimeToLive)).when(mockGudAttributesMutator)
				.setCustomEntryTimeToLive(any(GudCustomExpiry.class));

		doAnswer(newSetter(entryIdleTimeout)).when(mockGudAttributesMutator)
				.setEntryIdleTimeout(any(GudExpirationAttributes.class));

		doAnswer(newSetter(entryTimeToLive)).when(mockGudAttributesMutator)
				.setEntryTimeToLive(any(GudExpirationAttributes.class));

		doAnswer(newSetter(regionIdleTimeout)).when(mockGudAttributesMutator)
				.setRegionIdleTimeout(any(GudExpirationAttributes.class));

		doAnswer(newSetter(regionTimeToLive)).when(mockGudAttributesMutator)
				.setRegionTimeToLive(any(GudExpirationAttributes.class));

		// Mock GudEvictionAttributesMutator
		doAnswer(newSetter(evictionMaximum, null)).when(mockGudEvictionAttributesMutator).setMaximum(anyInt());

		// Mock GudRegionAttributes
		when(mockGudRegionAttributes.getCacheListeners())
				.thenAnswer(invocation -> cacheListeners.toArray(new GudCacheListener[cacheListeners.size()]));

		when(mockGudRegionAttributes.getCacheLoader()).thenAnswer(newGetter(cacheLoader::get));
		when(mockGudRegionAttributes.getCacheWriter()).thenAnswer(newGetter(cacheWriter::get));
		when(mockGudRegionAttributes.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled::get));
		when(mockGudRegionAttributes.getCompressor()).thenAnswer(newGetter(baseGudRegionAttributes::getCompressor));
		when(mockGudRegionAttributes.getConcurrencyChecksEnabled())
				.thenAnswer(newGetter(baseGudRegionAttributes::getConcurrencyChecksEnabled));
		when(mockGudRegionAttributes.getConcurrencyLevel()).thenAnswer(newGetter(baseGudRegionAttributes::getConcurrencyLevel));
		when(mockGudRegionAttributes.getCustomEntryIdleTimeout()).thenAnswer(newGetter(customEntryIdleTimeout::get));
		when(mockGudRegionAttributes.getCustomEntryTimeToLive()).thenAnswer(newGetter(customEntryTimeToLive::get));
		when(mockGudRegionAttributes.getDataPolicy()).thenAnswer(newGetter(baseGudRegionAttributes::getDataPolicy));
		when(mockGudRegionAttributes.getDiskStoreName()).thenAnswer(newGetter(baseGudRegionAttributes::getDiskStoreName));
		when(mockGudRegionAttributes.getEnableSubscriptionConflation())
				.thenAnswer(newGetter(baseGudRegionAttributes::getEnableSubscriptionConflation));
		when(mockGudRegionAttributes.getEntryIdleTimeout()).thenAnswer(newGetter(entryIdleTimeout::get));
		when(mockGudRegionAttributes.getEntryTimeToLive()).thenAnswer(newGetter(entryTimeToLive::get));

		when(mockGudRegionAttributes.getEvictionAttributes()).thenAnswer(invocation -> {

			GudEvictionAttributes mockEvictionAttibutes = mock(GudEvictionAttributes.class);
			GudEvictionAttributes regionGudEvictionAttributes = baseGudRegionAttributes.getEvictionAttributes();

			when(mockEvictionAttibutes.getAction()).thenAnswer(newGetter(regionGudEvictionAttributes::getAction));
			when(mockEvictionAttibutes.getAlgorithm()).thenAnswer(newGetter(regionGudEvictionAttributes::getAlgorithm));
			when(mockEvictionAttibutes.getMaximum()).thenAnswer(newGetter(evictionMaximum));
			when(mockEvictionAttibutes.getObjectSizer()).thenAnswer(newGetter(regionGudEvictionAttributes::getObjectSizer));

			return mockEvictionAttibutes;
		});

		when(mockGudRegionAttributes.getInitialCapacity()).thenAnswer(newGetter(baseGudRegionAttributes::getInitialCapacity));
		when(mockGudRegionAttributes.getKeyConstraint()).thenAnswer(newGetter(baseGudRegionAttributes::getKeyConstraint));
		when(mockGudRegionAttributes.getLoadFactor()).thenAnswer(newGetter(baseGudRegionAttributes::getLoadFactor));
		when(mockGudRegionAttributes.getMembershipAttributes())
				.thenAnswer(newGetter(baseGudRegionAttributes::getMembershipAttributes));
		when(mockGudRegionAttributes.getPoolName()).thenAnswer(newGetter(baseGudRegionAttributes::getPoolName));
		when(mockGudRegionAttributes.getRegionIdleTimeout()).thenAnswer(newGetter(regionIdleTimeout::get));
		when(mockGudRegionAttributes.getRegionTimeToLive()).thenAnswer(newGetter(regionTimeToLive::get));
		when(mockGudRegionAttributes.getScope()).thenAnswer(newGetter(baseGudRegionAttributes::getScope));
		when(mockGudRegionAttributes.getStatisticsEnabled()).thenAnswer(newGetter(baseGudRegionAttributes::getStatisticsEnabled));
		when(mockGudRegionAttributes.getSubscriptionAttributes())
				.thenAnswer(newGetter(baseGudRegionAttributes::getSubscriptionAttributes));
		when(mockGudRegionAttributes.getValueConstraint()).thenAnswer(newGetter(baseGudRegionAttributes::getValueConstraint));
		when(mockGudRegionAttributes.isDiskSynchronous()).thenAnswer(newGetter(baseGudRegionAttributes::isDiskSynchronous));
		when(mockGudRegionAttributes.isLockGrantor()).thenAnswer(newGetter(baseGudRegionAttributes::isLockGrantor));

		return mockGudRegionAttributes;
	}

	@SuppressWarnings("unchecked")
	private static <K, V> void mockRegionDataAccessOperations(GudRegion<K, V> mockRegion,
			GudRegionAttributes<K, V> mockGudRegionAttributes) {

		Map<K, V> data = new ConcurrentHashMap<>();

		Set<K> invalidatedKeys = new HashSet<>();

		// Map.clear() / Region.clear()
		doAnswer(invocation -> {
			data.clear();
			return null;
		}).when(mockRegion).clear();

		// Map.containsKey(key) / Region.containsKey(key)
		doAnswer(invocation -> data.containsKey(invocation.getArgument(0))).when(mockRegion).containsKey(any());

		// Map.containsValue(value) / Region.containsValue(value)
		doAnswer(invocation -> data.containsValue(invocation.getArgument(0))).when(mockRegion).containsValue(any());

		// Region.containsValueForKey(key)
		// NOTE: This containsValueForKey(..) operation is not atomic
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);

			return !invalidatedKeys.contains(key) && data.containsKey(key) && Objects.nonNull(data.get(key));

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

				value = Optional.ofNullable(mockGudRegionAttributes.getCacheLoader()).map(cacheLoader -> {

					GudLoaderHelper<K, V> mockGudLoaderHelper = mock(GudLoaderHelper.class, withSettings().lenient());

					when(mockGudLoaderHelper.getArgument()).thenReturn(null);
					when(mockGudLoaderHelper.getKey()).thenReturn(key);
					when(mockGudLoaderHelper.getRegion()).thenReturn(mockRegion);

					return cacheLoader.load(mockGudLoaderHelper);

				}).map(loadedValue -> {

					data.put(key, loadedValue);
					invalidatedKeys.remove(key);

					return loadedValue;

				}).orElse(value);
			}

			return value;

		}).when(mockRegion).get(ArgumentMatchers.<K> any());

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
		when(mockRegion.getEntry(ArgumentMatchers.<K> any()))
				.thenAnswer(regionGetEntryInvocation -> data.entrySet().stream()
						.filter(entry -> entry.getKey().equals(regionGetEntryInvocation.getArgument(0))).findFirst().map(entry -> {

							Map.Entry<K, V> entrySpy = spy(entry);

							doAnswer(entryGetValueInvocation -> invalidatedKeys.contains(entry.getKey()) ? null : entry.getValue())
									.when(entrySpy).getValue();

							return entrySpy;

						}).orElse(null));

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
				throw new GudEntryNotFoundException(String.format("Entry with key [%s] not found", key));
			}

			if (invalidatedKeys.add(key)) {

				GudEntryEvent<K, V> mockGudEntryEvent = mock(GudEntryEvent.class, withSettings().lenient());

				when(mockGudEntryEvent.getKey()).thenReturn(key);
				when(mockGudEntryEvent.getNewValue()).thenReturn(null);
				when(mockGudEntryEvent.getOldValue()).thenReturn(data.get(key));
				when(mockGudEntryEvent.getRegion()).thenReturn(mockRegion);

				Arrays.stream(ArrayUtils.nullSafeArray(mockGudRegionAttributes.getCacheListeners(), GudCacheListener.class))
						.filter(Objects::nonNull).forEach(cacheListener -> cacheListener.afterInvalidate(mockGudEntryEvent));
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

			GudEntryEvent<K, V> mockGudEntryEvent = mock(GudEntryEvent.class, withSettings().lenient());

			V entryEventValue = invalidatedKeys.contains(key) ? null : data.get(key);

			when(mockGudEntryEvent.getKey()).thenReturn(key);
			when(mockGudEntryEvent.getNewValue()).thenReturn(newValue);
			when(mockGudEntryEvent.getOldValue()).thenReturn(entryEventValue);
			when(mockGudEntryEvent.getRegion()).thenReturn(mockRegion);

			GudCacheWriter<K, V> cacheWriter = mockGudRegionAttributes.getCacheWriter();

			if (cacheWriter != null) {
				try {
					if (entryExists) {
						cacheWriter.beforeUpdate(mockGudEntryEvent);
					} else {
						cacheWriter.beforeCreate(mockGudEntryEvent);
					}
				} catch (Throwable cause) {
					throw new GudCacheWriterException("Create/Update Error", cause);
				}
			}

			V existingValue = data.put(key, newValue);

			Arrays.stream(ArrayUtils.nullSafeArray(mockGudRegionAttributes.getCacheListeners(), GudCacheListener.class))
					.filter(Objects::nonNull).forEach(cacheListener -> {

						if (entryExists) {
							cacheListener.afterUpdate(mockGudEntryEvent);
						} else {
							cacheListener.afterCreate(mockGudEntryEvent);
						}
					});

			return invalidatedKeys.remove(key) ? null : existingValue;

		}).when(mockRegion).put(any(), any());

		// Map.putAll(:Map<K, V>) / Region.putAll(:Map<K, V>)
		// NOTE: This putAll(..) operation is not atomic
		doAnswer(invocation -> {

			Map<K, V> map = invocation.getArgument(0);

			CollectionUtils.nullSafeMap(map).entrySet().forEach(entry -> mockRegion.put(entry.getKey(), entry.getValue()));

			return null;

		}).when(mockRegion).putAll(any(Map.class));

		// TODO Map.putIfAbsent(key, value) / Region.putIfAbsent(key, value) ???

		// Map.remove(key) / Region.remove(key)
		doAnswer(invocation -> {

			K key = invocation.getArgument(0);

			GudEntryEvent<K, V> mockGudEntryEvent = mock(GudEntryEvent.class, withSettings().lenient());

			V entryEventValue = invalidatedKeys.contains(key) ? null : data.get(key);

			when(mockGudEntryEvent.getKey()).thenReturn(key);
			when(mockGudEntryEvent.getNewValue()).thenReturn(null);
			when(mockGudEntryEvent.getOldValue()).thenReturn(entryEventValue);
			when(mockGudEntryEvent.getRegion()).thenReturn(mockRegion);

			GudCacheWriter<K, V> cacheWriter = mockGudRegionAttributes.getCacheWriter();

			if (cacheWriter != null) {
				try {
					cacheWriter.beforeDestroy(mockGudEntryEvent);
				} catch (Throwable cause) {
					throw new GudCacheWriterException("Destroy Error", cause);
				}
			}

			V value = data.remove(key);

			Arrays.stream(ArrayUtils.nullSafeArray(mockGudRegionAttributes.getCacheListeners(), GudCacheListener.class))
					.filter(Objects::nonNull).forEach(cacheListener -> cacheListener.afterDestroy(mockGudEntryEvent));

			return invalidatedKeys.remove(key) ? null : value;

		}).when(mockRegion).remove(any());

		// TODO Map.remove(key, value) / Region.remove(key, value) ???

		// Region.removeAll(:Collection<K>)
		// NOTE: This removeAll(..) implementation is not atomic
		doAnswer(invocation -> {

			Collection<K> keys = invocation.getArgument(0);

			CollectionUtils.nullSafeCollection(keys).stream().filter(Objects::nonNull).forEach(mockRegion::remove);

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

	public static <K, V> GudRegion<K, V> mockSubRegion(GudRegion<K, V> parent, String name,
			GudRegionAttributes<K, V> regionAttributes) {

		String subRegionName = String.format("%1$s%2$s", parent.getFullPath(), toRegionPath(name));

		GudRegion<K, V> mockSubRegion = mockRegion(parent.getRegionService(), subRegionName, regionAttributes);

		doReturn(parent).when(mockSubRegion).getParentRegion();

		parent.subregions(false).add(mockSubRegion);

		return mockSubRegion;
	}

	public static <K, V> GudRegionFactory<K, V> mockGudRegionFactory(GudClientCache mockCache) {
		return mockGudRegionFactory(mockCache, null, null);
	}

	public static <K, V> GudRegionFactory<K, V> mockGudRegionFactory(GudClientCache mockCache,
			GudRegionAttributes<K, V> regionAttributes) {

		return mockGudRegionFactory(mockCache, regionAttributes, null);
	}

	public static <K, V> GudRegionFactory<K, V> mockGudRegionFactory(GudClientCache mockCache, GudRegionShortcut regionShortcut) {
		return mockGudRegionFactory(mockCache, resolveGudRegionAttributesFromGudRegionShortcut(regionShortcut), regionShortcut);
	}

	public static <K, V> GudRegionFactory<K, V> mockGudRegionFactory(GudClientCache mockCache, String regionAttributesId) {
		return mockGudRegionFactory(mockCache, resolveGudRegionAttributes(regionAttributesId), null);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> GudRegionFactory<K, V> mockGudRegionFactory(GudClientCache mockCache,
			GudRegionAttributes<K, V> regionAttributes, GudRegionShortcut regionShortcut) {

		GudRegionFactory<K, V> mockGudRegionFactory = mock(GudRegionFactory.class, mockObjectIdentifier("MockGudRegionFactory"));

		Optional<GudRegionAttributes<K, V>> optionalGudRegionAttributes = Optional.ofNullable(regionAttributes);

		GudExpirationAttributes DEFAULT_EXPIRATION_ATTRIBUTES = GudExpirationAttributes.of(0, GudExpirationAction.INVALIDATE);

		AtomicBoolean cloningEnabled = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCloningEnabled).orElse(false));

		AtomicBoolean concurrencyChecksEnabled = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getConcurrencyChecksEnabled).orElse(true));

		AtomicBoolean diskSynchronous = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::isDiskSynchronous).orElse(true));

		AtomicBoolean enableSubscriptionConflation = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getEnableSubscriptionConflation).orElse(false));

		AtomicBoolean ignoreJta = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getIgnoreJTA).orElse(false));

		AtomicBoolean lockGrantor = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::isLockGrantor).orElse(false));

		AtomicBoolean statisticsEnabled = new AtomicBoolean(
				optionalGudRegionAttributes.map(GudRegionAttributes::getStatisticsEnabled).orElse(false));

		AtomicInteger concurrencyLevel = new AtomicInteger(
				optionalGudRegionAttributes.map(GudRegionAttributes::getConcurrencyLevel).orElse(16));

		AtomicInteger initialCapacity = new AtomicInteger(
				optionalGudRegionAttributes.map(GudRegionAttributes::getInitialCapacity).orElse(16));

		AtomicReference<GudCacheLoader> cacheLoader = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCacheLoader).orElse(null));

		AtomicReference<GudCacheWriter> cacheWriter = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCacheWriter).orElse(null));

		AtomicReference<GudCompressor> compressor = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCompressor).orElse(null));

		AtomicReference<GudCustomExpiry<K, V>> customEntryIdleTimeout = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCustomEntryIdleTimeout).orElse(null));

		AtomicReference<GudCustomExpiry<K, V>> customEntryTimeToLive = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCustomEntryTimeToLive).orElse(null));

		AtomicReference<GudDataPolicy> dataPolicy = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getDataPolicy).orElseGet(() -> convert(regionShortcut)));

		AtomicReference<String> diskStoreName = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getDiskStoreName).orElse(null));

		AtomicReference<GudExpirationAttributes> entryIdleTimeout = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getEntryIdleTimeout).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudExpirationAttributes> entryTimeToLive = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getEntryTimeToLive).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudEvictionAttributes> evictionAttributes = new AtomicReference<>(optionalGudRegionAttributes
				.map(GudRegionAttributes::getEvictionAttributes).orElseGet(GudEvictionAttributes::createLRUEntryAttributes));

		AtomicReference<Class<K>> keyConstraint = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getKeyConstraint).orElse(null));

		AtomicReference<Float> loadFactor = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getLoadFactor).orElse(0.75f));

		AtomicReference<GudMembershipAttributes> membershipAttributes = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getMembershipAttributes).orElseGet(GudMembershipAttributes::new));

		AtomicReference<String> poolName = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getPoolName).orElse(null));

		AtomicReference<GudExpirationAttributes> regionIdleTimeout = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getRegionIdleTimeout).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudExpirationAttributes> regionTimeToLive = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getRegionTimeToLive).orElse(DEFAULT_EXPIRATION_ATTRIBUTES));

		AtomicReference<GudScope> scope = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getScope).orElse(GudScope.DISTRIBUTED_NO_ACK));

		AtomicReference<GudSubscriptionAttributes> subscriptionAttributes = new AtomicReference<>(optionalGudRegionAttributes
				.map(GudRegionAttributes::getSubscriptionAttributes).orElseGet(() -> GudSubscriptionAttributes.create(GudInterestPolicy.DEFAULT)));

		AtomicReference<Class<V>> valueConstraint = new AtomicReference<>(
				optionalGudRegionAttributes.map(GudRegionAttributes::getValueConstraint).orElse(null));

		List<GudCacheListener> cacheListeners = new ArrayList<>(Arrays.asList(nullSafeArray(
				optionalGudRegionAttributes.map(GudRegionAttributes::getCacheListeners).orElse(null), GudCacheListener.class)));

		when(mockGudRegionFactory.addCacheListener(any(GudCacheListener.class)))
				.thenAnswer(newAdder(cacheListeners, mockGudRegionFactory));

		when(mockGudRegionFactory.initCacheListeners(any(GudCacheListener[].class))).thenAnswer(invocation -> {
			cacheListeners.clear();
			Collections.addAll(cacheListeners, invocation.getArgument(0));
			return mockGudRegionFactory;
		});

		when(mockGudRegionFactory.setCacheLoader(any(GudCacheLoader.class)))
				.thenAnswer(newSetter(cacheLoader, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setCacheWriter(any(GudCacheWriter.class)))
				.thenAnswer(newSetter(cacheWriter, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setCloningEnabled(anyBoolean())).thenAnswer(newSetter(cloningEnabled, mockGudRegionFactory));

		when(mockGudRegionFactory.setCompressor(any(GudCompressor.class)))
				.thenAnswer(newSetter(compressor, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setConcurrencyChecksEnabled(anyBoolean()))
				.then(newSetter(concurrencyChecksEnabled, mockGudRegionFactory));

		when(mockGudRegionFactory.setConcurrencyLevel(anyInt())).thenAnswer(newSetter(concurrencyLevel, mockGudRegionFactory));

		when(mockGudRegionFactory.setCustomEntryIdleTimeout(any(GudCustomExpiry.class)))
				.thenAnswer(newSetter(customEntryIdleTimeout, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setCustomEntryTimeToLive(any(GudCustomExpiry.class)))
				.thenAnswer(newSetter(customEntryTimeToLive, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setDataPolicy(any(GudDataPolicy.class)))
				.thenAnswer(newSetter(dataPolicy, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setDiskStoreName(anyString())).thenAnswer(newSetter(diskStoreName, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setDiskSynchronous(anyBoolean())).thenAnswer(newSetter(diskSynchronous, mockGudRegionFactory));

		when(mockGudRegionFactory.setEnableSubscriptionConflation(anyBoolean()))
				.thenAnswer(newSetter(enableSubscriptionConflation, mockGudRegionFactory));

		when(mockGudRegionFactory.setEntryIdleTimeout(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(entryIdleTimeout, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setEntryTimeToLive(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(entryTimeToLive, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setEvictionAttributes(any(GudEvictionAttributes.class)))
				.thenAnswer(newSetter(evictionAttributes, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setIgnoreJTA(anyBoolean())).thenAnswer(newSetter(ignoreJta, mockGudRegionFactory));

		when(mockGudRegionFactory.setInitialCapacity(anyInt())).thenAnswer(newSetter(initialCapacity, mockGudRegionFactory));

		when(mockGudRegionFactory.setKeyConstraint(any(Class.class)))
				.thenAnswer(newSetter(keyConstraint, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setLoadFactor(anyFloat())).thenAnswer(newSetter(loadFactor, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setLockGrantor(anyBoolean())).thenAnswer(newSetter(lockGrantor, mockGudRegionFactory));

		when(mockGudRegionFactory.setMembershipAttributes(any(GudMembershipAttributes.class)))
				.thenAnswer(newSetter(membershipAttributes, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setPoolName(anyString())).thenAnswer(newSetter(poolName, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setRegionIdleTimeout(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(regionIdleTimeout, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setRegionTimeToLive(any(GudExpirationAttributes.class)))
				.thenAnswer(newSetter(regionTimeToLive, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setScope(any(GudScope.class))).thenAnswer(newSetter(scope, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setStatisticsEnabled(anyBoolean()))
				.thenAnswer(newSetter(statisticsEnabled, mockGudRegionFactory));

		when(mockGudRegionFactory.setSubscriptionAttributes(any(GudSubscriptionAttributes.class)))
				.thenAnswer(newSetter(subscriptionAttributes, () -> mockGudRegionFactory));

		when(mockGudRegionFactory.setValueConstraint(any(Class.class)))
				.thenAnswer(newSetter(valueConstraint, () -> mockGudRegionFactory));

		GudRegionAttributes<K, V> mockGudRegionAttributes = mock(GudRegionAttributes.class,
				mockObjectIdentifier("MockGudRegionAttributes"));

		when(mockGudRegionAttributes.getCacheListeners())
				.thenAnswer(newGetter(() -> cacheListeners.toArray(new GudCacheListener[cacheListeners.size()])));

		when(mockGudRegionAttributes.getCacheLoader()).thenAnswer(newGetter(cacheLoader));
		when(mockGudRegionAttributes.getCacheWriter()).thenAnswer(newGetter(cacheWriter));
		when(mockGudRegionAttributes.getCloningEnabled()).thenAnswer(newGetter(cloningEnabled));
		when(mockGudRegionAttributes.getCompressor()).thenAnswer(newGetter(compressor));
		when(mockGudRegionAttributes.getConcurrencyChecksEnabled()).thenAnswer(newGetter(concurrencyChecksEnabled));
		when(mockGudRegionAttributes.getConcurrencyLevel()).thenAnswer(newGetter(concurrencyLevel));
		when(mockGudRegionAttributes.getCustomEntryIdleTimeout()).thenAnswer(newGetter(customEntryIdleTimeout));
		when(mockGudRegionAttributes.getCustomEntryTimeToLive()).thenAnswer(newGetter(customEntryTimeToLive));
		when(mockGudRegionAttributes.getDataPolicy()).thenAnswer(newGetter(dataPolicy));
		when(mockGudRegionAttributes.getDiskStoreName()).thenAnswer(newGetter(diskStoreName));
		when(mockGudRegionAttributes.isDiskSynchronous()).thenAnswer(newGetter(diskSynchronous));
		when(mockGudRegionAttributes.getEnableSubscriptionConflation()).thenAnswer(newGetter(enableSubscriptionConflation));
		when(mockGudRegionAttributes.getEntryIdleTimeout()).thenAnswer(newGetter(entryIdleTimeout));
		when(mockGudRegionAttributes.getEntryTimeToLive()).thenAnswer(newGetter(entryTimeToLive));
		when(mockGudRegionAttributes.getEvictionAttributes()).thenAnswer(newGetter(evictionAttributes));
		when(mockGudRegionAttributes.getInitialCapacity()).thenAnswer(newGetter(initialCapacity));
		when(mockGudRegionAttributes.getKeyConstraint()).thenAnswer(newGetter(keyConstraint));
		when(mockGudRegionAttributes.getLoadFactor()).thenAnswer(newGetter(loadFactor));
		when(mockGudRegionAttributes.isLockGrantor()).thenAnswer(newGetter(lockGrantor));
		when(mockGudRegionAttributes.getMembershipAttributes()).thenAnswer(newGetter(membershipAttributes));
		when(mockGudRegionAttributes.getPoolName()).thenAnswer(newGetter(poolName));
		when(mockGudRegionAttributes.getRegionIdleTimeout()).thenAnswer(newGetter(regionIdleTimeout));
		when(mockGudRegionAttributes.getRegionTimeToLive()).thenAnswer(newGetter(regionTimeToLive));
		when(mockGudRegionAttributes.getScope()).thenAnswer(newGetter(scope));
		when(mockGudRegionAttributes.getStatisticsEnabled()).thenAnswer(newGetter(statisticsEnabled));
		when(mockGudRegionAttributes.getSubscriptionAttributes()).thenAnswer(newGetter(subscriptionAttributes));
		when(mockGudRegionAttributes.getValueConstraint()).thenAnswer(newGetter(valueConstraint));

		when(mockGudRegionFactory.create(anyString()))
				.thenAnswer(invocation -> mockRegion(mockCache, invocation.getArgument(0), mockGudRegionAttributes));

		when(mockGudRegionFactory.createSubregion(any(GudRegion.class), anyString())).thenAnswer(
				invocation -> mockSubRegion(invocation.getArgument(0), invocation.getArgument(1), mockGudRegionAttributes));

		return mockGudRegionFactory;
	}

	public static GudResourceManager mockGudResourceManager() {

		GudResourceManager mockGudResourceManager = mock(GudResourceManager.class);

		AtomicReference<Float> criticalHeapPercentage = new AtomicReference<>(GudResourceManager.DEFAULT_CRITICAL_PERCENTAGE);

		AtomicReference<Float> evictionHeapPercentage = new AtomicReference<>(GudResourceManager.DEFAULT_EVICTION_PERCENTAGE);

		doAnswer(newSetter(criticalHeapPercentage, () -> null)).when(mockGudResourceManager)
				.setCriticalHeapPercentage(anyFloat());

		doAnswer(newSetter(evictionHeapPercentage, () -> null)).when(mockGudResourceManager)
				.setEvictionHeapPercentage(anyFloat());

		when(mockGudResourceManager.getCriticalHeapPercentage()).thenAnswer(newGetter(criticalHeapPercentage));
		when(mockGudResourceManager.getEvictionHeapPercentage()).thenAnswer(newGetter(evictionHeapPercentage));
		when(mockGudResourceManager.getRebalanceOperations()).thenReturn(Collections.emptySet());

		return mockGudResourceManager;
	}

	public static boolean resolveUseSingletonCache() {
		return Boolean
				.parseBoolean(System.getProperty(USE_SINGLETON_CACHE_PROPERTY, String.valueOf(DEFAULT_USE_SINGLETON_CACHE)));
	}

	public static GudClientCacheFactory spyOn(GudClientCacheFactory clientCacheFactory) {
		return spyOn(clientCacheFactory, resolveUseSingletonCache());
	}

	public static GudClientCacheFactory spyOn(GudClientCacheFactory clientCacheFactory, boolean useSingletonCache) {

		AtomicBoolean pdxIgnoreUnreadFields = new AtomicBoolean(false);
		AtomicBoolean pdxPersistent = new AtomicBoolean(false);
		AtomicBoolean pdxReadSerialized = new AtomicBoolean(false);

		AtomicReference<String> pdxGudDiskStoreName = new AtomicReference<>(null);
		AtomicReference<GudPdxSerializer> pdxSerializer = new AtomicReference<>(null);

		GudClientCacheFactory clientCacheFactorySpy = spy(clientCacheFactory);

		doAnswer(invocation -> {
			gemfireProperties.get().setProperty(invocation.getArgument(0), invocation.getArgument(1));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).set(anyString(), anyString());

		doAnswer(newSetter(pdxGudDiskStoreName, () -> clientCacheFactorySpy)).when(clientCacheFactorySpy)
				.setPdxDiskStore(anyString());

		doAnswer(newSetter(pdxIgnoreUnreadFields, clientCacheFactorySpy)).when(clientCacheFactorySpy)
				.setPdxIgnoreUnreadFields(anyBoolean());

		doAnswer(newSetter(pdxPersistent, clientCacheFactorySpy)).when(clientCacheFactorySpy)
				.setPdxPersistent(anyBoolean());

		doAnswer(newSetter(pdxReadSerialized, clientCacheFactorySpy)).when(clientCacheFactorySpy)
				.setPdxReadSerialized(anyBoolean());

		doAnswer(newSetter(pdxSerializer, () -> clientCacheFactorySpy)).when(clientCacheFactorySpy)
				.setPdxSerializer(any(GudPdxSerializer.class));

		GudPoolFactory mockGudPoolFactory = mockGudPoolFactory();

		doAnswer(invocation -> {
			mockGudPoolFactory.addLocator(invocation.getArgument(0), invocation.getArgument(1));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).addPoolLocator(anyString(), anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.addServer(invocation.getArgument(0), invocation.getArgument(1));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).addPoolServer(anyString(), anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setFreeConnectionTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolFreeConnectionTimeout(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setIdleTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolIdleTimeout(anyLong());

		doAnswer(invocation -> {
			mockGudPoolFactory.setLoadConditioningInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolLoadConditioningInterval(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setMaxConnections(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMaxConnections(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setMinConnections(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMinConnections(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setMaxConnectionsPerServer(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMaxConnectionsPerServer(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setMinConnectionsPerServer(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMinConnectionsPerServer(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setMultiuserAuthentication(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolMultiuserAuthentication(anyBoolean());

		doAnswer(invocation -> {
			mockGudPoolFactory.setPingInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolPingInterval(anyLong());

		doAnswer(invocation -> {
			mockGudPoolFactory.setPRSingleHopEnabled(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolPRSingleHopEnabled(anyBoolean());

		doAnswer(invocation -> {
			mockGudPoolFactory.setReadTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolReadTimeout(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setRetryAttempts(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolRetryAttempts(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setServerConnectionTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolServerConnectionTimeout(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setServerGroup(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolServerGroup(anyString());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSocketBufferSize(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSocketBufferSize(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSocketConnectTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSocketConnectTimeout(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSocketFactory(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSocketFactory(any(GudSocketFactory.class));

		doAnswer(invocation -> {
			mockGudPoolFactory.setStatisticInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolStatisticInterval(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSubscriptionAckInterval(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionAckInterval(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSubscriptionEnabled(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionEnabled(anyBoolean());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSubscriptionMessageTrackingTimeout(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionMessageTrackingTimeout(anyInt());

		doAnswer(invocation -> {
			mockGudPoolFactory.setSubscriptionRedundancy(invocation.getArgument(0));
			return clientCacheFactorySpy;
		}).when(clientCacheFactorySpy).setPoolSubscriptionRedundancy(anyInt());

		// doAnswer(invocation -> {
		// mockGudPoolFactory.setThreadLocalConnections(invocation.getArgument(0));
		// return clientCacheFactorySpy;
		// }).when(clientCacheFactorySpy).setPoolThreadLocalConnections(anyBoolean());

		Supplier<GudClientCache> resolvedMockedGudClientCache = () -> GemFireMockObjectsSupport
				.<GudClientCache> resolveMockedGemFireCache(useSingletonCache).orElseGet(() -> {

					GudClientCache mockGudClientCache = mockGudClientCache();

					GudPool mockDefaultGudPool = mockGudPoolFactory.create("DEFAULT");

					doAnswer(invocation -> mockGudClientCache.getQueryService()).when(mockDefaultGudPool).getQueryService();

					when(mockGudClientCache.getCurrentServers()).thenAnswer(
							invocation -> Collections.unmodifiableSet(new HashSet<>(mockGudClientCache.getDefaultPool().getServers())));

					when(mockGudClientCache.getDefaultPool()).thenReturn(mockDefaultGudPool);

					when(mockGudClientCache.getPdxDiskStore()).thenAnswer(newGetter(pdxGudDiskStoreName));
					when(mockGudClientCache.getPdxIgnoreUnreadFields()).thenAnswer(newGetter(pdxIgnoreUnreadFields));
					when(mockGudClientCache.getPdxPersistent()).thenAnswer(newGetter(pdxPersistent));
					when(mockGudClientCache.getPdxReadSerialized()).thenAnswer(newGetter(pdxReadSerialized));
					when(mockGudClientCache.getPdxSerializer()).thenAnswer(newGetter(pdxSerializer));

					return mockGudClientCache;
				});

		doAnswer(invocation -> {
			storeConfiguration(clientCacheFactory);
			return rememberMockedGemFireCache(constructGemFireObjects(resolvedMockedGudClientCache.get()), useSingletonCache);
		}).when(clientCacheFactorySpy).create();

		return clientCacheFactorySpy;
	}

	private static void storeConfiguration(GudClientCacheFactory clientCacheFactory) {
		storeConfiguration(clientCacheFactory, CLIENT_CACHE_FACTORY_DS_PROPS_FIELD_NAME);
	}

	private static void storeConfiguration(Object clientCacheFactory, String gemfirePropertiesFieldName) {

		Properties localGemFireProperties = gemfireProperties.get();

		localGemFireProperties.putAll(withGemFireApiProperties(clientCacheFactory, gemfirePropertiesFieldName));
		localGemFireProperties.putAll(withGemFireSystemProperties());
	}

	@SuppressWarnings("unchecked")
	private static Properties withGemFireApiProperties(Object clientCacheFactory, String gemfirePropertiesFieldName) {

		Class<?> cacheFactoryType = Optional.ofNullable(clientCacheFactory).map(Object::getClass)
				.orElse((Class) Object.class);

		try {

			Field dsPropsField = cacheFactoryType.getDeclaredField(gemfirePropertiesFieldName);

			dsPropsField.setAccessible(true);

			Properties gemfireApiProperties = (Properties) dsPropsField.get(clientCacheFactory);

			return gemfireApiProperties;
		} catch (Throwable cause) {

			if (cause instanceof NoSuchFieldException
					&& !CACHE_FACTORY_INTERNAL_CACHE_BUILDER_FIELD_NAME.equals(gemfirePropertiesFieldName)) {

				return Arrays.stream(ArrayUtils.nullSafeArray(cacheFactoryType.getDeclaredFields(), Field.class))
						.filter(field -> CACHE_FACTORY_INTERNAL_CACHE_BUILDER_FIELD_NAME.equals(field.getName())).findFirst()
						.map(field -> {

							field.setAccessible(true);

							Object internalCacheBuilder = ObjectUtils.doOperationSafely(() -> field.get(clientCacheFactory), null);

							if (internalCacheBuilder != null) {
								return withGemFireApiProperties(internalCacheBuilder,
										INTERNAL_CACHE_BUILDER_CONFIG_PROPERTIES_FIELD_NAME);
							}

							return null;

						}).orElseGet(Properties::new);
			}

			return new Properties();
		}
	}

	private static Properties withGemFireSystemProperties() {

		Properties gemfireSystemProperties = new Properties();

		List<String> gemfireSystemPropertyNames = System.getProperties().stringPropertyNames().stream()
				.filter(StringUtils::hasText).filter(it -> it.startsWith(GEMFIRE_SYSTEM_PROPERTY_PREFIX))
				.collect(Collectors.toList());

		gemfireSystemPropertyNames.stream().forEach(propertyName -> gemfireSystemProperties
				.setProperty(normalizeGemFirePropertyName(propertyName), System.getProperty(propertyName)));

		return gemfireSystemProperties;
	}
}
