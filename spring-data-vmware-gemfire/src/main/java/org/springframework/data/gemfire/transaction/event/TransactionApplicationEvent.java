/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.StringUtils;

/**
 * The {@link TransactionApplicationEvent} is an implementation of {@link ApplicationEvent} which is fired during
 * a transaction.
 *
 * @author John Blum
 * @see java.time.Instant
 * @see java.time.LocalDateTime
 * @see org.springframework.context.ApplicationEvent
 * @since 2.3.0
 */
public class TransactionApplicationEvent extends ApplicationEvent {

	protected static final String TIMESTAMP_PATTERN = "yyyy-MM-dd-hh:mm:ss.S";

	/**
	 * Factory method used to construct a new instance of {@link TransactionApplicationEvent} initialized with
	 * the given {@link Object source}.
	 *
	 * @param source {@link Object} defined as the source of this {@link TransactionApplicationEvent}.
	 * @return a new instance of {@link TransactionApplicationEvent}.
	 * @see #TransactionApplicationEvent(Object)
	 */
	public static TransactionApplicationEvent of(Object source) {
		return new TransactionApplicationEvent(source);
	}

	private String details;

	/**
	 * Constructs a new instance of {@link TransactionApplicationEvent} initialized with the given {@link Object source}
	 * of this transaction event.
	 *
	 * @param source {@link Object} defined as the source of this {@link TransactionApplicationEvent}.
	 */
	public TransactionApplicationEvent(Object source) {
		this(source, null);
	}

	/**
	 * Constructs a new instance of {@link TransactionApplicationEvent} initialized with the given {@link Object source}
	 * of this transaction event and {@link String details} describing the transaction event.
	 *
	 * @param source {@link Object} defined as the source of this {@link TransactionApplicationEvent}.
	 * @param details {@link String} describing the transaction event.
	 */
	public TransactionApplicationEvent(Object source, String details) {

		super(source);

		this.details = details;
	}

	public Optional<String> getDetails() {
		return Optional.ofNullable(this.details).filter(StringUtils::hasText);
	}

	public LocalDateTime getTimestampAsLocalDateTime() {
		return LocalDateTime.from(Instant.ofEpochMilli(getTimestamp()));
	}

	public String getTimestampAsString() {
		return getTimestampAsString(TIMESTAMP_PATTERN);
	}

	public String getTimestampAsString(String pattern) {
		return getTimestampAsLocalDateTime().format(DateTimeFormatter.ofPattern(pattern));
	}

	@Override
	public String toString() {

		return getDetails()
			.map(details -> String.format("%s - %s", getTimestampAsString(), details))
			.orElse(String.format("%s[%s]", getClass().getSimpleName(), getTimestampAsString()));
	}

	public TransactionApplicationEvent with(String details) {
		this.details = details;
		return this;
	}
}
