/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.test.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.PartitionRegion;

/**
 * {@link PartitionRegionEntity} persistent entity stored in the "Customers"
 * {@link org.apache.geode.cache.DataPolicy#PERSISTENT_PARTITION} {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 1.9.0
 */
@PartitionRegion(name = "Customers", ignoreIfExists = false, persistent = true, redundantCopies = 1,
	fixedPartitions = {
		@PartitionRegion.FixedPartition(name = "one", primary = true, numBuckets = 16),
		@PartitionRegion.FixedPartition(name = "two", numBuckets = 21)
	}
)
@SuppressWarnings("unused")
public class PartitionRegionEntity {

	@Id
	private Long id;

	private String firstName;

	private String lastName;

	private String title;

}
