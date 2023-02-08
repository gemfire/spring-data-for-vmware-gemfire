/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link RedisServerConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration using Pivotal GemFire/Apache Geode {@link Properties} to configure
 * an embedded Redis server.
 *
 * @author John Blum
 * @see ImportBeanDefinitionRegistrar
 * @see EnableRedisServer
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class RedisServerConfiguration extends EmbeddedServiceConfigurationSupport {

	protected static final int DEFAULT_REDIS_PORT = 6379;

	/**
	 * Returns the {@link EnableRedisServer} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableRedisServer} {@link Annotation} {@link Class} type.
	 * @see EnableRedisServer
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableRedisServer.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		return Optional.ofNullable(resolveProperty(redisServiceProperty("enabled"), Boolean.TRUE))
			.filter(Boolean.TRUE::equals)
			.map(enabled ->

				PropertiesBuilder.create()
					.setProperty("redis-bind-address",
						resolveProperty(redisServiceProperty("bind-address"),
							(String) annotationAttributes.get("bindAddress")))
					.setProperty("redis-port",
						resolvePort(resolveProperty(redisServiceProperty("port"),
							(Integer) annotationAttributes.get("port")), DEFAULT_REDIS_PORT))
					.build()

			).orElseGet(Properties::new);
	}
}
