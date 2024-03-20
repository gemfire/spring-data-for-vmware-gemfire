/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean;

/**
 * {@link GatewayReceiverConfigurer} used to customize the configuration of a {@link GatewayReceiverFactoryBean}.
 *
 * @author Udo Kohlmeyer
 * @author John Blum
 * @see Configurer
 */
@FunctionalInterface
public interface GatewayReceiverConfigurer extends Configurer<GatewayReceiverFactoryBean> {

}
