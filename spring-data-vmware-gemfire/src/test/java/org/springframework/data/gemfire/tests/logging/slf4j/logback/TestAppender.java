/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.logging.slf4j.logback;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.util.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

/**
 * The {@link TestAppender} class is a SLF4J, Logback {@link Appender} implementation used for testing purposes.
 *
 * @author John Blum
 * @see Stack
 * @see Appender
 * @see AppenderBase
 * @since 0.0.5.RELEASE
 */
@SuppressWarnings("unused")
public class TestAppender extends AppenderBase<ILoggingEvent> implements Appender<ILoggingEvent> {

	private static final AtomicReference<TestAppender> INSTANCE = new AtomicReference<>(null);

	private static final Stack<String> logMessages = new Stack<>();

	public static TestAppender getInstance() {

		return Optional.ofNullable(INSTANCE.get())
			.orElseThrow(() -> newIllegalStateException("[%s] was not properly configured",
				TestAppender.class.getName()));
	}

	public TestAppender() {
		INSTANCE.compareAndSet(null, this);
	}

	@Override
	protected void append(ILoggingEvent event) {

		Optional.ofNullable(event)
			.map(ILoggingEvent::getFormattedMessage)
			.filter(StringUtils::hasText)
			.ifPresent(logMessages::push);
	}

	public String lastLogMessage() {

		synchronized (logMessages) {
			return logMessages.empty() ? null : logMessages.pop();
		}
	}

	public void clear() {
		logMessages.clear();
	}
}
