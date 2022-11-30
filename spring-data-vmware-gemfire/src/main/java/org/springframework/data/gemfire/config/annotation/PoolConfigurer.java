// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link PoolConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link PoolFactoryBean} used to construct, configure and initialize a {@link Pool}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see Pool
 * @see PoolFactoryBean
 * @see EnablePool
 * @see EnablePools
 * @see Configurer
 * @since 2.0.0
 */
@FunctionalInterface
public interface PoolConfigurer extends Configurer<PoolFactoryBean> {

}
