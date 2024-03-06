/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.Region;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link RegionConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of Entity-defined {@link Region Regions} when a user annotates her Spring application
 * {@link Configuration} {@link Class} with {@link EnableEntityDefinedRegions}.
 *
 * @author John Blum
 * @see Region
 * @see PeerRegionFactoryBean
 * @see EnableEntityDefinedRegions
 * @see Configurer
 * @see org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean
 * @since 2.0.0
 */
public interface RegionConfigurer extends Configurer<PeerRegionFactoryBean<?, ?>> {

	/**
	 * Configuration callback method providing a reference to a {@link PeerRegionFactoryBean} used to construct,
	 * configure and initialize an instance of a peer {@link Region}.
	 *
	 * @param beanName name of {@link Region} bean declared in the Spring application context.
	 * @param bean reference to the {@link PeerRegionFactoryBean} used to create the peer {@link Region}.
	 * @see PeerRegionFactoryBean
	 */
	default void configure(String beanName, PeerRegionFactoryBean<?, ?> bean) { }

}
