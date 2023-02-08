/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.test.context.support;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringProperties;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Spring {@link TestContext} framework {@link TestExecutionListener} used to
 * close the {@link ApplicationContext} after test class execution.
 *
 * This {@link TestExecutionListener} is configurable via {@link SpringProperties}
 * or by declaring Java {@link System#getProperties() System properties}.
 *
 * @author John Blum
 * @see ApplicationContext
 * @see SpringProperties
 * @see DirtiesContext
 * @see TestContext
 * @see TestExecutionListener
 * @see AbstractTestExecutionListener
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class CloseApplicationContextAfterTestClassTestExecutionListener extends AbstractTestExecutionListener {

	protected static final boolean DEFAULT_SPRING_TEST_CONTEXT_CLOSED = false;

	protected static final String SPRING_TEST_CONTEXT_CLOSE_PROPERTY = "spring.test.context.close";

	protected static final DirtiesContext.HierarchyMode DEFAULT_HIERARCHY_MODE =
		DirtiesContext.HierarchyMode.CURRENT_LEVEL;

	private final AtomicReference<Boolean> springTestContextCloseEnabled = new AtomicReference<>();

	/**
	 * @inheritDoc
	 */
	@Override
	public int getOrder() {
		return 100_000;
	}

	protected boolean isSpringTestContextCloseEnabled() {
		return this.springTestContextCloseEnabled.updateAndGet(closeEnabled -> closeEnabled != null ? closeEnabled
			: getSpringTestContextCloseEnabledResolvingFunction().apply(DEFAULT_SPRING_TEST_CONTEXT_CLOSED));
	}

	@SuppressWarnings("all")
	protected Function<Boolean, Boolean> getSpringTestContextCloseEnabledResolvingFunction() {

		return defaultCloseEnabled -> {

			String closeEnabledProperty = SpringProperties.getProperty(SPRING_TEST_CONTEXT_CLOSE_PROPERTY);

			boolean resolvedCloseEnabled = Boolean.parseBoolean(closeEnabledProperty) || defaultCloseEnabled;

			return resolvedCloseEnabled;
		};
	}

	/**
	 * Closes the {@link ApplicationContext} associated with the given, required {@link TestContext}
	 * after the test class instance executes.
	 *
	 * This operation is implemented by marking the {@link ApplicationContext} as dirty.
	 *
	 * @param testContext Spring {@link TestContext}
	 * @throws Exception if the {@link ApplicationContext} close operation fails.
	 * @see TestContext#markApplicationContextDirty(DirtiesContext.HierarchyMode)
	 * @see #isSpringTestContextCloseEnabled()
	 */
	@Override
	public void afterTestClass(@NonNull TestContext testContext) throws Exception {

		if (isSpringTestContextCloseEnabled()) {
			super.afterTestClass(testContext);
			testContext.markApplicationContextDirty(DEFAULT_HIERARCHY_MODE);
		}
	}
}
