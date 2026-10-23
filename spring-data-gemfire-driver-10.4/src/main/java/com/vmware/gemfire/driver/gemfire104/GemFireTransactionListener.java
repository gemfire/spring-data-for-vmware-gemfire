/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.*;
import java.util.Properties;

final class GemFireTransactionListener implements TransactionListener {
	final DriverTransactionListener delegate;
	GemFireTransactionListener(DriverTransactionListener delegate) { this.delegate = delegate; }
	@Override public void afterCommit(TransactionEvent event) { delegate.afterCommit(new GemFireTransactionEvent(event)); }
	@Override public void afterFailedCommit(TransactionEvent event) { delegate.afterFailedCommit(new GemFireTransactionEvent(event)); }
	@Override public void afterRollback(TransactionEvent event) { delegate.afterRollback(new GemFireTransactionEvent(event)); }
	@Override public void init(Properties properties) { delegate.init(properties); }
	@Override public void initialize(Cache cache, Properties properties) { delegate.initialize(new GemFireConnection(cache), properties); }
	@Override public void close() { delegate.close(); }
	@Override public boolean equals(Object other) { return other instanceof GemFireTransactionListener listener && delegate.equals(listener.delegate); }
	@Override public int hashCode() { return delegate.hashCode(); }
}
