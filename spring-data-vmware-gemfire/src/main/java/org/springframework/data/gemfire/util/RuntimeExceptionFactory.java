/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.util;

import java.text.MessageFormat;

/**
 * The {@link RuntimeExceptionFactory} class is a factory for creating common {@link RuntimeException RuntimeExceptions}
 * with the added convenience of message formatting and optional {@link Throwable causes}.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class RuntimeExceptionFactory {

	public static final String NOT_IMPLEMENTED = "Not Implemented";
	public static final String NOT_SUPPORTED = "Operation Not Supported";

	/**
	 * Constructs and initializes an {@link IllegalArgumentException} with the given {@link String message}
	 * and {@link Object arguments} used to format the message.
	 *
	 * @param message {@link String} describing the {@link IllegalArgumentException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link IllegalArgumentException} with the given {@link String message}.
	 * @see #newIllegalArgumentException(Throwable, String, Object...)
	 * @see IllegalArgumentException
	 */
	public static IllegalArgumentException newIllegalArgumentException(String message, Object... args) {
		return newIllegalArgumentException(null, message, args);
	}

	/**
	 * Constructs and initializes an {@link IllegalArgumentException} with the given {@link Throwable cause},
	 * {@link String message} and {@link Object arguments} used to format the message.
	 *
	 * @param cause {@link Throwable} identifying the reason the {@link IllegalArgumentException} was thrown.
	 * @param message {@link String} describing the {@link IllegalArgumentException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link IllegalArgumentException} with the given {@link Throwable cause} and {@link String message}.
	 * @see IllegalArgumentException
	 */
	public static IllegalArgumentException newIllegalArgumentException(Throwable cause,
			String message, Object... args) {

		return new IllegalArgumentException(format(message, args), cause);
	}

	/**
	 * Constructs and initializes an {@link IllegalStateException} with the given {@link String message}
	 * and {@link Object arguments} used to format the message.
	 *
	 * @param message {@link String} describing the {@link IllegalStateException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link IllegalStateException} with the given {@link String message}.
	 * @see #newIllegalStateException(Throwable, String, Object...)
	 * @see IllegalStateException
	 */
	public static IllegalStateException newIllegalStateException(String message, Object... args) {
		return newIllegalStateException(null, message, args);
	}

	/**
	 * Constructs and initializes an {@link IllegalStateException} with the given {@link Throwable cause},
	 * {@link String message} and {@link Object arguments} used to format the message.
	 *
	 * @param cause {@link Throwable} identifying the reason the {@link IllegalStateException} was thrown.
	 * @param message {@link String} describing the {@link IllegalStateException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link IllegalStateException} with the given {@link Throwable cause} and {@link String message}.
	 * @see IllegalStateException
	 */
	public static IllegalStateException newIllegalStateException(Throwable cause, String message, Object... args) {
		return new IllegalStateException(format(message, args), cause);
	}

	/**
	 * Constructs and initializes an {@link RuntimeException} with the given {@link String message}
	 * and {@link Object arguments} used to format the message.
	 *
	 * @param message {@link String} describing the {@link RuntimeException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link RuntimeException} with the given {@link String message}.
	 * @see #newRuntimeException(Throwable, String, Object...)
	 * @see RuntimeException
	 */
	public static RuntimeException newRuntimeException(String message, Object... args) {
		return newRuntimeException(null, message, args);
	}

	/**
	 * Constructs and initializes an {@link RuntimeException} with the given {@link Throwable cause},
	 * {@link String message} and {@link Object arguments} used to format the message.
	 *
	 * @param cause {@link Throwable} identifying the reason the {@link RuntimeException} was thrown.
	 * @param message {@link String} describing the {@link RuntimeException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link RuntimeException} with the given {@link Throwable cause} and {@link String message}.
	 * @see RuntimeException
	 */
	public static RuntimeException newRuntimeException(Throwable cause, String message, Object... args) {
		return new RuntimeException(format(message, args), cause);
	}

	/**
	 * Constructs and initializes an {@link UnsupportedOperationException} with the given {@link String message}
	 * and {@link Object arguments} used to format the message.
	 *
	 * @param message {@link String} describing the {@link UnsupportedOperationException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}
	 * @return a new {@link UnsupportedOperationException} with the given {@link String message}.
	 * @see #newUnsupportedOperationException(Throwable, String, Object...)
	 * @see UnsupportedOperationException
	 */
	public static UnsupportedOperationException newUnsupportedOperationException(String message, Object... args) {
		return newUnsupportedOperationException(null, message, args);
	}

	/**
	 * Constructs and initializes an {@link UnsupportedOperationException} with the given {@link Throwable cause},
	 * {@link String message} and {@link Object arguments} used to format the message.
	 *
	 * @param cause {@link Throwable} identifying the reason the {@link UnsupportedOperationException} was thrown.
	 * @param message {@link String} describing the {@link UnsupportedOperationException exception}.
	 * @param args {@link Object arguments} used to replace format placeholders in the {@link String message}.
	 * @return a new {@link UnsupportedOperationException} with the given {@link Throwable cause}
	 * and {@link String message}.
	 * @see UnsupportedOperationException
	 */
	public static UnsupportedOperationException newUnsupportedOperationException(Throwable cause,
			String message, Object... args) {

		return new UnsupportedOperationException(format(message, args), cause);
	}

	/**
	 * Formats the given {@link String message} using the given {@link Object arguments}.
	 *
	 * @param message {@link String} containing the message pattern to format.
	 * @param args {@link Object arguments} used in the message to replace format placeholders.
	 * @return the formatted {@link String message}.
	 * @see String#format(String, Object...)
	 * @see MessageFormat#format(String, Object...)
	 */
	protected static String format(String message, Object... args) {
		return MessageFormat.format(String.format(message, args), args);
	}
}
