/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Repository;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

class GemfireExceptionTranslatorTests {

	@Test
	void retainsSpringSubtypesAndOriginalCauseWithoutNativeDependencies() {
		var translator = new GemfireExceptionTranslator();
		Map<DriverFailureKind, Class<? extends DataAccessException>> cases = Map.ofEntries(
			Map.entry(DriverFailureKind.QUERY_INVALID, GemfireQueryException.class),
			Map.entry(DriverFailureKind.QUERY_TIMEOUT, GemfireQueryException.class),
			Map.entry(DriverFailureKind.INDEX_FAILURE, GemfireIndexException.class),
			Map.entry(DriverFailureKind.CANCELLED, GemfireCancellationException.class),
			Map.entry(DriverFailureKind.LOADER_FAILURE, GemfireSystemException.class),
			Map.entry(DriverFailureKind.WRITER_VETO, GemfireSystemException.class),
			Map.entry(DriverFailureKind.UNKNOWN, GemfireSystemException.class),
			Map.entry(DriverFailureKind.DUPLICATE_ENTRY, DuplicateKeyException.class),
			Map.entry(DriverFailureKind.NOT_AUTHORIZED, PermissionDeniedDataAccessException.class));
		var original = new IllegalStateException("credential-secret");
		cases.forEach((kind, type) -> {
			var failure = new DriverOperationException(kind, "Safe operation failure", original);
			assertThat(translator.translateExceptionIfPossible(failure)).isExactlyInstanceOf(type)
				.hasMessage("Safe operation failure").hasCause(failure).hasRootCause(original);
		});
	}

	@Test
	void connectionFactorySuppliesTranslationToSpringRepositoryAdvice() {
		var original = new IllegalStateException("credential-secret");
		var failure = new DriverOperationException(DriverFailureKind.DUPLICATE_ENTRY, "Duplicate entry", original);
		try (var context = new GenericApplicationContext()) {
			context.registerBean("exceptions", PersistenceExceptionTranslationPostProcessor.class);
			context.registerBean("connection", DriverConnectionFactoryBean.class,
				() -> new DriverConnectionFactoryBean(new DriverConnection() { }));
			context.registerBean("repository", FailingRepository.class, () -> new FailingRepository(failure));
			context.refresh();
			assertThatThrownBy(() -> context.getBean(FailingRepository.class).save())
				.isInstanceOf(DuplicateKeyException.class).hasCause(failure).hasRootCause(original)
				.hasMessage("Duplicate entry");
			var factory = context.getBean("&connection", DriverConnectionFactoryBean.class);
			assertThat(factory.translateExceptionIfPossible(new IllegalStateException("unrelated"))).isNull();
		}
	}

	@Repository
	public static class FailingRepository {
		private final RuntimeException failure;
		public FailingRepository(RuntimeException failure) { this.failure = failure; }
		public void save() { throw failure; }
	}
}
