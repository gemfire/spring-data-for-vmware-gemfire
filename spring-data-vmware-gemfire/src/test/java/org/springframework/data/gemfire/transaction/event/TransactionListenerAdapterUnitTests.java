/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.TransactionEvent;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Unit Tests for {@link TransactionListenerAdapter}.
 *
 * @author John Blum
 * @see Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see ApplicationEventPublisher
 * @see TransactionListenerAdapter
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionListenerAdapterUnitTests {

	@Mock
	private ApplicationEventPublisher mockApplicationEventPublisher;

	@Mock
	private TransactionEvent mockTransactionEvent;

	@Test
	public void constructTransactionListenerAdapterIsCorrect() {

		TransactionListenerAdapter listener = new TransactionListenerAdapter(this.mockApplicationEventPublisher);

		assertThat(listener).isNotNull();
		assertThat(listener.getApplicationEventPublisher()).isEqualTo(this.mockApplicationEventPublisher);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructTransactionListenerAdapterWithNullApplicationEventPublisherThrowsIllegalArgumentException() {

		try {
			new TransactionListenerAdapter(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("ApplicationEventPublisher must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	private void invokingApplicationEventPublisherTest(Consumer<TransactionListenerAdapter> listenerConsumer) {

		doAnswer(invocation -> {

			ApplicationEvent applicationEvent = invocation.getArgument(0, ApplicationEvent.class);

			assertThat(applicationEvent).isInstanceOf(TransactionApplicationEvent.class);
			assertThat(applicationEvent.getSource()).isEqualTo(this.mockTransactionEvent);

			return null;

		}).when(this.mockApplicationEventPublisher).publishEvent(any(ApplicationEvent.class));

		TransactionListenerAdapter listener = new TransactionListenerAdapter(this.mockApplicationEventPublisher);

		listenerConsumer.accept(listener);

		verify(this.mockApplicationEventPublisher, times(1))
			.publishEvent(isA(TransactionApplicationEvent.class));
	}

	private void nonInvokingApplicationEventPublisherTest(Consumer<TransactionListenerAdapter> listenerConsumer) {

		TransactionListenerAdapter listener = new TransactionListenerAdapter(this.mockApplicationEventPublisher);

		listenerConsumer.accept(listener);

		verify(this.mockApplicationEventPublisher, never()).publishEvent(any(ApplicationEvent.class));
	}

	@Test
	public void beforeCommitDoesNotInvokeApplicationEventPublisher() {
		nonInvokingApplicationEventPublisherTest(listener -> listener.beforeCommit(this.mockTransactionEvent));
	}

	@Test
	public void afterCommitInvokesApplicationEventPublisher() {
		invokingApplicationEventPublisherTest(listener -> listener.afterCommit(this.mockTransactionEvent));
	}

	@Test
	public void afterFailedCommitDoesNotInvokeApplicationEventPublisher() {
		nonInvokingApplicationEventPublisherTest(listener -> listener.afterFailedCommit(this.mockTransactionEvent));
	}

	@Test
	public void afterRollbackInvokesApplicationEventPublisher() {
		invokingApplicationEventPublisherTest(listener -> listener.afterRollback(this.mockTransactionEvent));
	}
}
