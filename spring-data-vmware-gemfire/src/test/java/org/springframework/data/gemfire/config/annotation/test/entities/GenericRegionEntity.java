/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.test.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

/**
 * {@link GenericRegionEntity} persistent entity stored in the "GenericRegionEntity"
 * {@link org.apache.geode.cache.DataPolicy#NORMAL}, {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 1.9.0
 */
@Region
public class GenericRegionEntity {

	@Id
	private Long id;

	private String name;

}
