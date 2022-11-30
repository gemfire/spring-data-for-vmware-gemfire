// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Enables GemFire annotated Function implementations.
 *
 * Causes the container to discover any beans that are annotated with {code}@GemfireFunction{code},
 * wrap them in a {@link org.springframework.data.gemfire.function.PojoFunctionWrapper},
 * and register them with the cache.
 *
 * @author David Turanski
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GemfireFunctionBeanPostProcessorRegistrar.class)
@SuppressWarnings("unused")
public @interface EnableGemfireFunctions {

}
