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
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link EnableClusterDefinedRegions} annotation marks a Spring {@link Configuration @Configuration} application
 * annotated class to enable the creation of client Proxy-based {@link GudRegion Regions} for all {@link GudRegion Regions}
 * defined in an Apache Geode/Pivotal GemFire cluster.
 *
 * @author John Blum
 * @see GudRegion
 * @see GudClientCache
 * @see Configuration
 * @see Import
 * @see ClusterDefinedRegionsConfiguration
 * @since 2.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ClusterDefinedRegionsConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableClusterDefinedRegions {

	/**
	 * Configures the client {@link GudRegion} data management policy for all client {@link GudRegion Regions} created from
	 * the corresponding server-side {@link GudRegion}.
	 *
	 * Defaults to {@link GudClientRegionShortcut#PROXY}.
	 *
	 * @see GudClientRegionShortcut
	 */
	GudClientRegionShortcut clientRegionShortcut() default GudClientRegionShortcut.PROXY;

}
