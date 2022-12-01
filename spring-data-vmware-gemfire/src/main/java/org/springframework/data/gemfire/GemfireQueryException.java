/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.query.QueryException;
import org.apache.geode.cache.query.QueryExecutionTimeoutException;
import org.apache.geode.cache.query.QueryInvalidException;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * GemFire-specific subclass of {@link InvalidDataAccessResourceUsageException} thrown on invalid
 * OQL query syntax.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireQueryException extends InvalidDataAccessResourceUsageException {

	public GemfireQueryException(String message, QueryException ex) {
		super(message, ex);
	}

	public GemfireQueryException(QueryException ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireQueryException(String message, QueryExecutionTimeoutException ex) {
		super(message, ex);
	}

	public GemfireQueryException(QueryExecutionTimeoutException ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireQueryException(String message, QueryInvalidException ex) {
		super(message, ex);
	}

	public GemfireQueryException(QueryInvalidException ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireQueryException(String message, RuntimeException ex) {
		super(message, ex);
	}

	public GemfireQueryException(RuntimeException ex) {
		super(ex.getMessage(), ex);
	}
}
