/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.geode.CancelCriterion;
import org.apache.geode.SystemFailure;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.DistributedSystemDisconnectedException;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.ClientConfigurationProperties;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

class NativeClientRuntimeTests {

	@Test
	void legacyConnectionIdIsNotTheConfiguredDistributedSystemId() {
		var config = new CacheConfiguration(Map.of(
			"mcast-port", "0",
			ClientConfigurationProperties.LOCATORS, "",
			ClientConfigurationProperties.LOG_LEVEL, "error",
			ClientConfigurationProperties.NAME, "named-client",
			ClientConfigurationProperties.DISTRIBUTED_SYSTEM_ID, "17"));
		try (var connection = new GemFire104Driver().connect(config)) {
			var system = ((GemFireConnection) connection).cacheForTransactionCallback().getDistributedSystem();
			assertThat(connection.getRuntime().getLegacyId()).isEqualTo(system.getId()).isZero();
			assertThat(connection.getRuntime().getName()).isEqualTo(system.getName()).isEqualTo("named-client");
			assertThat(connection.getRuntime().getProperties().properties())
				.containsEntry(ClientConfigurationProperties.DISTRIBUTED_SYSTEM_ID, "17");
		}
	}

	@Test
	void identityReadFailuresAreTranslatedAndFatalErrorsPropagate() {
		var system = mock(DistributedSystem.class);
		when(system.getCancelCriterion()).thenReturn(CancelCriterion.NONE);
		var runtime = new GemFireClientRuntime(system);
		var cause = new IllegalStateException("private-token");
		when(system.getName()).thenThrow(cause);
		when(system.getId()).thenThrow(cause);
		for (Runnable read : java.util.List.<Runnable>of(runtime::getName, runtime::getLegacyId)) {
			assertThatThrownBy(read::run).isInstanceOfSatisfying(DriverOperationException.class, failure -> {
				assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
				assertThat(failure.getCause()).isSameAs(cause);
				assertThat(failure.getMessage()).doesNotContain("private-token");
			});
		}
		var fatal = new LinkageError("fatal");
		org.mockito.Mockito.doThrow(fatal).when(system).getName();
		org.mockito.Mockito.doThrow(fatal).when(system).getId();
		assertThatThrownBy(runtime::getName).isSameAs(fatal);
		assertThatThrownBy(runtime::getLegacyId).isSameAs(fatal);
	}

	@Test
	void propertyReadbackMatchesEffectiveNativeConfigurationIncludingDefaults() {
		var config = new CacheConfiguration(Map.of(
			"mcast-port", "0",
			ClientConfigurationProperties.LOCATORS, "",
			ClientConfigurationProperties.LOG_LEVEL, "error",
			ClientConfigurationProperties.NAME, "runtime-properties",
			"security-username", "private-user",
			"security-password", "private-token"));
		try (var connection = new GemFire104Driver().connect(config)) {
			var system = ((GemFireConnection) connection).cacheForTransactionCallback().getDistributedSystem();
			var runtime = connection.getRuntime();
			var expected = system.getProperties();
			assertThat(runtime.getProperties().properties()).isEqualTo(expected);
			assertThat(runtime.getProperties().properties()).containsKey(ClientConfigurationProperties.MEMBER_TIMEOUT)
				.containsEntry(ClientConfigurationProperties.NAME, "runtime-properties");
			assertThat(runtime.getSecurityProperties().properties()).isEqualTo(system.getSecurityProperties());
			runtime.getSecurityProperties().properties().clear();
			assertThat(system.getSecurityProperties()).containsEntry("security-password", "private-token");
			connection.close();
			assertThat(runtime.getProperties().properties()).isEqualTo(system.getProperties());
			assertThat(runtime.getSecurityProperties().properties()).isEqualTo(system.getSecurityProperties());
		}
	}

