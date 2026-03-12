/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudCacheException;
import org.springframework.data.gemfire.gud.api.GudCacheExistsException;
import org.springframework.data.gemfire.gud.api.GudCacheLoaderException;
import org.springframework.data.gemfire.gud.api.GudCacheRuntimeException;
import org.springframework.data.gemfire.gud.api.GudCacheWriterException;
import org.springframework.data.gemfire.gud.api.GudCacheXmlException;
import org.springframework.data.gemfire.gud.api.GudCancelException;
import org.springframework.data.gemfire.gud.api.GudCommitConflictException;
import org.springframework.data.gemfire.gud.api.GudCommitIncompleteException;
import org.springframework.data.gemfire.gud.api.GudCopyException;
import org.springframework.data.gemfire.gud.api.GudCqClosedException;
import org.springframework.data.gemfire.gud.api.GudDiskAccessException;
import org.springframework.data.gemfire.gud.api.GudEntryDestroyedException;
import org.springframework.data.gemfire.gud.api.GudEntryExistsException;
import org.springframework.data.gemfire.gud.api.GudEntryNotFoundException;
import org.springframework.data.gemfire.gud.api.GudFailedSynchronizationException;
import org.springframework.data.gemfire.gud.api.GudFunctionException;
import org.springframework.data.gemfire.gud.api.GudGemFireCacheException;
import org.springframework.data.gemfire.gud.api.GudGemFireCheckedException;
import org.springframework.data.gemfire.gud.api.GudGemFireConfigException;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudGemFireIOException;
import org.springframework.data.gemfire.gud.api.GudGemFireSecurityException;
import org.springframework.data.gemfire.gud.api.GudIncompatibleSystemException;
import org.springframework.data.gemfire.gud.api.GudIndexInvalidException;
import org.springframework.data.gemfire.gud.api.GudIndexMaintenanceException;
import org.springframework.data.gemfire.gud.api.GudInternalGemFireException;
import org.springframework.data.gemfire.gud.api.GudInvalidValueException;
import org.springframework.data.gemfire.gud.api.GudLeaseExpiredException;
import org.springframework.data.gemfire.gud.api.GudNoSystemException;
import org.springframework.data.gemfire.gud.api.GudOperationAbortedException;
import org.springframework.data.gemfire.gud.api.GudQueryException;
import org.springframework.data.gemfire.gud.api.GudQueryExecutionTimeoutException;
import org.springframework.data.gemfire.gud.api.GudQueryInvalidException;
import org.springframework.data.gemfire.gud.api.GudRegionDestroyedException;
import org.springframework.data.gemfire.gud.api.GudRegionExistsException;
import org.springframework.data.gemfire.gud.api.GudResourceException;
import org.springframework.data.gemfire.gud.api.GudRoleException;
import org.springframework.data.gemfire.gud.api.GudServerConnectivityException;
import org.springframework.data.gemfire.gud.api.GudStatisticsDisabledException;
import org.springframework.data.gemfire.gud.api.GudSynchronizationCommitConflictException;
import org.springframework.data.gemfire.gud.api.GudSystemConnectException;
import org.springframework.data.gemfire.gud.api.GudSystemIsRunningException;
import org.springframework.data.gemfire.gud.api.GudUnmodifiableException;
import org.springframework.data.gemfire.gud.api.GudVersionException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.util.ClassUtils;

/**
 * Abstract utility class featuring methods for Apache Geode / Pivotal GemFire Cache or Region handling.
 *
 * @author Costin Leau
 * @author John Blum
 */
public abstract class GemfireCacheUtils {

	private static Class<?> CQ_EXCEPTION_CLASS;

	static {

		Class<?> type = null;

		try {
			type = ClassUtils.resolveClassName("org.springframework.data.gemfire.gud.api.GudCqInvalidException",
				GemfireCacheUtils.class.getClassLoader());
		}
		catch (IllegalArgumentException ignore) { }

		CQ_EXCEPTION_CLASS = type;
	}


