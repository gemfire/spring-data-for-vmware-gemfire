/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.query.IndexCreationException;
import org.apache.geode.cache.query.IndexExistsException;
import org.apache.geode.cache.query.IndexInvalidException;
import org.apache.geode.cache.query.IndexMaintenanceException;
import org.apache.geode.cache.query.IndexNameConflictException;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * Gemfire-specific subclass thrown on Index management.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.apache.geode.cache.query.IndexCreationException
 * @see org.apache.geode.cache.query.IndexExistsException
 * @see org.apache.geode.cache.query.IndexInvalidException
 * @see org.apache.geode.cache.query.IndexMaintenanceException
 * @see org.apache.geode.cache.query.IndexNameConflictException
 */
@SuppressWarnings({ "serial", "unused" })
public class GemfireIndexException extends DataIntegrityViolationException {

	public GemfireIndexException(Exception cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, Exception cause) {
		super(message, cause);
	}

	public GemfireIndexException(IndexCreationException cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, IndexCreationException cause) {
		super(message, cause);
	}

	public GemfireIndexException(IndexExistsException cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, IndexExistsException cause) {
		super(message, cause);
	}

	public GemfireIndexException(IndexInvalidException cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, IndexInvalidException cause) {
		super(message, cause);
	}

	public GemfireIndexException(IndexMaintenanceException cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, IndexMaintenanceException cause) {
		super(message, cause);
	}

	public GemfireIndexException(IndexNameConflictException cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, IndexNameConflictException cause) {
		super(message, cause);
	}
}
