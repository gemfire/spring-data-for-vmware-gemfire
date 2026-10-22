/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TransactionDefaultsTests {
	@Test void olderConnectionProvidersFailExplicitlyBeforeExposingTransactionState() {
		assertThatThrownBy(() -> new DriverConnection() {}.getTransactionManager()).isInstanceOfSatisfying(UnsupportedFeatureException.class,
			e -> { assertThat(e.feature()).isEqualTo("Connection.getTransactionManager"); assertThat(e.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED); });
	}
	@Test void transactionVetoHasASeparateCheckedContractAndPreservesItsCause() {
		var cause = new IllegalArgumentException("private"); var veto = new DriverTransactionWriterException(cause);
		assertThat(veto).hasCause(cause).hasMessage("Transaction writer rejected commit");
		assertThat(Exception.class).isAssignableFrom(veto.getClass());
		assertThat(RuntimeException.class.isAssignableFrom(veto.getClass())).isFalse();
	}
}
