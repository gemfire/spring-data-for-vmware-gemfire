/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.gemfire.config.annotation.RegionConfigurer;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;

/**
 * {@link ConfigurableRegionFactoryBean} is an abstract base class encapsulating functionality common
 * to all configurable {@link Region} {@link FactoryBean FactoryBeans}.
 *
 * A {@literal configurable} {@link Region} {@link FactoryBean} includes all {@link FactoryBean FactoryBeans}
 * that create a {@link Region} and allow additional configuration to be applied via a {@link RegionConfigurer}.
 *
 * @author John Blum
 * @see Region
 * @see FactoryBean
 * @see ResolvableRegionFactoryBean
 * @see RegionConfigurer
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public abstract class ConfigurableRegionFactoryBean<K, V> extends ResolvableRegionFactoryBean<K, V> {

	private List<RegionConfigurer> regionConfigurers = Collections.emptyList();

	private final RegionConfigurer compositeRegionConfigurer = new RegionConfigurer() {

		@Override
		public void configure(String beanName, PeerRegionFactoryBean<?, ?> bean) {
			CollectionUtils.nullSafeCollection(regionConfigurers)
				.forEach(regionConfigurer -> regionConfigurer.configure(beanName, bean));
		}
	};

	/**
	 * Applies all {@link RegionConfigurer RegionConfigurers}.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		applyRegionConfigurers(requireRegionName());

		super.afterPropertiesSet();
	}

	/**
	 * Returns a reference to the Composite {@link RegionConfigurer} used to apply additional configuration
	 * to this {@link RegionFacB} on Spring container initialization.
	 *
	 * @return the Composite {@link RegionConfigurer}.
	 * @see RegionConfigurer
	 */
	protected RegionConfigurer getCompositeRegionConfigurer() {
		return this.compositeRegionConfigurer;
	}

	/**
	 * Null-safe operation to set an array of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ClientRegionFactoryBean} when using Annotation-based configuration.
	 *
	 * @param regionConfigurers array of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ClientRegionFactoryBean}.
	 * @see RegionConfigurer
	 * @see #setRegionConfigurers(List)
	 */
	public void setRegionConfigurers(RegionConfigurer... regionConfigurers) {
		setRegionConfigurers(Arrays.asList(ArrayUtils.nullSafeArray(regionConfigurers, RegionConfigurer.class)));
	}

	/**
	 * Null-safe operation to set an {@link Iterable} of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ClientRegionFactoryBean} when using Annotation-based configuration.
	 *
	 * @param regionConfigurers {@link Iterable} of {@link RegionConfigurer RegionConfigurers} used to apply
	 * additional configuration to this {@link ClientRegionFactoryBean}.
	 * @see RegionConfigurer
	 */
	public void setRegionConfigurers(List<RegionConfigurer> regionConfigurers) {

		this.regionConfigurers = regionConfigurers != null
			? regionConfigurers
			: Collections.emptyList();
	}

	/**
	 * Null-safe operation to apply the composite {@link RegionConfigurer RegionConfigurers}
	 * to this {@link ConfigurableRegionFactoryBean}.
	 *
	 * @param regionName {@link String} containing the name of the {@link Region}.
	 * to this {@link ConfigurableRegionFactoryBean}.
	 * @see RegionConfigurer
	 * @see #applyRegionConfigurers(String, Iterable)
	 * @see #getCompositeRegionConfigurer()
	 */
	protected void applyRegionConfigurers(String regionName) {
		applyRegionConfigurers(regionName, getCompositeRegionConfigurer());
	}

	/**
	 * Null-safe operation to apply the given array of {@link RegionConfigurer RegionConfigurers}
	 * to this {@link ConfigurableRegionFactoryBean}.
	 *
	 * @param regionName {@link String} containing the name of the {@link Region}.
	 * @param regionConfigurers array of {@link RegionConfigurer RegionConfigurers} applied
	 * to this {@link ConfigurableRegionFactoryBean}.
	 * @see RegionConfigurer
	 * @see #applyRegionConfigurers(String, Iterable)
	 */
	protected void applyRegionConfigurers(String regionName, RegionConfigurer... regionConfigurers) {
		applyRegionConfigurers(regionName, Arrays.asList(ArrayUtils.nullSafeArray(regionConfigurers, RegionConfigurer.class)));
	}

	/**
	 * Null-safe operation to apply the given {@link Iterable} of {@link RegionConfigurer RegionConfigurers}
	 * to this {@link ConfigurableRegionFactoryBean}.
	 *
	 * @param regionName {@link String} containing the name of the {@link Region}.
	 * @param regionConfigurers {@link Iterable} of {@link RegionConfigurer RegionConfigurers} applied
	 * to this {@link ConfigurableRegionFactoryBean}.
	 * @see RegionConfigurer
	 * @see #applyRegionConfigurers(String, RegionConfigurer...)
	 */
	protected void applyRegionConfigurers(String regionName, Iterable<RegionConfigurer> regionConfigurers) {
		if (this instanceof PeerRegionFactoryBean) {
			StreamSupport.stream(CollectionUtils.nullSafeIterable(regionConfigurers).spliterator(), false)
				.forEach(regionConfigurer -> regionConfigurer.configure(regionName, (PeerRegionFactoryBean<K, V>) this));
		}
	}
}
