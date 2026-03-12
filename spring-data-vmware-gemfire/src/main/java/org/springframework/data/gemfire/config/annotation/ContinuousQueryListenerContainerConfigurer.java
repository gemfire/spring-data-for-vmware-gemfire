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

import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;

/**
 * The {@link ContinuousQueryListenerContainerConfigurer} interfaces defines a contract for implementing {@link Object Objects}
 * in order to customize the configuration of a {@link ContinuousQueryListenerContainer} when enabling Continuous Query
 * (CQ) functionality in a Spring Boot, Apache Geode/Pivotal GemFire {@link GudClientCache} applications.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see GudClientCache
 * @see Configurer
 * @see ContinuousQueryListenerContainer
 * @since 2.0.0
 */
@FunctionalInterface
public interface ContinuousQueryListenerContainerConfigurer extends Configurer<ContinuousQueryListenerContainer> {

}
