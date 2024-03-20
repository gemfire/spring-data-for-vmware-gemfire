/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.support;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The {@link IdentifierSequence} class is an Identifier (ID) generator generating unique IDs in sequence.
 *
 * @author John Blum
 * @see System#currentTimeMillis()
 * @see AtomicLong
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class IdentifierSequence {

	private static final AtomicLong ID_SEQUENCE = new AtomicLong(System.currentTimeMillis());

	public static long nextId() {
		return ID_SEQUENCE.incrementAndGet();
	}
}
