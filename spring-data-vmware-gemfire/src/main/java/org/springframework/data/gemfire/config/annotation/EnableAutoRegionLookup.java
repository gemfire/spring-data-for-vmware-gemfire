/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
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
 * The {@link EnableAutoRegionLookup} annotation configures a Spring {@link Configuration} annotated class
 * with the ability to automatically look up and register any Apache Geode or Pivotal GemFire {@link Region Regions}
 * which may have be defined in {@literal cache.xml} or by using the Cluster Configuration Service.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.ResolvableRegionFactoryBean#setLookupEnabled(Boolean)
 * @see org.springframework.data.gemfire.config.annotation.AutoRegionLookupConfiguration
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
	 * Attribute indicating whether auto {@link org.apache.geode.cache.Region} lookup should be enabled;
	 *
	 * Defaults to {@literal true}.
	 *
	 * Use the {@literal spring.data.gemfire.cache.enable-auto-region-lookup} in {@literal application.properties}
	 * to dynamically customize this configuration setting.
	 */
	boolean enabled() default true;

}
