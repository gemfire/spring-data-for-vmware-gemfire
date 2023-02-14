/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.tests.integration.context.event.GemFireResourceCollectorApplicationListener;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.test.context.event.AfterTestClassEvent;

/**
 * Spring {@link Configuration} class used to register beans that collect resources and other garbage irresponsibly
 * left behind by Apache Geode when its processes shutdown, particularly in a test context in order to avoid conflicts
 * and interference between test runs.
 *
 * @author John Blum
 * @see Annotation
 * @see ApplicationEvent
 * @see ApplicationListener
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationMetadata
 * @see AbstractAnnotationConfigSupport
 * @see GemFireResourceCollectorApplicationListener
 * @since 0.0.17
 */
@Configuration
@SuppressWarnings("unused")
public class GemFireResourceCollectorConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	public static final boolean DEFAULT_CLEAN_DISK_STORE_FILES = false;

	private boolean tryCleanDiskStoreFiles = DEFAULT_CLEAN_DISK_STORE_FILES;

	@SuppressWarnings("unchecked")
	private Class<? extends ApplicationEvent>[] collectorEventTypes = new Class[] { AfterTestClassEvent.class };

	@Override
	@SuppressWarnings("unchecked")
	public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {

		Optional.of(importMetadata)
			.filter(this::isAnnotationPresent)
			.map(this::getAnnotationAttributes)
			.ifPresent(enableGemFireResourceCollectorAttributes -> {

				this.collectorEventTypes = (Class<? extends ApplicationEvent>[])
					enableGemFireResourceCollectorAttributes.getClassArray("collectOnEvents");

				this.tryCleanDiskStoreFiles =
					enableGemFireResourceCollectorAttributes.getBoolean("tryCleanDiskStoreFiles");
			});
	}

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableGemFireResourceCollector.class;
	}

	@SuppressWarnings("unchecked")
	protected @NonNull Class<? extends ApplicationEvent>[] getConfiguredCollectorEventTypes() {
		return ArrayUtils.nullSafeArray(this.collectorEventTypes, Class.class);
	}

	protected boolean isTryCleanDiskStoreFiles() {
		return this.tryCleanDiskStoreFiles;
	}

	@Bean
	ApplicationListener<ApplicationEvent> gemfireResourceCollectorApplicationListener() {
		return GemFireResourceCollectorApplicationListener.create(getConfiguredCollectorEventTypes())
			.tryCleanDiskStoreFiles(isTryCleanDiskStoreFiles());
	}
}
