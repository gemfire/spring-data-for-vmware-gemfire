/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.annotation;

import org.springframework.data.gemfire.gud.api.GudClientCache;

import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link ClientCacheConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link ClientCacheFactoryBean} used to construct, configure and initialize
 * an instance of a {@link GudClientCache}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see GudClientCache
 * @see ClientCacheFactoryBean
 * @see ClientCacheApplication
 * @see Configurer
 * @since 2.0.0
 */
@FunctionalInterface
public interface ClientCacheConfigurer extends Configurer<ClientCacheFactoryBean> {

}
