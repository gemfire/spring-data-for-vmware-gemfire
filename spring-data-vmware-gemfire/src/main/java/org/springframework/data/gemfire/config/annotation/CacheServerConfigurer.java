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
 * @see java.lang.FunctionalInterface
 * @see org.apache.geode.cache.server.CacheServer
 * @see org.springframework.data.gemfire.config.annotation.CacheServerApplication
 * @see org.springframework.data.gemfire.config.annotation.EnableCacheServer
 * @see org.springframework.data.gemfire.config.annotation.EnableCacheServers
 * @see org.springframework.data.gemfire.config.annotation.support.Configurer
 * @see org.springframework.data.gemfire.server.CacheServerFactoryBean
 * @since 2.0.0
 */
@FunctionalInterface
public interface CacheServerConfigurer extends Configurer<CacheServerFactoryBean> {

}
