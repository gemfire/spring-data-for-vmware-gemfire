/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Stable execution failure distinctions, independent of native exception classes and messages.
 * These describe the reported failure, not a retry policy or a guarantee that no side effects occurred.
 * Unsupported features and driver binding failures have their own exception contracts.
 */
public enum DriverFailureKind {
	DUPLICATE_ENTRY, ENTRY_NOT_FOUND, REGION_EXISTS,
	COMMIT_CONFLICT, COMMIT_INCOMPLETE, TRANSACTION_FAILURE, TRANSACTION_VETO,
	CANCELLED, CQ_CLOSED, CQ_EXISTS, CQ_FAILURE, ENTRY_DESTROYED, REGION_UNAVAILABLE,
	RESOURCE_FAILURE, LOCK_FAILURE, INVALID_ARGUMENT, INVALID_STATE,
	AUTHENTICATION_FAILED, AUTHENTICATION_EXPIRED, NOT_AUTHORIZED, SECURITY_FAILURE,
	QUERY_INVALID, QUERY_FAILURE, QUERY_TIMEOUT, INDEX_FAILURE, FUNCTION_FAILURE,
	LOADER_FAILURE, WRITER_VETO, TIMEOUT, SYSTEM_FAILURE, UNKNOWN
}
