/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.test.context.event;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.event.AfterTestClassEvent;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.AfterTestMethodEvent;
import org.springframework.test.context.event.BeforeTestClassEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestMethodEvent;
import org.springframework.test.context.event.PrepareTestInstanceEvent;
import org.springframework.test.context.event.TestContextEvent;
import org.springframework.util.Assert;

/**
 * An enumeration of {@link TestContextEvent} types in the Spring {@link TestContext} test framework.
 *
 * @author John Blum
 * @see TestContext
 * @see TestContextEvent
 * @since 0.0.16
 */
@SuppressWarnings("unused")
public enum TestContextEventType {

	BEFORE_TEST_CLASS(BeforeTestClassEvent.class),
	PREPARE_TEST_INSTANCE(PrepareTestInstanceEvent.class),
	BEFORE_TEST_METHOD(BeforeTestMethodEvent.class),
	BEFORE_TEST_EXECUTION(BeforeTestExecutionEvent.class),
	AFTER_TEST_EXECUTION(AfterTestExecutionEvent.class),
	AFTER_TEST_METHOD(AfterTestMethodEvent.class),
	AFTER_TEST_CLASS(AfterTestClassEvent.class);

	public static @Nullable TestContextEventType from(@Nullable TestContextEvent event) {

		for (TestContextEventType eventType : values()) {
			if (eventType.getTestContextEventType().isInstance(event)) {
				return eventType;
			}
		}

		return null;
	}

	private final Class<? extends TestContextEvent> eventType;

	/**
	 * Constructs a new instance of the {@link TestContextEventType} enumeration creating a new enumerated value
	 * initialized with a {@link TestContextEvent} {@link Class type}.
	 *
	 * @param eventType {@link TestContextEvent} {@link Class type} on which this enumerated type is based.
	 * @throws IllegalArgumentException if the {@link TestContextEvent} {@link Class type} is {@literal null}.
	 * @see TestContextEvent
	 * @see Class
	 */
	TestContextEventType(Class<? extends TestContextEvent> eventType) {

		Assert.notNull(eventType,
			"The Class type of the TestContextEvent for this enumerated value must not be null");

		this.eventType = eventType;
	}

	/**
	 * Returns the {@link TestContextEvent} {@link Class type} on which this enumerated type is based.
	 *
	 * @return the {@link TestContextEvent} {@link Class type} on which this enumerated type is based.
	 * @see TestContextEvent
	 * @see Class
	 */
	public @NonNull Class<? extends TestContextEvent> getTestContextEventType() {
		return eventType;
	}
}
