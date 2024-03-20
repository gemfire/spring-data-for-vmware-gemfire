/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.util;

import java.util.Comparator;

/**
 * The IdentityHashCodeComparator class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class IdentityHashCodeComparator implements Comparator<Object> {

	public static final IdentityHashCodeComparator INSTANCE = new IdentityHashCodeComparator();

	@Override
	public int compare(Object objectOne, Object objectTwo) {

		int objectOneHashCode = System.identityHashCode(objectOne);
		int objectTwoHashCode = System.identityHashCode(objectTwo);

		// Cannot use subtraction; Must be careful of overflow/underflow.
		return objectOneHashCode < objectTwoHashCode ? -1
			: objectOneHashCode > objectTwoHashCode ? 1
			: 0;
	}
}
