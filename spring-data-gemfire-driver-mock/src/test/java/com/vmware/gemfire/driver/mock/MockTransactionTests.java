/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MockTransactionTests {
	@Test void unsupportedTransactionsDoNotAcceptStoredOnlyCallbacksOrAlterLocalRegionBehavior() {
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(Map.of()))) {
			var region = connection.<String, String>createClientRegion("entries", ClientRegionShortcut.LOCAL); var manager = connection.getTransactionManager();
			assertThat(manager).isSameAs(connection.getTransactionManager());
			var listener = new DriverTransactionListenerAdapter() {
				@Override public void afterCommit(DriverTransactionEvent event) { throw new AssertionError("Mock must not simulate commit"); }
				@Override public void close() { throw new AssertionError("Unsupported registration must not own the callback"); }
			};
			assertThatThrownBy(manager::begin).isInstanceOfSatisfying(UnsupportedFeatureException.class, ex -> assertThat(ex.feature()).isEqualTo("TransactionManager.begin"));
			assertThatThrownBy(() -> manager.addListener(listener)).isInstanceOf(UnsupportedFeatureException.class);
			region.put("key", "ordinary"); assertThatThrownBy(manager::rollback).isInstanceOf(UnsupportedFeatureException.class);
			assertThat(region.get("key")).isEqualTo("ordinary");
		}
	}
}
