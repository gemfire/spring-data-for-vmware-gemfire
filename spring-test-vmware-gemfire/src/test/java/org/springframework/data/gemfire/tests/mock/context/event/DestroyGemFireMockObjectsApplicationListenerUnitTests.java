/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.context.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;

/**
 * The DestroyGemFireMockObjectsApplicationListenerUnitTests class...
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see ApplicationEvent
 * @see DestroyGemFireMockObjectsApplicationListener
 * @see TestContext
 * @since 0.0.16
 */
public class DestroyGemFireMockObjectsApplicationListenerUnitTests {

	private DestroyGemFireMockObjectsApplicationListener listener;

	@Before
	public void configureListener() {

		Iterable<Class<? extends ApplicationEvent>> destroyEvents =
			Arrays.asList(ContextClosedEvent.class, AfterTestExecutionEvent.class);

		this.listener = spy(new DestroyGemFireMockObjectsApplicationListener(destroyEvents));

		doNothing().when(this.listener).destroyGemFireMockObjects();
	}

	@Test
	public void destroysGemFireMockObjectsOnAfterTestExecutionEvent() {

		AfterTestExecutionEvent mockEvent = mock(AfterTestExecutionEvent.class);

		this.listener.onApplicationEvent(mockEvent);

		verify(this.listener, times(1)).destroyGemFireMockObjects();
	}

	@Test
	public void destroysGemFireMockObjectsOnAfterTestExecutionEventExtension() {

		AfterTestExecutionEventExtension mockEvent = mock(AfterTestExecutionEventExtension.class);

		this.listener.onApplicationEvent(mockEvent);

		verify(this.listener, times(1)).destroyGemFireMockObjects();
	}

	@Test
	public void destroysGemFireMockObjectsOnContextClosedEvent() {

		ContextClosedEvent mockEvent = mock(ContextClosedEvent.class);

		this.listener.onApplicationEvent(mockEvent);

		verify(this.listener, times(1)).destroyGemFireMockObjects();
	}

	@Test
	public void doesNotDestroyGemFireMockObjectsOnBeforeTestExecutionEvent() {

		BeforeTestExecutionEvent mockEvent = mock(BeforeTestExecutionEvent.class);

		this.listener.onApplicationEvent(mockEvent);

		verify(this.listener, never()).destroyGemFireMockObjects();
	}

	@Test
	public void doesNotDestroyGemFireMockObjectsOnContextRefreshedEvent() {

		ContextRefreshedEvent mockEvent = mock(ContextRefreshedEvent.class);

		this.listener.onApplicationEvent(mockEvent);

		verify(this.listener, never()).destroyGemFireMockObjects();
	}

	@Test
	public void doesNotDestroyGemFireMockObjectsOnRandomApplicationEvent() {

		ApplicationEvent mockEvent = mock(ApplicationEvent.class);

		this.listener.onApplicationEvent(mockEvent);

		verify(this.listener, never()).destroyGemFireMockObjects();
	}

	static class AfterTestExecutionEventExtension extends AfterTestExecutionEvent {

		AfterTestExecutionEventExtension(TestContext context) {
			super(context);
		}
	}
}
