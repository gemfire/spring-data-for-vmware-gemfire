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

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The LookupRegionFactoryBean class is a concrete implementation of ResolvableRegionFactoryBean for handling
 * &gt;gfe:lookup-region/&lt; SDG XML namespace (XSD) elements.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.gud.api.GudAttributesMutator
 * @see ResolvableRegionFactoryBean
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class LookupRegionFactoryBean<K, V> extends ResolvableRegionFactoryBean<K, V> {

	private Boolean cloningEnabled;
	private Boolean enableStatistics;

	private GudCacheListener<K, V>[] cacheListeners;

	private GudCacheLoader<K, V> cacheLoader;

	private GudCacheWriter<K, V> cacheWriter;

	private GudCustomExpiry<K, V> customEntryIdleTimeout;
	private GudCustomExpiry<K, V> customEntryTimeToLive;

	private GudExpirationAttributes entryIdleTimeout;
	private GudExpirationAttributes entryTimeToLive;
	private GudExpirationAttributes regionIdleTimeout;
	private GudExpirationAttributes regionTimeToLive;

	private Integer evictionMaximum;

	@Override
	public void afterPropertiesSet() throws Exception {

		super.afterPropertiesSet();

		Optional.ofNullable(getRegion().getAttributesMutator()).ifPresent(attributesMutator -> {

			// CacheListeners
			Arrays.stream(nullSafeArray(this.cacheListeners, GudCacheListener.class))
				.forEach(attributesMutator::addCacheListener);

			Optional.ofNullable(this.cacheLoader).ifPresent(attributesMutator::setCacheLoader);
			Optional.ofNullable(this.cacheWriter).ifPresent(attributesMutator::setCacheWriter);
			Optional.ofNullable(this.cloningEnabled).ifPresent(attributesMutator::setCloningEnabled);

			// Eviction
			Optional.ofNullable(attributesMutator.getEvictionAttributesMutator())
				.ifPresent(evictionAttributesMutator -> Optional.ofNullable(this.evictionMaximum)
					.ifPresent(evictionAttributesMutator::setMaximum));

			// Expiration
			if (isStatisticsEnabled()) {

				assertStatisticsEnabled();

				Optional.ofNullable(this.customEntryIdleTimeout).ifPresent(attributesMutator::setCustomEntryIdleTimeout);
				Optional.ofNullable(this.customEntryTimeToLive).ifPresent(attributesMutator::setCustomEntryTimeToLive);
				Optional.ofNullable(this.entryIdleTimeout).ifPresent(attributesMutator::setEntryIdleTimeout);
				Optional.ofNullable(this.entryTimeToLive).ifPresent(attributesMutator::setEntryTimeToLive);
				Optional.ofNullable(this.regionIdleTimeout).ifPresent(attributesMutator::setRegionIdleTimeout);
				Optional.ofNullable(this.regionTimeToLive).ifPresent(attributesMutator::setRegionTimeToLive);
			}
		});
	}

	@Override
	public final boolean isLookupEnabled() {
		return true;
	}

	public void setCacheListeners(GudCacheListener<K, V>[] cacheListeners) {
		this.cacheListeners = cacheListeners;
	}

	public void setCacheLoader(GudCacheLoader<K, V> cacheLoader) {
		this.cacheLoader = cacheLoader;
	}

	public void setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
		this.cacheWriter = cacheWriter;
	}

	public void setCloningEnabled(Boolean cloningEnabled) {
		this.cloningEnabled = cloningEnabled;
	}

	public void setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customEntryIdleTimeout) {
		setStatisticsEnabled(customEntryIdleTimeout != null);
		this.customEntryIdleTimeout = customEntryIdleTimeout;
	}

	public void setCustomEntryTimeToLive(GudCustomExpiry<K, V> customEntryTimeToLive) {
		setStatisticsEnabled(customEntryTimeToLive != null);
		this.customEntryTimeToLive = customEntryTimeToLive;
	}

	public void setEntryIdleTimeout(GudExpirationAttributes entryIdleTimeout) {
		setStatisticsEnabled(entryIdleTimeout != null);
		this.entryIdleTimeout = entryIdleTimeout;
	}

	public void setEntryTimeToLive(GudExpirationAttributes entryTimeToLive) {
		setStatisticsEnabled(entryTimeToLive != null);
		this.entryTimeToLive = entryTimeToLive;
	}

	public void setEvictionMaximum(final Integer evictionMaximum) {
		this.evictionMaximum = evictionMaximum;
	}

	public void setRegionIdleTimeout(GudExpirationAttributes regionIdleTimeout) {
		setStatisticsEnabled(regionIdleTimeout != null);
		this.regionIdleTimeout = regionIdleTimeout;
	}

	public void setRegionTimeToLive(GudExpirationAttributes regionTimeToLive) {
		setStatisticsEnabled(regionTimeToLive != null);
		this.regionTimeToLive = regionTimeToLive;
	}

	public void setStatisticsEnabled(Boolean enableStatistics) {
		this.enableStatistics = enableStatistics;
	}

	protected boolean isStatisticsEnabled() {
		return Boolean.TRUE.equals(this.enableStatistics);
	}

	private void assertStatisticsEnabled() {

		GudRegion<K, V> localRegion = getRegion();

		Assert.state(localRegion.getAttributes().getStatisticsEnabled(),
			String.format("Statistics for Region [%s] must be enabled to change Entry & Region TTL/TTI Expiration settings",
				localRegion.getFullPath()));
	}
}
