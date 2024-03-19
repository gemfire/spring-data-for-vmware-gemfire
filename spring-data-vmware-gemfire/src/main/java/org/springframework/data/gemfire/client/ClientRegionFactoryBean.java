/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.compression.Compressor;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.gemfire.ConfigurableRegionFactoryBean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.support.BeanFactoryPoolResolver;
import org.springframework.data.gemfire.client.support.ComposablePoolResolver;
import org.springframework.data.gemfire.client.support.PoolManagerPoolResolver;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.eviction.EvictingRegionFactoryBean;
import org.springframework.data.gemfire.expiration.ExpiringRegionFactoryBean;
import org.springframework.data.gemfire.support.SmartLifecycleSupport;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to construct, configure and initialize a {@literal client} {@link Region}.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see CacheListener
 * @see CacheLoader
 * @see CacheWriter
 * @see CustomExpiry
 * @see DataPolicy
 * @see EvictionAttributes
 * @see ExpirationAttributes
 * @see GemFireCache
 * @see Region
 * @see RegionAttributes
 * @see ClientCache
 * @see ClientRegionFactory
 * @see ClientRegionShortcut
 * @see Pool
 * @see Compressor
 * @see DisposableBean
 * @see FactoryBean
 * @see ConfigurableRegionFactoryBean
 * @see PoolResolver
 * @see org.springframework.data.gemfire.config.annotation.RegionConfigurer
 * @see EvictingRegionFactoryBean
 * @see ExpiringRegionFactoryBean
 * @see SmartLifecycleSupport
 */
