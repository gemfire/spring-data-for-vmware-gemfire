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

import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.gud.api.GudPool;

/**
 * The {@link EnablePools} annotation enables 1 or more GemFire {@link GudPool Pools}
 * to be defined and used in a GemFire client cache application configured with Spring (Data GemFire).
 *
 * @author John Blum
 * @see GudPool
 * @see AddPoolsConfiguration
 * @see EnablePool
 * @see PoolConfigurer
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
	 * Enables the definition of multiple GemFire {@link GudPool Pools}.
	 */
	EnablePool[] pools() default {};

}