	@Test
	void securitySnapshotDetachesStubbedPropertiesAndTranslatesReadFailures() {
		var system = mock(DistributedSystem.class);
		when(system.getCancelCriterion()).thenReturn(CancelCriterion.NONE);
		var source = new java.util.Properties();
		source.setProperty("security-password", "private-token");
		// Synthetic mutable source; the pinned native implementation already returns a copy.
		when(system.getSecurityProperties()).thenReturn(source);
		var runtime = new GemFireClientRuntime(system);
		var snapshot = runtime.getSecurityProperties();
		source.setProperty("security-password", "replacement");
		assertThat(snapshot.properties()).containsEntry("security-password", "private-token");
		assertThat(runtime.getSecurityProperties().properties()).containsEntry("security-password", "replacement");
		var cause = new IllegalStateException("private-diagnostic-token");
		when(system.getProperties()).thenThrow(cause);
		when(system.getSecurityProperties()).thenThrow(cause);
		for (Runnable read : java.util.List.<Runnable>of(runtime::getProperties, runtime::getSecurityProperties)) {
			assertThatThrownBy(read::run).isInstanceOfSatisfying(DriverOperationException.class, failure -> {
				assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
				assertThat(failure.getCause()).isSameAs(cause);
				assertThat(failure.getMessage()).doesNotContain("private-diagnostic-token");
			});
		}
	}

	@Test
	void neverCancelledStillChecksNativeJvmFailure() {
		var system = mock(DistributedSystem.class);
		when(system.getCancelCriterion()).thenReturn(CancelCriterion.NONE);
		var cancellation = new GemFireClientRuntime(system).noCancellation();
		var fatal = new InternalError("synthetic JVM failure");
		try (var failures = mockStatic(SystemFailure.class)) {
			failures.when(SystemFailure::getFailure).thenReturn(fatal);
			failures.when(SystemFailure::checkFailure).thenThrow(fatal);
			assertThat(cancellation.cancelInProgress()).isNull();
			assertThat(cancellation.isCancelInProgress()).isFalse();
			assertThat(cancellation.generateCancelledException(fatal)).isNull();
			assertThatThrownBy(cancellation::checkCancelInProgress).isSameAs(fatal);
			assertThatThrownBy(() -> cancellation.checkCancelInProgress(new IllegalStateException())).isSameAs(fatal);
		}
	}

	@Test
	void customCriterionObservesProtectedFailureAndChecksBeforeCallingSupplier() {
		var system = mock(DistributedSystem.class);
		when(system.getCancelCriterion()).thenReturn(CancelCriterion.NONE);
		var cancellation = new GemFireClientRuntime(system).createCancellation(() -> {
			throw new AssertionError("Must not consult caller state during JVM failure");
		});
		var fatal = new InternalError("private-failure-token");
		Throwable cause = new IllegalStateException("private-cause");
		try (var failures = mockStatic(SystemFailure.class)) {
			failures.when(SystemFailure::getFailure).thenReturn(fatal);
			failures.when(SystemFailure::checkFailure).thenThrow(fatal);
			assertThat(cancellation.cancelInProgress()).isNotBlank().doesNotContain("private-failure-token");
			assertThat(cancellation.isCancelInProgress()).isTrue();
			var failure = (DriverOperationException) cancellation.generateCancelledException(cause);
			assertThat(failure.kind()).isEqualTo(DriverFailureKind.CANCELLED);
			assertThat(failure.getRootCause()).isSameAs(cause);
			assertThat(failure.getMessage()).doesNotContain("private-failure-token", "private-cause");
			assertThatThrownBy(cancellation::checkCancelInProgress).isSameAs(fatal);
			assertThatThrownBy(() -> cancellation.checkCancelInProgress(cause)).isSameAs(fatal);
		}
	}

	@Test
	void localConnectivityDoesNotRequireAServerAndCacheCancellationIsDistinct() {
		var config = new CacheConfiguration(Map.of(
			"mcast-port", "0",
			ClientConfigurationProperties.LOCATORS, "",
			ClientConfigurationProperties.LOG_LEVEL, "error"));
		try (var connection = new GemFire104Driver().connect(config)) {
			var cache = ((GemFireConnection) connection).cacheForTransactionCallback();
			var system = cache.getDistributedSystem();
			var cancellation = connection.getCancellation();
			var systemCancellation = connection.getRuntime().getCancellation();
			assertThat(systemCancellation).isNotSameAs(cancellation);
			assertThat(connection.getRuntime().isConnected()).isTrue();
			assertThat(connection.getRuntime().getMember().getId()).isEqualTo(system.getDistributedMember().getId());
			assertThat(connection.getRuntime().getMember().getId()).isEqualTo(system.getMemberId());
			assertThat(connection.getRuntime().getMember().getGroups()).isEqualTo(system.getDistributedMember().getGroups());
			assertThat(cancellation.cancelInProgress()).isNull();
			connection.close();
			assertThat(connection.getRuntime().isConnected()).isFalse();
			// The pinned loner system criterion does not report cache closure.
			assertThat(system.getCancelCriterion().cancelInProgress()).isNull();
			assertThat(systemCancellation.cancelInProgress()).isNull();
			assertThat(systemCancellation.isCancelInProgress()).isFalse();
			systemCancellation.checkCancelInProgress();
			Throwable cause = new IllegalStateException("caller-diagnostic");
			systemCancellation.checkCancelInProgress(cause);
			assertThat(systemCancellation.generateCancelledException(cause)).isNull();
			assertThat(cancellation.isCancelInProgress()).isTrue();
		}
	}

