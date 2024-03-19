/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.test.partition.entities;

import org.springframework.data.gemfire.mapping.annotation.PartitionRegion;

/**
 * Application entity type stored in an Apache Geode
 * {@link org.apache.geode.cache.DataPolicy#PARTITION {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 2.7.0
 */
@PartitionRegion(name = "Before", redundantCopies = 1)
public class BeforeEntity {

}
