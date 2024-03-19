/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.Region;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link RegionConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of Entity-defined {@link Region Regions} when a user annotates her Spring application
 * {@link Configuration} {@link Class} with {@link EnableEntityDefinedRegions} or {@link EnableCachingDefinedRegions}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.PeerRegionFactoryBean
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.support.Configurer
 * @see org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean
 * @since 2.0.0
 */
public interface RegionConfigurer extends Configurer<ClientRegionFactoryBean<?, ?>> {

	/**
	 * Configuration callback method providing a reference to a {@link ClientRegionFactoryBean} used to construct,
	 * configure and initialize an instance of a client {@link Region}.
	 *
	 * @param beanName name of {@link Region} bean declared in the Spring application context.
	 * @param bean reference to the {@link ClientRegionFactoryBean} used to create the client {@link Region}.
	 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
	 */
	default void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) { }

	/**
	 * Configuration callback method providing a reference to a {@link PeerRegionFactoryBean} used to construct,
	 * configure and initialize an instance of a peer {@link Region}.
	 *
	 * @param beanName name of {@link Region} bean declared in the Spring application context.
	 * @param bean reference to the {@link PeerRegionFactoryBean} used to create the peer {@link Region}.
	 * @see org.springframework.data.gemfire.PeerRegionFactoryBean
	 */
	default void configure(String beanName, PeerRegionFactoryBean<?, ?> bean) { }

}
