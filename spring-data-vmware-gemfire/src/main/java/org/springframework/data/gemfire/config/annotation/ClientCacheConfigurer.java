/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link ClientCacheConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link ClientCacheFactoryBean} used to construct, configure and initialize
 * an instance of a {@link ClientCache}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see ClientCache
 * @see ClientCacheFactoryBean
 * @see ClientCacheApplication
 * @see Configurer
 * @since 2.0.0
 */
@FunctionalInterface
public interface ClientCacheConfigurer extends Configurer<ClientCacheFactoryBean> {

}
