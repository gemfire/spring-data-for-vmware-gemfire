/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.execute.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.admin.functions.CreateRegionFunction;

/**
 * The {@link AdministrativeConfiguration} class is a Spring {@link Configuration @Configuration} class that registers
 * SDG Administrative {@link Function Functions} used by SDG's {@link EnableClusterConfiguration} without HTTP enabled.
 *
 * Additionally, this class enable's SDG {@link Function} implementations so that the internal SDG administrative
 * {@link Function Functions} are properly created and registered in Apache Geode.
 *
 * @author John Blum
 * @see Function
 * @see Bean
 * @see Configuration
 * @see CreateRegionFunction
 * @since 2.0.3
 */
@Configuration
@SuppressWarnings("unused")
public class AdministrativeConfiguration {

	@Bean
	public CreateRegionFunction createRegionFunction() {
		return new CreateRegionFunction();
	}
}
