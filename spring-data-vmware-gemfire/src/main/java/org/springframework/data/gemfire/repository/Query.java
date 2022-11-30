// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.QueryAnnotation;

/**
 *
 * @author Oliver Gierke
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@QueryAnnotation
@Documented
public @interface Query {

	String value() default "";

}
