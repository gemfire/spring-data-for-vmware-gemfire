/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.test.entities;

import org.springframework.data.gemfire.ScopeType;
import org.springframework.data.gemfire.mapping.annotation.ReplicateRegion;

/**
 * {@link ReplicateRegionEntity} persistent entity stored in the "Accounts"
 * {@link org.apache.geode.cache.DataPolicy#REPLICATE} {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 1.9.0
 */
@ReplicateRegion(name = "Accounts", scope = ScopeType.DISTRIBUTED_ACK)
public class ReplicateRegionEntity {

	private String number;

}
