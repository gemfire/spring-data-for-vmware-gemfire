/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.geode.distributed.Locator;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link LocatorConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration by way of Pivotal GemFire/Apache Geode {@link Properties} to configure
 * an embedded {@link Locator}.
 *
 * @author John Blum
 * @see Locator
 * @see ImportBeanDefinitionRegistrar
 * @see EnableLocator
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class LocatorConfiguration extends EmbeddedServiceConfigurationSupport {

	protected static final int DEFAULT_LOCATOR_PORT = GemfireUtils.DEFAULT_LOCATOR_PORT;

	protected static final String START_LOCATOR_GEMFIRE_PROPERTY_NAME = "start-locator";

	/**
	 * Returns the {@link EnableLocator} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableLocator} {@link Annotation} {@link Class} type.
	 * @see EnableLocator
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableLocator.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		return Optional.of(resolveProperty(locatorProperty("enabled"), Boolean.TRUE))
			.filter(Boolean.TRUE::equals)
			.map(enabled -> {

				String host = resolveHost(resolveProperty(locatorProperty("host"),
					(String) annotationAttributes.get("host")));

				int port = resolvePort(resolveProperty(locatorProperty("port"),
					(Integer) annotationAttributes.get("port")), DEFAULT_LOCATOR_PORT);

				return PropertiesBuilder.create()
					.setProperty(START_LOCATOR_GEMFIRE_PROPERTY_NAME, String.format("%s[%d]", host, port))
					.build();

			}).orElseGet(Properties::new);
	}
}
