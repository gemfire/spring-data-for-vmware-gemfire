// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.snapshot;

/**
 * The ImportSnapshotException class is a RuntimeException indicating an error occurred while loading GemFire Snapshots
 * into the GemFire Cache Regions.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class ImportSnapshotException extends RuntimeException {

	public ImportSnapshotException() {
	}

	public ImportSnapshotException(String message) {
		super(message);
	}

	public ImportSnapshotException(Throwable cause) {
		super(cause);
	}

	public ImportSnapshotException(String message, Throwable cause) {
		super(message, cause);
	}

}
