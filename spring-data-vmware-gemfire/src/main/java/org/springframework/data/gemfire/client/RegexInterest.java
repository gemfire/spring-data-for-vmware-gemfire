/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import org.apache.geode.cache.InterestResultPolicy;

import org.springframework.util.Assert;

/**
 * Cache interest based on regular expression rather then individual key types.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Interest
 */
@SuppressWarnings("unused")
public class RegexInterest extends Interest<String> {

	public RegexInterest(String regex) {
		super(regex);
	}

	public RegexInterest(String regex, InterestResultPolicy policy) {
		super(regex, policy);
	}

	public RegexInterest(String regex, InterestResultPolicy policy, boolean durable) {
		super(regex, policy, durable);
	}

	public RegexInterest(String regex, InterestResultPolicy policy, boolean durable, boolean receiveValues) {
		super(regex, policy, durable, receiveValues);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void afterPropertiesSet() {
		Assert.hasText(getKey(), "Regex is required");
	}

	/**
	 * Returns the Regular Expression sent to the cache server to express interests in keys matching Regex pattern.
	 *
	 * Alias for {@link #getKey()}.
	 *
	 * @return the Regex pattern used in the interest registration.
	 * @see org.apache.geode.cache.Region#registerInterestRegex(String)
	 */
	public String getRegex() {
		return getKey();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Type getType() {
		return Type.REGEX;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setType(Type type) {
		logger.warn(String.format("Setting the Type [%1$s] of Interest on [%2$s] is ignored",
			type, getClass().getName()));
	}
}
