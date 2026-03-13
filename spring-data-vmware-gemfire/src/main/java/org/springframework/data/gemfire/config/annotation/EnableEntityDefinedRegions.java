/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.data.gemfire.mapping.annotation.ClientRegion;

/**
 * The {@link EnableEntityDefinedRegions} annotation marks a Spring {@link Configuration @Configuration} application
 * annotated class to enable the creation of GemFire/Geode {@link GudRegion Regions} based on
 * the application persistent entities.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see GudRegion
 * @see GudRegionShortcut
 * @see GudClientRegionShortcut
 * @see GudPool
 * @see ComponentScan
 * @see ComponentScan.Filter
 * @see Import
 * @see AliasFor
 * @see ClientRegionFactoryBean
 * @see EntityDefinedRegionsConfiguration
 * @see CacheTypeAwareRegionFactoryBean
 * @see ClientRegion
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(EntityDefinedRegionsConfiguration.class)
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
	 * indicates the default data policy applied to client {@link GudRegion Regions} where the persistent entities
	 * are only annotated with the generic {@link org.springframework.data.gemfire.mapping.annotation.Region}
	 * mapping annotation, or the non-data policy specific mapping annotation.
	 *
	 * Defaults to {@link GudClientRegionShortcut#PROXY}.
	 */
	GudClientRegionShortcut clientRegionShortcut() default GudClientRegionShortcut.PROXY;

	/**
	 * When this annotation is applied to a cache client application, the {@literal poolName} attribute refers to
	 * the default name of the GemFire/Geode {@link GudPool} assigned to client {@link GudRegion Region(s)}.
	 *
	 * This value can be overridden by annotating entities with the {@link ClientRegion} annotation.
	 *
	 * Defaults to {@literal DEFAULT}.
	 */
	String poolName() default ClientRegionFactoryBean.DEFAULT_POOL_NAME;

	/**
	 * When this annotation is applied to a peer cache application, the {@literal serverRegionShortcut} attribute
	 * indicates the default data policy applied to server {@link GudRegion Regions} where the persistent entities
	 * are only annotated with the generic {@link org.springframework.data.gemfire.mapping.annotation.Region}
	 * mapping annotation, or the non-data policy specific mapping annotation.
	 *
	 * Defaults to {@link GudRegionShortcut#REPLICATE}.
	 */
	GudRegionShortcut serverRegionShortcut() default GudRegionShortcut.REPLICATE;

	/**
	 * Determines whether the created {@link GudRegion} will have strongly-typed key and value constraints
	 * based on the ID and {@link Class} type of application persistent entity.
	 *
	 * Defaults to {@literal false}.
	 */
	boolean strict() default false;

}
