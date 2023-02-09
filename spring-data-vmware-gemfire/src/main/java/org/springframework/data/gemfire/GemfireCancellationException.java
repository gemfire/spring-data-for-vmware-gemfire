/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.CancelException;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * GemFire-specific class for exceptions caused by system cancellations.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireCancellationException extends InvalidDataAccessResourceUsageException {

	public GemfireCancellationException(CancelException ex) {
		super(ex.getMessage(), ex);
	}
}
