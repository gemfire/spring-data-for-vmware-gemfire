/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.*;
import java.io.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class GemFireTransactionManager implements DriverTransactionManager {
	private final CacheTransactionManager manager;
	GemFireTransactionManager(CacheTransactionManager manager) { this.manager = manager; }
	@Override public void begin() { run(manager::begin); }
	@Override public void commit() { run(manager::commit); }
	@Override public void rollback() { run(manager::rollback); }
	@Override public boolean exists() { return call(manager::exists); }
	@Override public boolean exists(DriverTransactionId id) { return call(() -> manager.exists(nativeId(id))); }
	@Override public DriverTransactionId getTransactionId() { return call(() -> id(manager.getTransactionId())); }
	@Override public DriverTransactionId suspend() { return call(() -> id(manager.suspend())); }
	@Override public void resume(DriverTransactionId id) { run(() -> manager.resume(nativeId(id))); }
	@Override public boolean isSuspended(DriverTransactionId id) { return call(() -> manager.isSuspended(nativeId(id))); }
	@Override public boolean tryResume(DriverTransactionId id) { return call(() -> manager.tryResume(nativeId(id))); }
	@Override public boolean tryResume(DriverTransactionId id, long timeout, TimeUnit unit) { return call(() -> manager.tryResume(nativeId(id), timeout, unit)); }
	@Override public DriverTransactionListener getListener() { return call(() -> listener(manager.getListener())); }
	@Override public DriverTransactionListener[] getListeners() { return call(() -> Arrays.stream(manager.getListeners()).map(this::listener).toArray(DriverTransactionListener[]::new)); }
	@Override public DriverTransactionListener setListener(DriverTransactionListener listener) { return call(() -> listener(manager.setListener(nativeListener(listener)))); }
	@Override public void addListener(DriverTransactionListener listener) { run(() -> manager.addListener(nativeListener(listener))); }
	@Override public void removeListener(DriverTransactionListener listener) { run(() -> manager.removeListener(nativeListener(listener))); }
	@Override public void initListeners(DriverTransactionListener[] listeners) {
		run(() -> manager.initListeners(listeners == null ? null : Arrays.stream(listeners).map(this::nativeListener).toArray(TransactionListener[]::new)));
	}
	@Override public DriverTransactionWriter getWriter() { return call(() -> writer(manager.getWriter())); }
	@Override public void setWriter(DriverTransactionWriter writer) { run(() -> manager.setWriter(nativeWriter(writer))); }
	@Override public boolean isDistributed() { return call(manager::isDistributed); }
	@Override public void setDistributed(boolean value) { run(() -> manager.setDistributed(value)); }
	@Override public void writeTransactionId(DriverTransactionId id, ObjectOutput output) throws IOException {
		try { nativeId(id).writeExternal(output); }
		catch (RuntimeException failure) { throw new GemFireExceptionMapper().translate("TransactionId.writeExternal", failure); }
	}
	@Override public DriverTransactionId readTransactionId(ObjectInput input) throws IOException, ClassNotFoundException {
		// Native Externalizable implementation stays inside the exact-version driver; no native object escapes.
		try { var id = new org.apache.geode.internal.cache.TXId(); id.readExternal(input); return new GemFireTransactionId(id); }
		catch (RuntimeException failure) { throw new GemFireExceptionMapper().translate("TransactionId.readExternal", failure); }
	}
	private DriverTransactionId id(TransactionId id) { return id == null ? null : new GemFireTransactionId(id); }
	private TransactionId nativeId(DriverTransactionId id) {
		if (id == null) return null;
		if (id instanceof GemFireTransactionId adapted) return adapted.value;
		throw new IllegalArgumentException("Transaction identity belongs to another provider");
	}
	private TransactionListener nativeListener(DriverTransactionListener listener) {
		if (listener == null) return null;
		if (listener instanceof ForeignListener foreign) return foreign.value;
		return new GemFireTransactionListener(listener);
	}
	private DriverTransactionListener listener(TransactionListener listener) {
		if (listener == null) return null;
		return listener instanceof GemFireTransactionListener adapted ? adapted.delegate : new ForeignListener(listener);
	}
	private TransactionWriter nativeWriter(DriverTransactionWriter writer) {
		if (writer == null) return null;
		if (writer instanceof ForeignWriter foreign) return foreign.value;
		return new GemFireTransactionWriter(writer);
	}
	private DriverTransactionWriter writer(TransactionWriter writer) {
		if (writer == null) return null;
		return writer instanceof GemFireTransactionWriter adapted ? adapted.delegate : new ForeignWriter(writer);
	}
	private TransactionEvent nativeEvent(DriverTransactionEvent event) {
		if (event instanceof GemFireTransactionEvent adapted) return adapted.value;
		throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Transaction event belongs to another provider", null);
	}
	private Cache nativeCache(DriverConnection connection) {
		if (connection instanceof GemFireConnection nativeConnection && nativeConnection.cacheForTransactionCallback() instanceof Cache cache) return cache;
		throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Transaction callback requires a native peer cache", null);
	}
	private final class ForeignListener implements DriverTransactionListener {
		private final TransactionListener value;
		ForeignListener(TransactionListener value) { this.value = value; }
		@Override public void afterCommit(DriverTransactionEvent event) { run(() -> value.afterCommit(nativeEvent(event))); }
		@Override public void afterFailedCommit(DriverTransactionEvent event) { run(() -> value.afterFailedCommit(nativeEvent(event))); }
		@Override public void afterRollback(DriverTransactionEvent event) { run(() -> value.afterRollback(nativeEvent(event))); }
		@Override public void init(Properties properties) { run(() -> value.init(properties)); }
		@Override public void initialize(DriverConnection connection, Properties properties) { run(() -> value.initialize(nativeCache(connection), properties)); }
		@Override public void close() { run(value::close); }
		@Override public boolean equals(Object other) { return other instanceof ForeignListener listener && value.equals(listener.value); }
		@Override public int hashCode() { return value.hashCode(); }
	}
	private final class ForeignWriter implements DriverTransactionWriter {
		private final TransactionWriter value;
		ForeignWriter(TransactionWriter value) { this.value = value; }
		@Override public void beforeCommit(DriverTransactionEvent event) throws DriverTransactionWriterException {
			try { value.beforeCommit(nativeEvent(event)); }
			catch (TransactionWriterException failure) { throw new DriverTransactionWriterException(failure); }
			catch (RuntimeException failure) { throw new GemFireExceptionMapper().translate("TransactionWriter.beforeCommit", failure); }
		}
		@Override public void init(Properties properties) { run(() -> value.init(properties)); }
		@Override public void initialize(DriverConnection connection, Properties properties) { run(() -> value.initialize(nativeCache(connection), properties)); }
		@Override public void close() { run(value::close); }
		@Override public boolean equals(Object other) { return other instanceof ForeignWriter writer && value.equals(writer.value); }
		@Override public int hashCode() { return value.hashCode(); }
	}
	private void run(Runnable action) { call(() -> { action.run(); return null; }); }
	private <T> T call(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException failure) { throw new GemFireExceptionMapper().translate("TransactionManager", failure); }
	}
}
