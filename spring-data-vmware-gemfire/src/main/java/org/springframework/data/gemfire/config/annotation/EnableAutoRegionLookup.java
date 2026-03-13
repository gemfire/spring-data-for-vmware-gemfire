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
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link EnableAutoRegionLookup} annotation configures a Spring {@link Configuration} annotated class
 * with the ability to automatically look up and register any Apache Geode or Pivotal GemFire {@link GudRegion Regions}
 * which may have be defined in {@literal cache.xml} or by using the Cluster Configuration Service.
 *
 * @author John Blum
 * @see GudRegion
 * @see Import
 * @see org.springframework.data.gemfire.ResolvableRegionFactoryBean#setLookupEnabled(Boolean)
 * @see AutoRegionLookupConfiguration
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(AutoRegionLookupConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableAutoRegionLookup {

	/**
	 * Attribute indicating whether auto {@link GudRegion} lookup should be enabled;
	 *
	 * Defaults to {@literal true}.
	 *
	 * Use the {@literal spring.data.gemfire.cache.enable-auto-region-lookup} in {@literal application.properties}
	 * to dynamically customize this configuration setting.
	 */
	boolean enabled() default true;

}
