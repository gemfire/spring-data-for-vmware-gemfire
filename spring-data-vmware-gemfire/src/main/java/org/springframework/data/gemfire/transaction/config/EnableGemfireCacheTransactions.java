/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.transaction.event.TransactionApplicationEvent;

/**
 * The {@link EnableGemfireCacheTransactions} annotation enables Pivotal GemFire or Apache Geode Cache Transactions
 * in Spring's Transaction Management infrastructure.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Import
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction">Spring Transaction Management</a>
 * @see <a href="https://docs.spring.io/spring-data-gemfire/docs/current/reference/html/#apis:transaction-management">Spring Data GemFire Transaction Management</a>
 * @see <a href="https://geode.apache.org/docs/guide/113/developing/transactions/chapter_overview.html">Geode Transactions</a>
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GemfireCacheTransactionsConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableGemfireCacheTransactions {

	/**
	 * Configures whether {@link TransactionApplicationEvent} objects are automatically fired by the framework.
	 *
	 * @return a boolean value indicating whether transactional events are automatically fired by the framework
	 * without the need to manually publish transaction events.  Defaults to {@literal false}.
	 */
	boolean enableAutoTransactionEventPublishing() default false;

}
