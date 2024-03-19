/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.util.ObjectSizer;

/**
 * @author Costin Leau
 */
public class SimpleObjectSizer implements ObjectSizer {

	private static final ObjectSizer sizer = ObjectSizer.DEFAULT;

	@Override
	public int sizeof(Object o) {
		return sizer.sizeof(o);
	}

}
