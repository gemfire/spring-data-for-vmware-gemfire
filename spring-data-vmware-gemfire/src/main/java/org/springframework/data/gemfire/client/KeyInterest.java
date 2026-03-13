/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client;

import org.springframework.data.gemfire.gud.api.GudInterestResultPolicy;

/**
 * Cache Region interest based on individual keys.
 *
 * @author John Blum
 * @param <K> {@link Class} type of the key.
 * @see Interest
 */
@SuppressWarnings("unused")
public class KeyInterest<K> extends Interest<K> {

	public KeyInterest(K key) {
		super(key);
	}

	public KeyInterest(K key, GudInterestResultPolicy policy) {
		super(key, policy);
	}

	public KeyInterest(K key, GudInterestResultPolicy policy, boolean durable) {
		super(key, policy, durable);
	}

	public KeyInterest(K key, GudInterestResultPolicy policy, boolean durable, boolean receiveValues) {
		super(key, policy, durable, receiveValues);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type getType() {
		return Type.KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setType(Type type) {
		logger.warn(String.format("Setting the Type [%1$s] of Interest on [%2$s] is ignored",
			type, getClass().getName()));
	}
}
