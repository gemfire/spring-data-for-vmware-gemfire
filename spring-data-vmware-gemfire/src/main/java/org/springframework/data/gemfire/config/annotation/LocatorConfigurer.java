/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.distributed.Locator;

import org.springframework.data.gemfire.LocatorFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * A Spring {@link Configurer} used to apply additional, customized configuration for an Apache Geode or Pivotal GemFire
 * {@link Locator}.
 *
 * This {@link Configurer} is particularly useful when using {@link LocatorApplication} annotation to configure
 * and bootstrap an Apache Geode or Pivotal GemFire {@link Locator}.
 *
 * This {@link Configurer} is NOT applied when configuring and enabling an embedded {@link Locator}
 * using the {@link @EnableLocator} annotation.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see Locator
 * @see LocatorFactoryBean
 * @see Configurer
 * @since 2.2.0
 */
@FunctionalInterface
public interface LocatorConfigurer extends Configurer<LocatorFactoryBean> {

}
