/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.*;
import java.util.Properties;

final class GemFireTransactionWriter implements TransactionWriter {
	final DriverTransactionWriter delegate;
	GemFireTransactionWriter(DriverTransactionWriter delegate) { this.delegate = delegate; }
	@Override public void beforeCommit(TransactionEvent event) throws TransactionWriterException {
		try { delegate.beforeCommit(new GemFireTransactionEvent(event)); }
		catch (DriverTransactionWriterException | GemFireDriverException failure) { throw new TransactionWriterException(failure); }
		catch (RuntimeException failure) {
			throw new TransactionWriterException(new DriverOperationException(DriverFailureKind.TRANSACTION_VETO, "Transaction writer failed", failure));
		}
	}
	@Override public void init(Properties properties) { delegate.init(properties); }
	@Override public void initialize(Cache cache, Properties properties) { delegate.initialize(new GemFireConnection(cache, ((org.apache.geode.internal.cache.InternalCache) cache).isClient() ? CacheConfiguration.Mode.CLIENT : CacheConfiguration.Mode.PEER), properties); }
	@Override public void close() { delegate.close(); }
}
