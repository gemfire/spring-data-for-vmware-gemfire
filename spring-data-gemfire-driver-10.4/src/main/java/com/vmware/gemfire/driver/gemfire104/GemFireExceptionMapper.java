/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.GemFireDriverException;

/** Native exception inspection belongs here. Never classify by message or by arbitrary root cause. */
final class GemFireExceptionMapper {

	/** Operation is a driver-owned constant, never a key, query, endpoint or other caller value. */
	GemFireDriverException translate(String operation, Exception failure) {
		if (failure instanceof GemFireDriverException driverFailure) return driverFailure;
		// Native callback wrappers may enclose an already classified SPI failure (including unsupported).
		if ((failure instanceof org.apache.geode.cache.CacheLoaderException
				|| failure instanceof org.apache.geode.cache.CacheWriterException)
				&& failure.getCause() instanceof GemFireDriverException driverFailure) return driverFailure;
		// The engine wraps transaction-writer vetoes in CommitConflictException. Inspect only this documented wrapper.
		if (failure instanceof org.apache.geode.cache.CommitConflictException
				&& failure.getCause() instanceof org.apache.geode.cache.TransactionWriterException writer
				&& writer.getCause() instanceof GemFireDriverException driverFailure) return driverFailure;
		Exception payload = failure instanceof org.apache.geode.GemFireCacheException wrapped && wrapped.getCacheException() != null
			? wrapped.getCacheException() : failure;
		if (payload instanceof org.apache.geode.cache.EntryExistsException e)
			return new DriverEntryExistsException("GemFire operation failed: " + operation, e.getOldValue(), failure);
		if (payload instanceof org.apache.geode.cache.RegionExistsException e)
			return new DriverRegionExistsException("GemFire operation failed: " + operation, e.getRegion() == null ? null : new GemFireRegion<>(e.getRegion()), failure);
		if (payload instanceof org.apache.geode.cache.RegionDestroyedException e)
			return new DriverRegionUnavailableException("GemFire operation failed: " + operation, e.getRegionFullPath(), failure);
		if (payload instanceof org.apache.geode.admin.RegionNotFoundException e)
			return new DriverRegionUnavailableException("GemFire operation failed: " + operation, e.getRegionFullPath(), failure);
		return new DriverOperationException(classify(failure), "GemFire operation failed: " + operation, failure);
	}

