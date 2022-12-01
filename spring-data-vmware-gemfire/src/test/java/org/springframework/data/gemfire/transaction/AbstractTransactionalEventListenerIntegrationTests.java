/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.io.Serializable;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.repository.support.GemfireRepositoryFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.transaction.event.TransactionApplicationEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * {@link AbstractTransactionalEventListenerIntegrationTests} is an abstract base class for writing transactional,
 * Apache Geode cache event listener tests.
 *
 * @author John Blum
 * @see ApplicationEventPublisher
 * @see Bean
 * @see Configuration
 * @see IntegrationTestsSupport
 * @see TransactionApplicationEvent
 * @see Transactional
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public abstract class AbstractTransactionalEventListenerIntegrationTests extends IntegrationTestsSupport {

	protected static final String GEMFIRE_LOG_LEVEL = "error";

	@Autowired
	private CustomerService customerService;

	protected abstract void assertTransactionEventListenerOnSuccess() throws Exception;

	protected abstract void assertTransactionEventListenerOnFailure() throws Exception;

	@Test
	public void successfulEntityTransactionTriggersCommitTransactionEvents() throws Exception {

		Customer jonDoe = this.customerService.save(Customer.newCustomer(1L, "Jon Doe"));

		Customer jonDoeLoaded = this.customerService.findById(jonDoe.getId());

		assertThat(jonDoeLoaded).isEqualTo(jonDoe);
		assertTransactionEventListenerOnSuccess();
	}

	@Test(expected = IllegalStateException.class)
	public void failingEntityTransactionTriggersRollbackTransactionEvent() throws Exception {

		Customer janeDoe = Customer.newCustomer(2L, "Jane Doe");

		try {
			this.customerService.saveFailsAndRollsback(janeDoe);
		}
		catch (IllegalStateException expected) {

			assertTransactionEventListenerOnFailure();
			assertThat(expected).hasMessage("TEST");
			assertThat(expected).hasNoCause();

			try {
				this.customerService.findById(janeDoe.getId());
			}
			catch (IllegalStateException alsoExpected) {

				assertThat(alsoExpected).hasMessage("No Customer having ID [2] was found");
				assertThat(alsoExpected).hasNoCause();

				throw alsoExpected;
			}
		}
	}

	@Data
	@EqualsAndHashCode
	@Region("Customers")
	@ToString(of = "name")
	@RequiredArgsConstructor(staticName = "newCustomer")
	static class Customer implements Serializable {

		@Id @NonNull
		private Long id;

		@NonNull
		private String name;

	}

	public interface CustomerRepository extends CrudRepository<Customer, Long> { }

	@Configuration
	static class CustomerRepositoryConfiguration {

		@Bean
		GemfireRepositoryFactoryBean<CustomerRepository, Customer, Long> customerRepositoryFactoryBean() {

			GemfireRepositoryFactoryBean<CustomerRepository, Customer, Long> customerRepositoryFactoryBean
				= new GemfireRepositoryFactoryBean<>(CustomerRepository.class);

			customerRepositoryFactoryBean.setGemfireMappingContext(new GemfireMappingContext());

			return customerRepositoryFactoryBean;
		}
	}

	@Service
	public static class CustomerService {

		private final CustomerRepository customerRepository;

		public CustomerService(CustomerRepository customerRepository) {

			Assert.notNull(customerRepository, "CustomerRepository is required");

			this.customerRepository = customerRepository;
		}

		public Customer findById(Long id) {
			return this.customerRepository.findById(id)
				.orElseThrow(() -> newIllegalStateException("No Customer having ID [%d] was found", id));
		}

		@Transactional
		public Customer save(Customer customer) {
			return this.customerRepository.save(customer);
		}

		@Transactional
		public Customer saveFailsAndRollsback(Customer customer) {

			this.customerRepository.save(customer);

			throw newIllegalStateException("TEST");
		}
	}

	@Service
	public static class TransactionEventPublishingCustomerService extends CustomerService {

		private final ApplicationEventPublisher eventPublisher;

		public TransactionEventPublishingCustomerService(ApplicationEventPublisher eventPublisher,
			CustomerRepository customerRepository) {

			super(customerRepository);

			Assert.notNull(eventPublisher, "ApplicationEventPublisher is required");

			this.eventPublisher = eventPublisher;
		}

		@Override
		@Transactional
		public Customer save(Customer customer) {

			customer = super.save(customer);

			this.eventPublisher.publishEvent(new TransactionApplicationEvent(this,
				String.valueOf(customer.getId())));

			return customer;
		}

		@Override
		@Transactional
		public Customer saveFailsAndRollsback(Customer customer) {

			this.eventPublisher.publishEvent(new TransactionApplicationEvent(this,
				String.valueOf(customer.getId())));

			return super.saveFailsAndRollsback(customer);
		}
	}
}
