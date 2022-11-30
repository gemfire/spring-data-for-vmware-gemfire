// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

/**
 * {@link AbstractSmartLifecycle} is an abstract base class implementing the Spring{@link SmartLifecycle} interface
 * to support custom implementations.
 *
 * @author John Blum
 * @see SmartLifecycle
 * @since 2.0.2
 */
@SuppressWarnings("unused")
public abstract class AbstractSmartLifecycle implements ApplicationContextAware, SmartLifecycle {

	protected static final int DEFAULT_PHASE = 0;

	private volatile boolean running = false;

	private ApplicationContext applicationContext;

	@Override
	public boolean isAutoStartup() {
		return false;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	protected void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	protected Optional<ApplicationContext> getApplicationContext() {
		return Optional.ofNullable(this.applicationContext);
	}

	protected ApplicationContext requireApplicationContext() {

		return getApplicationContext()
			.orElseThrow(() -> newIllegalStateException("ApplicationContext could not be resolved"));
	}

	@Override
	public int getPhase() {
		return DEFAULT_PHASE;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void stop(Runnable callback) {
	}
}
