/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.query.Index;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.gemfire.IndexType;
import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.gemfire.mapping.GemfirePersistentProperty;

/**
 * The {@link Indexed} annotation is used to index a {@link GemfirePersistentEntity} {@link GemfirePersistentProperty},
 * which creates a GemFire/Geode {@link Index} on a GemFire/Geode {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @see AliasFor
 * @see IndexType
 * @see org.apache.geode.cache.Region
 * @see Index
 * @since 1.9.0
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SuppressWarnings({ "unused" })
public @interface Indexed {

	/**
	 * Name of the Index.
	 */
	@AliasFor(attribute = "name")
	String value() default "";

	/**
	 * Name of the Index.
	 */
	@AliasFor(attribute = "value")
	String name() default "";

	/**
	 * Expression to index.
	 */
	String expression() default "";

	/**
	 * The Apache Geode {@link org.apache.geode.cache.Region} on which the {@link Index} is created.
	 */
	String from() default "";

	/**
	 * Type of Index to create.
	 *
	 * Defaults to {@link IndexType#HASH}.
	 */
	IndexType type() default IndexType.FUNCTIONAL;

}
