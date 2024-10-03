/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.test.context.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.springframework.test.context.event.AfterTestClassEvent;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.AfterTestMethodEvent;
import org.springframework.test.context.event.BeforeTestClassEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestMethodEvent;
import org.springframework.test.context.event.PrepareTestInstanceEvent;
import org.springframework.test.context.event.TestContextEvent;

/**
 * Unit Tests for {@link TestContextEventType}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see TestContextEventType
 * @see TestContextEvent
 * @since 0.0.16
 */
public class TestContextEventTypeUnitTests {

	@Test
	public void fromTestContextEventToEnum() {

		List<TestContextEvent> events = Arrays.asList(
			mock(BeforeTestClassEvent.class),
			mock(PrepareTestInstanceEvent.class),
			mock(BeforeTestMethodEvent.class),
			mock(BeforeTestExecutionEvent.class),
			mock(AfterTestExecutionEvent.class),
			mock(AfterTestMethodEvent.class),
			mock(AfterTestClassEvent.class)
		);

		events.forEach(event -> {

			TestContextEventType eventType = TestContextEventType.from(event);

			assertThat(eventType).isNotNull();
			assertThat(eventType.getTestContextEventType()).isAssignableFrom(event.getClass());
		});
	}

	@Test
	public void fromTestContextEventReturnsNull() {
		assertThat(TestContextEventType.from(mock(TestContextEvent.class))).isNull();
	}

	@Test
	public void fromNullIsNullSafeAndReturnsNull() {
		assertThat(TestContextEventType.from(null)).isNull();
	}
}
