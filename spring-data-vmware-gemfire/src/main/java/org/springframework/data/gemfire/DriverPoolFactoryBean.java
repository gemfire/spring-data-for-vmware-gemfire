/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.DriverConnection;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Owns a newly created named pool or explicitly borrows a supplied pool. The owning constructor
 * creates once during initialization, rejects a null/destroyed result and cleans up a returned
 * handle on failure. Duplicate creation never looks up or adopts an existing pool.
 *
 * <p>Borrowing neither inspects nor reconfigures the supplied handle. Initialization failure and
 * shutdown are terminal; access before initialization or after shutdown fails. Owned destruction
 * is attempted at most once, propagating failure; initialization cleanup failures are suppressed
 * on the original failure. The injected connection is never closed by this factory.
 *
 * <p>Inject the connection bean into this factory so Spring destroys the pool before its connection.
 * Spring destroys this factory, not its FactoryBean product; do not register a second destruction
 * callback on that product. Ownership does not promise native pool isolation or unconditional
 * destruction. Synchronization protects only the factory lifecycle.
 */
public final class DriverPoolFactoryBean
		implements FactoryBean<DriverPool>, InitializingBean, DisposableBean {

	private enum State { NEW, INITIALIZING, READY, FAILED, DESTROYED }

	private final DriverConnection connection;
	private final String name;
	private final PoolConfiguration configuration;
	private final DriverPool borrowed;
	private State state = State.NEW;
	private DriverPool pool;
	private Throwable initializationFailure;

	/** Create and own a new named pool, retaining the caller's exact configuration. */
	public DriverPoolFactoryBean(DriverConnection connection, String name, PoolConfiguration configuration) {
		Assert.notNull(connection, "A driver connection is required");
		Assert.hasText(name, "A pool name is required");
		Assert.notNull(configuration, "A pool configuration is required");
		this.connection = connection;
		this.name = name;
		this.configuration = configuration;
		this.borrowed = null;
	}

	/** Borrow a handle; the caller retains responsibility for its destruction. */
	public DriverPoolFactoryBean(DriverPool pool) {
		Assert.notNull(pool, "A borrowed driver pool is required");
		this.borrowed = pool;
		this.connection = null;
		this.name = null;
		this.configuration = null;
	}

	@Override
	public synchronized void afterPropertiesSet() {
		if (state == State.READY) return;
		requireState(State.NEW);
		state = State.INITIALIZING;
		DriverPool acquired = null;
		try {
			if (borrowed != null) {
				pool = borrowed;
			}
			else {
				acquired = connection.createPool(name, configuration);
				Assert.state(acquired != null, "Driver returned no pool");
				Assert.state(!acquired.isDestroyed(), "Driver returned a destroyed pool");
				pool = acquired;
			}
			state = State.READY;
		}
		catch (RuntimeException | Error failure) {
			initializationFailure = failure;
			state = State.FAILED;
			if (acquired != null) {
				try { acquired.destroy(); }
				catch (RuntimeException | Error cleanupFailure) {
					if (cleanupFailure != failure) failure.addSuppressed(cleanupFailure);
				}
			}
			throw failure;
		}
	}

	@Override
	public synchronized DriverPool getObject() {
		requireState(State.READY);
		return pool;
	}

	@Override
	public Class<?> getObjectType() { return DriverPool.class; }

	@Override
	public boolean isSingleton() { return true; }

	@Override
	public synchronized void destroy() {
		Assert.state(state != State.INITIALIZING, "Cannot destroy a pool during initialization");
		if (state == State.DESTROYED) return;
		DriverPool owned = borrowed == null ? pool : null;
		pool = null;
		state = State.DESTROYED;
		if (owned != null) owned.destroy();
	}

	private void requireState(State expected) {
		if (state != expected) {
			throw new IllegalStateException("Driver pool factory is " + state + "; expected " + expected,
				initializationFailure);
		}
	}
}
