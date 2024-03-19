/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see org.springframework.data.gemfire.config.annotation.EnableRedisServer
 * @see org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 * @deprecated To be removed in GemFire 10 integration
 */
@Deprecated
public class RedisServerConfiguration extends EmbeddedServiceConfigurationSupport {

	protected static final int DEFAULT_REDIS_PORT = 6379;

	/**
	 * Returns the {@link EnableRedisServer} {@link java.lang.annotation.Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableRedisServer} {@link java.lang.annotation.Annotation} {@link Class} type.
	 * @see org.springframework.data.gemfire.config.annotation.EnableRedisServer
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
