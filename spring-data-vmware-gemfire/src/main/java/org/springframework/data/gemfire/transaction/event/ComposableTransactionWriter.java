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

import java.util.Properties;

import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudTransactionEvent;
import org.springframework.data.gemfire.gud.api.GudTransactionWriter;
import org.springframework.data.gemfire.gud.api.GudTransactionWriterException;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * An implementation of Apache Geode's {@link GudTransactionWriter} interface that uses the {@literal Composite Software Design}
 * Pattern to compose multiple {@link GudTransactionWriter} objects into a single instance.
 *
 * @author John Blum
 * @see GudTransactionWriter
 * @since 2.3.0
 */
public class ComposableTransactionWriter implements GudTransactionWriter {

	/**
	 * Factory method used to construct and compose 2 {@link GudTransactionWriter} objects into a composite instance of
	 * {@link GudTransactionWriter} functioning as a single instance.
	 *
	 * @param transactionWriterOne first {@link GudTransactionWriter} in the composition.
	 * @param transactionWriterTwo second {@link GudTransactionWriter} in the composition.
	 * @return the first {@link GudTransactionWriter} if the second {@link GudTransactionWriter} is {@literal null}, or return
	 * the second {@link GudTransactionWriter} if the first {@link GudTransactionWriter} is {@literal null}, or return
	 * the composition of both {@link GudTransactionWriter} one and {@link GudTransactionWriter} two.
	 * @see GudTransactionWriter
	 */
	public static @Nullable GudTransactionWriter compose(@Nullable GudTransactionWriter transactionWriterOne,
			@Nullable GudTransactionWriter transactionWriterTwo) {

		return transactionWriterOne == null ? transactionWriterTwo
			: transactionWriterTwo == null ? transactionWriterOne
			: new ComposableTransactionWriter(transactionWriterOne, transactionWriterTwo);
	}

	private final GudTransactionWriter transactionWriterOne;
	private final GudTransactionWriter transactionWriterTwo;

	private ComposableTransactionWriter(@NonNull GudTransactionWriter transactionWriterOne,
			@NonNull GudTransactionWriter transactionWriterTwo) {

		Assert.notNull(transactionWriterOne, "TransactionWriter one must not be null");
		Assert.notNull(transactionWriterTwo, "TransactionWriter two must not be null");

		this.transactionWriterOne = transactionWriterOne;
		this.transactionWriterTwo = transactionWriterTwo;
	}

	/**
	 * Returns a reference to the first {@link GudTransactionWriter} in the composition.
	 *
	 * @return a reference to the first {@link GudTransactionWriter} in the composition.
	 * @see GudTransactionWriter
	 */
	protected GudTransactionWriter getTransactionWriterOne() {
		return this.transactionWriterOne;
	}

	/**
	 * Returns a reference to the second {@link GudTransactionWriter} in the composition.
	 *
	 * @return a reference to the second {@link GudTransactionWriter} in the composition.
	 * @see GudTransactionWriter
	 */
	protected GudTransactionWriter getTransactionWriterTwo() {
		return this.transactionWriterTwo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeCommit(GudTransactionEvent event) throws GudTransactionWriterException {

		getTransactionWriterOne().beforeCommit(event);
		getTransactionWriterTwo().beforeCommit(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {

		getTransactionWriterOne().close();
		getTransactionWriterTwo().close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Properties properties) {

		getTransactionWriterOne().init(properties);
		getTransactionWriterTwo().init(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(GudCache cache, Properties properties) {

		getTransactionWriterOne().initialize(cache, properties);
		getTransactionWriterTwo().initialize(cache, properties);
	}
}
