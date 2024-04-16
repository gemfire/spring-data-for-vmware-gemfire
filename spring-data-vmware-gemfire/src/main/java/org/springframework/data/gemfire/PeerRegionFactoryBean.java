/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.geode.cache.AttributesFactory;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.PartitionAttributesFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;
import org.apache.geode.compression.Compressor;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.eviction.EvictingRegionFactoryBean;
import org.springframework.data.gemfire.expiration.ExpiringRegionFactoryBean;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} and abstract base class extended by other SDG {@link FactoryBean FactoryBeans}
 * used to construct, configure and initialize {@literal peer} {@link Region Regions}.
 *
 * This {@link FactoryBean} allows for very easy and flexible creation of {@literal peer} {@link Region Regions}.
 * For {@literal client} {@link Region Regions}, see the {@link ClientRegionFactoryBean}.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see Cache
 * @see CacheListener
 * @see CacheLoader
 * @see CacheWriter
 * @see CustomExpiry
 * @see DataPolicy
 * @see DiskStore
 * @see EvictionAttributes
 * @see ExpirationAttributes
 * @see GemFireCache
 * @see PartitionAttributes
 * @see Region
 * @see RegionAttributes
 * @see RegionFactory
 * @see RegionShortcut
 * @see Scope
 * @see Compressor
 * @see DisposableBean
 * @see SmartLifecycle
 * @see ConfigurableRegionFactoryBean
 * @see ResolvableRegionFactoryBean
 * @see ClientRegionFactoryBean
 * @see EvictingRegionFactoryBean
 * @see ExpiringRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.RegionConfigurer
 */
