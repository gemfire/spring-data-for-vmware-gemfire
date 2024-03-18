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

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean;

/**
 * The {@link EnableEntityDefinedRegions} annotation marks a Spring {@link Configuration @Configuration} application
 * annotated class to enable the creation of GemFire/Geode {@link Region Regions} based on
 * the application persistent entities.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Region
 * @see RegionShortcut
 * @see ClientRegionShortcut
 * @see Pool
 * @see ComponentScan
 * @see ComponentScan.Filter
 * @see Import
 * @see AliasFor
 * @see EntityDefinedRegionsConfiguration
 * @see IndexConfiguration
 * @see CacheTypeAwareRegionFactoryBean
 * @see org.springframework.data.gemfire.mapping.annotation.LocalRegion
 * @see org.springframework.data.gemfire.mapping.annotation.PartitionRegion
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @see org.springframework.data.gemfire.mapping.annotation.ReplicateRegion
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(IndexConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableEntityDefinedRegions {

	/**
	 * Alias for {@link #basePackages()} attribute.
	 *
	 * @return a {@link String} array specifying the packages to search for application persistent entities.
	 * @see #basePackages()
	 */
	@AliasFor(attribute = "basePackages")
	String[] value() default {};

	/**
	 * Base packages to scan for {@link org.springframework.data.gemfire.mapping.annotation.Region @Region} annotated
	 * application persistent entities.
	 *
	 * The {@link #value()} attribute is an alias for this attribute.
	 *
	 * Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
	 *
	 * Use the {@literal spring.data.gemfire.entities.base-packages} property in {@literal application.properties}.
	 *
	 * @return a {@link String} array specifying the packages to search for application persistent entities.
	 * @see #value()
	 */
	@AliasFor(attribute = "value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to the {@link #basePackages()} attribute for specifying the packages to scan for
	 * {@link org.springframework.data.gemfire.mapping.annotation.Region @Region} annotated application persistent entities.
	 *
	 * The package of each class specified will be scanned.
	 *
	 * Consider creating a special no-op marker class or interface in each package that serves no other purpose
	 * than being referenced by this attribute.
	 *
	 * @return an array of {@link Class classes} used to determine the packages to scan
	 * for application persistent entities.
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * Specifies which types are not eligible for component scanning.
	 *
	 * @return an array of {@link ComponentScan.Filter Filters} used to
	 * specify application persistent entities to be excluded during the component scan.
	 */
	ComponentScan.Filter[] excludeFilters() default {};

	/**
	 * Specifies which types are eligible for component scanning.
	 *
	 * Further narrows the set of candidate components from everything in {@link #basePackages()}
	 * or {@link #basePackageClasses()} to everything in the base packages that matches the given filter or filters.
	 *
	 * @return an array {@link ComponentScan.Filter} of Filters used to specify application persistent entities
	 * to be included during the component scan.
	 */
	ComponentScan.Filter[] includeFilters() default {};

	/**
	 * When this annotation is applied to a cache client application, the {@literal clientRegionShortcut} attribute
	 * indicates the default data policy applied to client {@link Region Regions} where the persistent entities
	 * are only annotated with the generic {@link org.springframework.data.gemfire.mapping.annotation.Region}
	 * mapping annotation, or the non-data policy specific mapping annotation.
	 *
	 * Defaults to {@link ClientRegionShortcut#PROXY}.
	 */
	ClientRegionShortcut clientRegionShortcut() default ClientRegionShortcut.PROXY;

	/**
	 * When this annotation is applied to a cache client application, the {@literal poolName} attribute refers to
	 * the default name of the GemFire/Geode {@link Pool} assigned to client {@link Region Region(s)}.
	 *
	 * This value can be overridden by annotating entities with the ClientRegion annotation.
	 *
	 * Defaults to {@literal DEFAULT}.
	 */
	String poolName() default "DEFAULT";

	/**
	 * When this annotation is applied to a peer cache application, the {@literal serverRegionShortcut} attribute
	 * indicates the default data policy applied to server {@link Region Regions} where the persistent entities
	 * are only annotated with the generic {@link org.springframework.data.gemfire.mapping.annotation.Region}
	 * mapping annotation, or the non-data policy specific mapping annotation.
	 *
	 * Defaults to {@link RegionShortcut#PARTITION}.
	 */
	RegionShortcut serverRegionShortcut() default RegionShortcut.PARTITION;

	/**
	 * Determines whether the created {@link Region} will have strongly-typed key and value constraints
	 * based on the ID and {@link Class} type of application persistent entity.
	 *
	 * Defaults to {@literal false}.
	 */
	boolean strict() default false;

}
