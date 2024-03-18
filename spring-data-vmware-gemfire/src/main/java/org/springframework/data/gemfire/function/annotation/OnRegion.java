/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare an interface as a GemFire OnRegion Function Execution
 * @author David Turanski
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OnRegion {

	/**
	 * The bean name of the POJO interface defining the GemFire Function executions.
	 *
	 * @return the bean name (id) of the POJO interface defining the GemFire Function executions.
	 */
	String id() default "";

	/**
	 * A reference to the bean id of the GemFire Region.
	 *
	 * @return the bean name (id) of the GemFire Region.
	 */
	String region();

	/**
	 * Optional ResultCollector bean reference.
	 *
	 * @return an optional bean name of the ResultCollector to process the Function results.
	 */
	String resultCollector() default "";

}
