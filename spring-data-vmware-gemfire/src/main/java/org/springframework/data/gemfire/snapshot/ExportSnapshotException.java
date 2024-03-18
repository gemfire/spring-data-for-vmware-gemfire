/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.snapshot;

/**
 * The ExportSnapshotException class is a RuntimeException indicating an error occurred while saving a snapshhot
 * of GemFire's Cache Regions.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class ExportSnapshotException extends RuntimeException {

	public ExportSnapshotException() {
	}

	public ExportSnapshotException(String message) {
		super(message);
	}

	public ExportSnapshotException(Throwable cause) {
		super(cause);
	}

	public ExportSnapshotException(String message, Throwable cause) {
		super(message, cause);
	}

}
