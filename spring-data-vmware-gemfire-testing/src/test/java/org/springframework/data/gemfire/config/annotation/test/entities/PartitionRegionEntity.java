/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.test.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.IndexType;
import org.springframework.data.gemfire.mapping.annotation.Indexed;
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

	@Indexed(expression = "first_name", from = "/LoyalCustomers")
	private String firstName;

	@Indexed(name = "LastNameIdx", expression = "surname", type = IndexType.HASH)
	private String lastName;

	private String title;

}
