/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newRuntimeException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.data.gemfire.support.GemfireFunctions;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to look up or create {@link Region Regions}.
 *
 * For declaring and configuring new {@literal client} {@link Region Regions}, see {@link ClientRegionFactoryBean}.
 * and {@link Class subclasses}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see ClientCache
 * @see Region
 * @see FactoryBean
 * @see InitializingBean
 * @see AbstractFactoryBeanSupport
 */
@SuppressWarnings("unused")
public abstract class ResolvableRegionFactoryBean<K, V> extends AbstractFactoryBeanSupport<Region<K, V>>
		implements InitializingBean {

	protected static final String CREATING_REGION_LOG_MESSAGE = "Creating Region [%1$s] in Cache [%2$s]";
	protected static final String REGION_FOUND_LOG_MESSAGE = "Found Region [%1$s] in Cache [%2$s]";
	protected static final String REGION_NOT_FOUND_ERROR_MESSAGE = "Region [%1$s] in Cache [%2$s] not found";

	private Boolean lookupEnabled = false;

	private ClientCache cache;

	private Region<?, ?> parent;

	private Resource snapshot;

	private volatile Region<K, V> region;

	private String name;
	private String regionName;

	/**
	 * Initializes this {@link ResolvableRegionFactoryBean} after properties have been set by the Spring container.
	 *
	 * @throws Exception if initialization fails.
	 * @see InitializingBean#afterPropertiesSet()
	 * @see #createRegion(ClientCache, String)
	 */
	@Override
	@SuppressWarnings("all")
	public void afterPropertiesSet() throws Exception {

		ClientCache cache = requireCache();

		String regionName = requireRegionName();

		synchronized (cache) {

			setRegion(resolveRegion(cache, regionName));

			if (getRegion() != null) {
				logInfo(REGION_FOUND_LOG_MESSAGE, regionName, cache.getName());
			}
			else {
				logInfo(CREATING_REGION_LOG_MESSAGE, regionName, cache.getName());
				setRegion(postProcess(loadSnapshot(createRegion(cache, regionName))));
			}
		}
	}

	private @NonNull ClientCache requireCache() {

		ClientCache cache = getCache();

		Assert.notNull(cache, "Cache is required");

		return cache;
	}

	@NonNull String requireRegionName() {

		String regionName = resolveRegionName();

		Assert.hasText(regionName, "regionName, name or the beanName property must be set");

		return regionName;
	}

	private @Nullable Region<K, V> resolveRegion(@NonNull ClientCache cache, @NonNull String regionName) {

		return isLookupEnabled()
			? Optional.ofNullable(getParent())
				.<Region<K, V>>map(GemfireFunctions.getSubregionFromRegion(regionName))
				.orElseGet(GemfireFunctions.getRegionFromCache(cache, regionName))
			: null;
	}

	/**
	 * Resolves the configured {@link String name} of the {@link Region}.
	 *
	 * @return a {@link String} containing the {@literal name} of the {@link Region}.
	 * @see Region#getName()
	 */
	public String resolveRegionName() {

		String name = this.name;
		String regionName = this.regionName;

		return StringUtils.hasText(regionName) ? regionName
			: StringUtils.hasText(name) ? name
			: getBeanName();
	}

	/**
	 * Creates a new {@link Region} with the given {@link String name}.
	 *
	 * This method gets called when a {@link Region} with the specified {@link String name} does not already exist.
	 * By default, this method implementation throws a {@link BeanInitializationException} and it is expected
	 * that {@link Class subclasses} will override this method.
	 *
	 * @param cache reference to the {@link ClientCache}.
	 * @param regionName {@link String name} of the new {@link Region}.
	 * @return a new {@link Region} with the given {@link String name}.
	 * @throws BeanInitializationException by default unless a {@link Class subclass} overrides this method.
	 * @see ClientCache
	 * @see Region
	 */
	protected Region<K, V> createRegion(ClientCache cache, String regionName) throws Exception {
		throw new BeanInitializationException(String.format(REGION_NOT_FOUND_ERROR_MESSAGE, regionName, cache));
	}

	/**
	 * Loads data from the configured {@link Resource snapshot} into the given {@link Region}.
	 *
	 * @param region {@link Region} to load; must not be {@literal null}.
	 * @return the given {@link Region}.
	 * @throws RuntimeException if loading the snapshot fails.
	 * @see Region#loadSnapshot(InputStream)
	 * @see Region
	 */
	protected @NonNull Region<K, V> loadSnapshot(@NonNull Region<K, V> region) {

		Resource snapshot = this.snapshot;

		if (snapshot != null) {

			SpringExtensions.VoidReturningThrowableOperation operation =
				() -> region.loadSnapshot(snapshot.getInputStream());

			Function<Throwable, RuntimeException> exceptionHandler =
				cause -> newRuntimeException(cause, "Failed to load snapshot [%s]", snapshot);

			SpringExtensions.safeRunOperation(operation, exceptionHandler);
		}

		return region;
	}

	/**
	 * Post-process the {@link Region} created by this {@link ClientRegionFactoryBean}.
	 *
	 * @param region {@link Region} to process.
	 * @see Region
	 */
	protected Region<K, V> postProcess(Region<K, V> region) {
		return region;
	}

	/**
	 * Returns an object reference to the {@link Region} created by this {@link ResolvableRegionFactoryBean}.
	 *
	 * @return an object reference to the {@link Region} created by this {@link ResolvableRegionFactoryBean}.
	 * @see FactoryBean#getObject()
	 * @see Region
	 * @see #getRegion()
	 */
	@Override
	public Region<K, V> getObject() throws Exception {
		return getRegion();
	}

	/**
	 * Returns the {@link Class} type of the {@link Region} produced by this {@link ResolvableRegionFactoryBean}.
	 *
	 * @return the {@link Class} type of the {@link Region} produced by this {@link ResolvableRegionFactoryBean}.
	 * @see FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {

		Region<?, ?> region = getRegion();

		return region != null ? region.getClass() : Region.class;
	}

	/**
	 * Returns a reference to the {@link ClientCache} used to create the {@link Region}.
	 *
	 * @return a reference to the {@link ClientCache} used to create the {@link Region}.
	 * @see ClientCache
	 */
	public ClientCache getCache() {
		return this.cache;
	}

	/**
	 * Sets a reference to the {@link ClientCache} used to create the {@link Region}.
	 *
	 * @param cache reference to the {@link ClientCache}.
	 * @see ClientCache
	 */
	public void setCache(ClientCache cache) {
		this.cache = cache;
	}

	public void setLookupEnabled(@Nullable Boolean lookupEnabled) {
		this.lookupEnabled = lookupEnabled;
	}

	public @Nullable Boolean getLookupEnabled() {
		return this.lookupEnabled;
	}

	public boolean isLookupEnabled() {
		return Boolean.TRUE.equals(getLookupEnabled());
	}

	/**
	 * Sets the name of the cache {@link Region} based on the bean 'name' attribute.  If no {@link Region} is found
	 * with the given name, a new one will be created.  If no name is given, the value of the 'beanName' property
	 * will be used.
	 *
	 * @param name {@link Region} name.
	 * @see #setBeanName(String)
	 * @see Region#getFullPath()
	 */
	public void setName(@NonNull String name) {
		this.name = name;
	}

	/**
	 * Sets a reference to the parent {@link Region} making this {@link FactoryBean}
	 * represent a cache {@link Region Sub-Region}.
	 *
	 * @param parent reference to the parent {@link Region}.
	 * @see Region
	 */
	public void setParent(@Nullable Region<?, ?> parent) {
		this.parent = parent;
	}

	/**
	 * Returns a reference to the parent {@link Region} making this {@link FactoryBean}
	 * represent a cache {@link Region Sub-Region}.
	 *
	 * @return a reference to the parent {@link Region}, or {@literal null} if this {@link Region}
	 * is not a {@link Region Sub-Region}.
	 * @see Region
	 */
	protected @Nullable Region<?, ?> getParent() {
		return this.parent;
	}

	/**
	 * Sets a reference to the {@link Region} to be resolved by this Spring {@link FactoryBean}.
	 *
	 * @param region reference to the resolvable {@link Region}.
	 * @see Region
	 */
	protected void setRegion(@Nullable Region<K, V> region) {
		this.region = region;
	}

	/**
	 * Returns a reference to the {@link Region} resolved by this Spring {@link FactoryBean}
	 * during the lookup operation; maybe a new {@link Region}.
	 *
	 * @return a reference to the {@link Region} resolved during lookup.
	 * @see Region
	 */
	public @Nullable Region<K, V> getRegion() {
		return this.region;
	}

	/**
	 * Sets the name of the cache {@link Region}.  If no {@link Region} is found with the given name,
	 * a new one will be created.  If no name is given, the value of the 'name' property will be used.
	 *
	 * @param regionName name of the {@link Region}.
	 * @see #setName(String)
	 * @see Region#getName()
	 */
	public void setRegionName(@Nullable String regionName) {
		this.regionName = regionName;
	}

	/**
	 * Sets the snapshots used for loading a newly <i>created</i> region. That
	 * is, the snapshot will be used <i>only</i> when a new region is created -
	 * if the region already exists, no loading will be performed.
	 *
	 * @see #setName(String)
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(@Nullable Resource snapshot) {
		this.snapshot = snapshot;
	}
}
