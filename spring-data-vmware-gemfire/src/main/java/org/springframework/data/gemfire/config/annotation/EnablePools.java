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

import org.apache.geode.cache.client.Pool;

import org.springframework.context.annotation.Import;

/**
 * The {@link EnablePools} annotation enables 1 or more GemFire {@link org.apache.geode.cache.client.Pool Pools}
 * to be defined and used in a GemFire client cache application configured with Spring (Data GemFire).
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.data.gemfire.config.annotation.AddPoolsConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnablePool
 * @see org.springframework.data.gemfire.config.annotation.PoolConfigurer
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(AddPoolsConfiguration.class)
@SuppressWarnings("unused")
public @interface EnablePools {

	/**
	 * Enables the definition of multiple GemFire {@link Pool Pools}.
	 */
	EnablePool[] pools() default {};

}
