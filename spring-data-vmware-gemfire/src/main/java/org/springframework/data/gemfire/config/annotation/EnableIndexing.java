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

import org.apache.geode.cache.query.Index;

import org.springframework.context.annotation.Configuration;

/**
 * The {@link EnableIndexing} annotation marks a Spring {@link Configuration @Configuration} annotated application class
 * to enable the creation of GemFire/Geode {@link Index Indexes} based on
 * application persistent entity field/property annotations, such as the {@link @Id}, {@link @Indexed}
 * and {@link @LuceneIndex} annotations.
 *
 * @author John Blum
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.data.gemfire.IndexFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.IndexConfiguration
 * @see org.springframework.data.gemfire.config.annotation.IndexConfigurer
 * @since 1.9.0
 * @deprecated to be removed in 2.0 release
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Deprecated(forRemoval = true)
@SuppressWarnings({ "unused" })
public @interface EnableIndexing {

	/**
	 * Determines whether all GemFire/Geode {@link Index Indexes} will be defined before created.
	 * If set to {@literal true}, then all {@link Index Indexes} are defined first and the created
	 * in a single, bulk operation, thereby improving {@link Index} creation process efficiency.
	 * Defaults to {@literal false}.
	 */
	boolean define() default false;

}
