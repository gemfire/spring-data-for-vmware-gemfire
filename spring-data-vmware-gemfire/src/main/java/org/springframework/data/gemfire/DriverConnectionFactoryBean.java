/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * Exposes a single, explicitly initialized driver connection to Spring consumers.
 * The two-argument constructor creates and owns a connection; the one-argument constructor
 * borrows the exact supplied handle without inspecting, reconfiguring or closing it.
 *
 * <p>Initialization opens an owned connection and checks that the returned handle is not closed;
 * this does not establish distributed-system connectivity. Access before initialization or after
 * destruction fails. Initialization failure is terminal: the original failure is propagated,
 * any acquired owned handle is closed, and cleanup failures are suppressed on the original failure.
 * Repeated successful initialization is a no-op. Destruction attempts owned closure once, propagates
 * any close failure, and is otherwise idempotent, including when initialization never happened.
 *
 * <p>Ownership means responsibility for closure, not exclusive creation of a native cache.
 * A driver may reuse an existing compatible native cache. Share one owner through injected
 * borrowers; independently creating owners does not imply independently closeable native caches.
 * Spring destroys this factory, not its FactoryBean product. Consumers must not independently
 * register the product for destruction. No lifecycle retry or reconnection is performed.
 *
 * <p>Synchronization protects only this factory's lifecycle, not operations on its product.
 */
public final class DriverConnectionFactoryBean
		implements FactoryBean<DriverConnection>, InitializingBean, DisposableBean, PersistenceExceptionTranslator {

	private final GemfireExceptionTranslator exceptionTranslator = new GemfireExceptionTranslator();

	private enum State { NEW, INITIALIZING, READY, FAILED, DESTROYED }

	private final GemFireDriverManager driverManager;
	private final CacheConfiguration configuration;
	private final DriverConnection borrowed;
	private State state = State.NEW;
	private DriverConnection connection;
	private Throwable initializationFailure;

	/** Create a connection at initialization, passing the configuration unchanged to the validated driver. */
	public DriverConnectionFactoryBean(GemFireDriverManager driverManager, CacheConfiguration configuration) {
		Assert.notNull(driverManager, "A validated driver manager is required");
		Assert.notNull(configuration, "A cache configuration is required");
		this.driverManager = driverManager;
		this.configuration = configuration;
		this.borrowed = null;
	}

	/** Borrow a handle; the caller retains responsibility for closing it. */
	public DriverConnectionFactoryBean(DriverConnection connection) {
		Assert.notNull(connection, "A borrowed driver connection is required");
		this.borrowed = connection;
		this.driverManager = null;
		this.configuration = null;
	}

	@Override
	public synchronized void afterPropertiesSet() {
		if (state == State.READY) return;
		requireState(State.NEW);
		state = State.INITIALIZING;
		DriverConnection acquired = null;
		try {
			if (borrowed != null) {
				connection = borrowed;
			}
			else {
				acquired = driverManager.driver().connect(configuration);
				Assert.state(acquired != null, "Driver returned no connection");
				Assert.state(!acquired.isClosed(), "Driver returned a closed connection");
				connection = acquired;
			}
			state = State.READY;
		}
		catch (RuntimeException | Error failure) {
			initializationFailure = failure;
			state = State.FAILED;
			if (acquired != null) {
				try { acquired.close(); }
				catch (RuntimeException | Error cleanupFailure) {
					if (cleanupFailure != failure) failure.addSuppressed(cleanupFailure);
				}
			}
			throw failure;
		}
	}

	@Override
	public synchronized DriverConnection getObject() {
		requireState(State.READY);
		return connection;
	}

	@Override
	public Class<?> getObjectType() { return DriverConnection.class; }

	@Override
	public boolean isSingleton() { return true; }

	@Override
	public synchronized void destroy() {
		Assert.state(state != State.INITIALIZING, "Cannot destroy a connection during initialization");
		if (state == State.DESTROYED) return;
		DriverConnection owned = borrowed == null ? connection : null;
		connection = null;
		state = State.DESTROYED;
		if (owned != null) owned.close();
	}

	/** Allows Spring repository exception advice to discover translation through the connection factory. */
	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException failure) {
		return exceptionTranslator.translateExceptionIfPossible(failure);
	}

	private void requireState(State expected) {
		if (state != expected) {
			throw new IllegalStateException("Driver connection factory is " + state + "; expected " + expected,
				initializationFailure);
		}
	}
}
