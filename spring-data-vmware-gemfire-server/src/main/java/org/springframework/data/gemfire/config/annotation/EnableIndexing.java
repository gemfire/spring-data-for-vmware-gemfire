/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
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
 * application persistent entity field/property annotations, such as the {@link @Id}, and {@link @Indexed}
 * annotations.
 *
 * @author John Blum
 * @see Index
 * @see org.springframework.data.gemfire.IndexFactoryBean
 * @see IndexConfiguration
 * @see IndexConfigurer
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SuppressWarnings({ "unused" })
public @interface EnableIndexing {

	/**
	 * Determines whether all GemFire/Geode {@link Index Indexes} will be defined before created.
	 * If set to {@literal true}, then all {@link Index Indexes} are defined first and the created
	 * in a single, bulk operation, thereby improving {@link Index} creation process efficiency.
	 *
	 * Only applies to OQL-based {@link Index Indexes}.
	 *
	 * Defaults to {@literal false}.
	 */
	boolean define() default false;

}