@SuppressWarnings("unused")
public class ClientRegionFactoryBean<K, V> extends ConfigurableRegionFactoryBean<K, V>
		implements SmartLifecycleSupport, EvictingRegionFactoryBean, ExpiringRegionFactoryBean<K, V>, DisposableBean {

	public static final String DEFAULT_POOL_NAME = "DEFAULT";
	public static final String GEMFIRE_POOL_NAME = GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME;

	private boolean close = false;
	private boolean destroy = false;

	private Boolean cloningEnabled;
	private Boolean concurrencyChecksEnabled;
	private Boolean diskSynchronous;
	private Boolean persistent;
	private Boolean statisticsEnabled;

	private CacheListener<K, V>[] cacheListeners;

	private CacheLoader<K, V> cacheLoader;

	private CacheWriter<K, V> cacheWriter;

	private Class<K> keyConstraint;
	private Class<V> valueConstraint;

	private ClientRegionShortcut shortcut;

	private Compressor compressor;

	private CustomExpiry<K, V> customEntryIdleTimeout;
	private CustomExpiry<K, V> customEntryTimeToLive;

	private DataPolicy dataPolicy;

	private EvictionAttributes evictionAttributes;

	private ExpirationAttributes entryIdleTimeout;
	private ExpirationAttributes entryTimeToLive;
	private ExpirationAttributes regionIdleTimeout;
	private ExpirationAttributes regionTimeToLive;

	private Integer concurrencyLevel;
	private Integer initialCapacity;

	private Interest<K>[] interests;

	private Float loadFactor;

	private PoolResolver defaultPoolResolver;
	private PoolResolver poolResolver;

	private RegionAttributes<K, V> attributes;

	private String diskStoreName;
	private String poolName;

	/**
	 * Initializes a the instance of {@link ClientRegionFactoryBean}.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		initializePoolResolver();
		super.afterPropertiesSet();
	}

	/**
	 * Initializes the {@literal default} {@link PoolResolver} and optionally sets the main {@link PoolResolver}
	 * used to resolve {@link Pool} objects from Apache Geode if not configured by the user.
	 *
	 * @see PoolResolver
	 * @see BeanFactoryPoolResolver
	 * @see PoolManagerPoolResolver
	 */
	void initializePoolResolver() {

		this.defaultPoolResolver = ComposablePoolResolver
			.compose(new BeanFactoryPoolResolver(getBeanFactory()), new PoolManagerPoolResolver());

		this.poolResolver = this.poolResolver != null ? this.poolResolver : this.defaultPoolResolver;
	}

	/**
	 * Creates a new {@link Region} with the given {@link String name}.
	 *
	 * @param gemfireCache reference to the {@link GemFireCache}.
	 * @param regionName {@link String name} of the new {@link Region}.
	 * @return a new {@link Region} with the given {@link String name}.
	 * @see #createClientRegionFactory(ClientCache, ClientRegionShortcut)
	 * @see #newRegion(ClientRegionFactory, Region, String)
	 * @see GemFireCache
	 * @see Region
	 */
	@Override
	protected Region<K, V> createRegion(GemFireCache gemfireCache, String regionName) {

		ClientCache clientCache = resolveCache(gemfireCache);

		ClientRegionFactory<K, V> clientRegionFactory =
			postProcess(configure(createClientRegionFactory(clientCache, resolveClientRegionShortcut())));

		return newRegion(clientRegionFactory, getParent(), regionName);
	}

	/**
	 * Constructs a new {@link Region} using the provided {@link ClientRegionFactory} as either
	 * a {@link Region root Region} or a {@link Region sub-Region} if {@link Region parent}
	 * is not {@literal null}.
	 *
	 * @param clientRegionFactory {@link ClientRegionFactory} containing the configuration
	 * for the new {@link Region}.
	 * @param parent {@link Region} designated as the parent of the new {@link Region}
	 * if the new {@link Region} is a {@link Region sub-Region}.
	 * @param regionName {@link String name} of the new {@link Region}.
	 * @return the new {@link Region} initialized with the given {@link String name}.
	 */
	private Region<K, V> newRegion(ClientRegionFactory<K, V> clientRegionFactory,
			Region<?, ?> parent, String regionName) {

		if (parent != null) {

			logInfo("Creating client Subregion [%1$s] with parent Region [%2$s]",
				regionName, parent.getName());

			return clientRegionFactory.createSubregion(parent, regionName);
		}
		else {

			logInfo("Creating client Region [%s]", regionName);

			return clientRegionFactory.create(regionName);
		}
	}

	private ClientCache resolveCache(GemFireCache gemfireCache) {

		return Optional.ofNullable(gemfireCache)
			.filter(GemfireUtils::isClient)
			.map(ClientCache.class::cast)
			.orElseThrow(() -> newIllegalArgumentException("ClientCache is required"));
	}

	/**
	 * Resolves the {@link ClientRegionShortcut} used to configure the {@link DataPolicy}
	 * for the {@link Region client Region}.
	 *
	 * @return a {@link ClientRegionShortcut} used to configure the {@link DataPolicy}
	 * for the {@link Region client Region}.
	 * @see ClientRegionShortcut
	 * @see DataPolicy
	 */
	ClientRegionShortcut resolveClientRegionShortcut() {

		ClientRegionShortcut resolvedShortcut = this.shortcut;

		if (resolvedShortcut == null) {

			DataPolicy dataPolicy = this.dataPolicy;

			if (dataPolicy != null) {

				RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(dataPolicy, this.persistent);

				if (DataPolicy.EMPTY.equals(dataPolicy)) {
					resolvedShortcut = ClientRegionShortcut.PROXY;
				}
				else if (DataPolicy.NORMAL.equals(dataPolicy)) {
					resolvedShortcut = ClientRegionShortcut.CACHING_PROXY;
				}
				else if (DataPolicy.PERSISTENT_REPLICATE.equals(dataPolicy)) {
					resolvedShortcut = ClientRegionShortcut.LOCAL_PERSISTENT;
				}
				else {
					// NOTE: DataPolicy validation is based on the ClientRegionShortcut initialization logic
					// in org.apache.geode.internal.cache.GemFireCacheImpl.initializeClientRegionShortcuts.
					throw newIllegalArgumentException("Data Policy [%s] is not valid for a client Region", dataPolicy);
				}
			}
			else {
				resolvedShortcut = isPersistent() ? ClientRegionShortcut.LOCAL_PERSISTENT : ClientRegionShortcut.LOCAL;
			}
		}

		// NOTE: The ClientRegionShortcut and Persistent attribute will be compatible
		// if the shortcut was derived from the DataPolicy.
		RegionUtils.assertClientRegionShortcutAndPersistentAttributeAreCompatible(resolvedShortcut, this.persistent);

		return resolvedShortcut;
	}

	private String resolvePoolName(String factoryPoolName, String attributesPoolName) {

		String resolvedPoolName = StringUtils.hasText(factoryPoolName) ? factoryPoolName : attributesPoolName;

		return Optional.ofNullable(resolvedPoolName)
			.filter(StringUtils::hasText)
			.filter(GemfireUtils::isNotDefaultPool)
			.map(it -> {

				Assert.isTrue(eagerlyInitializePool(it),
					String.format("[%s] is not resolvable as a Pool in the application context", it));

				return it;
			})
			.orElse(null);
	}

	private boolean eagerlyInitializePool(String poolName) {

		return Optional.ofNullable(getPoolResolver().resolve(poolName))
			.map(it -> true)
			.orElseGet(() ->
				SpringExtensions.safeGetValue(() ->
					getBeanFactory().getBean(poolName, Pool.class) != null, false));
	}

	/**
	 * Constructs a new instance of {@link ClientRegionFactory} using the given {@link ClientCache}
	 * and {@link ClientRegionShortcut}.
	 *
	 * @param clientCache reference to the {@link ClientCache}.
	 * @param clientRegionShortcut {@link ClientRegionShortcut} used to configure
	 * the {@link Region client Region} {@link DataPolicy}.
	 * @return a new instance of {@link ClientRegionFactory}.
	 * @see ClientCache#createClientRegionFactory(ClientRegionShortcut)
	 * @see ClientRegionShortcut
	 * @see ClientRegionFactory
	 */
	protected ClientRegionFactory<K, V> createClientRegionFactory(ClientCache clientCache,
			ClientRegionShortcut clientRegionShortcut) {

		return clientCache.createClientRegionFactory(clientRegionShortcut);
	}

	/**
	 * Configures the given {@link ClientRegionFactoryBean} from the configuration settings
	 * of this {@link ClientRegionFactoryBean} and any {@link RegionAttributes}.
	 *
	 * @param clientRegionFactory {@link ClientRegionFactory} to configure.
	 * @return the configured {@link ClientRegionFactory}.
	 * @see ClientRegionFactory
	 */
	protected ClientRegionFactory<K, V> configure(ClientRegionFactory<K, V> clientRegionFactory) {

		Optional<String> regionAttributesPoolName = configureWithRegionAttributes(clientRegionFactory);

		stream(nullSafeArray(this.cacheListeners, CacheListener.class)).forEach(clientRegionFactory::addCacheListener);

		clientRegionFactory.setStatisticsEnabled(resolveStatisticsEnabled());

		Optional.ofNullable(this.cloningEnabled).ifPresent(clientRegionFactory::setCloningEnabled);
		Optional.ofNullable(this.compressor).ifPresent(clientRegionFactory::setCompressor);
		Optional.ofNullable(this.concurrencyChecksEnabled).ifPresent(clientRegionFactory::setConcurrencyChecksEnabled);
		Optional.ofNullable(this.concurrencyLevel).ifPresent(clientRegionFactory::setConcurrencyLevel);
		Optional.ofNullable(this.customEntryIdleTimeout).ifPresent(clientRegionFactory::setCustomEntryIdleTimeout);
		Optional.ofNullable(this.customEntryTimeToLive).ifPresent(clientRegionFactory::setCustomEntryTimeToLive);
		Optional.ofNullable(this.diskStoreName).filter(StringUtils::hasText).ifPresent(clientRegionFactory::setDiskStoreName);
		Optional.ofNullable(this.diskSynchronous).ifPresent(clientRegionFactory::setDiskSynchronous);
		Optional.ofNullable(this.entryIdleTimeout).ifPresent(clientRegionFactory::setEntryIdleTimeout);
		Optional.ofNullable(this.entryTimeToLive).ifPresent(clientRegionFactory::setEntryTimeToLive);
		Optional.ofNullable(this.evictionAttributes).ifPresent(clientRegionFactory::setEvictionAttributes);
		Optional.ofNullable(this.initialCapacity).ifPresent(clientRegionFactory::setInitialCapacity);
		Optional.ofNullable(this.keyConstraint).ifPresent(clientRegionFactory::setKeyConstraint);
		Optional.ofNullable(this.loadFactor).ifPresent(clientRegionFactory::setLoadFactor);

		Optional.ofNullable(resolvePoolName(getPoolName().orElse(null), regionAttributesPoolName.orElse(null)))
			.ifPresent(clientRegionFactory::setPoolName);

		Optional.ofNullable(this.regionIdleTimeout).ifPresent(clientRegionFactory::setRegionIdleTimeout);
		Optional.ofNullable(this.regionTimeToLive).ifPresent(clientRegionFactory::setRegionTimeToLive);
		Optional.ofNullable(this.valueConstraint).ifPresent(clientRegionFactory::setValueConstraint);

		return clientRegionFactory;
	}

	private Optional<String> configureWithRegionAttributes(ClientRegionFactory<K, V> clientRegionFactory) {

		AtomicReference<String> regionAttributesPoolName = new AtomicReference<>(null);

		Optional.ofNullable(getAttributes()).ifPresent(regionAttributes -> {

			regionAttributesPoolName.set(regionAttributes.getPoolName());

			stream(nullSafeArray(regionAttributes.getCacheListeners(), CacheListener.class))
				.forEach(clientRegionFactory::addCacheListener);

			clientRegionFactory.setCloningEnabled(regionAttributes.getCloningEnabled());
			clientRegionFactory.setCompressor(regionAttributes.getCompressor());
			clientRegionFactory.setConcurrencyChecksEnabled(regionAttributes.getConcurrencyChecksEnabled());
			clientRegionFactory.setConcurrencyLevel(regionAttributes.getConcurrencyLevel());
			clientRegionFactory.setCustomEntryIdleTimeout(regionAttributes.getCustomEntryIdleTimeout());
			clientRegionFactory.setCustomEntryTimeToLive(regionAttributes.getCustomEntryTimeToLive());
			clientRegionFactory.setDiskStoreName(regionAttributes.getDiskStoreName());
			clientRegionFactory.setDiskSynchronous(regionAttributes.isDiskSynchronous());
			clientRegionFactory.setEntryIdleTimeout(regionAttributes.getEntryIdleTimeout());
			clientRegionFactory.setEntryTimeToLive(regionAttributes.getEntryTimeToLive());
			clientRegionFactory.setEvictionAttributes(regionAttributes.getEvictionAttributes());
			clientRegionFactory.setInitialCapacity(regionAttributes.getInitialCapacity());
			clientRegionFactory.setKeyConstraint(regionAttributes.getKeyConstraint());
			clientRegionFactory.setLoadFactor(regionAttributes.getLoadFactor());
			clientRegionFactory.setRegionIdleTimeout(regionAttributes.getRegionIdleTimeout());
			clientRegionFactory.setRegionTimeToLive(regionAttributes.getRegionTimeToLive());
			clientRegionFactory.setStatisticsEnabled(regionAttributes.getStatisticsEnabled());
			clientRegionFactory.setValueConstraint(regionAttributes.getValueConstraint());
		});

		return Optional.ofNullable(regionAttributesPoolName.get()).filter(StringUtils::hasText);
	}

	/**
	 * Post-process the given {@link ClientRegionFactory} setup by this {@link ClientRegionFactoryBean}.
	 *
	 * @param clientRegionFactory {@link ClientRegionFactory} to process.
	 * @return the given {@link ClientRegionFactory}.
	 * @see ClientRegionFactory
	 */
	protected ClientRegionFactory<K, V> postProcess(ClientRegionFactory<K, V> clientRegionFactory) {
		return clientRegionFactory;
	}

	/**
	 * Post-process the {@link Region} created by this {@link ClientRegionFactoryBean}.
	 *
	 * @param region {@link Region} to process.
	 * @see Region
	 */
	@Override
	protected Region<K, V> postProcess(Region<K, V> region) {

		super.postProcess(region);

		Optional.ofNullable(this.cacheLoader)
			.ifPresent(cacheLoader -> region.getAttributesMutator().setCacheLoader(cacheLoader));

		Optional.ofNullable(this.cacheWriter)
			.ifPresent(cacheWriter -> region.getAttributesMutator().setCacheWriter(cacheWriter));

		return region;
	}

	/**
	 * Registers interests in the startup lifecycle phase of the Spring container.
	 *
	 * @see #getRegion()
	 * @see #registerInterests(Region)
	 */
	@Override
	public void start() {
		registerInterests(getRegion());
	}

	@SuppressWarnings("unchecked")
	private Region<K, V> registerInterests(Region<K, V> region) {

		stream(nullSafeArray(getInterests(), Interest.class)).forEach(interest -> {

			if (interest.isRegexType()) {
				region.registerInterestRegex((String) interest.getKey(), interest.getPolicy(),
					interest.isDurable(), interest.isReceiveValues());
			}
			else {
				region.registerInterest(((Interest<K>) interest).getKey(), interest.getPolicy(),
					interest.isDurable(), interest.isReceiveValues());
			}

		});

		return region;
	}

	/**
	 * Closes and destroys the {@link Region}.
	 *
	 * @throws Exception if destroy fails.
	 * @see DisposableBean
	 */
	@Override
	public void destroy() throws Exception {

		Optional.ofNullable(getObject()).ifPresent(region -> {

			if (isClose() && RegionUtils.isCloseable(region)) {
				RegionUtils.close(region);
			}

			if (isDestroy()) {
				region.destroyRegion();
			}
		});
	}

	/**
	 * Sets the region attributes used for the region used by this factory.
	 * Allows maximum control in specifying the region settings. Used only when
	 * a new region is created. Note that using this method allows for advanced
	 * customization of the region - while it provides a lot of flexibility,
	 * note that it's quite easy to create misconfigured regions (especially in
	 * a client/server scenario).
	 *
	 * @param attributes the attributes to set on a newly created region
	 */
	public void setAttributes(RegionAttributes<K, V> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets the {@link RegionAttributes} used to configure the {@link Region client Region}
	 * created by this {@link ClientRegionFactoryBean}.
	 *
	 * @return the {@link RegionAttributes} used to configure the {@link Region client Region}.
	 * @see RegionAttributes
	 */
	protected RegionAttributes<K, V> getAttributes() {
		return this.attributes;
	}

	/**
	 * Sets the cache listeners used for the region used by this factory. Used
	 * only when a new region is created.Overrides the settings specified
	 * through {@link #setAttributes(RegionAttributes)}.
	 *
	 * @param cacheListeners the cacheListeners to set on a newly created region
	 */
	public void setCacheListeners(CacheListener<K, V>[] cacheListeners) {
		this.cacheListeners = cacheListeners;
	}

	/**
	 * Sets the CacheLoader used to load data local to the client's Region on cache misses.
	 *
	 * @param cacheLoader a GemFire CacheLoader used to load data into the client Region.
	 * @see CacheLoader
	 */
	public void setCacheLoader(CacheLoader<K, V> cacheLoader) {
		this.cacheLoader = cacheLoader;
	}

	/**
	 * Sets the CacheWriter used to perform a synchronous write-behind when data is put into the client's Region.
	 *
	 * @param cacheWriter the GemFire CacheWriter used to perform synchronous write-behinds on put ops.
	 * @see CacheWriter
	 */
	public void setCacheWriter(CacheWriter<K, V> cacheWriter) {
		this.cacheWriter = cacheWriter;
	}

	public void setCloningEnabled(Boolean cloningEnabled) {
		this.cloningEnabled = cloningEnabled;
	}

	final boolean isClose() {
		return this.close;
	}

	/**
	 * Indicates whether the region referred by this factory bean will be closed on shutdown (default true).
	 *
	 * Note: destroy and close are mutually exclusive. Enabling one will automatically disable the other.
	 *
	 * @param close whether to close or not the region
	 * @see #setDestroy(boolean)
	 */
	public void setClose(boolean close) {
		this.close = close;
		this.destroy = this.destroy && !close; // retain previous value iff close is false.
	}

	/**
	 * Configures the {@link Compressor} used to compress the this {@link Region Region's} data.
	 *
	 * @param compressor {@link Compressor} used to compress the this {@link Region Region's} data.
	 * @see Compressor
	 */
	public void setCompressor(Compressor compressor) {
		this.compressor = compressor;
	}

	public void setConcurrencyChecksEnabled(Boolean concurrencyChecksEnabled) {
		this.concurrencyChecksEnabled = concurrencyChecksEnabled;
	}

	public void setConcurrencyLevel(Integer concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

	public void setCustomEntryIdleTimeout(CustomExpiry<K, V> customEntryIdleTimeout) {
		this.customEntryIdleTimeout = customEntryIdleTimeout;
	}

	public void setCustomEntryTimeToLive(CustomExpiry<K, V> customEntryTimeToLive) {
		this.customEntryTimeToLive = customEntryTimeToLive;
	}

	/**
	 * Sets the Data Policy. Used only when a new Region is created.
	 *
	 * @param dataPolicy the client Region's Data Policy.
	 * @see DataPolicy
	 */
	public void setDataPolicy(DataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
	}

	final boolean isDestroy() {
		return this.destroy;
	}

	/**
	 * Indicates whether the region referred by this factory bean will be
	 * destroyed on shutdown (default false). Note: destroy and close are
	 * mutually exclusive. Enabling one will automatically disable the other.
	 *
	 * @param destroy whether or not to destroy the region
	 * @see #setClose(boolean)
	 */
	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
		this.close = this.close && !destroy; // retain previous value iff destroy is false;
	}

	/**
	 * Sets the name of disk store to use for overflow and persistence
	 *
	 * @param diskStoreName a String specifying the 'name' of the client Region Disk Store.
	 */
	public void setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
	}

	public void setDiskSynchronous(Boolean diskSynchronous) {
		this.diskSynchronous = diskSynchronous;
	}

	public void setEntryIdleTimeout(ExpirationAttributes entryIdleTimeout) {
		this.entryIdleTimeout = entryIdleTimeout;
	}

	public void setEntryTimeToLive(ExpirationAttributes entryTimeToLive) {
		this.entryTimeToLive = entryTimeToLive;
	}

	public void setEvictionAttributes(EvictionAttributes evictionAttributes) {
		this.evictionAttributes = evictionAttributes;
	}

	public void setInitialCapacity(Integer initialCapacity) {
		this.initialCapacity = initialCapacity;
	}

	/**
	 * Set the interests for this client region. Both key and regex interest are
	 * supported.
	 *
	 * @param interests the interests to set
	 */
	public void setInterests(Interest<K>[] interests) {
		this.interests = interests;
	}

	Interest<K>[] getInterests() {
		return this.interests;
	}

	/**
	 * Sets a {@link Class type} constraint on this {@link Region client Region's} keys.
	 *
	 * @param keyConstraint {@link Class type} of this {@link Region client Region's} keys.
	 * @see Class
	 */
	public void setKeyConstraint(Class<K> keyConstraint) {
		this.keyConstraint = keyConstraint;
	}

	public void setLoadFactor(Float loadFactor) {
		this.loadFactor = loadFactor;
	}

	protected boolean isPersistent() {
		return Boolean.TRUE.equals(persistent);
	}

	protected boolean isNotPersistent() {
		return Boolean.FALSE.equals(persistent);
	}

	/**
	 * Configures whether this {@link Region client Region} is persistent, i.e. stores data to disk.
	 *
	 * @param persistent boolean value used to enable disk persistence.
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * Configures the {@link Pool} used by this client {@link Region}.
	 *
	 * @param pool {@link Pool} used by this client {@link Region} to send/receive data to/from the server.
	 * @see Pool
	 * @see #setPoolName(String)
	 */
	public void setPool(Pool pool) {
		setPoolName(Optional.ofNullable(pool).map(Pool::getName).orElse(null));
	}

	/**
	 * Configures the {@link String name} of the {@link Pool} to be used by this client {@link Region}.
	 *
	 * @param poolName {@link String} containing the name of the client {@link Pool}
	 * to be used by this client {@link Region}.
	 * @see #getPoolName()
	 * @see #setPool(Pool)
	 */
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * Returns the {@link String name} of the configured {@link Pool} used by this client {@link Region}.
	 *
	 * @return the {@link Optional} {@link String name} of the configured {@link Pool} to be used by
	 * this client {@link Region}.
	 * @see #setPoolName(String)
	 */
	public Optional<String> getPoolName() {
		return Optional.ofNullable(this.poolName);
	}

	/**
	 * Sets (configures) the {@link PoolResolver} used by this client {@link Region} to resolve {@link Pool} objects.
	 *
	 * The {@link Pool} objects may be managed or un-managed depending on the {@link PoolResolver} implementation.
	 *
	 * @param poolResolver {@link PoolResolver} used to resolve the configured {@link Pool}.
	 * @see PoolResolver
	 */
	public void setPoolResolver(@Nullable PoolResolver poolResolver) {
		this.poolResolver = poolResolver;
	}

	/**
	 * Gets the configured {@link PoolResolver} used by this client {@link Region} to resolve {@link Pool} objects.
	 *
	 * @return the configured {@link PoolResolver}.  If no {@link PoolResolver} was configured, then return the default,
	 * {@link PoolManagerPoolResolver}.
	 * @see PoolResolver
	 * @see #getDefaultPoolResolver()
	 */
	public @NonNull PoolResolver getPoolResolver() {

		PoolResolver poolResolver = this.poolResolver;

		return poolResolver != null ? poolResolver : getDefaultPoolResolver();
	}

	/**
	 * Gets a reference to the configured, default {@link PoolResolver} used by this client {@link Region} to resolve
	 * {@link Pool} objects if a explicit {@link PoolResolver} was not configured.
	 *
	 * The {@literal default} {@link PoolResolver} uses a composition of the {@link BeanFactoryPoolResolver}
	 * and {@link PoolManagerPoolResolver} to fallback on.
	 *
	 * @return the {@literal default} {@link PoolResolver}.
	 * @see BeanFactoryPoolResolver
	 * @see PoolManagerPoolResolver
	 * @see PoolResolver
	 */
	public @NonNull PoolResolver getDefaultPoolResolver() {
		return this.defaultPoolResolver;
	}

	public void setRegionIdleTimeout(ExpirationAttributes regionIdleTimeout) {
		this.regionIdleTimeout = regionIdleTimeout;
	}

	public void setRegionTimeToLive(ExpirationAttributes regionTimeToLive) {
		this.regionTimeToLive = regionTimeToLive;
	}

	/**
	 * Initializes the {@link DataPolicy} of the {@link Region client Region}
	 * using the given {@link ClientRegionShortcut}.
	 *
	 * @param shortcut {@link ClientRegionShortcut} used to initialize the {@link DataPolicy}
	 * of this {@link Region client Region}.
	 * @see ClientRegionShortcut
	 */
	public void setShortcut(ClientRegionShortcut shortcut) {
		this.shortcut = shortcut;
	}

	public void setStatisticsEnabled(Boolean statisticsEnabled) {
		this.statisticsEnabled = statisticsEnabled;
	}

	public Boolean getStatisticsEnabled() {
		return this.statisticsEnabled;
	}

	public boolean isStatisticsEnabled() {
		return Boolean.TRUE.equals(getStatisticsEnabled());
	}

	protected boolean resolveStatisticsEnabled() {

		return isStatisticsEnabled()
			|| this.customEntryIdleTimeout != null
			|| this.customEntryTimeToLive != null
			|| this.entryIdleTimeout != null
			|| this.entryTimeToLive != null
			|| this.regionIdleTimeout != null
			|| this.regionTimeToLive != null
			|| Optional.ofNullable(getAttributes())
				.map(RegionAttributes::getStatisticsEnabled)
				.orElse(false);
	}

	/**
	 * Sets a {@link Class type} constraint on this {@link Region client Region's} values.
	 *
	 * @param valueConstraint {@link Class type} of this {@link Region client Region's} values.
	 * @see Class
	 */
	public void setValueConstraint(Class<V> valueConstraint) {
		this.valueConstraint = valueConstraint;
	}
}
