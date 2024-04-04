/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.Region;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The {@link EnableOffHeap} annotation marks a Spring {@link Configuration @Configuration} annotated application
 * {@link Class} to configure and enable Off-Heap Memory data storage in cache {@link Region Regions}.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.config.annotation.OffHeapConfiguration
 * @since 1.9.0
 * @deprecated to be removed in 2.0 release
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(OffHeapConfiguration.class)
@UsesGemFireProperties
@Deprecated(forRemoval = true)
@SuppressWarnings("unused")
public @interface EnableOffHeap {

	/**
	 * Specifies the size of off-heap memory in megabytes (m) or gigabytes (g).
	 *
	 * For example:
	 *
	 * <pre>
	 *     <code>
	 *       off-heap-memory-size=4096m
	 *       off-heap-memory-size=120g
	 *     </code>
	 * </pre>
	 *
	 * Defaults to unset.
	 *
	 * Use the {@literal spring.data.gemfire.cache.off-heap.memory-size} property in {@literal application.properties}.
	 */
	String memorySize();

	/**
	 * Identifies all the {@link Region Regions} by name in which the Off-Heap Memory settings will be applied.
	 *
	 * Defaults to all {@link Region Regions}.
	 *
	 * Use the {@literal spring.data.gemfire.cache.off-heap.region-names} property in {@literal application.properties}.
	 */
	String[] regionNames() default {};

}