	/**
	 * Converts the given (unchecked) Gemfire exception to an appropriate one from the
	 * <code>org.springframework.dao</code> hierarchy.
	 *
	 * @param cause Gemfire unchecked exception
	 * @return new the corresponding DataAccessException instance
	 */
	public static DataAccessException convertGemfireAccessException(GudGemFireException cause) {

		if (cause instanceof GudCacheException) {
			if (cause instanceof GudCacheExistsException) {
				return new DataIntegrityViolationException(cause.getMessage(), cause);
			}
			if (cause instanceof GudCommitConflictException) {
				return new DataIntegrityViolationException(cause.getMessage(), cause);
			}
			if (cause instanceof GudCommitIncompleteException) {
				return new DataIntegrityViolationException(cause.getMessage(), cause);
			}
			if (cause instanceof GudEntryExistsException) {
				return new DuplicateKeyException(cause.getMessage(), cause);
			}
			if (cause instanceof GudEntryNotFoundException) {
				return new DataRetrievalFailureException(cause.getMessage(), cause);
			}
			if (cause instanceof GudRegionExistsException) {
				return new DataIntegrityViolationException(cause.getMessage(), cause);
			}
		}

		if (cause instanceof GudCacheRuntimeException) {
			if (cause instanceof GudCacheXmlException) {
				return new GemfireSystemException(cause);
			}
			if (cause instanceof GudCancelException) {
				return new GemfireCancellationException(cause);
			}
			if (cause instanceof GudCqClosedException) {
				return new InvalidDataAccessApiUsageException(cause.getMessage(), cause);
			}
			if (cause instanceof GudDiskAccessException) {
				return new DataAccessResourceFailureException(cause.getMessage(), cause);
			}
			if (cause instanceof GudEntryDestroyedException) {
				return new InvalidDataAccessApiUsageException(cause.getMessage(), cause);
			}
			if (cause instanceof GudFailedSynchronizationException) {
				return new PessimisticLockingFailureException(cause.getMessage(), cause);
			}
			if (cause instanceof GudIndexMaintenanceException) {
				return new GemfireIndexException((Exception) cause);
			}
			if (cause instanceof GudOperationAbortedException) {
				if (cause instanceof GudCacheLoaderException) {
					return new GemfireSystemException(cause);
				}
				if (cause instanceof GudCacheWriterException) {
					return new GemfireSystemException(cause);
				}
				return new DataAccessResourceFailureException(cause.getMessage(), cause);
			}
			if (cause instanceof GudQueryExecutionTimeoutException) {
				return new GemfireQueryException((Exception) cause);
			}
			if (cause instanceof GudRegionDestroyedException) {
				return new InvalidDataAccessResourceUsageException(cause.getMessage(), cause);
			}
			if (cause instanceof GudResourceException) {
				return new DataAccessResourceFailureException(cause.getMessage(), cause);
			}
			if (cause instanceof GudRoleException) {
				return new GemfireSystemException(cause);
			}
			if (cause instanceof GudStatisticsDisabledException) {
				return new GemfireSystemException(cause);
			}
			if (cause instanceof GudSynchronizationCommitConflictException) {
				return new PessimisticLockingFailureException(cause.getMessage(), cause);
			}
		}

		if (cause instanceof GudCopyException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudFunctionException) {
			return new InvalidDataAccessApiUsageException(cause.getMessage(), cause);
		}
		if (cause instanceof GudGemFireCacheException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudGemFireConfigException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudGemFireIOException) {
			return new DataAccessResourceFailureException(cause.getMessage(), cause);
		}
		if (cause instanceof GudGemFireSecurityException) {
			return new PermissionDeniedDataAccessException(cause.getMessage(), cause);
		}
		if (cause instanceof GudIncompatibleSystemException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudInternalGemFireException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudInvalidValueException) {
			return new TypeMismatchDataAccessException(cause.getMessage(), cause);
		}
		if (cause instanceof GudLeaseExpiredException) {
			return new PessimisticLockingFailureException(cause.getMessage(), cause);
		}
		if (cause instanceof GudNoSystemException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudServerConnectivityException) {
			return new DataAccessResourceFailureException(cause.getMessage(), cause);
		}
		if (cause instanceof GudSystemConnectException) {
			return new DataAccessResourceFailureException(cause.getMessage(), cause);
		}
		if (cause instanceof GudSystemIsRunningException) {
			return new GemfireSystemException(cause);
		}
		if (cause instanceof GudUnmodifiableException) {
			return new GemfireSystemException(cause);
		}

		// for exceptions that had their parent changed in 6.5
		DataAccessException dataAccessException = convertQueryExceptions(cause);

		if (dataAccessException != null) {
			return dataAccessException;
		}

		return new GemfireSystemException(cause);
	}

