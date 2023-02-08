/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link LoggingConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration using Pivotal GemFire/Apache Geode {@link Properties} to configure
 * Pivotal GemFire/Apache Geode logging.
 *
 * @author John Blum
 * @see ImportBeanDefinitionRegistrar
 * @see EnableLogging
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class LoggingConfiguration extends EmbeddedServiceConfigurationSupport {

	public static final int DEFAULT_LOG_DISK_SPACE_LIMIT = 0;
	public static final int DEFAULT_LOG_FILE_SIZE_LIMIT = 0;

	public static final String DEFAULT_LOG_LEVEL = "config";

	/**
	 * Returns the {@link EnableLogging} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableLogging} {@link Annotation} {@link Class} type.
	 * @see EnableLogging
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableLogging.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

		gemfireProperties.setPropertyIfNotDefault("log-disk-space-limit",
			resolveProperty(loggingProperty("log-disk-space-limit"),
				(Integer) annotationAttributes.get("logDiskSpaceLimit")), DEFAULT_LOG_DISK_SPACE_LIMIT);

		gemfireProperties.setProperty("log-file",
			resolveProperty(loggingProperty("log-file"),
				(String) annotationAttributes.get("logFile")));

		gemfireProperties.setPropertyIfNotDefault("log-file-size-limit",
			resolveProperty(loggingProperty("log-file-size-limit"),
				(Integer) annotationAttributes.get("logFileSizeLimit")), DEFAULT_LOG_FILE_SIZE_LIMIT);

		gemfireProperties.setPropertyIfNotDefault("log-level",
			resolveProperty(loggingProperty("level"),
				(String) annotationAttributes.get("logLevel")), DEFAULT_LOG_LEVEL);

		return gemfireProperties.build();
	}
}
