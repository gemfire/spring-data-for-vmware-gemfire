/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import org.springframework.web.client.RestTemplate;

/**
 * Configurer for a {@link RestTemplate}.
 *
 * @author John Blum
 * @see org.springframework.web.client.RestTemplate
 * @since 2.3.0
 */
@FunctionalInterface
public interface RestTemplateConfigurer {

	/**
	 * User-defined method and contract for applying custom configuration to the given {@link RestTemplate}.
	 *
	 * @param restTemplate {@link RestTemplate} to customize the configuration for.
	 * @see org.springframework.web.client.RestTemplate
	 */
	void configure(RestTemplate restTemplate);

}
