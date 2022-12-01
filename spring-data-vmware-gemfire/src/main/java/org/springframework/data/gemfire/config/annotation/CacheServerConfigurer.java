/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.server.CacheServer;

import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;

/**
 * The {@link CacheServerConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link CacheServerFactoryBean} used to construct, configure and initialize
 * an instance of {@link CacheServer}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see CacheServer
 * @see CacheServerApplication
 * @see EnableCacheServer
 * @see EnableCacheServers
 * @see Configurer
 * @see CacheServerFactoryBean
 * @since 2.0.0
 */
@FunctionalInterface
public interface CacheServerConfigurer extends Configurer<CacheServerFactoryBean> {

}
