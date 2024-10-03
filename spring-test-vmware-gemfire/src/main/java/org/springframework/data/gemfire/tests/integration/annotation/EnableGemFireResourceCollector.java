/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration.annotation;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.DiskStore;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.AfterTestClassEvent;

/**
 * The {@link EnableGemFireResourceCollector} annotation enables the cleanup of resources (e.g. files) and other garbage
 * irresponsibly left behind by Apache Geode (or VMware GemFire) after the GemFire/Geode process shuts down, especially
 * in a test context in order to avoid conflicts and interference between test runs.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see org.apache.geode.cache.GemFireCache
 * @see Import
 * @since 0.0.17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GemFireResourceCollectorConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableGemFireResourceCollector {

	/**
	 * Determines the Spring {@link ApplicationEvent ApplicationEvents} that trigger the framework to cleanup after
	 * Apache Geode / VMware GemFire given the junk it leaves behind after a process (e.g. CacheServer, Locator,
	 * Manager, etc) terminates.
	 *
	 * @return an array of {@link ApplicationEvent ApplicationEvents} that trigger the GemFire/Geode resource
	 * and garbage collection algorithm.
	 * @see ApplicationEvent
	 * @see Class
	 */
	Class<? extends ApplicationEvent>[] collectOnEvents() default { AfterTestClassEvent.class };

	/**
	 * Tries to cleanup all the {@link File Files} left behind by GemFire/Geode {@link DiskStore DiskStores}.
	 *
	 * @return a boolean value indicating whether the GemFire Resource Collector should cleanup all {@link File Files}
	 * left behind by GemFire/Geode {@link DiskStore DiskStores}, whether for persistence or overflow;
	 * defaults to {@literal false}.
	 */
	boolean tryCleanDiskStoreFiles() default GemFireResourceCollectorConfiguration.DEFAULT_CLEAN_DISK_STORE_FILES;

}
