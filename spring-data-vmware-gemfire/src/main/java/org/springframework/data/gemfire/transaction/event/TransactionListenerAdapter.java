/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.transaction.event;

import org.springframework.data.gemfire.gud.api.GudTransactionEvent;
import org.springframework.data.gemfire.gud.api.GudTransactionListener;
import org.springframework.data.gemfire.gud.api.GudTransactionWriter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * The {@link TransactionListenerAdapter} class is an Apache Geode {@link GudTransactionListener}
 * and {@link GudTransactionWriter} implementation that publishes the {@link GudTransactionEvent} to application components
 * and beans declared in the Spring {@link ApplicationContext} using the {@link ApplicationEventPublisher}.
 *
 * @author John Blum
 * @see GudTransactionEvent
 * @see GudTransactionListener
 * @see GudTransactionWriter
 * @see ApplicationContext
 * @see ApplicationEventPublisher
 * @since 2.3.0
 */
public class TransactionListenerAdapter implements GudTransactionListener, GudTransactionWriter {

	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Constructs a new instance of the {@link TransactionListenerAdapter} initialized with the required
	 * {@link ApplicationEventPublisher} to publish Apache Geode cache {@link GudTransactionEvent TransactionEvents}
	 * to application declared components and beans in a Spring {@link ApplicationContext}.
	 *
	 * @param applicationEventPublisher {@link ApplicationEventPublisher} used to publish Apache Geode cache
	 * {@link GudTransactionEvent TransactionEvents}.
	 * @throws IllegalArgumentException if the {@link ApplicationEventPublisher} is {@literal null}.
	 * @see ApplicationEventPublisher
	 */
	public TransactionListenerAdapter(ApplicationEventPublisher applicationEventPublisher) {

		Assert.notNull(applicationEventPublisher, "ApplicationEventPublisher must not be null");

		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Returns a reference to the configured {@link ApplicationEventPublisher}.
	 *
	 * @return a reference to the configured {@link ApplicationEventPublisher}.
	 * @see ApplicationEventPublisher
	 */
	protected @NonNull ApplicationEventPublisher getApplicationEventPublisher() {
		return this.applicationEventPublisher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeCommit(GudTransactionEvent event) {

		// NOTE: this will not work because Apache Geode's cache before commit transaction event is only triggered
		// after Spring's AbstractPlatformTransaction.triggerBeforeCommit(:TransactionStatus) method, which is where
		// all application @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT) annotated transaction
		// event handler methods are invoked.

		//getApplicationEventPublisher().publishEvent(TransactionApplicationEvent.of(event));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterCommit(GudTransactionEvent event) {
		getApplicationEventPublisher().publishEvent(TransactionApplicationEvent.of(event));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterFailedCommit(GudTransactionEvent event) { }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterRollback(GudTransactionEvent event) {
		getApplicationEventPublisher().publishEvent(TransactionApplicationEvent.of(event));
	}
}