	@Test
	void systemCancellationUsesItsOwnCriterionAndRetainsClassifiedFailures() {
		var system = mock(DistributedSystem.class);
		when(system.getCancelCriterion()).thenReturn(new CancelCriterion() {
			@Override public String cancelInProgress() { return "password=private-token"; }
			@Override public RuntimeException generateCancelledException(Throwable cause) {
				return new DistributedSystemDisconnectedException(cancelInProgress(), cause);
			}
		});
		var cancellation = new GemFireClientRuntime(system).getCancellation();
		Throwable cause = new IllegalStateException("private-cause");
		assertThat(cancellation.isCancelInProgress()).isTrue();
		assertThat(cancellation.cancelInProgress()).doesNotContain("password", "private-token");
		var failure = (DriverOperationException) cancellation.generateCancelledException(cause);
		assertThat(failure.kind()).isEqualTo(DriverFailureKind.CANCELLED);
		assertThat(failure.getMessage()).doesNotContain("password", "private-token", "private-cause");
		assertThat(failure.getCause()).isInstanceOf(DistributedSystemDisconnectedException.class);
		assertThat(failure.getRootCause()).isSameAs(cause);
		assertThatThrownBy(() -> cancellation.checkCancelInProgress(cause))
			.isInstanceOfSatisfying(DriverOperationException.class, ex -> {
				assertThat(ex.kind()).isEqualTo(DriverFailureKind.CANCELLED);
				assertThat(ex.getRootCause()).isSameAs(cause);
				assertThat(ex.getMessage()).doesNotContain("password", "private-token", "private-cause");
			});
	}

	@Test
	void cancellationRedactsNativeReasonAndPreservesTrustedCause() {
		var cancellation = new GemFireCancellation(new CancelCriterion() {
			@Override public String cancelInProgress() { return "password=private-token"; }
			@Override public RuntimeException generateCancelledException(Throwable cause) {
				return new CacheClosedException(cancelInProgress(), cause);
			}
		});
		Throwable cause = new IllegalStateException("private-cause");
		assertThat(cancellation.cancelInProgress()).doesNotContain("password", "private-token");
		var failure = (DriverOperationException) cancellation.generateCancelledException(cause);
		assertThat(failure.kind()).isEqualTo(DriverFailureKind.CANCELLED);
		assertThat(failure.getMessage()).doesNotContain("password", "private-token", "private-cause");
		assertThat(failure.getCause()).isInstanceOf(CacheClosedException.class).hasMessage("password=private-token");
		assertThat(failure.getRootCause()).isSameAs(cause);
		assertThatThrownBy(() -> cancellation.checkCancelInProgress(cause))
			.isInstanceOfSatisfying(DriverOperationException.class, ex -> {
				assertThat(ex.getMessage()).doesNotContain("password", "private-token", "private-cause");
				assertThat(ex.getRootCause()).isSameAs(cause);
			});
	}

	@Test
	void fatalErrorsAreNotTranslated() {
		Error fatal = new LinkageError("synthetic fatal error");
		var cancellation = new GemFireCancellation(new CancelCriterion() {
			@Override public String cancelInProgress() { throw fatal; }
			@Override public RuntimeException generateCancelledException(Throwable cause) { throw fatal; }
		});
		assertThatThrownBy(cancellation::cancelInProgress).isSameAs(fatal);
		assertThatThrownBy(cancellation::isCancelInProgress).isSameAs(fatal);
		assertThatThrownBy(cancellation::checkCancelInProgress).isSameAs(fatal);
		assertThatThrownBy(() -> cancellation.generateCancelledException(null)).isSameAs(fatal);
	}
}
