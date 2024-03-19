/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.expiration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Expiration is an enumerated type encapsulating custom expiration settings for application domain objects
 * to express their general expiration policy.
 *
 * @author John Blum
 * @see ExpirationActionType
 * @since 1.7.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
public @interface Expiration {

	String action() default "INVALIDATE";

	String timeout() default "0";

}
