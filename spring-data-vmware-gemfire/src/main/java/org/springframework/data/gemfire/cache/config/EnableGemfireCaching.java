/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.cache.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * The {@link EnableGemfireCaching} annotation enables Pivotal GemFire or Apache Geode as a caching provider
 * in Spring's Cache Abstraction.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Import
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache">Cache Abstraction</a>
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache-store-configuration-gemfire">GemFire-based Cache</a>
 * @see <a href="https://docs.spring.io/spring-data-gemfire/docs/current/reference/html/#apis:spring-cache-abstraction">Support for Spring Cache Abstraction</a>
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GemfireCachingConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableGemfireCaching {

}
