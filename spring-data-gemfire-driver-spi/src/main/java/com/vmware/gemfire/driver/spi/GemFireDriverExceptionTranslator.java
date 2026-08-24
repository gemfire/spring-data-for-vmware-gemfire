/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import org.springframework.dao.DataAccessException;
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

	private DataAccessException translate(GemFireDriverException ex) {
		if (ex instanceof UnsupportedCapabilityException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		if (ex instanceof DriverBindException) {
			return new DataAccessResourceFailureException(ex.getMessage(), ex);
		}
		return new UncategorizedDataAccessException(ex.getMessage(), ex) { };
	}

}
