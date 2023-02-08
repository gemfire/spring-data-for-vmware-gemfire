/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.execute.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.admin.functions.CreateIndexFunction;
import org.springframework.data.gemfire.config.admin.functions.CreateRegionFunction;
import org.springframework.data.gemfire.config.admin.functions.ListIndexesFunction;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctions;

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
 * @see CreateIndexFunction
 * @see CreateRegionFunction
 * @see ListIndexesFunction
 * @see EnableGemfireFunctions
 * @since 2.0.3
 */
@Configuration
@EnableGemfireFunctions
@SuppressWarnings("unused")
public class AdministrativeConfiguration {

	@Bean
	public CreateIndexFunction createIndexFunction() {
		return new CreateIndexFunction();
	}

	@Bean
	public CreateRegionFunction createRegionFunction() {
		return new CreateRegionFunction();
	}

	@Bean
	public ListIndexesFunction listIndexFunction() {
		return new ListIndexesFunction();
	}
}
