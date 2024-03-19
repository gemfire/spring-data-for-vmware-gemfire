/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newRuntimeException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link RuntimeExceptionFactory}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see RuntimeExceptionFactory
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RuntimeExceptionFactoryUnitTests {

	@Mock
	private Throwable mockCause;

	protected void assertThrowable(Throwable actual, Class<? extends Throwable> type, String message) {
		assertThrowable(actual, type, message, null);
	}

	protected void assertThrowable(Throwable actual, Class<? extends Throwable> type, String message, Throwable cause) {
		assertThat(actual).isNotNull();
		assertThat(actual).isInstanceOf(type);
		assertThat(actual).hasMessage(message);
		assertThat(actual).hasCause(cause);
	}

	@Test
	public void newIllegalArgumentExceptionWithMessage() {
		assertThrowable(newIllegalArgumentException("test"), IllegalArgumentException.class, "test");
	}

	@Test
	public void newIllegalArgumentExceptionWithFormattedMessageAndCause() {
		assertThrowable(newIllegalArgumentException(mockCause, "%1$s is a {1}", "This", "test"),
			IllegalArgumentException.class, "This is a test", mockCause);
	}

	@Test
	public void newIllegalStateExceptionWithMessage() {
		assertThrowable(newIllegalStateException("test"), IllegalStateException.class, "test");
	}

	@Test
	public void newIllegalStateExceptionWithFormattedMessageAndCause() {
		assertThrowable(newIllegalStateException(mockCause, "%1$s is a {1}", "This", "test"),
			IllegalStateException.class, "This is a test", mockCause);
	}

	@Test
	public void newRuntimeExceptionWithMessage() {
		assertThrowable(newRuntimeException("test"), RuntimeException.class, "test");
	}

	@Test
	public void newRuntimeExceptionWithFormattedMessageAndCause() {
		assertThrowable(newRuntimeException(mockCause, "%1$s is a {1}", "This", "test"),
			RuntimeException.class, "This is a test", mockCause);
	}

	@Test
	public void newUnsupportedOperationExceptionWithMessage() {
		assertThrowable(newUnsupportedOperationException("test"), UnsupportedOperationException.class, "test");
	}

	@Test
	public void newUnsupportedOperationExceptionWithFormattedMessageAndCause() {
		assertThrowable(newUnsupportedOperationException(mockCause, "%1$s is a {1}", "This", "test"),
			UnsupportedOperationException.class, "This is a test", mockCause);
	}
}
