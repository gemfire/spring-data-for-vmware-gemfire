/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.lang.NonNull;

/**
 * {@link ThreadUtils} is an abstract utility class for managing Java {@link Thread Threads}.
 *
 * @author John Blum
 * @see Thread
 * @see Duration
 * @see TimeUnit
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class ThreadUtils {

	public static boolean sleep(long milliseconds) {

		try {
			Thread.sleep(milliseconds);
			return true;
		}
		catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public static boolean timedWait(@NonNull Duration duration) {
		return timedWait(duration.toMillis());
	}

	public static boolean timedWait(long duration) {
		return timedWait(duration, duration);
	}

	public static boolean timedWait(@NonNull Duration duration, long interval) {
		return timedWait(duration.toMillis(), interval);
	}

	public static boolean timedWait(long duration, long interval) {
		return timedWait(duration, interval, () -> true);
	}

	public static boolean timedWait(@NonNull Duration duration, long interval, @NonNull Condition condition) {
		return timedWait(duration.toMillis(), interval, condition);
	}

	@SuppressWarnings("all")
	public static boolean timedWait(long duration, long interval, @NonNull Condition condition) {

		final long timeout = System.currentTimeMillis() + duration;

		interval = Math.min(interval, duration);

		try {
			while (!condition.evaluate() && System.currentTimeMillis() < timeout) {
				synchronized (condition) {
					TimeUnit.MILLISECONDS.timedWait(condition, interval);
				}
			}
		}
		catch (InterruptedException cause) {
			Thread.currentThread().interrupt();
		}

		return condition.evaluate();
	}

	@FunctionalInterface
	public interface Condition {
		boolean evaluate();
	}

	/**
	 * @deprecated use {@link Condition}.
	 */
	@Deprecated
	public interface WaitCondition extends Condition {

		default boolean waiting() {
			return evaluate();
		}
	}
}
