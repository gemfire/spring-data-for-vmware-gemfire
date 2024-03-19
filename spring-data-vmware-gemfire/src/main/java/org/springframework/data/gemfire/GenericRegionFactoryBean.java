/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

/**
 * The GenericRegionFactoryBean class is an extension of the abstract, base PeerRegionFactoryBean class enabling developers
 * to define a GemFire Cache Region with defaults.
 *
 * The defaults for DataPolicy is NORMAL and Scope is DISTRIBUTED_NO_ACK, effectively creating a "non-replicate",
 * Distributed Region.
 *
 * This class enables developers to create various non-strongly-typed Regions (e.g. PARTITION, REPLICATE) based on
 * various combinations of the DataPolicy, Scope and Subscription settings as defined in the Region Types section
 * of the GemFire User Guide (see link below).  How GemFire Regions receive and distribute entry updates
 * is defined in the Storage and Distribution Options section.
 *
 * Note, it is generally better to define strong-typed Regions (e.g. PARTITION with PartitionedRegionFactoryBean)
 * in your applications.  However, different forms of distribution patterns and updates are desired
 * in certain use cases.
 *
 * @author John Blum
 * @see PeerRegionFactoryBean
 * @link https://gemfire.docs.pivotal.io/latest/userguide/index.html#developing/region_options/region_types.html
 * @link https://gemfire.docs.pivotal.io/latest/userguide/index.html#developing/region_options/storage_distribution_options.html
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class GenericRegionFactoryBean<K, V> extends PeerRegionFactoryBean<K, V> {

}
