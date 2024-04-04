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
 * @see CacheServer
 * @see AddCacheServersConfiguration
 * @see CacheServerConfigurer
 * @see EnableCacheServer
 * @since 1.9.0
 * @deprecated to be removed in 2.0 release
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(AddCacheServersConfiguration.class)
@Deprecated(forRemoval = true)
@SuppressWarnings("unused")
public @interface EnableCacheServers {

	/**
	 * Enables the definition of multiple GemFire {@link CacheServer CacheServers}.
	 */
	EnableCacheServer[] servers() default {};

}
