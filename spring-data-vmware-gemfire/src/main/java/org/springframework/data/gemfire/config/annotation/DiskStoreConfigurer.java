/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.DiskStore;

import org.springframework.data.gemfire.DiskStoreFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link DiskStoreConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link DiskStoreFactoryBean} used to construct, configure and initialize
 * a {@link DiskStore}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see DiskStore
 * @see DiskStoreFactoryBean
 * @see EnableDiskStore
 * @see EnableDiskStores
 * @see Configurer
 * @since 2.0.0
 */
@FunctionalInterface
public interface DiskStoreConfigurer extends Configurer<DiskStoreFactoryBean> {

}
