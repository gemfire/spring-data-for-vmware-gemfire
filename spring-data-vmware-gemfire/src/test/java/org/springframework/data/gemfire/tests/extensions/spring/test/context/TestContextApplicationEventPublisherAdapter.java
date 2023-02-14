/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.test.context;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;

/**
 * {@literal Adapter} used to adapt the {@link TestContext} API as an {@link ApplicationEventPublisher}.
 *
 * @author John Blum
 * @see ApplicationEventPublisher
 * @see TestContext
 * @since 0.0.23
 */
@SuppressWarnings("unused")
public class TestContextApplicationEventPublisherAdapter implements ApplicationEventPublisher {

	protected static @NonNull TestContextApplicationEventPublisherAdapter from(@NonNull TestContext testContext) {
		return new TestContextApplicationEventPublisherAdapter(testContext);
	}

	private final TestContext testContext;

	/**
	 * Constructs a new instance of {@link TestContextApplicationEventPublisherAdapter} initialized with the given,
	 * required {@link TestContext}.
	 *
	 * @param testContext Spring {@link TestContext} to be adapted as a Spring {@link ApplicationEventPublisher}.
	 * @throws IllegalArgumentException if {@link TestContext} is {@literal null}.
	 * @see TestContext
	 */
	protected TestContextApplicationEventPublisherAdapter(@NonNull TestContext testContext) {
		Assert.notNull(testContext, "TestContext must not be null");
		this.testContext = testContext;
	}

	/**
	 * Returns a reference to the configured {@link TestContext}.
	 *
	 * @return a reference to the configured {@link TestContext}; never {@literal null}.
	 * @see TestContext
	 */
	protected @NonNull TestContext getTestContext() {
		return this.testContext;
	}

	/**
	 * Returns an {@link Optional} reference to the {@link ApplicationEventPublisher} if available.
	 *
	 * The {@link ApplicationEventPublisher}, or rather Spring {@link ApplicationContext}, is resolved from
	 * the {@link TestContext} depending on whether the {@link ApplicationContext} has been initialized yet.
	 *
	 * @return an {@link Optional} reference to the {@link ApplicationEventPublisher} if available.
	 * @see ApplicationEventPublisher
	 * @see Optional
	 * @see #getTestContext()
	 */
	protected Optional<ApplicationEventPublisher> getApplicationEventPublisher() {

		return Optional.ofNullable(getTestContext())
			.filter(TestContext::hasApplicationContext)
			.map(TestContext::getApplicationContext);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void publishEvent(@NonNull Object event) {
		getApplicationEventPublisher().ifPresent(applicationEventPublisher ->
			applicationEventPublisher.publishEvent(event));
	}
}
