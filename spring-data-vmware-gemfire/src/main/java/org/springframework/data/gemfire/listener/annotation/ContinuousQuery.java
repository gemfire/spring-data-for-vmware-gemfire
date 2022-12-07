/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link ContinuousQuery} annotation to define a GemFire/Geode Continuous Query (CQ) on a POJO method
 * which handles all CQ events and errors.
 *
 * @author John Blum
 * @see java.lang.annotation.Documented
 * @see java.lang.annotation.Inherited
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @since 2.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ContinuousQuery {

	/**
	 * Determines whether the CQ is durable.
	 *
	 * Defaults to {@literal false}.
	 */
	boolean durable() default false;

	/**
	 * {@link String Name} assigned to the registered CQ.
	 *
	 * Defaults to the fully-qualified method name.
	 */
	String name() default "";

	/**
	 * Defines the OQL query used by the CQ to determine CQ events.
	 */
	String query();

}
