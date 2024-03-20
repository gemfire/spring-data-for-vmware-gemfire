/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.test.partition.entities;

import org.springframework.data.gemfire.mapping.annotation.PartitionRegion;

/**
 * Application entity type stored in an Apache Geode
 * {@link org.apache.geode.cache.DataPolicy#PARTITION {@link org.apache.geode.cache.Region}
 * collocated with the {@link BeforeEntity} application entity type.
 *
 * This entity type is deliberately named to alphabetically come before the {@link BeforeEntity} application entity type
 * in order to see how the entity component scan finds, defines, declares and registers
 * {@link org.apache.geode.cache.Region} bean definitions for these application entity types since Apache Geode expects
 * the {@link org.apache.geode.cache.DataPolicy#PARTITION} {@link org.apache.geode.cache.Region}
 * for the {@link BeforeEntity} to be created before the {@link org.apache.geode.cache.DataPolicy#PARTITION}
 * {@link org.apache.geode.cache.Region} for the {@literal AfterEntity} application entity type,
 * given this {@literal AfterEntity} is collocated with the {@link BeforeEntity}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.mapping.annotation.PartitionRegion
 * @see org.springframework.data.gemfire.config.annotation.test.partition.entities.BeforeEntity
 * @since 2.7.0
 */
@PartitionRegion(name = "After", collocatedWith = "Before", redundantCopies = 1)
public class AfterEntity {

}
