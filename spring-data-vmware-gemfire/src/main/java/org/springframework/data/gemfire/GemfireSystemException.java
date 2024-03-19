/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.GemFireCheckedException;
import org.apache.geode.GemFireException;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * GemFire-specific subclass of UncategorizedDataAccessException, for GemFire system errors that do not match any concrete <code>org.springframework.dao</code> exceptions.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireSystemException extends UncategorizedDataAccessException {

	public GemfireSystemException(GemFireCheckedException ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireSystemException(GemFireException ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireSystemException(RuntimeException ex) {
		super(ex.getMessage(), ex);
	}
}
