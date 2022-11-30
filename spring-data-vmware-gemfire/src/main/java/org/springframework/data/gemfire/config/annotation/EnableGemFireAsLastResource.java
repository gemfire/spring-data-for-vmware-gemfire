// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * The {@link EnableGemFireAsLastResource} annotation is used to enable GemFire as a Last Resource in a Spring,
 * CMT/JTA Transaction.
 *
 * @author John Blum
 * @see EnableAspectJAutoProxy
 * @see Import
 * @see GemFireAsLastResourceConfiguration
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableAspectJAutoProxy
@Import(GemFireAsLastResourceConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableGemFireAsLastResource {

}
