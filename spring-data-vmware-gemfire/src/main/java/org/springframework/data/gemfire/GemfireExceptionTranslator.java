/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.GemFireDriverExceptionTranslator;
import org.springframework.dao.DataAccessException;

/** Preserves Spring Data's exception subtypes using only SPI failure semantics. */
public class GemfireExceptionTranslator extends GemFireDriverExceptionTranslator {

	@Override
	protected DataAccessException translateOperation(DriverOperationException failure) {
		return switch (failure.kind()) {
			case QUERY_INVALID, QUERY_FAILURE, QUERY_TIMEOUT, CQ_EXISTS, CQ_FAILURE ->
				new GemfireQueryException(failure.getMessage(), failure);
			case INDEX_FAILURE -> new GemfireIndexException(failure.getMessage(), failure);
			case CANCELLED -> new GemfireCancellationException(failure.getMessage(), failure);
			case LOADER_FAILURE, WRITER_VETO, TRANSACTION_VETO, TRANSACTION_FAILURE, SYSTEM_FAILURE, UNKNOWN ->
				new GemfireSystemException(failure.getMessage(), failure);
			default -> super.translateOperation(failure);
		};
	}
}
