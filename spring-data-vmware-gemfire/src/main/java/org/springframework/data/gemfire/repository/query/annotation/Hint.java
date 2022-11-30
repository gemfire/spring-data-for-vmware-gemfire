// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository.query.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Hint class is a annotation type indicating a GemFire OQL Query Hint.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @since 1.7.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@SuppressWarnings("unused")
public @interface Hint {

	String[] value() default {};

}
