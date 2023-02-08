/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.support.GemfireBeanFactoryLocator;

/**
 * The {@link EnableBeanFactoryLocator} annotation annotates a Spring {@link Configuration @Configuration} class
 * enabling SDG's {@link GemfireBeanFactoryLocator} in order to auto-wire and configure GemFire/Geode Objects
 * declared in GemFire/Geode config.
 *
 * @author John Blum
 * @see java.lang.annotation.Documented
 * @see java.lang.annotation.Inherited
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(BeanFactoryLocatorConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableBeanFactoryLocator {

}
