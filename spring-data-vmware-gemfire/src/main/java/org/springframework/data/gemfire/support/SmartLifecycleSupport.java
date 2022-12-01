/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.support;

import org.springframework.context.SmartLifecycle;

/**
 * The {@link SmartLifecycleSupport} interface is an extension of Spring's {@link SmartLifecycle} interface
 * providing default, convenient behavior for many of the lifecycle methods as well as a serving
 * as a {@link FunctionalInterface}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see SmartLifecycle
 * @since 1.0.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface SmartLifecycleSupport extends SmartLifecycle {

	boolean DEFAULT_AUTO_STARTUP = true;
	boolean DEFAULT_IS_RUNNING = false;

	int DEFAULT_PHASE = 0;

	@Override
	default boolean isAutoStartup() {
		return DEFAULT_AUTO_STARTUP;
	}

	@Override
	default void stop(Runnable runnable) {
		stop();
		runnable.run();
	}

	@Override
	default void stop() { }

	@Override
	default boolean isRunning() {
		return DEFAULT_IS_RUNNING;
	}

	@Override
	default int getPhase() {
		return DEFAULT_PHASE;
	}
}
