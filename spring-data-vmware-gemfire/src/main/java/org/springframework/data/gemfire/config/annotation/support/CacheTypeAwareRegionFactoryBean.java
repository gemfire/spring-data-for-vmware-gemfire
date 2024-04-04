/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.support;

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.compression.Compressor;
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
 * create a client or server {@link Region} depending on whether the {@link ClientCache} is a {@link ClientCache}
 * or a peer {@link Cache}.
 *
 * @author John Blum
 * @see CustomExpiry
 * @see DataPolicy
 * @see EvictionAttributes
 * @see ExpirationAttributes
 * @see ClientCache
 * @see Region
 * @see RegionAttributes
 * @see RegionShortcut
 * @see Scope
 * @see ClientCache
 * @see ClientRegionShortcut
 * @see Compressor
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

	private ClientCache gemfireCache;

	private Boolean close = false;
	private Boolean statisticsEnabled = false;

	private Class<K> keyConstraint;
	private Class<V> valueConstraint;

	private ClientRegionShortcut clientRegionShortcut = ClientRegionShortcut.PROXY;

	private Compressor compressor;

	private CustomExpiry<K, V> customEntryIdleTimeout;
	private CustomExpiry<K, V> customEntryTimeToLive;

	private DataPolicy dataPolicy = DataPolicy.DEFAULT;

	private EvictionAttributes evictionAttributes;

	private ExpirationAttributes entryIdleTimeout;
	private ExpirationAttributes entryTimeToLive;
	private ExpirationAttributes regionIdleTimeout;
	private ExpirationAttributes regionTimeToLive;

	private Interest<K>[] interests;

	private List<RegionConfigurer> regionConfigurers = Collections.emptyList();

	private RegionAttributes<K, V> regionAttributes;

	private RegionShortcut serverRegionShortcut;

	private Scope scope;

	private volatile SmartLifecycle smartLifecycleComponent;

	private String diskStoreName;
	private String poolName;
	private String regionName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Region<K, V> createRegion(ClientCache gemfireCache, String regionName) throws Exception {

		return newClientRegion(gemfireCache, regionName);
	}

	/**
	 * Constructs, configures and initialize\s a new client {@link Region} using the {@link ClientRegionFactoryBean}.
	 *
	 * @param gemfireCache reference to the {@link ClientCache} used to create/initialize the factory
	 * used to create the client {@link Region}.
	 * @param regionName name given to the client {@link Region}.
	 * @return a new instance of a client {@link Region} with the given {@code regionName}.
	 * @throws Exception if the client {@link Region} could not be created.
	 * @see ClientRegionFactoryBean
	 * @see ClientCache
	 * @see Region
	 * @see #newClientRegionFactoryBean()
	 */
	protected Region<K, V> newClientRegion(ClientCache gemfireCache, String regionName) throws Exception {

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
	 * @param <K> {@link Class type} of the created {@link Region Region's} key.
	 * @param <V> {@link Class type} of the created {@link Region Region's} value.
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

	public void setAttributes(RegionAttributes<K, V> regionAttributes) {
		this.regionAttributes = regionAttributes;
	}

	protected RegionAttributes<K, V> getAttributes() {
		return this.regionAttributes;
	}

	public void setClientRegionShortcut(ClientRegionShortcut clientRegionShortcut) {
		this.clientRegionShortcut = clientRegionShortcut;
	}

	protected ClientRegionShortcut getClientRegionShortcut() {
		return Optional.ofNullable(this.clientRegionShortcut).orElse(ClientRegionShortcut.PROXY);
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
	 * Configures the {@link Compressor} used to compress the this {@link Region Region's} data.
	 *
	 * @param compressor {@link Compressor} used to compress the this {@link Region Region's} data.
	 * @see Compressor
	 */
	public void setCompressor(Compressor compressor) {
		this.compressor = compressor;
	}

	/**
	 * Returns the configured {@link Compressor} used to compress the this {@link Region Region's} data.
	 *
	 * @return the configured {@link Compressor} used to compress the this {@link Region Region's} data.
	 * @see Compressor
	 */
	protected Compressor getCompressor() {
		return this.compressor;
	}

	public void setCustomEntryIdleTimeout(CustomExpiry<K, V> customEntryIdleTimeout) {
		this.customEntryIdleTimeout = customEntryIdleTimeout;
	}

	protected CustomExpiry<K, V> getCustomEntryIdleTimeout() {
		return this.customEntryIdleTimeout;
	}

	public void setCustomEntryTimeToLive(CustomExpiry<K, V> customEntryTimeToLive) {
		this.customEntryTimeToLive = customEntryTimeToLive;
	}

	protected CustomExpiry<K, V> getCustomEntryTimeToLive() {
		return this.customEntryTimeToLive;
	}

	public void setDataPolicy(DataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
	}

	protected DataPolicy getDataPolicy() {
		return Optional.ofNullable(this.dataPolicy).orElse(DataPolicy.DEFAULT);
	}

	public void setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
	}

	protected String getDiskStoreName() {
		return this.diskStoreName;
	}

	public void setEvictionAttributes(EvictionAttributes evictionAttributes) {
		this.evictionAttributes = evictionAttributes;
	}

	protected EvictionAttributes getEvictionAttributes() {
		return this.evictionAttributes;
	}

	public void setEntryIdleTimeout(ExpirationAttributes entryIdleTimeout) {
		this.entryIdleTimeout = entryIdleTimeout;
	}

	protected ExpirationAttributes getEntryIdleTimeout() {
		return this.entryIdleTimeout;
	}

	public void setEntryTimeToLive(ExpirationAttributes entryTimeToLive) {
		this.entryTimeToLive = entryTimeToLive;
	}

	protected ExpirationAttributes getEntryTimeToLive() {
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

	public void setRegionIdleTimeout(ExpirationAttributes regionIdleTimeout) {
		this.regionIdleTimeout = regionIdleTimeout;
	}

	protected ExpirationAttributes getRegionIdleTimeout() {
		return this.regionIdleTimeout;
	}

	public void setRegionTimeToLive(ExpirationAttributes regionTimeToLive) {
		this.regionTimeToLive = regionTimeToLive;
	}

	protected ExpirationAttributes getRegionTimeToLive() {
		return this.regionTimeToLive;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	protected Scope getScope() {
		return this.scope;
	}

	public void setServerRegionShortcut(RegionShortcut shortcut) {
		this.serverRegionShortcut = shortcut;
	}

	protected RegionShortcut getServerRegionShortcut() {
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
