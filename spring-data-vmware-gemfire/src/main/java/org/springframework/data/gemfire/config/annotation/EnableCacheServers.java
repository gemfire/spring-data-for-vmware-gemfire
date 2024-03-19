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

import org.apache.geode.cache.server.CacheServer;

import org.springframework.context.annotation.Import;

/**
 * The {@link EnableCacheServers} annotation enables 1 or more {@link CacheServer CacheServers}
 * to be defined and used in a peer cache application configured with Spring (Data GemFire/Geode).
 *
 * @author John Blum
 * @see org.apache.geode.cache.server.CacheServer
 * @see org.springframework.data.gemfire.config.annotation.AddCacheServersConfiguration
 * @see org.springframework.data.gemfire.config.annotation.CacheServerConfigurer
 * @see org.springframework.data.gemfire.config.annotation.EnableCacheServer
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(AddCacheServersConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableCacheServers {

	/**
	 * Enables the definition of multiple GemFire {@link org.apache.geode.cache.server.CacheServer CacheServers}.
	 */
	EnableCacheServer[] servers() default {};

}
