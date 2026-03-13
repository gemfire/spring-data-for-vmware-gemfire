/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import java.util.Optional;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.data.gemfire.support.GemfireFunctions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to look up or create {@link GudRegion Regions}.
 *
 * For declaring and configuring new {@literal client} {@link GudRegion Regions}, see {@link ClientRegionFactoryBean}.
 * and {@link Class subclasses}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see GudClientCache
 * @see GudRegion
 * @see FactoryBean
 * @see InitializingBean
 * @see AbstractFactoryBeanSupport
 */
@SuppressWarnings("unused")
public abstract class ResolvableRegionFactoryBean<K, V> extends AbstractFactoryBeanSupport<GudRegion<K, V>>
		implements InitializingBean {

	protected static final String CREATING_REGION_LOG_MESSAGE = "Creating Region [%1$s] in Cache [%2$s]";
	protected static final String REGION_FOUND_LOG_MESSAGE = "Found Region [%1$s] in Cache [%2$s]";
	protected static final String REGION_NOT_FOUND_ERROR_MESSAGE = "Region [%1$s] in Cache [%2$s] not found";

	private Boolean lookupEnabled = false;

	private GudClientCache cache;

	private GudRegion<?, ?> parent;

	private volatile GudRegion<K, V> region;

	private String name;
	private String regionName;

	/**
	 * Initializes this {@link ResolvableRegionFactoryBean} after properties have been set by the Spring container.
	 *
	 * @throws Exception if initialization fails.
	 * @see InitializingBean#afterPropertiesSet()
	 * @see #createRegion(GudClientCache, String)
	 */
	@Override
	@SuppressWarnings("all")
	public void afterPropertiesSet() throws Exception {

		GudClientCache cache = requireCache();

		String regionName = requireRegionName();

		synchronized (cache) {

			setRegion(resolveRegion(cache, regionName));

			if (getRegion() != null) {
				logInfo(REGION_FOUND_LOG_MESSAGE, regionName, cache.getName());
			} else {
				logInfo(CREATING_REGION_LOG_MESSAGE, regionName, cache.getName());
				setRegion(postProcess(createRegion(cache, regionName)));
			}
		}
	}

	private @NonNull GudClientCache requireCache() {

		GudClientCache cache = getCache();

		Assert.notNull(cache, "Cache is required");

		return cache;
	}

	@NonNull String requireRegionName() {

		String regionName = resolveRegionName();

		Assert.hasText(regionName, "regionName, name or the beanName property must be set");

		return regionName;
	}

	private @Nullable GudRegion<K, V> resolveRegion(@NonNull GudClientCache cache, @NonNull String regionName) {

		return isLookupEnabled()
			? Optional.ofNullable(getParent())
				.<GudRegion<K, V>>map(GemfireFunctions.getSubregionFromRegion(regionName))
				.orElseGet(GemfireFunctions.getRegionFromCache(cache, regionName))
			: null;
	}

	/**
	 * Resolves the configured {@link String name} of the {@link GudRegion}.
	 *
	 * @return a {@link String} containing the {@literal name} of the {@link GudRegion}.
	 * @see GudRegion#getName()
	 */
	public String resolveRegionName() {

		String name = this.name;
		String regionName = this.regionName;

		return StringUtils.hasText(regionName) ? regionName
			: StringUtils.hasText(name) ? name
			: getBeanName();
	}

	/**
	 * Creates a new {@link GudRegion} with the given {@link String name}.
	 *
	 * This method gets called when a {@link GudRegion} with the specified {@link String name} does not already exist.
	 * By default, this method implementation throws a {@link BeanInitializationException} and it is expected
	 * that {@link Class subclasses} will override this method.
	 *
	 * @param cache reference to the {@link GudClientCache}.
	 * @param regionName {@link String name} of the new {@link GudRegion}.
	 * @return a new {@link GudRegion} with the given {@link String name}.
	 * @throws BeanInitializationException by default unless a {@link Class subclass} overrides this method.
	 * @see GudClientCache
	 * @see GudRegion
	 */
	protected GudRegion<K, V> createRegion(GudClientCache cache, String regionName) throws Exception {
		throw new BeanInitializationException(String.format(REGION_NOT_FOUND_ERROR_MESSAGE, regionName, cache));
	}

	/**
	 * Post-process the {@link GudRegion} created by this {@link ClientRegionFactoryBean}.
	 *
	 * @param region {@link GudRegion} to process.
	 * @see GudRegion
	 */
	protected GudRegion<K, V> postProcess(GudRegion<K, V> region) {
		return region;
	}

	/**
	 * Returns an object reference to the {@link GudRegion} created by this {@link ResolvableRegionFactoryBean}.
	 *
	 * @return an object reference to the {@link GudRegion} created by this {@link ResolvableRegionFactoryBean}.
	 * @see FactoryBean#getObject()
	 * @see GudRegion
	 * @see #getRegion()
	 */
	@Override
	public GudRegion<K, V> getObject() throws Exception {
		return getRegion();
	}

	/**
	 * Returns the {@link Class} type of the {@link GudRegion} produced by this {@link ResolvableRegionFactoryBean}.
	 *
	 * @return the {@link Class} type of the {@link GudRegion} produced by this {@link ResolvableRegionFactoryBean}.
	 * @see FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {

		GudRegion<?, ?> region = getRegion();

		return region != null ? region.getClass() : GudRegion.class;
	}

	/**
	 * Returns a reference to the {@link GudClientCache} used to create the {@link GudRegion}.
	 *
	 * @return a reference to the {@link GudClientCache} used to create the {@link GudRegion}.
	 * @see GudClientCache
	 */
	public GudClientCache getCache() {
		return this.cache;
	}

	/**
	 * Sets a reference to the {@link GudClientCache} used to create the {@link GudRegion}.
	 *
	 * @param cache reference to the {@link GudClientCache}.
	 * @see GudClientCache
	 */
	public void setCache(GudClientCache cache) {
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
	 * Sets the name of the cache {@link GudRegion} based on the bean 'name' attribute.  If no {@link GudRegion} is found
	 * with the given name, a new one will be created.  If no name is given, the value of the 'beanName' property
	 * will be used.
	 *
	 * @param name {@link GudRegion} name.
	 * @see #setBeanName(String)
	 * @see GudRegion#getFullPath()
	 */
	public void setName(@NonNull String name) {
		this.name = name;
	}

	/**
	 * Sets a reference to the parent {@link GudRegion} making this {@link FactoryBean}
	 * represent a cache {@link GudRegion Sub-Region}.
	 *
	 * @param parent reference to the parent {@link GudRegion}.
	 * @see GudRegion
	 */
	public void setParent(@Nullable GudRegion<?, ?> parent) {
		this.parent = parent;
	}

	/**
	 * Returns a reference to the parent {@link GudRegion} making this {@link FactoryBean}
	 * represent a cache {@link GudRegion Sub-Region}.
	 *
	 * @return a reference to the parent {@link GudRegion}, or {@literal null} if this {@link GudRegion}
	 * is not a {@link GudRegion Sub-Region}.
	 * @see GudRegion
	 */
	protected @Nullable GudRegion<?, ?> getParent() {
		return this.parent;
	}

	/**
	 * Sets a reference to the {@link GudRegion} to be resolved by this Spring {@link FactoryBean}.
	 *
	 * @param region reference to the resolvable {@link GudRegion}.
	 * @see GudRegion
	 */
	protected void setRegion(@Nullable GudRegion<K, V> region) {
		this.region = region;
	}

	/**
	 * Returns a reference to the {@link GudRegion} resolved by this Spring {@link FactoryBean}
	 * during the lookup operation; maybe a new {@link GudRegion}.
	 *
	 * @return a reference to the {@link GudRegion} resolved during lookup.
	 * @see GudRegion
	 */
	public @Nullable GudRegion<K, V> getRegion() {
		return this.region;
	}

	/**
	 * Sets the name of the cache {@link GudRegion}.  If no {@link GudRegion} is found with the given name,
	 * a new one will be created.  If no name is given, the value of the 'name' property will be used.
	 *
	 * @param regionName name of the {@link GudRegion}.
	 * @see #setName(String)
	 * @see GudRegion#getName()
	 */
	public void setRegionName(@Nullable String regionName) {
		this.regionName = regionName;
	}
}
