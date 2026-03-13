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
package org.springframework.data.gemfire.tests.integration.config;

import static org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport.GEMFIRE_CACHE_SERVER_PORT_PROPERTY;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.gud.api.GudCacheServer;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.util.ClassUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ClientServerIntegrationTestsConfiguration} class is a Spring {@link Configuration} class that registers
 * a {@link ClientCacheConfigurer} used to configure the {@link GudClientCache} {@literal DEFAULT} {@link GudPool} port
 * to connect to the launched Apache Geode/VMware GemFire Server during integration testing.
 *
 * Finally, this class provides a Spring {@link Configuration} class enable the embedded Locator and Manager services
 * in a server providing the {@literal locator-manager} Spring profile is activated.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudPool
 * @see GudCacheServer
 * @see Bean
 * @see Configuration
 * @see org.springframework.context.annotation.Profile
 * @see ClientCacheConfigurer
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class ClientServerIntegrationTestsConfiguration {

	private static final int DEFAULT_PORT = GudCacheServer.DEFAULT_PORT;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected Logger getLogger() {
		return this.logger;
	}

	@Bean
	@Conditional(SpringBootIsAbsentCondition.class)
	// Required bean to resolve property placeholders in Spring @Value annotations.
	static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	ClientCacheConfigurer clientCachePoolPortConfigurer(
			@Value("${" + GEMFIRE_CACHE_SERVER_PORT_PROPERTY + ":" + DEFAULT_PORT + "}") int port) {

		return (beanName, clientCacheFactoryBean) -> {

			List<ConnectionEndpoint> servers =
				Collections.singletonList(new ConnectionEndpoint("localhost", port));

			clientCacheFactoryBean.setServers(servers);
		};
	}

	public static class SpringBootIsAbsentCondition implements Condition {

		@Override
		@SuppressWarnings("all")
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

			return !ClassUtils.isPresent("org.springframework.boot.SpringApplication",
				Thread.currentThread().getContextClassLoader());
		}
	}
}
