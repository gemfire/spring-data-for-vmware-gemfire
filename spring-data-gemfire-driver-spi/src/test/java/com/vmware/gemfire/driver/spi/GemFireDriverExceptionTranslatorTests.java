/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.dao.*;

class GemFireDriverExceptionTranslatorTests {

	private final GemFireDriverExceptionTranslator translator = new GemFireDriverExceptionTranslator();

	@Test
	void systemFailuresRetainTheirClassificationWithoutNativeTypes() {
		var root = new IllegalArgumentException("trusted-secret");
		var failure = new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "Runtime failed", root);
		assertThat(failure.kind()).isEqualTo(DriverFailureKind.SYSTEM_FAILURE);
		assertThat(failure.getRootCause()).isSameAs(root);
		assertThat(failure.toString()).doesNotContain("trusted-secret");
		assertThat(translator.translateExceptionIfPossible(failure))
			.isInstanceOf(UncategorizedDataAccessException.class)
			.hasMessage("Runtime failed").hasCause(failure).hasRootCause(root);
	}

	@Test
	void callbackFailuresExposeTheirRootCauseWithoutLoopingOnCyclicDiagnostics() {
		var root = new IllegalArgumentException("trusted detail");
		var cause = new IllegalStateException("trusted detail", root);
		var failure = new DriverOperationException(DriverFailureKind.WRITER_VETO, "Rejected", cause);
		assertThat(failure.getRootCause()).isSameAs(root);
		assertThat(new DriverOperationException(DriverFailureKind.LOADER_FAILURE, "Failed", null).getRootCause()).isNull();
		root.initCause(cause);
		assertThat(failure.getRootCause()).isSameAs(root);
	}

	@Test
	void translatesDistinctFailuresAndRetainsSafeMessageAndCause() {
		Map<DriverFailureKind, Class<? extends DataAccessException>> cases = Map.ofEntries(
			Map.entry(DriverFailureKind.DUPLICATE_ENTRY, DuplicateKeyException.class),
			Map.entry(DriverFailureKind.ENTRY_NOT_FOUND, DataRetrievalFailureException.class),
			Map.entry(DriverFailureKind.COMMIT_CONFLICT, DataIntegrityViolationException.class),
			Map.entry(DriverFailureKind.COMMIT_INCOMPLETE, DataIntegrityViolationException.class),
			Map.entry(DriverFailureKind.LOCK_FAILURE, PessimisticLockingFailureException.class),
			Map.entry(DriverFailureKind.NOT_AUTHORIZED, PermissionDeniedDataAccessException.class),
			Map.entry(DriverFailureKind.AUTHENTICATION_EXPIRED, PermissionDeniedDataAccessException.class),
			Map.entry(DriverFailureKind.RESOURCE_FAILURE, DataAccessResourceFailureException.class),
			Map.entry(DriverFailureKind.QUERY_INVALID, InvalidDataAccessResourceUsageException.class),
			Map.entry(DriverFailureKind.FUNCTION_FAILURE, InvalidDataAccessApiUsageException.class),
			Map.entry(DriverFailureKind.INVALID_ARGUMENT, InvalidDataAccessApiUsageException.class));
		RuntimeException original = new IllegalStateException("credential-secret");
		cases.forEach((kind, expected) -> {
			var failure = new DriverOperationException(kind, "Safe operation failure", original);
			assertThat(translator.translateExceptionIfPossible(failure)).isExactlyInstanceOf(expected)
				.hasMessage("Safe operation failure").hasCause(failure).hasRootCause(original);
		});
	}

	@Test
	void unsupportedAndBindFailuresRemainSeparateFromExecution() {
		for (var unsupported : new UnsupportedFeatureException[] {
			UnsupportedFeatureException.notIntroduced("test.feature"),
			UnsupportedFeatureException.removed("test.feature", "10.4.0") }) {
			assertThat(translator.translateExceptionIfPossible(unsupported))
				.isInstanceOf(InvalidDataAccessApiUsageException.class).hasCause(unsupported);
		}
		var bind = new DriverBindException("Safe bind failure", new IllegalStateException("secret"));
		assertThat(translator.translateExceptionIfPossible(bind))
			.isInstanceOf(DataAccessResourceFailureException.class).hasCause(bind).hasMessage("Safe bind failure");
	}

	@Test
	void legacyUnknownAndUnrelatedErrorsDoNotAcquireGuessedSemantics() {
		var failure = new DriverOperationException("Unclassified failure", new IllegalStateException("secret"));
		assertThat(failure.kind()).isEqualTo(DriverFailureKind.UNKNOWN);
		assertThat(translator.translateExceptionIfPossible(failure)).isInstanceOf(UncategorizedDataAccessException.class);
		assertThat(translator.translateExceptionIfPossible(new IllegalStateException("unknown", failure))).isNull();
		assertThat(translator.translateExceptionIfPossible(new DuplicateKeyException("already translated"))).isNull();
		assertThatThrownBy(() -> new DriverOperationException(null, "failure", null)).isInstanceOf(NullPointerException.class);
	}
}