@SuppressWarnings("unused")
public abstract class PeerRegionFactoryBean<K, V> extends ConfigurableRegionFactoryBean<K, V>
		implements DisposableBean, EvictingRegionFactoryBean, ExpiringRegionFactoryBean<K, V>, SmartLifecycle {

	private boolean close = false;
	private boolean destroy = false;
	private boolean running = false;

	private Boolean persistent;
	private Boolean statisticsEnabled;

	private CacheListener<K, V>[] cacheListeners;

	private CacheLoader<K, V> cacheLoader;

	private CacheWriter<K, V> cacheWriter;

	private Class<K> keyConstraint;
	private Class<V> valueConstraint;

	private Compressor compressor;

	private CustomExpiry<K, V> customEntryIdleTimeout;
	private CustomExpiry<K, V> customEntryTimeToLive;

	private DataPolicy dataPolicy;

	private EvictionAttributes evictionAttributes;

	private ExpirationAttributes entryIdleTimeout;
	private ExpirationAttributes entryTimeToLive;
	private ExpirationAttributes regionIdleTimeout;
	private ExpirationAttributes regionTimeToLive;

	private RegionAttributes<K, V> attributes;

	private RegionShortcut shortcut;

	private Resource snapshot;

	private Scope scope;

	private String diskStoreName;

	/**
	 * Creates a new {@link Region} with the given {@link String name}.
	 *
	 * @param gemfireCache reference to the {@link GemFireCache}.
	 * @param regionName {@link String name} of the new {@link Region}.
	 * @return a new {@link Region} with the given {@link String name}.
	 * @see GemFireCache
	 * @see Region
	 */
	@Override
	protected Region<K, V> createRegion(GemFireCache gemfireCache, String regionName) {

		verifyLockGrantorEligibility(getAttributes(), getScope());

		Cache cache = resolveCache(gemfireCache);

		RegionFactory<K, V> regionFactory = postProcess(configure(createRegionFactory(cache)));

		Region<K, V> region = newRegion(regionFactory, getParent(), regionName);

		region = becomeLockGrantor(region);

		return region;
	}

	private Region<K, V> becomeLockGrantor(Region<K, V> region) {

		if (isLockGrantor(region)) {
			region.becomeLockGrantor();
		}

		return region;
	}

	private boolean isLockGrantor(@Nullable Region<K, V> region) {
		return region != null && region.getAttributes() != null && region.getAttributes().isLockGrantor();
	}

	private Region<K, V> newRegion(RegionFactory<K, V> regionFactory, Region<?, ?> parentRegion, String regionName) {

		if (parentRegion != null) {

			logInfo("Creating Subregion [%1$s] with parent Region [%2$s]", regionName, parentRegion.getName());

			return regionFactory.createSubregion(parentRegion, regionName);
		}
		else {

			logInfo("Created Region [%s]", regionName);

			return regionFactory.create(regionName);
		}
	}

	private Cache resolveCache(GemFireCache gemfireCache) {

		return Optional.ofNullable(gemfireCache)
			.filter(Cache.class::isInstance)
			.map(Cache.class::cast)
			.orElseThrow(() -> newIllegalArgumentException("Peer Cache is required"));
	}

	private RegionAttributes<K, V> verifyLockGrantorEligibility(RegionAttributes<K, V> regionAttributes, Scope scope) {

		Optional.ofNullable(regionAttributes).ifPresent(attributes ->
			Assert.state(!attributes.isLockGrantor() || verifyScope(scope),
				"Lock Grantor only applies to GLOBAL Scoped Regions"));

		return regionAttributes;
	}

	private boolean verifyScope(Scope scope) {
		return scope == null || Scope.GLOBAL.equals(scope);
	}

	/**
	 * Create a new instance of {@link RegionFactory} initialized with the given {@link Cache} that is then used
	 * to construct, configure and initialize the {@link Region} specified by this {@link PeerRegionFactoryBean}.
	 *
	 * @param cache reference to the {@link Cache}.
	 * @return a {@link RegionFactory} used to construct, configure and initialize the {@link Region}
	 * specified by this {@link PeerRegionFactoryBean}.
	 * @see Cache#createRegionFactory(RegionShortcut)
	 * @see Cache#createRegionFactory(RegionAttributes)
	 * @see Cache#createRegionFactory()
	 * @see RegionFactory
	 */
	protected RegionFactory<K, V> createRegionFactory(Cache cache) {

		if (this.shortcut != null) {

			RegionFactory<K, V> regionFactory =
				mergeRegionAttributes(cache.createRegionFactory(this.shortcut), this.attributes);

			setDataPolicy(getDataPolicy(regionFactory, this.shortcut));

			return regionFactory;
		}
		else if (this.attributes != null) {
			return cache.createRegionFactory(this.attributes);
		}
		else {
			return cache.createRegionFactory();
		}
	}

	/**
	 * Configures the {@link RegionFactory} based on the configuration settings of this {@link PeerRegionFactoryBean}.
	 *
	 * @param regionFactory {@link RegionFactory} to configure
	 * @return the given {@link RegionFactory}.
	 * @see RegionFactory
	 */
	protected RegionFactory<K, V> configure(RegionFactory<K, V> regionFactory) {

		regionFactory.setStatisticsEnabled(resolveStatisticsEnabled());

		Arrays.stream(ArrayUtils.nullSafeArray(this.cacheListeners, CacheListener.class))
			.forEach(regionFactory::addCacheListener);

		Optional.ofNullable(this.cacheLoader).ifPresent(regionFactory::setCacheLoader);

		Optional.ofNullable(this.cacheWriter).ifPresent(regionFactory::setCacheWriter);

		Optional.ofNullable(this.compressor).ifPresent(regionFactory::setCompressor);

		Optional.ofNullable(this.customEntryIdleTimeout).ifPresent(regionFactory::setCustomEntryIdleTimeout);

		Optional.ofNullable(this.customEntryTimeToLive).ifPresent(regionFactory::setCustomEntryTimeToLive);

		resolveDataPolicy(regionFactory, this.persistent, this.dataPolicy);

		Optional.ofNullable(this.diskStoreName)
			.filter(name -> isDiskStoreConfigurationAllowed())
			.ifPresent(regionFactory::setDiskStoreName);

		Optional.ofNullable(this.entryIdleTimeout).ifPresent(regionFactory::setEntryIdleTimeout);

		Optional.ofNullable(this.entryTimeToLive).ifPresent(regionFactory::setEntryTimeToLive);

		Optional.ofNullable(this.evictionAttributes).ifPresent(regionFactory::setEvictionAttributes);

		Optional.ofNullable(this.keyConstraint).ifPresent(regionFactory::setKeyConstraint);

		Optional.ofNullable(this.regionIdleTimeout).ifPresent(regionFactory::setRegionIdleTimeout);

		Optional.ofNullable(this.regionTimeToLive).ifPresent(regionFactory::setRegionTimeToLive);

		Optional.ofNullable(getScope()).ifPresent(regionFactory::setScope);

		Optional.ofNullable(this.valueConstraint).ifPresent(regionFactory::setValueConstraint);

		return regionFactory;
	}

	/**
	 * Post-process the {@link RegionFactory} used to create the {@link Region} specified by
	 * this {@link PeerRegionFactoryBean} during initialization.
	 *
	 * The {@link RegionFactory} has been already constructed, configured and initialized by
	 * this {@link PeerRegionFactoryBean} before this method gets invoked.
	 *
	 * @param regionFactory {@link RegionFactory} used to create the {@link Region}.
	 * @return the given {@link RegionFactory}.
	 * @see RegionFactory
	 */
	protected RegionFactory<K, V> postProcess(RegionFactory<K, V> regionFactory) {
		return regionFactory;
	}

	/*
	 * This method is not considered part of the PeerRegionFactoryBean API and is strictly used for testing purposes!
	 *
	 * NOTE: Cannot pass RegionAttributes.class as the "targetType" in the second invocation of getFieldValue(..)
	 * since the "regionAttributes" field is naively declared as a instance of the implementation class type
	 * (RegionAttributesImpl) rather than the interface type (RegionAttributes)...
	 * so much for 'programming to interfaces' in GemFire!
	 *
	 * @see org.apache.geode.cache.RegionFactory#attrsFactory
	 * @see org.apache.geode.cache.AttributesFactory#regionAttributes
	 * @see org.apache.geode.cache.RegionAttributes#getDataPolicy
	 * @see org.apache.geode.cache.DataPolicy
	 */
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	DataPolicy getDataPolicy(RegionFactory regionFactory, RegionShortcut regionShortcut) {

		return getFieldValue(regionFactory, "attrsFactory", AttributesFactory.class)
			.flatMap(attributesFactory -> getFieldValue(attributesFactory,"regionAttributes", null))
			.map(regionAttributes -> ((RegionAttributes<K, V>) regionAttributes).getDataPolicy())
			.orElseGet(() -> RegionShortcutToDataPolicyConverter.INSTANCE.convert(regionShortcut));
	}

	@SuppressWarnings("unchecked")
	private <T> Optional<T> getFieldValue(Object source, String fieldName, Class<T> targetType) {

		return Optional.ofNullable(source)
			.map(Object::getClass)
			.map(type -> ReflectionUtils.findField(type, fieldName, targetType))
			.map(field -> {
				ReflectionUtils.makeAccessible(field);
				return field;
			})
			.map(field -> (T) ReflectionUtils.getField(field, source));
	}

	/**
	 * Intelligently merges the given RegionAttributes with the configuration setting of the RegionFactory. This method
	 * is used to merge the RegionAttributes and PartitionAttributes with the RegionFactory that is created when the
	 * user specified a RegionShortcut.  This method gets called by the createRegionFactory method depending upon
	 * the value passed to the Cache.createRegionFactory() method (i.e. whether there was a RegionShortcut specified
	 * or not).
	 *
	 * @param <K> the Class type fo the Region key.
	 * @param <V> the Class type of the Region value.
	 * @param regionFactory the GemFire RegionFactory used to configure and create the Region that is the product
	 * of this PeerRegionFactoryBean.
	 * @param regionAttributes the RegionAttributes containing the Region configuration settings to merge to the
	 * RegionFactory.
	 * @return the RegionFactory with the configuration settings of the RegionAttributes merged.
	 * @see #isUserSpecifiedEvictionAttributes(RegionAttributes)
	 * @see #validateRegionAttributes(RegionAttributes)
	 * @see RegionAttributes
	 * @see RegionFactory
	 */
	protected <K, V> RegionFactory<K, V> mergeRegionAttributes(RegionFactory<K, V> regionFactory,
			RegionAttributes<K, V> regionAttributes) {

		if (regionAttributes != null) {

			// NOTE: this validation may not be strictly necessary depending on how the RegionAttributes were "created".
			validateRegionAttributes(regionAttributes);

			regionFactory.setCloningEnabled(regionAttributes.getCloningEnabled());
			regionFactory.setCompressor(regionAttributes.getCompressor());
			regionFactory.setConcurrencyChecksEnabled(regionAttributes.getConcurrencyChecksEnabled());
			regionFactory.setConcurrencyLevel(regionAttributes.getConcurrencyLevel());
			regionFactory.setCustomEntryIdleTimeout(regionAttributes.getCustomEntryIdleTimeout());
			regionFactory.setCustomEntryTimeToLive(regionAttributes.getCustomEntryTimeToLive());
			regionFactory.setDiskSynchronous(regionAttributes.isDiskSynchronous());
			regionFactory.setEnableSubscriptionConflation(regionAttributes.getEnableSubscriptionConflation());
			regionFactory.setEntryIdleTimeout(regionAttributes.getEntryIdleTimeout());
			regionFactory.setEntryTimeToLive(regionAttributes.getEntryTimeToLive());

			// NOTE: EvictionAttributes are created by certain RegionShortcuts; null check needed!
			if (isUserSpecifiedEvictionAttributes(regionAttributes)) {
				regionFactory.setEvictionAttributes(regionAttributes.getEvictionAttributes());
			}

			regionFactory.setIgnoreJTA(regionAttributes.getIgnoreJTA());
			regionFactory.setInitialCapacity(regionAttributes.getInitialCapacity());
			regionFactory.setKeyConstraint(regionAttributes.getKeyConstraint());
			regionFactory.setLoadFactor(regionAttributes.getLoadFactor());
			regionFactory.setLockGrantor(regionAttributes.isLockGrantor());
			regionFactory.setMembershipAttributes(regionAttributes.getMembershipAttributes());

			mergePartitionAttributes(regionFactory, regionAttributes);

			regionFactory.setPoolName(regionAttributes.getPoolName());
			regionFactory.setRegionIdleTimeout(regionAttributes.getRegionIdleTimeout());
			regionFactory.setRegionTimeToLive(regionAttributes.getRegionTimeToLive());
			regionFactory.setStatisticsEnabled(regionAttributes.getStatisticsEnabled());
			regionFactory.setSubscriptionAttributes(regionAttributes.getSubscriptionAttributes());
			regionFactory.setValueConstraint(regionAttributes.getValueConstraint());
		}

		return regionFactory;
	}

	/**
	 * Merges the {@link RegionAttributes} into the {@link RegionFactory}.
	 *
	 * @param regionFactory {@link RegionFactory} to configure.
	 * @param regionAttributes {@link RegionAttributes} used to configure the {@link RegionFactory}
	 * if not {@literal null}.
	 * @see RegionAttributes
	 * @see RegionFactory
	 */
	@SuppressWarnings("rawtypes")
	protected <K, V> void mergePartitionAttributes(RegionFactory<K, V> regionFactory,
			RegionAttributes<K, V> regionAttributes) {

		// NOTE: PartitionAttributes are created by certain RegionShortcuts; need the null check since RegionAttributes
		// can technically return null!
		// NOTE: Most likely, the PartitionAttributes will never be null since the PartitionRegionFactoryBean always
		// sets a PartitionAttributesFactoryBean BeanBuilder on the RegionAttributesFactoryBean "partitionAttributes"
		// property.
		if (regionAttributes.getPartitionAttributes() != null) {

			PartitionAttributes partitionAttributes = regionAttributes.getPartitionAttributes();

			PartitionAttributesFactory partitionAttributesFactory = new PartitionAttributesFactory(partitionAttributes);

			RegionShortcutWrapper shortcutWrapper = RegionShortcutWrapper.valueOf(shortcut);

			// NOTE: However, since the default value of redundancy is 0, we need to account for 'redundant'
			// RegionShortcut types, which specify a redundancy of 1.
			if (shortcutWrapper.isRedundant() && partitionAttributes.getRedundantCopies() == 0) {
				partitionAttributesFactory.setRedundantCopies(1);
			}

			// NOTE: And, since the default value of localMaxMemory is based on the system memory, we need to
			// account for 'proxy' RegionShortcut types, which specify a local max memory of 0.
			if (shortcutWrapper.isProxy()) {
				partitionAttributesFactory.setLocalMaxMemory(0);
			}

			// NOTE: Internally, RegionFactory.setPartitionAttributes handles merging the PartitionAttributes, hooray!
			regionFactory.setPartitionAttributes(partitionAttributesFactory.create());
		}
	}

	private boolean isDiskStoreConfigurationAllowed() {

		boolean allow = StringUtils.hasText(this.diskStoreName);

		allow &= getDataPolicy().withPersistence()
			|| (getAttributes() != null && getAttributes().getEvictionAttributes() != null
				&& EvictionAction.OVERFLOW_TO_DISK.equals(this.attributes.getEvictionAttributes().getAction()));

		return allow;
	}

	/*
	 * This method is not part of the PeerRegionFactoryBean API and is strictly used for testing purposes!
	 *
	 * @see org.apache.geode.internal.cache.UserSpecifiedRegionAttributes#hasEvictionAttributes
	 */
	boolean isUserSpecifiedEvictionAttributes(RegionAttributes<?, ?> regionAttributes) {

		SpringExtensions.ValueReturningThrowableOperation<Boolean> hasEvictionAttributes = () ->
			Optional.ofNullable(regionAttributes)
				.map(Object::getClass)
				.map(type -> ReflectionUtils.findMethod(type, "hasEvictionAttributes"))
				.map(method -> ReflectionUtils.invokeMethod(method, regionAttributes))
				.map(Boolean.TRUE::equals)
				.orElse(false);

		return SpringExtensions.safeGetValue(hasEvictionAttributes, false);
	}

	/*
	 * This method is not part of the PeerRegionFactoryBean API and is strictly used for testing purposes!
	 *
	 * @see org.apache.geode.cache.AttributesFactory#validateAttributes(:RegionAttributes)
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	void validateRegionAttributes(RegionAttributes regionAttributes) {
		AttributesFactory.validateAttributes(regionAttributes);
	}

	/**
	 * Returns true when the user explicitly specified a value for the persistent attribute and it is false.  If the
	 * persistent attribute was not explicitly specified, then the persistence setting is implicitly undefined
	 * and will be determined by the Data Policy.
	 *
	 * @return true when the user specified an explicit value for the persistent attribute and it is false;
	 * false otherwise.
	 * @see #isPersistent()
	 */
	protected boolean isNotPersistent() {
		return Boolean.FALSE.equals(this.persistent);
	}

	/**
	 * Returns true when the user explicitly specified a value for the persistent attribute and it is true.  If the
	 * persistent attribute was not explicitly specified, then the persistence setting is implicitly undefined
	 * and will be determined by the Data Policy.
	 *
	 * @return true when the user specified an explicit value for the persistent attribute and it is true;
	 * false otherwise.
	 * @see #isNotPersistent()
	 */
	protected boolean isPersistent() {
		return Boolean.TRUE.equals(this.persistent);
	}

	/**
	 * Validates and sets the Data Policy on the RegionFactory used to create and configure the Region from this
	 * FactoryBean.
	 *
	 * @param regionFactory the RegionFactory used by this FactoryBean to create and configure the Region.
	 * @param persistent a boolean value indicating whether the Region should be persistent and persist it's
	 * data to disk.
	 * @param dataPolicy the configured Data Policy for the Region.
	 * @see #resolveDataPolicy(RegionFactory, Boolean, String)
	 * @see DataPolicy
	 * @see RegionFactory
	 */
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, DataPolicy dataPolicy) {

		if (dataPolicy != null) {
			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(dataPolicy, this.persistent);
			regionFactory.setDataPolicy(dataPolicy);
			setDataPolicy(dataPolicy);
		}
		else {
			resolveDataPolicy(regionFactory, persistent, (String) null);
		}
	}

	/**
	 * Validates the configured Data Policy and may override it, taking into account the 'persistent' attribute
	 * and constraints for the Region type.
	 *
	 * @param regionFactory the GemFire RegionFactory used to create the desired Region.
	 * @param persistent a boolean value indicating whether the Region should persist it's data to disk.
	 * @param dataPolicy requested Data Policy as set by the user in the Spring GemFire configuration meta-data.
	 * @see DataPolicy
	 * @see RegionFactory
	 */
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, String dataPolicy) {

		if (dataPolicy != null) {

			DataPolicy resolvedDataPolicy = new DataPolicyConverter().convert(dataPolicy);

			Assert.notNull(resolvedDataPolicy, String.format("Data Policy [%s] is invalid", dataPolicy));

			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(resolvedDataPolicy, this.persistent);

			regionFactory.setDataPolicy(resolvedDataPolicy);
			setDataPolicy(resolvedDataPolicy);
		}
		else {

			DataPolicy regionAttributesDataPolicy = getDataPolicy(getAttributes(), DataPolicy.DEFAULT);

			DataPolicy resolvedDataPolicy = isPersistent() && DataPolicy.DEFAULT.equals(regionAttributesDataPolicy)
				? DataPolicy.PERSISTENT_REPLICATE
				: regionAttributesDataPolicy;

			RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(resolvedDataPolicy, this.persistent);

			regionFactory.setDataPolicy(resolvedDataPolicy);
			setDataPolicy(resolvedDataPolicy);
		}
	}

	@SuppressWarnings("rawtypes")
	private DataPolicy getDataPolicy(RegionAttributes regionAttributes, DataPolicy defaultDataPolicy) {

		return Optional.ofNullable(regionAttributes)
			.map(RegionAttributes::getDataPolicy)
			.orElse(defaultDataPolicy);
	}

	/**
	 * Closes and destroys this {@link Region}.
	 *
	 * @throws Exception if {@code destroy()} fails.
	 * @see DisposableBean
	 * @see Region#close()
	 * @see Region#destroyRegion()
	 */
	@Override
	public void destroy() throws Exception {

		Optional.ofNullable(getObject()).ifPresent(region -> {

			if (this.close && RegionUtils.isCloseable(region)) {
				RegionUtils.close(region);
			}

			if (this.destroy) {
				region.destroyRegion();
			}
		});
	}

	/**
	 * Sets the {@link RegionAttributes} used to configure this {@link Region}.
	 *
	 * Specifying {@link RegionAttributes} allows full control in configuring various {@link Region} settings.
	 * Used only when the {@link Region} is created and not when the {@link Region} is looked up.
	 *
	 * @param attributes {@link RegionAttributes} used to configure this {@link Region}.
	 * @see RegionAttributes
	 */
	public void setAttributes(RegionAttributes<K, V> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns the {@link RegionAttributes} used to configure this {@link Region}.
	 *
	 * @return the {@link RegionAttributes} used to configure this {@link Region}.
	 * @see RegionAttributes
	 */
	public RegionAttributes<K, V> getAttributes() {

		return Optional.ofNullable(getRegion())
			.map(Region::getAttributes)
			.orElse(this.attributes);
	}

	/**
	 * Configures {@link CacheListener CacheListeners} used to listen for entry events on this {@link Region}.
	 *
	 * Used only when a new {@link Region} is created and not {@link #isLookupEnabled() looked up}.
	 *
	 * Overrides the {@link Region} settings specified in {@link RegionAttributes}
	 * set with {@link #setAttributes(RegionAttributes)}.
	 *
	 * @param cacheListeners array {@link CacheListener CacheListeners} to register with this {@link Region}.
	 * @see CacheListener
	 */
	public void setCacheListeners(CacheListener<K, V>[] cacheListeners) {
		this.cacheListeners = cacheListeners;
	}

	/**
	 * Configures the {@link CacheLoader} used by this {@link Region} to perform {@literal synchronous read-through}
	 * data access operations to an underlying, external data source.
	 *
	 * Used only when a new {@link Region} is created and not {@link #isLookupEnabled() looked up}.
	 *
	 * Overrides the {@link Region} settings specified in {@link RegionAttributes}
	 * set with {@link #setAttributes(RegionAttributes)}.
	 *
	 * @param cacheLoader {@link CacheLoader} to register for this {@link Region}.
	 * @see CacheLoader
	 */
	public void setCacheLoader(CacheLoader<K, V> cacheLoader) {
		this.cacheLoader = cacheLoader;
	}

	/**
	 * Configures the {@link CacheWriter} used by this {@link Region} to perform {@literal synchronous write-through}
	 * data access operations to an underlying, external data source.
	 *
	 * Used only when a new {@link Region} is created and not {@link #isLookupEnabled() looked up}.
	 *
	 * Overrides the {@link Region} settings specified in {@link RegionAttributes}
	 * set with {@link #setAttributes(RegionAttributes)}.
	 *
	 * @param cacheWriter {@link CacheWriter} to register for this {@link Region}.
	 * @see CacheWriter
	 */
	public void setCacheWriter(CacheWriter<K, V> cacheWriter) {
		this.cacheWriter = cacheWriter;
	}

	/**
	 * Configure whether to close this {@literal Region} during shutdown.
	 *
	 * Defaults to {@literal true}.
	 *
	 * @param close boolean value indicating whether this {@link Region} should be closed during shutdown.
	 * @see #setDestroy(boolean)
	 */
	public void setClose(boolean close) {
		this.close = close;
	}

	/**
	 * Configures the {@link Compressor} used to compress this {@link Region Region's} data.
	 *
	 * @param compressor {@link Compressor} used to compress this {@link Region Region's} data.
	 * @see Compressor
	 */
	public void setCompressor(Compressor compressor) {
		this.compressor = compressor;
	}

	public void setCustomEntryIdleTimeout(CustomExpiry<K, V> customEntryIdleTimeout) {
		this.customEntryIdleTimeout = customEntryIdleTimeout;
	}

	public void setCustomEntryTimeToLive(CustomExpiry<K, V> customEntryTimeToLive) {
		this.customEntryTimeToLive = customEntryTimeToLive;
	}

	/**
	 * Configure whether to destroy this {@link Region} during shutdown.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @param destroy value indicating whether this {@link Region} should be destroyed during shutdown.
	 * @see #setClose(boolean)
	 */
	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	/**
	 * Configure the {@link DataPolicy} for this {@link Region}.
	 *
	 * @param dataPolicy {@link DataPolicy} used when configuring this {@link Region}.
	 * @see DataPolicy
	 * @since 1.4.0
	 */
	public void setDataPolicy(DataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
	}

	/**
	 * Returns resolved {@link DataPolicy} as configured with the {@link RegionFactory}
	 * when creating this {@link Region}.
	 *
	 * @return the configured, resolved {@link DataPolicy} used by this {@link Region}.
	 * @throws IllegalStateException if the {@link DataPolicy} has not been configured
	 * or is not resolvable.
	 * @see DataPolicy
	 */
	public DataPolicy getDataPolicy() {

		Assert.state(this.dataPolicy != null, "Data Policy has not been properly resolved yet");

		return this.dataPolicy;
	}

	/**
	 * Configures the {@link String name} of the {@link DiskStore} used by this {@link Region}
	 * for overflow and/or persistence.
	 *
	 * @param diskStoreName {@link String} containing the name of the {@link DiskStore} bean
	 * configured for this {@link Region}.
	 */
	public void setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
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

	/**
	 * Configures the {@link Class key constraint} used to enforce key {@link Class types} for this {@link Region}.
	 *
	 * @param keyConstraint {@link Class} specifying the key type constraint for this {@link Region}.
	 * @see RegionFactory#setKeyConstraint(Class)
	 * @see RegionAttributes#getKeyConstraint()
	 * @see Class
	 */
	public void setKeyConstraint(Class<K> keyConstraint) {
		this.keyConstraint = keyConstraint;
	}

	/**
	 * Configures whether to enable {@literal persistence} for this {@link Region}.
	 *
	 * When {@literal persistence} is enable, then data in the {@link Region} is persisted to disk
	 * using the configured, specified {@link DiskStore}, or the {@literal DEFAULT} {@link DiskStore}
	 * if a {@link DiskStore} was not explicitly configured.
	 *
	 * @param persistent {@link Boolean} value indicating whether to enaable {@literal persistence}
	 * for this {@link Region}.
	 */
	public void setPersistent(Boolean persistent) {
		this.persistent = persistent;
	}

	public void setRegionIdleTimeout(ExpirationAttributes regionIdleTimeout) {
		this.regionIdleTimeout = regionIdleTimeout;
	}

	public void setRegionTimeToLive(ExpirationAttributes regionTimeToLive) {
		this.regionTimeToLive = regionTimeToLive;
	}

	/**
	 * Configures the {@link Region Region's} {@link Scope}, which affects data distribution
	 * and acknowledgement strategy (useful in consistency) for the {@link Region}.
	 *
	 * @param scope {@link Scope} used to configure the {@link Region Region's} data distribution
	 * and acknowledgement strategy.
	 * @see Scope
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * Returns the configured {@link Scope} of the {@link Region} affecting data distribution
	 * and acknowledgement strategy (useful in consistency) for the {@link Region}.
	 *
	 * @return the configured {@link Scope} of the {@link Region}.
	 * @see Scope
	 */
	public Scope getScope() {
		return this.scope;
	}

	/**
	 * Configures the {@link Region} with the given {@link RegionShortcut}.
	 *
	 * @param shortcut {@link RegionShortcut} used to configure pre-defined defaults for the {@link Region}.
	 * @see RegionShortcut
	 */
	public void setShortcut(RegionShortcut shortcut) {
		this.shortcut = shortcut;
	}

	/**
	 * Returns the configured {@link RegionShortcut}.
	 *
	 * @return the configured {@link RegionShortcut}.
	 * @see RegionShortcut
	 */
	public RegionShortcut getShortcut() {
		return this.shortcut;
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
	 * Configures the {@link Class value constraint} used to enforce value {@link Class types} for this {@link Region}.
	 *
	 * @param valueConstraint {@link Class} specifying the value type constraint for this {@link Region}.
	 * @see RegionFactory#setValueConstraint(Class)
	 * @see RegionAttributes#getValueConstraint()
	 * @see Class
	 */
	public void setValueConstraint(Class<V> valueConstraint) {
		this.valueConstraint = valueConstraint;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("all")
	public void start() {
		this.running = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		this.running = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoStartup() {
		return true;
	}
}
