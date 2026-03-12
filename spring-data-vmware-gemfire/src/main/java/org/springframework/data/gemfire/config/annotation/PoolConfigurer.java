/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.annotation;

import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.gud.api.GudPool;

/**
 * The {@link PoolConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link PoolFactoryBean} used to construct, configure and initialize a {@link GudPool}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see GudPool
 * @see PoolFactoryBean
 * @see EnablePool
 * @see EnablePools
 * @see Configurer
 * @since 2.0.0
 */
@FunctionalInterface
public interface PoolConfigurer extends Configurer<PoolFactoryBean> {

}
