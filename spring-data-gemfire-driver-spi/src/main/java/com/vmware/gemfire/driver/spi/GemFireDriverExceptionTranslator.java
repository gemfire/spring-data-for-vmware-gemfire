/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

public class GemFireDriverExceptionTranslator implements PersistenceExceptionTranslator {

	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if (ex instanceof GemFireDriverException driverException) {
			return translate(driverException);
		}
		return null;
	}

	protected DataAccessException translate(GemFireDriverException ex) {
		if (ex instanceof UnsupportedFeatureException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		if (ex instanceof DriverBindException) {
			return new DataAccessResourceFailureException(ex.getMessage(), ex);
		}
		if (ex instanceof DriverOperationException operation) {
			return translateOperation(operation);
		}
		return new UncategorizedDataAccessException(ex.getMessage(), ex) { };
	}

	/** Subclasses may retain framework-specific exception subtypes without inspecting native causes. */
	protected DataAccessException translateOperation(DriverOperationException ex) {
		String message = ex.getMessage();
		return switch (ex.kind()) {
			case DUPLICATE_ENTRY -> new DuplicateKeyException(message, ex);
			case ENTRY_NOT_FOUND -> new DataRetrievalFailureException(message, ex);
			case REGION_EXISTS, COMMIT_CONFLICT, COMMIT_INCOMPLETE, INDEX_FAILURE ->
				new DataIntegrityViolationException(message, ex);
			case LOCK_FAILURE -> new PessimisticLockingFailureException(message, ex);
			case AUTHENTICATION_FAILED, AUTHENTICATION_EXPIRED, NOT_AUTHORIZED, SECURITY_FAILURE ->
				new PermissionDeniedDataAccessException(message, ex);
			case CQ_CLOSED, ENTRY_DESTROYED, INVALID_ARGUMENT, INVALID_STATE, FUNCTION_FAILURE ->
				new InvalidDataAccessApiUsageException(message, ex);
			case CANCELLED, REGION_UNAVAILABLE, QUERY_INVALID, QUERY_FAILURE, QUERY_TIMEOUT, CQ_EXISTS, CQ_FAILURE ->
				new InvalidDataAccessResourceUsageException(message, ex);
			case RESOURCE_FAILURE -> new DataAccessResourceFailureException(message, ex);
			case TIMEOUT -> new QueryTimeoutException(message, ex);
			default -> new UncategorizedDataAccessException(message, ex) { };
		};
	}

}
