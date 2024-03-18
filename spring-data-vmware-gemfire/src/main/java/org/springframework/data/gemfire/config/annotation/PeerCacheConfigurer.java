/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.Cache;

import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link PeerCacheConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link CacheFactoryBean} used to construct, configure and initialize
 * an instance of a peer {@link Cache}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see Cache
 * @see CacheFactoryBean
 * @see PeerCacheApplication
 * @see Configurer
 * @since 1.9.0
 */
@FunctionalInterface
public interface PeerCacheConfigurer extends Configurer<CacheFactoryBean> {

}
