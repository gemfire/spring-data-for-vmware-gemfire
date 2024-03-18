/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.mapping.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;

/**
 * {@link Annotation} defining the Client {@link Region} in which the application persistent entity will be stored.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.EntityDefinedRegionsConfiguration
 * @see Region
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Region
@SuppressWarnings("unused")
public @interface ClientRegion {

	/**
	 * Name, or fully-qualified bean name of the {@link org.apache.geode.cache.Region}
	 * in which the application persistent entity will be stored (e.g. "Users", or "/Local/Admin/Users").
	 *
	 * Defaults to simple name of the application persistent entity defined by {@link Class#getSimpleName()}.
	 *
	 * @return the name or fully-qualified path of the {@link Region} in which the application persistent entity
	 * will be stored.
	 */
	@AliasFor(annotation = Region.class, attribute = "name")
	String name() default "";

	/**
	 * Name, or fully-qualified bean name of the {@link org.apache.geode.cache.Region}
	 * in which the application persistent entity will be stored (e.g. "Users", or "/Local/Admin/Users").
	 *
	 * Defaults to simple name of the application persistent entity defined by {@link Class#getSimpleName()}.
	 *
	 * @return the name or fully-qualified path of the {@link Region} in which the application persistent entity
	 * will be stored.
	 */
	@AliasFor(annotation = Region.class, attribute = "value")
	String value() default "";

	/**
	 * Name of the {@link org.apache.geode.cache.DiskStore} in which this persistent entity's data is overflowed
	 * and/or persisted.
	 *
	 * Maybe the name of a Spring bean defined in the Spring context.
	 *
	 * Defaults to unset.
	 */
	String diskStoreName() default "";

	/**
	 * Determines whether disk-based operations (used in overflow and persistence) are synchronous or asynchronous.
	 *
	 * Defaults to {@literal synchronous}.
	 */
	boolean diskSynchronous() default true;

	/**
	 * Determines whether an entity annotated with this Region annotation will ignore any existing Region definition
	 * identified by the given {@link #name()} for this entity.
	 *
	 * Defaults to {@literal true}.
	 */
	boolean ignoreIfExists() default true;

	/**
	 * Name of the GemFire/Geode {@link Pool} used by this persistent entity's {@link org.apache.geode.cache.Region}
	 * data access operations sent to the corresponding {@link org.apache.geode.cache.Region}
	 * on the GemFire/Geode Server.
	 *
	 * Defaults to {@literal DEFAULT}.
	 */
	String poolName() default ClientRegionFactoryBean.DEFAULT_POOL_NAME;

	/**
	 * {@link ClientRegionShortcut} used by this persistent entity's client {@link org.apache.geode.cache.Region}
	 * to define the {@link org.apache.geode.cache.DataPolicy}.
	 *
	 * Defaults to {@link ClientRegionShortcut#PROXY}.
	 *
	 * @see ClientRegionShortcut
	 */
	ClientRegionShortcut shortcut() default ClientRegionShortcut.PROXY;

}
