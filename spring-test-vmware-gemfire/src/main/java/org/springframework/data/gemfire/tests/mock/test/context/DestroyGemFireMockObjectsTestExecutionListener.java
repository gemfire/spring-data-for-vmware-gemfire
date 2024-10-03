/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.test.context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.gemfire.tests.extensions.spring.test.context.event.TestContextEventType;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.event.TestContextEvent;

/**
 * A Spring {@link TestExecutionListener} implementation listening for and handling different {@link TestContextEvent}
 * in order to destroy all GemFire/Geode {@link Object Mock Objects} at the appropriate test lifecycle event.
 *
 * By default, event handling for {@link TestContextEventType#AFTER_TEST_CLASS} is enabled and GemFire/Geode
 * {@link Object Mock Objects} will be destroyed when this event occurs.
 *
 * @author John Blum
 * @see TestContextEventType
 * @see GemFireMockObjectsSupport
 * @see TestContext
 * @see TestExecutionListener
 * @see TestContextEvent
 * @since 0.0.16
 */
public class DestroyGemFireMockObjectsTestExecutionListener implements TestExecutionListener {

	private static final AtomicReference<DestroyGemFireMockObjectsTestExecutionListener> instance =
		new AtomicReference<>(null);

	/**
	 * Returns an {@link Optional} reference to the constructed {@link DestroyGemFireMockObjectsTestExecutionListener}
	 * created by the Spring {@link TestContext} test framework on test execution.
	 *
	 * @return an {@link Optional} reference to a {@link DestroyGemFireMockObjectsTestExecutionListener} instance.
	 * @see Optional
	 */
	public static Optional<DestroyGemFireMockObjectsTestExecutionListener> getInstance() {
		return Optional.ofNullable(instance.get());
	}

	private final Set<TestContextEventType> destroyOnEventTypes =
		Collections.synchronizedSet(new HashSet<>(TestContextEventType.values().length));

	/**
	 * Constructs a new instance of the {@link DestroyGemFireMockObjectsTestExecutionListener}.
	 */
	public DestroyGemFireMockObjectsTestExecutionListener() {
		instance.set(this);
		enableDestroyOnEventType(TestContextEventType.AFTER_TEST_CLASS);
	}

	/**
	 * Disables event handling and destruction of GemFire/Geode {@link Object Mock Objects} for
	 * the given {@link TestContextEventType}.
	 *
	 * @param eventType {@link TestContextEventType} to disable event handling for.
	 * @return a boolean value indicating whether event handling for the given {@link TestContextEventType}
	 * was successfully disabled.
	 * @see TestContextEventType
	 * @see #enableDestroyOnEventType(TestContextEventType)
	 */
	public boolean disableDestroyOnEventType(@Nullable TestContextEventType eventType) {

		return eventType != null
			&& (this.destroyOnEventTypes.remove(eventType) || !this.destroyOnEventTypes.contains(eventType));
	}

	/**
	 * Enables event handling and destruction of GemFire/Geode {@link Object Mock Objects} for
	 * the given {@link TestContextEventType}.
	 *
	 * @param eventType {@link TestContextEventType} to enable event handling for.
	 * @return a boolean value indicating whether event handling for the given {@link TestContextEventType}
	 * was successfully enabled.
	 * @see TestContextEventType
	 * @see #disableDestroyOnEventType(TestContextEventType)
	 */
	public boolean enableDestroyOnEventType(@Nullable TestContextEventType eventType) {

		return eventType != null
			&& (this.destroyOnEventTypes.add(eventType) || this.destroyOnEventTypes.contains(eventType));
	}

	/**
	 * Determines whether event handling for the given {@link TestContextEventType} is enabled.
	 *
	 * @param eventType {{@link TestContextEventType} to evaluate.
	 * @return a boolean value indicating whether event handling for the given {@link TestContextEventType} is enabled.
	 * @see TestContextEventType
	 */
	protected boolean isDestroyOnEventTypeEnabled(@Nullable TestContextEventType eventType) {
		return this.destroyOnEventTypes.contains(eventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTestClass(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.BEFORE_TEST_CLASS)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareTestInstance(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.PREPARE_TEST_INSTANCE)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTestMethod(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.BEFORE_TEST_METHOD)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTestExecution(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.BEFORE_TEST_EXECUTION)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterTestExecution(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.AFTER_TEST_EXECUTION)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterTestMethod(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.AFTER_TEST_METHOD)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterTestClass(@NonNull TestContext testContext) {

		if (isDestroyOnEventTypeEnabled(TestContextEventType.AFTER_TEST_CLASS)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * Destroys all GemFire/Geode {@link Object Mock Objects}.
	 *
	 * @see GemFireMockObjectsSupport#destroy()
	 */
	protected void destroyGemFireMockObjects() {
		GemFireMockObjectsSupport.destroy();
	}
}
