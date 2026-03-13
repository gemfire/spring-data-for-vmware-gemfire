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

package org.springframework.data.gemfire.config.annotation.support;

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudCompressor;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.gemfire.ResolvableRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.Interest;
import org.springframework.data.gemfire.config.annotation.RegionConfigurer;
import org.springframework.data.gemfire.eviction.EvictingRegionFactoryBean;
import org.springframework.data.gemfire.expiration.ExpiringRegionFactoryBean;
import org.springframework.util.StringUtils;

/**
 * The {@link CacheTypeAwareRegionFactoryBean} class is a smart Spring {@link FactoryBean} that knows how to
 * create a client or server {@link GudRegion} depending on whether the {@link GudClientCache} is a {@link GudClientCache}
 * or a peer cache.
 *
 * @author John Blum
 * @see GudCustomExpiry
 * @see GudDataPolicy
 * @see GudEvictionAttributes
 * @see GudExpirationAttributes
 * @see GudClientCache
 * @see GudRegion
 * @see GudRegionAttributes
 * @see GudRegionShortcut
 * @see GudScope
 * @see GudClientCache
 * @see GudClientRegionShortcut
 * @see GudCompressor
 * @see GenericRegionFactoryBean
 * @see ClientRegionFactoryBean
 * @see ResolvableRegionFactoryBean
 * @see ClientRegionFactoryBean
 * @see RegionConfigurer
 * @see EvictingRegionFactoryBean
 * @see ExpiringRegionFactoryBean
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class CacheTypeAwareRegionFactoryBean<K, V> extends ResolvableRegionFactoryBean<K, V>
		implements EvictingRegionFactoryBean, ExpiringRegionFactoryBean<K, V>, SmartLifecycle {

	private GudClientCache gemfireCache;

	private Boolean close = false;
	private Boolean statisticsEnabled = false;

	private Class<K> keyConstraint;
	private Class<V> valueConstraint;

	private GudClientRegionShortcut clientRegionShortcut = GudClientRegionShortcut.PROXY;

	private GudCompressor compressor;

	private GudCustomExpiry<K, V> customEntryIdleTimeout;
	private GudCustomExpiry<K, V> customEntryTimeToLive;

	private GudDataPolicy dataPolicy = GudDataPolicy.DEFAULT;

	private GudEvictionAttributes evictionAttributes;

	private GudExpirationAttributes entryIdleTimeout;
	private GudExpirationAttributes entryTimeToLive;
	private GudExpirationAttributes regionIdleTimeout;
	private GudExpirationAttributes regionTimeToLive;

	private Interest<K>[] interests;

	private List<RegionConfigurer> regionConfigurers = Collections.emptyList();

	private GudRegionAttributes<K, V> regionAttributes;

	private GudRegionShortcut serverRegionShortcut;

	private GudScope scope;

	private volatile SmartLifecycle smartLifecycleComponent;

	private String diskStoreName;
	private String poolName;
	private String regionName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GudRegion<K, V> createRegion(GudClientCache gemfireCache, String regionName) throws Exception {

		return newClientRegion(gemfireCache, regionName);
	}

	/**
	 * Constructs, configures and initialize\s a new client {@link GudRegion} using the {@link ClientRegionFactoryBean}.
	 *
	 * @param gemfireCache reference to the {@link GudClientCache} used to create/initialize the factory
	 * used to create the client {@link GudRegion}.
	 * @param regionName name given to the client {@link GudRegion}.
	 * @return a new instance of a client {@link GudRegion} with the given {@code regionName}.
	 * @throws Exception if the client {@link GudRegion} could not be created.
	 * @see ClientRegionFactoryBean
	 * @see GudClientCache
	 * @see GudRegion
	 * @see #newClientRegionFactoryBean()
	 */
	protected GudRegion<K, V> newClientRegion(GudClientCache gemfireCache, String regionName) throws Exception {

		ClientRegionFactoryBean<K, V> clientRegionFactory = newClientRegionFactoryBean();

		clientRegionFactory.setAttributes(getAttributes());
		clientRegionFactory.setBeanFactory(getBeanFactory());
		clientRegionFactory.setCache(gemfireCache);
		clientRegionFactory.setClose(isClose());
		clientRegionFactory.setCompressor(getCompressor());
		clientRegionFactory.setDiskStoreName(getDiskStoreName());
		clientRegionFactory.setInterests(getInterests());
		clientRegionFactory.setKeyConstraint(getKeyConstraint());
		clientRegionFactory.setLookupEnabled(getLookupEnabled());
		clientRegionFactory.setRegionConfigurers(this.regionConfigurers);
		clientRegionFactory.setRegionName(regionName);
		clientRegionFactory.setShortcut(getClientRegionShortcut());
		clientRegionFactory.setStatisticsEnabled(getStatisticsEnabled());
		clientRegionFactory.setValueConstraint(getValueConstraint());

		getPoolName().ifPresent(clientRegionFactory::setPoolName);

		configureEviction(clientRegionFactory);
		configureExpiration(clientRegionFactory);

		clientRegionFactory.afterPropertiesSet();

		this.smartLifecycleComponent = clientRegionFactory;

		return clientRegionFactory.getObject();
	}

	/**
	 * Constructs a new instance of the {@link ClientRegionFactoryBean}.
	 *
	 * @param <K> {@link Class type} of the created {@link GudRegion Region's} key.
	 * @param <V> {@link Class type} of the created {@link GudRegion Region's} value.
	 * @return a new instance of the {@link ClientRegionFactoryBean}.
	 * @see ClientRegionFactoryBean
	 */
	protected <K, V> ClientRegionFactoryBean<K, V> newClientRegionFactoryBean() {
		return new ClientRegionFactoryBean<>();
	}

	protected void configureEviction(EvictingRegionFactoryBean regionFactoryBean) {
		regionFactoryBean.setEvictionAttributes(getEvictionAttributes());
	}

	protected void configureExpiration(ExpiringRegionFactoryBean<K, V> regionFactoryBean) {

		regionFactoryBean.setCustomEntryIdleTimeout(getCustomEntryIdleTimeout());
		regionFactoryBean.setCustomEntryTimeToLive(getCustomEntryTimeToLive());
		regionFactoryBean.setEntryIdleTimeout(getEntryIdleTimeout());
		regionFactoryBean.setEntryTimeToLive(getEntryTimeToLive());
		regionFactoryBean.setRegionIdleTimeout(getRegionIdleTimeout());
		regionFactoryBean.setRegionTimeToLive(getRegionTimeToLive());
	}

	public void setAttributes(GudRegionAttributes<K, V> regionAttributes) {
		this.regionAttributes = regionAttributes;
	}

	protected GudRegionAttributes<K, V> getAttributes() {
		return this.regionAttributes;
	}

	public void setClientRegionShortcut(GudClientRegionShortcut clientRegionShortcut) {
		this.clientRegionShortcut = clientRegionShortcut;
	}

	protected GudClientRegionShortcut getClientRegionShortcut() {
		return Optional.ofNullable(this.clientRegionShortcut).orElse(GudClientRegionShortcut.PROXY);
	}

	public void setClose(Boolean close) {
		this.close = close;
	}

	protected Boolean getClose() {
		return this.close;
	}

	protected boolean isClose() {
		return Boolean.TRUE.equals(getClose());
	}

	/**
	 * Configures the {@link GudCompressor} used to compress the this {@link GudRegion Region's} data.
	 *
	 * @param compressor {@link GudCompressor} used to compress the this {@link GudRegion Region's} data.
	 * @see GudCompressor
	 */
	public void setCompressor(GudCompressor compressor) {
		this.compressor = compressor;
	}

	/**
	 * Returns the configured {@link GudCompressor} used to compress the this {@link GudRegion Region's} data.
	 *
	 * @return the configured {@link GudCompressor} used to compress the this {@link GudRegion Region's} data.
	 * @see GudCompressor
	 */
	protected GudCompressor getCompressor() {
		return this.compressor;
	}

	public void setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customEntryIdleTimeout) {
		this.customEntryIdleTimeout = customEntryIdleTimeout;
	}

	protected GudCustomExpiry<K, V> getCustomEntryIdleTimeout() {
		return this.customEntryIdleTimeout;
	}

	public void setCustomEntryTimeToLive(GudCustomExpiry<K, V> customEntryTimeToLive) {
		this.customEntryTimeToLive = customEntryTimeToLive;
	}

	protected GudCustomExpiry<K, V> getCustomEntryTimeToLive() {
		return this.customEntryTimeToLive;
	}

	public void setDataPolicy(GudDataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
	}

	protected GudDataPolicy getDataPolicy() {
		return Optional.ofNullable(this.dataPolicy).orElse(GudDataPolicy.DEFAULT);
	}

	public void setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
	}

	protected String getDiskStoreName() {
		return this.diskStoreName;
	}

	public void setEvictionAttributes(GudEvictionAttributes evictionAttributes) {
		this.evictionAttributes = evictionAttributes;
	}

	protected GudEvictionAttributes getEvictionAttributes() {
		return this.evictionAttributes;
	}

	public void setEntryIdleTimeout(GudExpirationAttributes entryIdleTimeout) {
		this.entryIdleTimeout = entryIdleTimeout;
	}

	protected GudExpirationAttributes getEntryIdleTimeout() {
		return this.entryIdleTimeout;
	}

	public void setEntryTimeToLive(GudExpirationAttributes entryTimeToLive) {
		this.entryTimeToLive = entryTimeToLive;
	}

	protected GudExpirationAttributes getEntryTimeToLive() {
		return this.entryTimeToLive;
	}

	public void setInterests(Interest<K>[] interests) {
		this.interests = interests;
	}

	protected Interest<K>[] getInterests() {
		return this.interests;
	}

	public void setKeyConstraint(Class<K> keyConstraint) {
		this.keyConstraint = keyConstraint;
	}

	protected Class<K> getKeyConstraint() {
		return this.keyConstraint;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	protected Optional<String> getPoolName() {
		return Optional.ofNullable(this.poolName).filter(StringUtils::hasText);
	}

	protected String resolvePoolName() {
		return getPoolName().orElse(null);
	}

	/**
	 * Null-safe operation used to set an array of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ResolvableRegionFactoryBean} when using Annotation-based configuration.
	 *
	 * @param regionConfigurers array of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ResolvableRegionFactoryBean}.
	 * @see RegionConfigurer
	 * @see #setRegionConfigurers(List)
	 */
	public void setRegionConfigurers(RegionConfigurer... regionConfigurers) {
		setRegionConfigurers(Arrays.asList(nullSafeArray(regionConfigurers, RegionConfigurer.class)));
	}

	/**
	 * Null-safe operation used to set an {@link Iterable} of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ResolvableRegionFactoryBean} when using Annotation-based configuration.
	 *
	 * @param regionConfigurers {@link Iterable} of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ResolvableRegionFactoryBean}.
	 * @see RegionConfigurer
	 */
	public void setRegionConfigurers(List<RegionConfigurer> regionConfigurers) {
		this.regionConfigurers = Optional.ofNullable(regionConfigurers).orElseGet(Collections::emptyList);
	}

	public void setRegionIdleTimeout(GudExpirationAttributes regionIdleTimeout) {
		this.regionIdleTimeout = regionIdleTimeout;
	}

	protected GudExpirationAttributes getRegionIdleTimeout() {
		return this.regionIdleTimeout;
	}

	public void setRegionTimeToLive(GudExpirationAttributes regionTimeToLive) {
		this.regionTimeToLive = regionTimeToLive;
	}

	protected GudExpirationAttributes getRegionTimeToLive() {
		return this.regionTimeToLive;
	}

	public void setScope(GudScope scope) {
		this.scope = scope;
	}

	protected GudScope getScope() {
		return this.scope;
	}

	public void setServerRegionShortcut(GudRegionShortcut shortcut) {
		this.serverRegionShortcut = shortcut;
	}

	protected GudRegionShortcut getServerRegionShortcut() {
		return this.serverRegionShortcut;
	}

	protected Optional<SmartLifecycle> getSmartLifecycleComponent() {
		return Optional.ofNullable(this.smartLifecycleComponent);
	}

	public void setStatisticsEnabled(Boolean statisticsEnabled) {
		this.statisticsEnabled = statisticsEnabled;
	}

	public Boolean getStatisticsEnabled() {
		return statisticsEnabled;
	}

	public void setValueConstraint(Class<V> valueConstraint) {
		this.valueConstraint = valueConstraint;
	}

	protected Class<V> getValueConstraint() {
		return this.valueConstraint;
	}

	@Override
	public boolean isAutoStartup() {

		return getSmartLifecycleComponent()
			.map(SmartLifecycle::isAutoStartup)
			.orElse(false);
	}

	@Override
	public boolean isRunning() {

		return getSmartLifecycleComponent()
			.map(SmartLifecycle::isRunning)
			.orElse(false);
	}

	@Override
	public int getPhase() {

		return getSmartLifecycleComponent()
			.map(SmartLifecycle::getPhase)
			.orElse(0);
	}

	@Override
	public void start() {
		getSmartLifecycleComponent().ifPresent(SmartLifecycle::start);
	}

	@Override
	public void stop() {
		getSmartLifecycleComponent().ifPresent(SmartLifecycle::stop);
	}

	@Override
	public void stop(Runnable callback) {
		getSmartLifecycleComponent().ifPresent(it -> it.stop(callback));
	}
}
