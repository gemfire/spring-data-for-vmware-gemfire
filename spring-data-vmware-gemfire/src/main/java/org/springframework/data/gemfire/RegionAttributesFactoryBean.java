/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
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
 * 2026-04-02: Removed broken GudAttributesFactoryImpl.create() delegate; class now holds its
 *             own state directly and builds a SimpleGudRegionAttributes value object in create()
 */

package org.springframework.data.gemfire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudAttributesFactory;
import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudCompressor;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudMembershipAttributes;
import org.springframework.data.gemfire.gud.api.GudPartitionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.gud.api.GudSubscriptionAttributes;

/**
 * Spring {@link FactoryBean} used to create {@link GudRegionAttributes}.
 *
 * Eliminates the need of using a XML bean 'factory-method' tag.
 *
 * @author Costin Leau
 * @author John Blum
 * @see GudAttributesFactory
 * @see GudRegionAttributes
 * @see FactoryBean
 * @see InitializingBean
 */
@SuppressWarnings({ "unused" })
public class RegionAttributesFactoryBean<K, V>
		implements GudAttributesFactory<K, V>, FactoryBean<GudRegionAttributes<K, V>>, InitializingBean {

	// --- State fields set via GudAttributesFactory interface ---
	private GudScope scope;
	private GudDataPolicy dataPolicy;
	private GudExpirationAttributes entryIdleTimeout;
	private GudExpirationAttributes entryTimeToLive;
	private GudExpirationAttributes regionIdleTimeout;
	private GudExpirationAttributes regionTimeToLive;
	private GudEvictionAttributes evictionAttributes;
	private GudCacheLoader<K, V> cacheLoader;
	private GudCacheWriter<K, V> cacheWriter;
	private final List<GudCacheListener<K, V>> cacheListeners = new ArrayList<>();
	private boolean statisticsEnabled;
	private String poolName;
	private String diskStoreName;
	private Class<K> keyConstraint;
	private Class<V> valueConstraint;

	// --- Additional state fields injected by Spring XML parsers ---
	private GudCustomExpiry<K, V> customEntryTimeToLive;
	private GudCustomExpiry<K, V> customEntryIdleTimeout;
	private GudCompressor compressor;
	private GudMembershipAttributes membershipAttributes;
	private GudSubscriptionAttributes subscriptionAttributes;
	private boolean cloningEnabled;
	private int concurrencyLevel = 16;
	private boolean diskSynchronous = true;
	private boolean enableSubscriptionConflation;
	private int initialCapacity = 16;
	private float loadFactor = 0.75f;
	private boolean lockGrantor;
	private boolean concurrencyChecksEnabled = true;
	private boolean multicastEnabled;
	private boolean offHeap;
	private Set<String> gatewaySenderIds = Collections.emptySet();
	private Set<String> asyncEventQueueIds = Collections.emptySet();

	private GudRegionAttributes<K, V> regionAttributes;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.regionAttributes = create();
	}

	@Override
	public GudRegionAttributes<K, V> getObject() throws Exception {
		return this.regionAttributes;
	}

	@Override
	public Class<?> getObjectType() {

		return this.regionAttributes != null
			? this.regionAttributes.getClass()
			: GudRegionAttributes.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	// --- GudAttributesFactory setters ---

	@Override
	public GudAttributesFactory<K, V> setScope(GudScope scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setDataPolicy(GudDataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout) {
		this.entryIdleTimeout = idleTimeout;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive) {
		this.entryTimeToLive = timeToLive;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout) {
		this.regionIdleTimeout = idleTimeout;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive) {
		this.regionTimeToLive = timeToLive;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes) {
		this.evictionAttributes = evictionAttributes;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader) {
		this.cacheLoader = cacheLoader;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
		this.cacheWriter = cacheWriter;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> addCacheListener(GudCacheListener<K, V> listener) {
		this.cacheListeners.add(listener);
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled) {
		this.statisticsEnabled = statisticsEnabled;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setPoolName(String poolName) {
		this.poolName = poolName;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setKeyConstraint(Class<K> keyConstraint) {
		this.keyConstraint = keyConstraint;
		return this;
	}

	@Override
	public GudAttributesFactory<K, V> setValueConstraint(Class<V> valueConstraint) {
		this.valueConstraint = valueConstraint;
		return this;
	}

	// --- Additional setters for Spring XML property injection ---

	public void setCustomEntryTimeToLive(GudCustomExpiry<K, V> customEntryTimeToLive) {
		this.customEntryTimeToLive = customEntryTimeToLive;
	}

	public void setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customEntryIdleTimeout) {
		this.customEntryIdleTimeout = customEntryIdleTimeout;
	}

	public void setCompressor(GudCompressor compressor) {
		this.compressor = compressor;
	}

	public void setMembershipAttributes(GudMembershipAttributes membershipAttributes) {
		this.membershipAttributes = membershipAttributes;
	}

	public void setSubscriptionAttributes(GudSubscriptionAttributes subscriptionAttributes) {
		this.subscriptionAttributes = subscriptionAttributes;
	}

	public void setCloningEnabled(boolean cloningEnabled) {
		this.cloningEnabled = cloningEnabled;
	}

	public void setConcurrencyLevel(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

	public void setDiskSynchronous(boolean diskSynchronous) {
		this.diskSynchronous = diskSynchronous;
	}

	public void setEnableSubscriptionConflation(boolean enableSubscriptionConflation) {
		this.enableSubscriptionConflation = enableSubscriptionConflation;
	}

	public void setInitialCapacity(int initialCapacity) {
		this.initialCapacity = initialCapacity;
	}

	public void setLoadFactor(float loadFactor) {
		this.loadFactor = loadFactor;
	}

	public void setLockGrantor(boolean lockGrantor) {
		this.lockGrantor = lockGrantor;
	}

	public void setConcurrencyChecksEnabled(boolean concurrencyChecksEnabled) {
		this.concurrencyChecksEnabled = concurrencyChecksEnabled;
	}

	public void setGatewaySenderIds(Set<String> gatewaySenderIds) {
		this.gatewaySenderIds = gatewaySenderIds != null ? gatewaySenderIds : Collections.emptySet();
	}

	public void setAsyncEventQueueIds(Set<String> asyncEventQueueIds) {
		this.asyncEventQueueIds = asyncEventQueueIds != null ? asyncEventQueueIds : Collections.emptySet();
	}

	/** Legacy XML attribute — accepted for backwards compatibility but has no effect. */
	public void setPublisher(boolean publisher) {
	}

	@Override
	public GudRegionAttributes<K, V> create() {
		return new SimpleGudRegionAttributes<>(this);
	}

	/**
	 * Immutable value object capturing all region attribute state set on this factory bean.
	 * Lives here rather than in {@code gud-api} to avoid placing an implementation class
	 * in the API module (architecture invariant #7).
	 */
	private static final class SimpleGudRegionAttributes<K, V> implements GudRegionAttributes<K, V> {

		private final GudDataPolicy dataPolicy;
		private final GudScope scope;
		private final boolean statisticsEnabled;
		private final boolean cloningEnabled;
		private final boolean concurrencyChecksEnabled;
		private final int concurrencyLevel;
		private final boolean lockGrantor;
		private final boolean diskSynchronous;
		private final String diskStoreName;
		private final String poolName;
		private final GudCacheLoader<K, V> cacheLoader;
		private final GudCacheWriter<K, V> cacheWriter;
		private final GudCacheListener<K, V>[] cacheListeners;
		private final GudExpirationAttributes regionTimeToLive;
		private final GudExpirationAttributes regionIdleTimeout;
		private final GudExpirationAttributes entryTimeToLive;
		private final GudExpirationAttributes entryIdleTimeout;
		private final GudCustomExpiry<K, V> customEntryTimeToLive;
		private final GudCustomExpiry<K, V> customEntryIdleTimeout;
		private final GudEvictionAttributes evictionAttributes;
		private final GudMembershipAttributes membershipAttributes;
		private final GudSubscriptionAttributes subscriptionAttributes;
		private final boolean enableSubscriptionConflation;
		private final GudCompressor compressor;
		private final Class<K> keyConstraint;
		private final Class<V> valueConstraint;
		private final int initialCapacity;
		private final float loadFactor;
		private final boolean multicastEnabled;
		private final boolean offHeap;
		private final Set<String> gatewaySenderIds;
		private final Set<String> asyncEventQueueIds;

		@SuppressWarnings("unchecked")
		SimpleGudRegionAttributes(RegionAttributesFactoryBean<K, V> factory) {
			this.dataPolicy = factory.dataPolicy;
			this.scope = factory.scope;
			this.statisticsEnabled = factory.statisticsEnabled;
			this.cloningEnabled = factory.cloningEnabled;
			this.concurrencyChecksEnabled = factory.concurrencyChecksEnabled;
			this.concurrencyLevel = factory.concurrencyLevel;
			this.lockGrantor = factory.lockGrantor;
			this.diskSynchronous = factory.diskSynchronous;
			this.diskStoreName = factory.diskStoreName;
			this.poolName = factory.poolName;
			this.cacheLoader = factory.cacheLoader;
			this.cacheWriter = factory.cacheWriter;
			this.cacheListeners = factory.cacheListeners.toArray(new GudCacheListener[0]);
			this.regionTimeToLive = factory.regionTimeToLive;
			this.regionIdleTimeout = factory.regionIdleTimeout;
			this.entryTimeToLive = factory.entryTimeToLive;
			this.entryIdleTimeout = factory.entryIdleTimeout;
			this.customEntryTimeToLive = factory.customEntryTimeToLive;
			this.customEntryIdleTimeout = factory.customEntryIdleTimeout;
			this.evictionAttributes = factory.evictionAttributes;
			this.membershipAttributes = factory.membershipAttributes;
			this.subscriptionAttributes = factory.subscriptionAttributes;
			this.enableSubscriptionConflation = factory.enableSubscriptionConflation;
			this.compressor = factory.compressor;
			this.keyConstraint = factory.keyConstraint;
			this.valueConstraint = factory.valueConstraint;
			this.initialCapacity = factory.initialCapacity;
			this.loadFactor = factory.loadFactor;
			this.multicastEnabled = factory.multicastEnabled;
			this.offHeap = factory.offHeap;
			this.gatewaySenderIds = factory.gatewaySenderIds;
			this.asyncEventQueueIds = factory.asyncEventQueueIds;
		}

		@Override public GudDataPolicy getDataPolicy() { return dataPolicy; }
		@Override public GudScope getScope() { return scope; }
		@Override public boolean getStatisticsEnabled() { return statisticsEnabled; }
		@Override public boolean getCloningEnabled() { return cloningEnabled; }
		@Override public boolean getConcurrencyChecksEnabled() { return concurrencyChecksEnabled; }
		@Override public int getConcurrencyLevel() { return concurrencyLevel; }
		@Override public boolean isLockGrantor() { return lockGrantor; }
		@Override public boolean isDiskSynchronous() { return diskSynchronous; }
		@Override public String getDiskStoreName() { return diskStoreName; }
		@Override public String getPoolName() { return poolName; }
		@Override public GudCacheLoader<K, V> getCacheLoader() { return cacheLoader; }
		@Override public GudCacheWriter<K, V> getCacheWriter() { return cacheWriter; }
		@Override public GudCacheListener<K, V>[] getCacheListeners() { return cacheListeners; }
		@Override public GudExpirationAttributes getRegionTimeToLive() { return regionTimeToLive; }
		@Override public GudExpirationAttributes getRegionIdleTimeout() { return regionIdleTimeout; }
		@Override public GudExpirationAttributes getEntryTimeToLive() { return entryTimeToLive; }
		@Override public GudExpirationAttributes getEntryIdleTimeout() { return entryIdleTimeout; }
		@Override public GudCustomExpiry<K, V> getCustomEntryTimeToLive() { return customEntryTimeToLive; }
		@Override public GudCustomExpiry<K, V> getCustomEntryIdleTimeout() { return customEntryIdleTimeout; }
		@Override public GudEvictionAttributes getEvictionAttributes() { return evictionAttributes; }
		@Override public GudMembershipAttributes getMembershipAttributes() { return membershipAttributes; }
		@Override public GudSubscriptionAttributes getSubscriptionAttributes() { return subscriptionAttributes; }
		@Override public boolean getEnableSubscriptionConflation() { return enableSubscriptionConflation; }
		@Override public boolean getIgnoreJTA() { return false; }
		@Override public GudPartitionAttributes<K, V> getPartitionAttributes() { return null; }
		@Override public GudCompressor getCompressor() { return compressor; }
		@Override public Class<K> getKeyConstraint() { return keyConstraint; }
		@Override public Class<V> getValueConstraint() { return valueConstraint; }
		@Override public int getInitialCapacity() { return initialCapacity; }
		@Override public float getLoadFactor() { return loadFactor; }
		@Override public boolean getMulticastEnabled() { return multicastEnabled; }
		@Override public boolean getOffHeap() { return offHeap; }
		@Override public Set<String> getGatewaySenderIds() { return gatewaySenderIds; }
		@Override public Set<String> getAsyncEventQueueIds() { return asyncEventQueueIds; }
	}
}
