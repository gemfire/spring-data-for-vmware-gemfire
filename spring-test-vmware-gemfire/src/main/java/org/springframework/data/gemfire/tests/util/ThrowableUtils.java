/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The {@link ThrowableUtils} class is a utility class for working with {@link Throwable},
 * {@link Exception} and {@link Error} objects.
 *
 * @author John Blum
 * @see Error
 * @see Exception
 * @see Throwable
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class ThrowableUtils {

	public static String toString(Throwable throwable) {

		StringWriter writer = new StringWriter();

		throwable.printStackTrace(new PrintWriter(writer));

		return writer.toString();
	}
}
