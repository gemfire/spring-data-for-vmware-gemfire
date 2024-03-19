/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.test.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.LocalRegion;

/**
 * {@link LocalRegionEntity} persistent entity stored in the "LocalRegionEntity" {@link org.apache.geode.cache.DataPolicy#NORMAL},
 * {@link org.apache.geode.cache.Scope#LOCAL} {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 1.9.0
 */
@LocalRegion
public class LocalRegionEntity {

	@Id
	private String id;

}
