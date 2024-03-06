/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare an interface as a GemFire OnMember Function Execution
 *
 * @author David Turanski
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OnMember {

	/**
	 * The bean name of the POJO interface defining the GemFire Function executions.
	 *
	 * @return the bean name (id) of the POJO interface defining the GemFire Function executions.
	 */
	String id() default "";

	/**
	 * The GemFire Group to which the members must belong to target the Function execution.
	 *
	 * @return the name of the GemFire Group to which the members must belong for the targeted the Function execution.
	 */
	String groups() default "";

	/**
	 * Optional ResultCollector bean reference.
	 *
	 * @return an optional bean name of the ResultCollector to process the Function results.
	 */
	String resultCollector() default "";

}