	/**
	 * Converts the given (checked) Gemfire exception to an appropriate one from the
	 * <code>org.springframework.dao</code> hierarchy.
	 *
	 * @param cause Gemfire checked exception
	 * @return new the corresponding DataAccessException instance
	 */
	public static DataAccessException convertGemfireAccessException(GudGemFireCheckedException cause) {

		if (cause instanceof GudQueryException) {
			return new GemfireQueryException((Exception) cause);
		}

		if (cause instanceof GudVersionException) {
			return new DataAccessResourceFailureException(cause.getMessage(), cause);
		}

		return new GemfireSystemException(cause);
	}

	/**
	 * Converts the given (unchecked) Gemfire exception to an appropriate one from the
	 * <code>org.springframework.dao</code> hierarchy. This method exists to handle backwards compatibility
	 * for exceptions that had their parents changed in GemFire 6.5.
	 *
	 * @param cause Gemfire unchecked exception
	 * @return new the corresponding DataAccessException instance
	 */
	public static DataAccessException convertGemfireAccessException(GudIndexInvalidException cause) {
		return new GemfireIndexException((Exception) cause);
	}

	/**
	 * Converts the given (unchecked) Gemfire exception to an appropriate one from the
	 * <code>org.springframework.dao</code> hierarchy. This method exists to handle backwards compatibility
	 * for exceptions that had their parents changed in GemFire 6.5.
	 *
	 * @param cause Gemfire unchecked exception
	 * @return new the corresponding DataAccessException instance
	 */
	public static DataAccessException convertGemfireAccessException(GudQueryInvalidException cause) {
		return new GemfireQueryException((Exception) cause);
	}

	/**
	 * Package protected method for detecting CqInvalidException which has been removed in GemFire 6.5 GA.
	 */
	static boolean isCqInvalidException(RuntimeException cause) {
		return CQ_EXCEPTION_CLASS != null && CQ_EXCEPTION_CLASS.isInstance(cause);
	}

	/**
	 * Dedicated method for converting exceptions changed in 6.5 that had their
	 * parent changed. This method exists to 'fool' the compiler type checks
	 * by loosening the type so the code compiles on both 6.5 (pre and current) branches.
	 */
	static DataAccessException convertQueryExceptions(RuntimeException cause) {

		if (cause instanceof GudIndexInvalidException) {
			return convertGemfireAccessException((GudIndexInvalidException) cause);
		}

		if (cause instanceof GudQueryInvalidException) {
			return convertGemfireAccessException((GudQueryInvalidException) cause);
		}

		if (isCqInvalidException(cause)) {
			return convertCqInvalidException(cause);
		}

		return new GemfireSystemException(cause);
	}

	/**
	 * Converts the given (unchecked) Gemfire exception to an appropriate one from the
	 * <code>org.springframework.dao</code> hierarchy. This method exists to handle backwards compatibility
	 * for exceptions that have been removed in 6.5.
	 *
	 * @param cause Gemfire unchecked exception
	 * @return new the corresponding DataAccessException instance
	 */
	static DataAccessException convertCqInvalidException(RuntimeException cause) {
		return new GemfireQueryException(cause);
	}

}
