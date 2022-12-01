/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link UsesGemFireProperties} is a meta-annotation used to mark other SDG {@link Annotation Annotations}
 * that uses GemFire properties to configure the Spring-based GemFire cache instance.
 *
 * @author John Blum
 * @see Annotation
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @since 2.0.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface UsesGemFireProperties {

}