	@SuppressWarnings("deprecation")
	private DriverFailureKind classify(Exception failure) {
		// This native wrapper explicitly designates its cache exception as the failure.
		if (failure instanceof org.apache.geode.GemFireCacheException wrapped
				&& wrapped.getCacheException() != null) return classify(wrapped.getCacheException());
		if (failure instanceof org.apache.geode.cache.EntryExistsException) return DriverFailureKind.DUPLICATE_ENTRY;
		if (failure instanceof org.apache.geode.cache.EntryNotFoundException) return DriverFailureKind.ENTRY_NOT_FOUND;
		if (failure instanceof org.apache.geode.cache.RegionExistsException) return DriverFailureKind.REGION_EXISTS;
		if (failure instanceof org.apache.geode.cache.SynchronizationCommitConflictException
				|| failure instanceof org.apache.geode.cache.FailedSynchronizationException
				|| failure instanceof org.apache.geode.distributed.LeaseExpiredException) return DriverFailureKind.LOCK_FAILURE;
		if (failure instanceof org.apache.geode.cache.CommitConflictException
				&& failure.getCause() instanceof org.apache.geode.cache.TransactionWriterException) return DriverFailureKind.TRANSACTION_VETO;
		if (failure instanceof org.apache.geode.cache.CommitConflictException) return DriverFailureKind.COMMIT_CONFLICT;
		if (failure instanceof org.apache.geode.cache.CommitIncompleteException) return DriverFailureKind.COMMIT_INCOMPLETE;
		if (failure instanceof org.apache.geode.cache.TransactionWriterException) return DriverFailureKind.TRANSACTION_VETO;
		if (failure instanceof org.apache.geode.cache.TransactionException) return DriverFailureKind.TRANSACTION_FAILURE;
		if (failure instanceof org.apache.geode.CancelException) return DriverFailureKind.CANCELLED;
		if (failure instanceof org.apache.geode.cache.query.CqClosedException) return DriverFailureKind.CQ_CLOSED;
		if (failure instanceof org.apache.geode.cache.query.CqExistsException) return DriverFailureKind.CQ_EXISTS;
		if (failure instanceof org.apache.geode.cache.query.CqException) return DriverFailureKind.CQ_FAILURE;
		if (failure instanceof org.apache.geode.cache.EntryDestroyedException) return DriverFailureKind.ENTRY_DESTROYED;
		if (failure instanceof org.apache.geode.cache.RegionDestroyedException
				|| failure instanceof org.apache.geode.admin.RegionNotFoundException) return DriverFailureKind.REGION_UNAVAILABLE;
		if (failure instanceof org.apache.geode.security.AuthenticationExpiredException) return DriverFailureKind.AUTHENTICATION_EXPIRED;
		if (failure instanceof org.apache.geode.security.AuthenticationFailedException
				|| failure instanceof org.apache.geode.security.AuthenticationRequiredException) return DriverFailureKind.AUTHENTICATION_FAILED;
		if (failure instanceof org.apache.geode.security.NotAuthorizedException) return DriverFailureKind.NOT_AUTHORIZED;
		if (failure instanceof org.apache.geode.security.GemFireSecurityException) return DriverFailureKind.SECURITY_FAILURE;
		if (failure instanceof org.apache.geode.cache.query.IndexInvalidException
				|| failure instanceof org.apache.geode.cache.query.IndexMaintenanceException
				|| failure instanceof org.apache.geode.cache.query.IndexCreationException
				|| failure instanceof org.apache.geode.cache.query.IndexExistsException
				|| failure instanceof org.apache.geode.cache.query.IndexNameConflictException
				|| failure instanceof org.apache.geode.cache.query.MultiIndexCreationException) return DriverFailureKind.INDEX_FAILURE;
		if (failure instanceof org.apache.geode.cache.query.QueryExecutionTimeoutException) return DriverFailureKind.QUERY_TIMEOUT;
		if (failure instanceof org.apache.geode.cache.query.QueryInvalidException) return DriverFailureKind.QUERY_INVALID;
		if (failure instanceof org.apache.geode.cache.query.QueryException) return DriverFailureKind.QUERY_FAILURE;
		if (failure instanceof org.apache.geode.cache.CacheLoaderException) return DriverFailureKind.LOADER_FAILURE;
		if (failure instanceof org.apache.geode.cache.CacheWriterException) return DriverFailureKind.WRITER_VETO;
		if (failure instanceof org.apache.geode.cache.execute.FunctionException) return DriverFailureKind.FUNCTION_FAILURE;
		if (failure instanceof org.apache.geode.cache.TimeoutException) return DriverFailureKind.TIMEOUT;
		if (failure instanceof org.apache.geode.cache.DiskAccessException
				|| failure instanceof org.apache.geode.cache.OperationAbortedException
				|| failure instanceof org.apache.geode.cache.ResourceException
				|| failure instanceof org.apache.geode.GemFireIOException
				|| failure instanceof org.apache.geode.cache.client.ServerConnectivityException) return DriverFailureKind.RESOURCE_FAILURE;
		if (failure instanceof org.apache.geode.GemFireException
				|| failure instanceof org.apache.geode.GemFireCheckedException
				|| failure instanceof org.apache.geode.json.JsonParseException) return DriverFailureKind.SYSTEM_FAILURE;
		if (failure instanceof java.lang.IllegalArgumentException
				|| failure instanceof java.lang.NullPointerException) return DriverFailureKind.INVALID_ARGUMENT;
		if (failure instanceof java.lang.IllegalStateException) return DriverFailureKind.INVALID_STATE;
		return DriverFailureKind.UNKNOWN;
	}
}
