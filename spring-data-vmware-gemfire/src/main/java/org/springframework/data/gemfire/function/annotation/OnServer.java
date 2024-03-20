/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare an interface as a GemFire OnServer Function Execution
 * @author David Turanski
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OnServer {

	/**
	 * The bean name of the POJO interface defining the GemFire Function executions.
	 *
	 * @return the bean name (id) of the POJO interface defining the GemFire Function executions.
	 */
	String id() default "";

	/**
	 *  A reference to the cache.
	 *
	 *  @return a bean name reference to the GemFire Cache.
	 */
	String cache() default "";

	/**
	 * The pool bean name (optional).
	 *
	 * @return an optional bean name of the GemFire Pool.
	 */
	String pool() default "";

	/**
	 * Optional ResultCollector bean reference.
	 *
	 * @return an optional bean name of the ResultCollector to process the Function results.
	 */
	String resultCollector() default "";

}
