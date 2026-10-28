/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Map;
import org.apache.geode.GemFireCacheException;
import org.apache.geode.cache.*;
import org.apache.geode.cache.query.*;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.security.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

class GemFireExceptionMapperTests {

	private final GemFireExceptionMapper mapper = new GemFireExceptionMapper();

	@Test
	void classifiesNativeSubtypesIncludingCheckedFailuresWithoutExposingTheirMessages() {
		Map<Exception, DriverFailureKind> cases = Map.ofEntries(
			Map.entry(new EntryExistsException("secret", "old-value-secret"), DriverFailureKind.DUPLICATE_ENTRY),
			Map.entry(new EntryNotFoundException("secret"), DriverFailureKind.ENTRY_NOT_FOUND),
			Map.entry(new CommitConflictException("secret"), DriverFailureKind.COMMIT_CONFLICT),
			Map.entry(new CommitIncompleteException("secret"), DriverFailureKind.COMMIT_INCOMPLETE),
			Map.entry(new SynchronizationCommitConflictException("secret"), DriverFailureKind.LOCK_FAILURE),
			Map.entry(new TransactionWriterException("secret"), DriverFailureKind.TRANSACTION_VETO),
			Map.entry(new TransactionException("secret"), DriverFailureKind.TRANSACTION_FAILURE),
			Map.entry(new CacheClosedException("secret"), DriverFailureKind.CANCELLED),
			Map.entry(new DiskAccessException("secret"), DriverFailureKind.RESOURCE_FAILURE),
			Map.entry(new AuthenticationFailedException("secret"), DriverFailureKind.AUTHENTICATION_FAILED),
			Map.entry(new AuthenticationRequiredException("secret"), DriverFailureKind.AUTHENTICATION_FAILED),
			Map.entry(new AuthenticationExpiredException("secret"), DriverFailureKind.AUTHENTICATION_EXPIRED),
			Map.entry(new NotAuthorizedException("secret"), DriverFailureKind.NOT_AUTHORIZED),
			Map.entry(new QueryInvalidException("secret"), DriverFailureKind.QUERY_INVALID),
			Map.entry(new QueryExecutionTimeoutException("secret"), DriverFailureKind.QUERY_TIMEOUT),
			Map.entry(new QueryException("secret"), DriverFailureKind.QUERY_FAILURE),
			Map.entry(new IndexExistsException("secret"), DriverFailureKind.INDEX_FAILURE),
			Map.entry(new IndexInvalidException("secret"), DriverFailureKind.INDEX_FAILURE),
			Map.entry(new CqClosedException("secret"), DriverFailureKind.CQ_CLOSED),
			Map.entry(new CqExistsException("secret"), DriverFailureKind.CQ_EXISTS),
			Map.entry(new FunctionException("secret"), DriverFailureKind.FUNCTION_FAILURE),
			Map.entry(new CacheLoaderException("secret"), DriverFailureKind.LOADER_FAILURE),
			Map.entry(new CacheWriterException("secret"), DriverFailureKind.WRITER_VETO),
			Map.entry(new TimeoutException("secret"), DriverFailureKind.TIMEOUT),
			Map.entry(new IllegalArgumentException("secret"), DriverFailureKind.INVALID_ARGUMENT));
		cases.forEach((nativeFailure, expected) -> {
			var translated = mapper.translate("test", nativeFailure);
			assertThat(translated).isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(expected))
				.hasCause(nativeFailure).hasMessage("GemFire operation failed: test");
			assertThat(new GemFireDriverExceptionTranslator().translateExceptionIfPossible(translated))
				.hasCause(translated).hasMessage("GemFire operation failed: test");
		});
	}

	@Test
	void knownNativeWrapperUsesItsCacheFailureButArbitraryNestedCausesDoNotOverrideTheOuterFailure() {
		var duplicate = new EntryExistsException("secret", "old-secret");
		var nativeWrapper = new GemFireCacheException(duplicate);
		assertThat(mapper.translate("create", nativeWrapper))
			.isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.DUPLICATE_ENTRY))
			.isInstanceOfSatisfying(DriverEntryExistsException.class, failure -> assertThat(failure.getOldValue()).isEqualTo("old-secret"))
			.hasCause(nativeWrapper).hasRootCause(duplicate);
		var unknown = new RuntimeException("secret", duplicate);
		assertThat(mapper.translate("test", unknown)).isInstanceOfSatisfying(DriverOperationException.class,
			failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.UNKNOWN)).hasCause(unknown);
	}

	@Test
	void regionFailurePayloadsKeepPathsOutOfMessages() {
		var nativeFailure = new RegionDestroyedException("private-message", "/private-region");
		assertThat(mapper.translate("read", nativeFailure))
			.isInstanceOfSatisfying(DriverRegionUnavailableException.class, failure ->
				assertThat(failure.getRegionFullPath()).isEqualTo("/private-region"))
			.hasMessage("GemFire operation failed: read");
		assertThat(mapper.translate("lookup", new org.apache.geode.admin.RegionNotFoundException("private-message", "/private-region")))
			.isInstanceOfSatisfying(DriverRegionUnavailableException.class, failure ->
				assertThat(failure.getRegionFullPath()).isEqualTo("/private-region"));
	}

	@Test
	void alreadyTypedFailuresAndCallbackUnsupportedKeepIdentity() {
		for (GemFireDriverException failure : new GemFireDriverException[] {
			UnsupportedFeatureException.notIntroduced("test.feature"),
			UnsupportedFeatureException.removed("test.feature", "10.4.0"),
			new DriverOperationException(DriverFailureKind.WRITER_VETO, "Veto", null) }) {
			assertThat(mapper.translate("test", failure)).isSameAs(failure);
			assertThat(mapper.translate("test", new CacheLoaderException(failure))).isSameAs(failure);
			assertThat(mapper.translate("test", new CacheWriterException(failure))).isSameAs(failure);
		}
	}

	@Test
	void clientCacheCreationReusesCompatibleCacheAndClassifiesInvalidConfiguration() {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			assertThat(new ClientCacheFactory().set("log-level", "error").create()).isSameAs(cache);
			assertThatThrownBy(() -> new ClientCacheFactory().set("locators", "localhost[1]").create())
				.isInstanceOfSatisfying(IllegalStateException.class, failure ->
					assertThat(mapper.translate("create", failure))
						.isInstanceOfSatisfying(DriverOperationException.class, translated ->
							assertThat(translated.kind()).isEqualTo(DriverFailureKind.INVALID_STATE))
						.hasCause(failure).hasMessage("GemFire operation failed: create"));
		}
	}

	@Test
	void nativeWriterVetoCrossesRegionBoundaryAndDoesNotInsertAnEntry() {
		try (var cache = new ClientCacheFactory().set("mcast-port", "0").set("locators", "")
			.set("log-level", "error").create()) {
			Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.LOCAL).create("entries");
			var veto = new CacheWriterException("credential-secret");
			region.getAttributesMutator().setCacheWriter(new org.apache.geode.cache.util.CacheWriterAdapter<>() {
				@Override public void beforeCreate(EntryEvent<String, String> event) { throw veto; }
			});
			var driverRegion = new GemFireRegion<>(region);
			assertThatThrownBy(() -> driverRegion.put("key-secret", "value-secret"))
				.isInstanceOfSatisfying(DriverOperationException.class,
					failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.WRITER_VETO))
				.hasCause(veto).hasMessage("GemFire operation failed: put");
			assertThat(region.containsKey("key-secret")).isFalse();
		}
	}
}
