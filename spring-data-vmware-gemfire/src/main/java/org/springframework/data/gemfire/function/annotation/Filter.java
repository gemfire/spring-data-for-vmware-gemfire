/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Used to inject a set of cache keys into a function execution,  The annotated parameter must be of type
 * {@link java.util.Set}. This is used by the function invocation to specify a set of keys of interest and also to define
 * an additional parameter on the function implementation method containing the filter.
 * @author David Turanski
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Filter {
}
