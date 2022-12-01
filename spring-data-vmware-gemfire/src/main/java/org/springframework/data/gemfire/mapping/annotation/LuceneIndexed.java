/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.mapping.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.lucene.LuceneIndex;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.gemfire.mapping.GemfirePersistentProperty;

/**
 * The {@link LuceneIndexed} annotation is used to index a {@link GemfirePersistentEntity}
 * {@link GemfirePersistentProperty}, creating a GemFire/Geode {@link LuceneIndex}
 * on a GemFire/Geode {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @see AliasFor
 * @see org.springframework.data.gemfire.IndexType
 * @see org.apache.geode.cache.Region
 * @see LuceneIndex
 * @since 1.1.0
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SuppressWarnings("unused")
public @interface LuceneIndexed {

	/**
	 * {@link String Name} of the {@link LuceneIndex}.
	 *
	 * @return a {@link String} containing the name of the {@link LuceneIndex}
	 */
	@AliasFor(attribute = "name")
	String value() default "";

	/**
	 * {@link String Name} of the {@link LuceneIndex}.
	 *
	 * @return a {@link String} containing the name of the {@link LuceneIndex}
	 */
	@AliasFor(attribute = "value")
	String name() default "";

	/**
	 * Determine whether the {@link LuceneIndex} should be destroy when the application shutsdown.
	 *
	 * Default is {@literal false}.
	 */
	boolean destroy() default false;

}
