/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.util;

import java.util.Comparator;

/**
 * The IdentityComparator class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class IdentityComparator implements Comparator<Object> {

	public static final IdentityComparator INSTANCE = new IdentityComparator();

	@Override
	public int compare(Object objectOne, Object objectTwo) {
		return objectOne == objectTwo ? 0 : ObjectToByteArrayComparator.INSTANCE.compare(objectOne, objectTwo);
	}
}
