/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.support.sample;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.support.SpringContextBootstrappingInitializerIntegrationTests;

/**
 * The UserApplicationConfiguration class is a configuration component for configuring the user's application.
 *
 * @author John Blum
 * @see Bean
 * @see Configuration
 * @see ImportResource
 * @since 1.4.0
 */
@Configuration
@ImportResource({ "classpath:org/springframework/data/gemfire/support/sample/initializer-gemfire-context.xml" })
@SuppressWarnings("unused")
public class UserApplicationConfiguration {

	@Bean
	public DataSource userDataSource() {
		return new SpringContextBootstrappingInitializerIntegrationTests.TestDataSource();
	}

}
