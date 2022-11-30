// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.support.RegionDataAccessTracingAspect;

/**
 * The RegionDataAccessTracingConfiguration class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class RegionDataAccessTracingConfiguration {

	@Bean
	public RegionDataAccessTracingAspect regionDataAccessTracingAspect() {
		return new RegionDataAccessTracingAspect();
	}
}
