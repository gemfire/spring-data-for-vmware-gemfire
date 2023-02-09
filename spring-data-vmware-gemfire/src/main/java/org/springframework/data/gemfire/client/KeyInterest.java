/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import org.apache.geode.cache.InterestResultPolicy;

/**
 * Cache Region interest based on individual keys.
 *
 * @author John Blum
 * @param <K> {@link Class} type of the key.
 * @see org.springframework.data.gemfire.client.Interest
 */
@SuppressWarnings("unused")
public class KeyInterest<K> extends Interest<K> {

	public KeyInterest(K key) {
		super(key);
	}

	public KeyInterest(K key, InterestResultPolicy policy) {
		super(key, policy);
	}

	public KeyInterest(K key, InterestResultPolicy policy, boolean durable) {
		super(key, policy, durable);
	}

	public KeyInterest(K key, InterestResultPolicy policy, boolean durable, boolean receiveValues) {
		super(key, policy, durable, receiveValues);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Type getType() {
		return Type.KEY;
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
