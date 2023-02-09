/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.snapshot.event;

import static org.springframework.data.gemfire.snapshot.SnapshotServiceFactoryBean.SnapshotMetadata;

/**
 * The ExportSnapshotApplicationEvent class is a Spring ApplicationEvent signaling a GemFire Cache or Region 'import'
 * snapshot event.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.snapshot.event.SnapshotApplicationEvent
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class ExportSnapshotApplicationEvent<K, V> extends SnapshotApplicationEvent<K, V> {

	/**
	 * Constructs an instance of ExportSnapshotApplicationEvent initialized with an event source and optional meta-data
	 * describing the data snapshots to be exported.
	 *
	 * @param source the source of the ApplicationEvent.
	 * @param snapshotMetadata an array of SnapshotMetadata containing details for each export.
	 * @see org.springframework.data.gemfire.snapshot.SnapshotServiceFactoryBean.SnapshotMetadata
	 */
	public ExportSnapshotApplicationEvent(Object source, SnapshotMetadata<K, V>... snapshotMetadata) {
		super(source, snapshotMetadata);
	}

	/**
	 * Constructs an instance of ExportSnapshotApplicationEvent initialized with an event source, a pathname
	 * of the Region from which data is exported along with meta-data describing the details of the snapshot source.
	 *
	 * @param source the source of the ApplicationEvent.
	 * @param regionPath absolute pathname of the Region.
	 * @param snapshotMetadata an array of SnapshotMetadata containing details for each export.
	 * @see org.springframework.data.gemfire.snapshot.SnapshotServiceFactoryBean.SnapshotMetadata
	 */
	public ExportSnapshotApplicationEvent(Object source, String regionPath, SnapshotMetadata<K, V>... snapshotMetadata) {
		super(source, regionPath, snapshotMetadata);
	}

}
