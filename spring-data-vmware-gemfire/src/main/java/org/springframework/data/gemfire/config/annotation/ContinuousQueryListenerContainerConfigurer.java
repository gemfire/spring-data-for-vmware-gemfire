/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;

/**
 * The {@link ContinuousQueryListenerContainerConfigurer} interfaces defines a contract for implementing {@link Object Objects}
 * in order to customize the configuration of a {@link ContinuousQueryListenerContainer} when enabling Continuous Query
 * (CQ) functionality in a Spring Boot, Apache Geode/Pivotal GemFire {@link ClientCache} applications.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see ClientCache
 * @see Configurer
 * @see ContinuousQueryListenerContainer
 * @since 2.0.0
 */
@FunctionalInterface
public interface ContinuousQueryListenerContainerConfigurer extends Configurer<ContinuousQueryListenerContainer> {

}
