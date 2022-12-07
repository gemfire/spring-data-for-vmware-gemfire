/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link PoolConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link PoolFactoryBean} used to construct, configure and initialize a {@link Pool}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.data.gemfire.client.PoolFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.EnablePool
 * @see org.springframework.data.gemfire.config.annotation.EnablePools
 * @see org.springframework.data.gemfire.config.annotation.support.Configurer
 * @since 2.0.0
 */
@FunctionalInterface
public interface PoolConfigurer extends Configurer<PoolFactoryBean> {

}
