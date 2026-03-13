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

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.data.gemfire.mapping.annotation.ClientRegion;

/**
 * The {@link EnableCachingDefinedRegions} annotation marks a Spring {@link Configuration @Configuration} application
 * annotated class to enable the creation of GemFire/Geode {@link GudRegion Regions} based on Spring's Cache Abstraction
 * Annotations applied to application service methods and types.
 *
 * Additionally, this annotation enables Spring's Cache Abstraction with SDG's {@link EnableGemfireCaching} annotation,
 * which declares Spring's {@link org.springframework.cache.annotation.EnableCaching} annotation as well as declares
 * the SDG {@link org.springframework.data.gemfire.cache.GemfireCacheManager} bean definition.
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
 * @see Configuration
 * @see Import
 * @see EnableGemfireCaching
 * @see CachingDefinedRegionsConfiguration
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableGemfireCaching
@Import(CachingDefinedRegionsConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableCachingDefinedRegions {

	/**
	 * When this annotation is applied to a cache client application, the {@literal clientRegionShortcut} attribute
	 * specifies the data management policy applied to client {@link GudRegion Regions} where persistent entities are
	 * only annotated with the generic {@link org.springframework.data.gemfire.mapping.annotation.Region}
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
	 * specifies the data management policy applied to server {@link GudRegion Regions} where persistent entities are
	 * only annotated with the generic {@link org.springframework.data.gemfire.mapping.annotation.Region}
	 * mapping annotation, or the non-data policy specific mapping annotation.
	 *
	 * Defaults to {@link GudRegionShortcut#REPLICATE}.
	 */
	GudRegionShortcut serverRegionShortcut() default GudRegionShortcut.REPLICATE;

}
