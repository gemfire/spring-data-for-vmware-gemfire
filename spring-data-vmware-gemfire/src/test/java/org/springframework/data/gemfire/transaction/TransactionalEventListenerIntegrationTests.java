/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.transaction.config.EnableGemfireCacheTransactions;
import org.springframework.data.gemfire.transaction.event.TransactionApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Integration Tests for the Spring {@link TransactionalEventListener} in the context of Apache Geode
 * cache transactions.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see ApplicationEvent
 * @see ApplicationEventPublisher
 * @see Bean
 * @see Import
 * @see ClientCacheApplication
 * @see AbstractTransactionalEventListenerIntegrationTests
 * @see EnableGemfireCacheTransactions
 * @see TransactionApplicationEvent
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see org.springframework.transaction.annotation.Transactional
 * @see TransactionPhase
 * @see TransactionalEventListener
 * @since 2.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class TransactionalEventListenerIntegrationTests extends AbstractTransactionalEventListenerIntegrationTests {

	@Autowired
	private TestTransactionEventListener transactionEventListener;

	@Override
	protected void assertTransactionEventListenerOnSuccess() {

		assertThat(this.transactionEventListener.getAndClearTransactionDetails()).containsExactly("1");

		assertThat(this.transactionEventListener.getAndClearTransactionPhases())
			.containsExactly(TransactionPhase.BEFORE_COMMIT, TransactionPhase.AFTER_COMMIT);
	}

	@Override
	protected void assertTransactionEventListenerOnFailure() {

		assertThat(this.transactionEventListener.getAndClearTransactionDetails()).containsExactly("2");

		assertThat(this.transactionEventListener.getAndClearTransactionPhases())
			.containsExactly(TransactionPhase.AFTER_ROLLBACK);
	}

	@ClientCacheApplication(logLevel = GEMFIRE_LOG_LEVEL)
	@EnableEntityDefinedRegions(
		basePackageClasses = Customer.class,
		clientRegionShortcut = ClientRegionShortcut.LOCAL
	)
	@EnableGemfireCacheTransactions
	@Import(CustomerRepositoryConfiguration.class)
	static class TestConfiguration {

		@Bean
		CustomerService customerService(ApplicationEventPublisher eventPublisher,
				CustomerRepository customerRepository) {

			return new TransactionEventPublishingCustomerService(eventPublisher, customerRepository);
		}

		@Bean
		TestTransactionEventListener testTransactionEventListener() {
			return new TestTransactionEventListener();
		}
	}

	@Component
	public static class TestTransactionEventListener {

		private final List<TransactionPhase> transactionPhases = new ArrayList<>();

		private final Set<String> transactionDetails = new HashSet<>();

		public Set<String> getAndClearTransactionDetails() {

			Set<String> copy = new HashSet<>(this.transactionDetails);

			this.transactionDetails.clear();

			return copy;
		}

		public List<TransactionPhase> getAndClearTransactionPhases() {

			List<TransactionPhase> copy = new ArrayList<>(this.transactionPhases);

			this.transactionPhases.clear();

			return copy;
		}

		private void appendTransactionPhase(TransactionPhase transactionPhase) {
			Optional.ofNullable(transactionPhase).ifPresent(this.transactionPhases::add);
		}

		private void extractTransactionDetails(ApplicationEvent event) {

			Optional.ofNullable(event)
				.filter(TransactionApplicationEvent.class::isInstance)
				.map(TransactionApplicationEvent.class::cast)
				.flatMap(TransactionApplicationEvent::getDetails)
				.ifPresent(this.transactionDetails::add);
		}

		@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
		public void handleTransactionBeforeCommit(ApplicationEvent event) {
			appendTransactionPhase(TransactionPhase.BEFORE_COMMIT);
			extractTransactionDetails(event);
		}

		@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
		public void handleTransactionAfterCommit(ApplicationEvent event) {
			appendTransactionPhase(TransactionPhase.AFTER_COMMIT);
			extractTransactionDetails(event);
		}

		@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
		public void handleTransactionAfterRollback(ApplicationEvent event) {
			appendTransactionPhase(TransactionPhase.AFTER_ROLLBACK);
			extractTransactionDetails(event);
		}
	}
}
