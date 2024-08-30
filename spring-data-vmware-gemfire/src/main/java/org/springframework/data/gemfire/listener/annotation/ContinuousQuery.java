/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener.annotation;

import org.springframework.data.gemfire.listener.CQEvent;

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
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @since 2.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ContinuousQuery {

	/**
	 * Specifies event types to be excluded from the CQ.
	 *
	 * Defaults to empty.
	 */
	CQEvent[] excludedEvents() default {};

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
