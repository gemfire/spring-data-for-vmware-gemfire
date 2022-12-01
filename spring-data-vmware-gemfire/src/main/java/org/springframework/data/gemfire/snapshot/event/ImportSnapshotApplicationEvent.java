/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.snapshot.event;

import static org.springframework.data.gemfire.snapshot.SnapshotServiceFactoryBean.SnapshotMetadata;

/**
 * The ImportSnapshotApplicationEvent class is a Spring ApplicationEvent signaling a GemFire Cache or Region 'export'
 * snapshot event.
 *
 * @author John Blum
 * @see SnapshotApplicationEvent
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class ImportSnapshotApplicationEvent<K, V> extends SnapshotApplicationEvent<K, V> {

	/**
	 * Constructs an instance of ImportSnapshotApplicationEvent initialized with an event source and optional meta-data
	 * describing the data snapshots to be imported.
	 *
	 * @param source the source of the ApplicationEvent.
	 * @param snapshotMetadata an array of SnapshotMetadata containing details for each import.
	 * @see SnapshotMetadata
	 */
	public ImportSnapshotApplicationEvent(Object source, SnapshotMetadata<K, V>... snapshotMetadata) {
		super(source, snapshotMetadata);
	}

	/**
	 * Constructs an instance of ImportSnapshotApplicationEvent initialized with an event source, a pathname
	 * of the Region in which data is imported along with meta-data describing the details of the snapshot source.
	 *
	 * @param source the source of the ApplicationEvent.
	 * @param regionPath absolute pathname of the Region.
	 * @param snapshotMetadata an array of SnapshotMetadata containing details for each import/export.
	 * @see SnapshotMetadata
	 */
	public ImportSnapshotApplicationEvent(Object source, String regionPath, SnapshotMetadata<K, V>... snapshotMetadata) {
		super(source, regionPath, snapshotMetadata);
	}

}
