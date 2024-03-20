/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/**
 * Abstract utility class containing functions to process Spring Framework objects and components (beans).
 *
 * @author John Blum
 * @see Function
 * @see ApplicationContext
 * @see ConfigurableApplicationContext
 * @see org.springframework.data.gemfire.util.SpringExtensions
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class SpringUtils extends org.springframework.data.gemfire.util.SpringExtensions {

	public static final Function<ConfigurableApplicationContext, Boolean> APPLICATION_CONTEXT_CLOSING_FUNCTION =
		applicationContext -> {

			if (applicationContext != null) {
				applicationContext.close();
				return true;
			}

			return false;
		};

	/**
	 * Determines whether the given {@link ApplicationContext} is still {@literal active}.
	 *
	 * An {@link ApplicationContext} is considered to be {@literal active} if the {@literal refresh} method
	 * has been called but the {@link ApplicationContext} has not be {@literal closed} yet.
	 *
	 * @param applicationContext {@link ApplicationContext} to evaluate.
	 * @return a boolean value indicating if the given {@link ApplicationContext} is still {@literal active}.
	 * Returns {@literal false} if the {@link ApplicationContext} is {@literal null}, is not an instance of
	 * {@link ConfigurableApplicationContext} (which is required to evaluate the {@literal active} state)
	 * or the {@link ConfigurableApplicationContext#isActive()} methods returns {@literal false} (which occurs after
	 * the {@link ConfigurableApplicationContext#close()} method has been called.
	 * @see ApplicationContext
	 */
	public static boolean isApplicationContextActive(@Nullable ApplicationContext applicationContext) {

		return Optional.ofNullable(applicationContext)
			.filter(ConfigurableApplicationContext.class::isInstance)
			.map(ConfigurableApplicationContext.class::cast)
			.map(ConfigurableApplicationContext::isActive)
			.orElse(false);
	}

	/**
	 * Closes the given optionally provided {@link ApplicationContext}.
	 *
	 * @param applicationContext {@link ApplicationContext} to close.
	 * @return a boolean value indicating whether the {@link ApplicationContext} could be closed successfully or not.
	 * @see ApplicationContext
	 */
	public static boolean closeApplicationContext(@Nullable ApplicationContext applicationContext) {

		return Optional.ofNullable(applicationContext)
			.filter(ConfigurableApplicationContext.class::isInstance)
			.map(ConfigurableApplicationContext.class::cast)
			.map(APPLICATION_CONTEXT_CLOSING_FUNCTION)
			.orElse(false);
	}
}
