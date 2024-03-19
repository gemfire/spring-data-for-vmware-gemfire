/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.test.entities;

import org.springframework.data.gemfire.mapping.annotation.PartitionRegion;

/**
 * {@link CollocatedPartitionRegionEntity} persistent entity stored in the "ContactEvents"
 * {@link org.apache.geode.cache.DataPolicy#PERSISTENT_PARTITION} {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 1.9.0
 */
@PartitionRegion(value = "ContactEvents", collocatedWith = "Customers", diskStoreName = "mockDiskStore",
	diskSynchronous = false, ignoreJta = true, partitionResolverName = "mockPartitionResolver",
	persistent = true, redundantCopies = 2)
public class CollocatedPartitionRegionEntity {

	private String email;

	private String phoneNumber;

}
