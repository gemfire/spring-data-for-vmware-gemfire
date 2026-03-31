/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * @author Costin Leau
 */
public class SimpleObjectSizer implements GudObjectSizer {

	private static final GudObjectSizer sizer = GudObjectSizer.DEFAULT;

	@Override
	public int sizeof(Object o) {
		return sizer.sizeof(o);
	}

}
