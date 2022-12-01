/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.support;

import org.springframework.core.Ordered;

/**
 * The {@link Configurer} interface defines a contract for implementing objects that can modify some aspect
 * of configuration given a reference to the Spring component responsible for the configuration of some
 * Apache Geode or Pivotal GemFire {@link Object} declared as a bean in the Spring container.
 *
 * @author John Blum
 * @param <T> {@link Class} type of the configurable Spring component processed by this {@link Configurer}.
 * @see Ordered
 * @since 2.2.0
 */
@FunctionalInterface
public interface Configurer<T> extends Ordered {

	/**
	 * Applies additional user-defined configuration to the given Spring component.
	 *
	 * @param beanName {@link String} containing the name of the Spring bean (component).
	 * @param bean Spring component used to construct, configure and initialize the Apache Geode or Pivotal GemFire
	 * {@link Object} declared as a bean in the Spring container.
	 */
	void configure(String beanName, T bean);

	/**
	 * Determines the {@literal order} of this {@link Configurer} bean relative to other {@link Configurer Configurers}
	 * of the same {@link Class type}.
	 *
	 * Returns {@literal 0} by default.
	 *
	 * @return the {@literal order} in which this {@link Configurer} is applied.
	 * @see Ordered#getOrder()
	 */
	@Override
	default int getOrder() {
		return 0;
	}
}
