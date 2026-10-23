/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.*;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** Every operation probes its own unsupported boundary even on providers without transactions. */
public abstract class TransactionContract extends PdxValueContract {
	protected Map<Method, Probe> transactionProbes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverConnection.class.getMethod("getTransactionManager"), new Probe("Connection.getTransactionManager", () -> withManager(m -> assertThat(m).isNotNull())));
		probes.put(DriverTransactionManager.class.getMethod("begin"), new Probe("TransactionManager.begin", () -> withManager(m -> { m.begin(); assertThat(m.getTransactionId()).isNotNull(); })));
		probes.put(DriverTransactionManager.class.getMethod("commit"), new Probe("TransactionManager.commit", () -> withManager(m -> { prepare(m, false); m.commit(); assertThat(m.exists()).isFalse(); })));
		probes.put(DriverTransactionManager.class.getMethod("rollback"), new Probe("TransactionManager.rollback", () -> withManager(m -> { prepare(m, false); m.rollback(); assertThat(m.exists()).isFalse(); })));
		probes.put(DriverTransactionManager.class.getMethod("suspend"), new Probe("TransactionManager.suspend", () -> withManager(m -> { prepare(m, false); assertThat(m.suspend()).isNotNull(); })));
		probes.put(DriverTransactionManager.class.getMethod("resume", DriverTransactionId.class), new Probe("TransactionManager.resume", () -> withManager(m -> { var id = prepare(m, true); m.resume(id); assertThat(m.getTransactionId()).isEqualTo(id); })));
		probes.put(DriverTransactionManager.class.getMethod("isSuspended", DriverTransactionId.class), new Probe("TransactionManager.isSuspended", () -> withManager(m -> { var id = prepare(m, true); assertThat(m.isSuspended(id)).isTrue(); })));
		probes.put(DriverTransactionManager.class.getMethod("tryResume", DriverTransactionId.class), new Probe("TransactionManager.tryResume", () -> withManager(m -> { var id = prepare(m, true); assertThat(m.tryResume(id)).isTrue(); })));
		probes.put(DriverTransactionManager.class.getMethod("tryResume", DriverTransactionId.class, long.class, TimeUnit.class), new Probe("TransactionManager.tryResumeTimed", () -> withManager(m -> { var id = prepare(m, true); assertThat(m.tryResume(id, 1, TimeUnit.SECONDS)).isTrue(); })));
		probes.put(DriverTransactionManager.class.getMethod("exists", DriverTransactionId.class), new Probe("TransactionManager.existsById", () -> withManager(m -> { var id = prepare(m, true); assertThat(m.exists(id)).isTrue(); })));
		probes.put(DriverTransactionManager.class.getMethod("exists"), new Probe("TransactionManager.exists", () -> withManager(m -> { assertThat(m.exists()).isFalse(); })));
		probes.put(DriverTransactionManager.class.getMethod("getTransactionId"), new Probe("TransactionManager.getTransactionId", () -> withManager(m -> { assertThat(m.getTransactionId()).isNull(); })));
		probes.put(DriverTransactionManager.class.getMethod("getListener"), new Probe("TransactionManager.getListener", () -> withManager(m -> { assertThat(m.getListener()).isNull(); })));
		probes.put(DriverTransactionManager.class.getMethod("getListeners"), new Probe("TransactionManager.getListeners", () -> withManager(m -> { assertThat(m.getListeners()).isEmpty(); })));
		probes.put(DriverTransactionManager.class.getMethod("setListener", DriverTransactionListener.class), new Probe("TransactionManager.setListener", () -> withManager(m -> { var listener = new DriverTransactionListenerAdapter(); assertThat(m.setListener(listener)).isNull(); assertThat(m.getListener()).isSameAs(listener); })));
		probes.put(DriverTransactionManager.class.getMethod("addListener", DriverTransactionListener.class), new Probe("TransactionManager.addListener", () -> withManager(m -> { var listener = new DriverTransactionListenerAdapter(); m.addListener(listener); assertThat(m.getListeners()).containsExactly(listener); })));
		probes.put(DriverTransactionManager.class.getMethod("removeListener", DriverTransactionListener.class), new Probe("TransactionManager.removeListener", () -> withManager(m -> { m.removeListener(new DriverTransactionListenerAdapter()); assertThat(m.getListeners()).isEmpty(); })));
		probes.put(DriverTransactionManager.class.getMethod("initListeners", DriverTransactionListener[].class), new Probe("TransactionManager.initListeners", () -> withManager(m -> { var listener = new DriverTransactionListenerAdapter(); m.initListeners(new DriverTransactionListener[] {listener}); assertThat(m.getListeners()).containsExactly(listener); })));
		probes.put(DriverTransactionManager.class.getMethod("writeTransactionId", DriverTransactionId.class, ObjectOutput.class), new Probe("TransactionManager.writeTransactionId", () -> withManager(m -> { var id = prepare(m, false); var bytes = new ByteArrayOutputStream(); try (var output = new ObjectOutputStream(bytes)) { m.writeTransactionId(id, output); } assertThat(bytes.size()).isGreaterThan(4); })));
		probes.put(DriverTransactionManager.class.getMethod("readTransactionId", ObjectInput.class), new Probe("TransactionManager.readTransactionId", () -> withManager(m -> { var id = prepare(m, false); var bytes = new ByteArrayOutputStream(); try (var output = new ObjectOutputStream(bytes)) { if (id != null) m.writeTransactionId(id, output); } try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { assertThat(m.readTransactionId(input)).isEqualTo(id); } })));
		return probes;
	}
	private DriverTransactionId prepare(DriverTransactionManager manager, boolean suspend) {
		try { manager.begin(); return suspend ? manager.suspend() : manager.getTransactionId(); }
		catch (UnsupportedFeatureException unsupported) { return null; }
	}
	private void withManager(Action action) throws Exception {
		try (var connection = connect()) { action.run(connection.getTransactionManager()); }
	}
	@FunctionalInterface private interface Action { void run(DriverTransactionManager manager) throws Exception; }
}
