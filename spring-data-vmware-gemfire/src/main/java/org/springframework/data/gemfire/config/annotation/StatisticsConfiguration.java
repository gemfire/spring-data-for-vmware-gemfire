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
 * The {@link StatisticsConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration using Pivotal GemFire/Apache Geode {@link Properties} to configure
 * Pivotal GemFire/Apache Geode Statistics.
 *
 * @author John Blum
 * @see ImportBeanDefinitionRegistrar
 * @see EnableStatistics
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class StatisticsConfiguration extends EmbeddedServiceConfigurationSupport {

	public static final boolean DEFAULT_ENABLE_TIME_STATISTICS = false;

	public static final int DEFAULT_ARCHIVE_DISK_SPACE_LIMIT = 0;
	public static final int DEFAULT_ARCHIVE_FILE_SIZE_LIMIT = 0;
	public static final int DEFAULT_STATISTIC_SAMPLE_RATE = 1000;

	/**
	 * Returns the {@link EnableStatistics} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableStatistics} {@link Annotation} {@link Class} type.
	 * @see EnableStatistics
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableStatistics.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		PropertiesBuilder gemfireProperties = new PropertiesBuilder();

		gemfireProperties.setProperty("statistic-sampling-enabled",
			resolveProperty(statsProperty("sampling-enabled"), true));

		gemfireProperties.setPropertyIfNotDefault("archive-disk-space-limit",
			resolveProperty(statsProperty("archive-disk-space-limit"),
				(Integer) annotationAttributes.get("archiveDiskSpaceLimit")), DEFAULT_ARCHIVE_DISK_SPACE_LIMIT);

		gemfireProperties.setProperty("statistic-archive-file",
			resolveProperty(statsProperty("archive-file"),
				(String) annotationAttributes.get("archiveFile")));

		gemfireProperties.setPropertyIfNotDefault("archive-file-size-limit",
			resolveProperty(statsProperty("archive-file-size-limit"),
				(Integer) annotationAttributes.get("archiveFileSizeLimit")), DEFAULT_ARCHIVE_FILE_SIZE_LIMIT);

		gemfireProperties.setProperty("enable-time-statistics",
			resolveProperty(statsProperty("enable-time-statistics"),
				Boolean.TRUE.equals(annotationAttributes.get("enableTimeStatistics"))));

		gemfireProperties.setPropertyIfNotDefault("statistic-sample-rate",
			resolveProperty(statsProperty("sample-rate"),
				(Long) annotationAttributes.get("sampleRate")), DEFAULT_STATISTIC_SAMPLE_RATE);

		return gemfireProperties.build();
	}
}
