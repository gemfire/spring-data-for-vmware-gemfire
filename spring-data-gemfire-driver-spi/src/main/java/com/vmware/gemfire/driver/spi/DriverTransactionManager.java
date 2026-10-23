/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Cache-owned transaction service. Thread association, isolation, callback lifecycle and supported
 * client capabilities follow the provider. Registration does not guarantee transaction listener
 * notifications; GemFire 10.4 client transactions notify entry listeners but not transaction listeners. A handle does not own or close its cache. Timed tryResume waits
 * for suspension; it does not set a transaction lifetime or a Spring transaction timeout.
 * ID externalization uses the bound engine's format, not a version-independent SPI wire format.
 */
public interface DriverTransactionManager {
	default void begin() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.begin");
	}
	default void commit() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.commit");
	}
	default void rollback() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.rollback");
	}
	default DriverTransactionId suspend() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.suspend");
	}
	default void resume(DriverTransactionId id) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.resume");
	}
	default boolean isSuspended(DriverTransactionId id) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.isSuspended");
	}
	default boolean tryResume(DriverTransactionId id) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.tryResume");
	}
	default boolean tryResume(DriverTransactionId id, long timeout, TimeUnit unit) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.tryResumeTimed");
	}
	default boolean exists(DriverTransactionId id) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.existsById");
	}
	default boolean exists() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.exists");
	}
	default DriverTransactionId getTransactionId() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.getTransactionId");
	}
	default DriverTransactionListener getListener() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.getListener");
	}
	default DriverTransactionListener[] getListeners() {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.getListeners");
	}
	default DriverTransactionListener setListener(DriverTransactionListener listener) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.setListener");
	}
	default void addListener(DriverTransactionListener listener) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.addListener");
	}
	default void removeListener(DriverTransactionListener listener) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.removeListener");
	}
	default void initListeners(DriverTransactionListener[] listeners) {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.initListeners");
	}
	default void writeTransactionId(DriverTransactionId id, ObjectOutput output) throws IOException {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.writeTransactionId");
	}
	default DriverTransactionId readTransactionId(ObjectInput input) throws IOException, ClassNotFoundException {
		throw UnsupportedFeatureException.notIntroduced("TransactionManager.readTransactionId");
	}
}
