// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link MemcachedServerConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration using Pivotal GemFire/Apache Geode {@link Properties} to configure
 * an embedded Memcached server in this cluster member.
 *
 * @author John Blum
 * @see ImportBeanDefinitionRegistrar
 * @see EnableMemcachedServer
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class MemcachedServerConfiguration extends EmbeddedServiceConfigurationSupport {

	protected static final int DEFAULT_MEMCACHED_SERVER_PORT = 11211;

	/**
	 * Returns the {@link EnableMemcachedServer} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableMemcachedServer} {@link Annotation} {@link Class} type.
	 * @see EnableMemcachedServer
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableMemcachedServer.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		return Optional.of(resolveProperty(memcachedServiceProperty("enabled"), Boolean.TRUE))
			.filter(Boolean.TRUE::equals)
			.map(enabled ->

				PropertiesBuilder.create()
					.setProperty("memcached-port",
						resolvePort(resolveProperty(memcachedServiceProperty("port"),
							(Integer) annotationAttributes.get("port")), DEFAULT_MEMCACHED_SERVER_PORT))
					.setProperty("memcached-protocol",
						resolveProperty(memcachedServiceProperty("protocol"),
							EnableMemcachedServer.MemcachedProtocol.class,
								(EnableMemcachedServer.MemcachedProtocol) annotationAttributes.get("protocol")))
					.build()

			).orElseGet(Properties::new);
	}
}
