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

package org.springframework.data.gemfire.config.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link RegionConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of Entity-defined {@link GudRegion Regions} when a user annotates her Spring application
 * {@link Configuration} {@link Class} with {@link EnableEntityDefinedRegions} or {@link EnableCachingDefinedRegions}.
 *
 * @author John Blum
 * @see GudRegion
 * @see ClientRegionFactoryBean
 * @see EnableCachingDefinedRegions
 * @see EnableEntityDefinedRegions
 * @see Configurer
 * @see org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean
 * @since 2.0.0
 */
public interface RegionConfigurer extends Configurer<ClientRegionFactoryBean<?, ?>> {

	/**
	 * Configuration callback method providing a reference to a {@link ClientRegionFactoryBean} used to construct,
	 * configure and initialize an instance of a client {@link GudRegion}.
	 *
	 * @param beanName name of {@link GudRegion} bean declared in the Spring application context.
	 * @param bean reference to the {@link ClientRegionFactoryBean} used to create the client {@link GudRegion}.
	 * @see ClientRegionFactoryBean
	 */
	default void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) { }
}
